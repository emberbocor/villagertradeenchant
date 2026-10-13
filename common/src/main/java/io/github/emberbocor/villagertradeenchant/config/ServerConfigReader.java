package io.github.emberbocor.villagertradeenchant.config;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.jetbrains.annotations.Nullable;
import org.slf4j.helpers.MessageFormatter;

import com.electronwill.nightconfig.core.UnmodifiableConfig;

import io.github.emberbocor.villagertradeenchant.VillagerTradeEnchant;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;

final class ServerConfigReader {
    private static final int MAX_COUNT = 64;

    private final Path file;
    private int cappedPrices;

    ServerConfigReader(Path file) {
        this.file = file;
    }

    ServerConfig.Settings read(@Nullable UnmodifiableConfig config, List<Holder.Reference<Enchantment>> enchantments) {
        double multiplier = config == null ? 1.0 : multiplier(config);
        boolean allowConflicts = config != null && bool(config, "allowConflicts", false);
        boolean allowBooks = config == null || bool(config, "allowBooks", true);
        Map<String, UnmodifiableConfig> tables = config == null ? Map.of() : tables(config, enchantments);

        Map<ResourceKey<Enchantment>, ServerConfig.EnchantmentPrices> prices = new HashMap<>();
        for (Holder.Reference<Enchantment> enchantment : enchantments) {
            prices.put(enchantment.key(), prices(enchantment, tables.get(ServerConfigFile.id(enchantment)), multiplier));
        }
        if (cappedPrices > 0) {
            VillagerTradeEnchant.LOGGER.warn("{}: {} prices went above the limit with priceMultiplier {} and were capped at 64 (or the item's max stack size)",
                    file, cappedPrices, multiplier);
        }
        return new ServerConfig.Settings(allowConflicts, allowBooks, Map.copyOf(prices));
    }

    private double multiplier(UnmodifiableConfig config) {
        Object value = config.get("priceMultiplier");
        if (value == null) {
            return 1.0;
        }
        if (value instanceof Number number && Double.isFinite(number.doubleValue()) && number.doubleValue() > 0) {
            return number.doubleValue();
        }
        warn("priceMultiplier must be a number above 0, got '{}'; using 1.0", value);
        return 1.0;
    }

    private boolean bool(UnmodifiableConfig config, String key, boolean fallback) {
        Object value = config.get(key);
        if (value == null) {
            return fallback;
        }
        if (value instanceof Boolean bool) {
            return bool;
        }
        warn("{} must be true or false, got '{}'; using {}", key, value, fallback);
        return fallback;
    }

    private Map<String, UnmodifiableConfig> tables(UnmodifiableConfig config, List<Holder.Reference<Enchantment>> enchantments) {
        if (!(config.get("prices") instanceof UnmodifiableConfig prices)) {
            return Map.of();
        }
        Set<String> known = enchantments.stream().map(ServerConfigFile::id).collect(Collectors.toSet());
        Map<String, UnmodifiableConfig> tables = new HashMap<>();
        for (UnmodifiableConfig.Entry entry : prices.entrySet()) {
            String id = entry.getKey();
            if (!known.contains(id)) {
                warn("unknown enchantment '{}', skipped", id);
            } else if (entry.getValue() instanceof UnmodifiableConfig table) {
                tables.put(id, table);
            } else {
                warn("prices.\"{}\" must be a table, skipped", id);
            }
        }
        return tables;
    }

    private ServerConfig.EnchantmentPrices prices(Holder.Reference<Enchantment> enchantment, @Nullable UnmodifiableConfig table, double multiplier) {
        String id = ServerConfigFile.id(enchantment);
        boolean enabled = table == null || enabled(id, table);
        Int2ObjectMap<Price> configured = table == null ? new Int2ObjectOpenHashMap<>() : levels(enchantment, table);

        Int2ObjectMap<Price> levels = new Int2ObjectOpenHashMap<>();
        Enchantment value = enchantment.value();
        for (int level = value.getMinLevel(); level <= value.getMaxLevel(); level++) {
            Price price = configured.get(level);
            levels.put(level, multiply(price != null ? price : DefaultPrices.of(enchantment, level), multiplier));
        }
        return new ServerConfig.EnchantmentPrices(enabled, levels);
    }

    private boolean enabled(String id, UnmodifiableConfig table) {
        Object value = table.get("enabled");
        if (value == null) {
            return true;
        }
        if (value instanceof Boolean bool) {
            return bool;
        }
        warn("prices.\"{}\".enabled must be true or false, got '{}'; using true", id, value);
        return true;
    }

    private Int2ObjectMap<Price> levels(Holder.Reference<Enchantment> enchantment, UnmodifiableConfig table) {
        String id = ServerConfigFile.id(enchantment);
        Int2ObjectMap<Price> levels = new Int2ObjectOpenHashMap<>();
        Object value = table.get("levels");
        if (value == null) {
            return levels;
        }
        if (!(value instanceof UnmodifiableConfig entries)) {
            warn("prices.\"{}\".levels must be a table, using default prices", id);
            return levels;
        }
        int min = enchantment.value().getMinLevel();
        int max = enchantment.value().getMaxLevel();
        for (UnmodifiableConfig.Entry entry : entries.entrySet()) {
            Integer level = parseLevel(entry.getKey());
            if (level == null || level < min || level > max) {
                warn("level '{}' of {} is outside {}-{}, skipped", entry.getKey(), id, min, max);
                continue;
            }
            price(id, level, entry.getValue()).ifPresent(price -> levels.put(level.intValue(), price));
        }
        return levels;
    }

    @Nullable
    private static Integer parseLevel(String key) {
        try {
            return Integer.parseInt(key);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Optional<Price> price(String id, int level, Object value) {
        if (!(value instanceof UnmodifiableConfig price)) {
            warn("level {} of {} must be { item = \"...\", count = ... }, using the default price", level, id);
            return Optional.empty();
        }
        Item item = item(price.get("item"));
        if (item == null) {
            warn("unknown item '{}' for level {} of {}, using the default price", price.get("item"), level, id);
            return Optional.empty();
        }
        Object count = price.get("count");
        if (!(count instanceof Integer || count instanceof Long) || ((Number) count).longValue() < 1 || ((Number) count).longValue() > MAX_COUNT) {
            warn("count '{}' for level {} of {} must be a whole number from 1 to {}, using the default price", count, level, id, MAX_COUNT);
            return Optional.empty();
        }
        int amount = ((Number) count).intValue();
        if (amount > item.getDefaultMaxStackSize()) {
            warn("count {} for level {} of {} is above the max stack size of {}, using {}",
                    amount, level, id, BuiltInRegistries.ITEM.getKey(item), item.getDefaultMaxStackSize());
            amount = item.getDefaultMaxStackSize();
        }
        return Optional.of(new Price(item, amount));
    }

    @Nullable
    private static Item item(@Nullable Object value) {
        ResourceLocation id = value instanceof String string ? ResourceLocation.tryParse(string) : null;
        return id == null ? null : BuiltInRegistries.ITEM.getOptional(id).filter(item -> item != Items.AIR).orElse(null);
    }

    private Price multiply(Price price, double multiplier) {
        int limit = Math.min(MAX_COUNT, price.item().getDefaultMaxStackSize());
        int count = Mth.ceil(price.count() * multiplier);
        if (count > limit) {
            cappedPrices++;
            count = limit;
        }
        return new Price(price.item(), Math.max(1, count));
    }

    private void warn(String message, Object... args) {
        VillagerTradeEnchant.LOGGER.warn("{}: {}", file, MessageFormatter.arrayFormat(message, args).getMessage());
    }
}
