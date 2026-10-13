package io.github.emberbocor.villagertradeenchant.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.electronwill.nightconfig.toml.TomlFormat;

import io.github.emberbocor.villagertradeenchant.VillagerTradeEnchant;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.enchantment.Enchantment;

final class ServerConfigFile {
    static final String FILE_NAME = VillagerTradeEnchant.MODID + "-server.toml";

    private static final String HEADER = """
            # Villager Trade Enchant server config.
            # Created on the first server start with every enchantment in the game. Enchantments that appear later
            # (new mods or datapacks) are added at the end of this file; existing entries are never changed.
            # Changes take effect after a server restart.

            # Multiplies every price count below. The result is rounded up and capped at 64
            # (and at the max stack size of the price item).
            priceMultiplier = 1.0

            # true: enchantments that conflict with one already on the item (for example Sharpness and Smite)
            # are offered too.
            allowConflicts = false

            # true: plain books and enchanted books can be enchanted. A plain book becomes an enchanted book.
            allowBooks = true

            # Prices, one table per enchantment id.
            #   enabled = false hides the enchantment from the Enchanter.
            #   levels."<level>" = { item = "<item id>", count = <1-64> } is the price of one level.
            #   A missing level uses the default price. Upgrading an enchantment costs the full price of the new level.
            # Default price: ceil(1.5 * (2 + 3 * level + (5 + 10 * level) / 2)) emeralds, doubled for treasure
            # enchantments; above 64 it is paid in emerald blocks (count / 9, rounded up).
            """;

    private ServerConfigFile() {
    }

    static Optional<UnmodifiableConfig> load(Path file, List<? extends Holder.Reference<Enchantment>> enchantments) {
        if (!Files.exists(file)) {
            String text = HEADER + tables(enchantments, "\n");
            write(file, text);
            VillagerTradeEnchant.LOGGER.info("Created {} with {} enchantments", file, enchantments.size());
            return parse(file, text);
        }

        String text;
        try {
            text = Files.readString(file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            VillagerTradeEnchant.LOGGER.warn("Could not read {}, using default prices: {}", file, e.getMessage());
            return Optional.empty();
        }
        Optional<UnmodifiableConfig> config = parse(file, text);
        if (config.isEmpty()) {
            return config;
        }

        Object prices = config.get().get("prices");
        if (prices != null && !(prices instanceof UnmodifiableConfig)) {
            VillagerTradeEnchant.LOGGER.warn("'prices' in {} is not a table, new enchantments are not added", file);
            return config;
        }
        List<? extends Holder.Reference<Enchantment>> missing = enchantments.stream()
                .filter(enchantment -> prices == null || !((UnmodifiableConfig) prices).contains(List.of(id(enchantment))))
                .toList();
        if (missing.isEmpty()) {
            return config;
        }

        String newline = text.contains("\r\n") ? "\r\n" : "\n";
        String appended = text + (text.isEmpty() || text.endsWith("\n") ? "" : newline) + tables(missing, newline);
        Optional<UnmodifiableConfig> updated = parse(file, appended);
        if (updated.isEmpty()) {
            VillagerTradeEnchant.LOGGER.warn("New enchantments could not be added to {} without breaking it, the file was not changed", file);
            return config;
        }
        if (!write(file, appended)) {
            return config;
        }
        VillagerTradeEnchant.LOGGER.info("Added {} new enchantments to {}", missing.size(), file);
        return updated;
    }

    private static Optional<UnmodifiableConfig> parse(Path file, String text) {
        try {
            return Optional.of(TomlFormat.instance().createParser().parse(text));
        } catch (RuntimeException e) {
            VillagerTradeEnchant.LOGGER.warn("Could not parse {}, using default prices: {}", file, e.getMessage());
            return Optional.empty();
        }
    }

    private static boolean write(Path file, String text) {
        try {
            Files.createDirectories(file.getParent());
            Files.writeString(file, text, StandardCharsets.UTF_8);
            return true;
        } catch (IOException e) {
            VillagerTradeEnchant.LOGGER.warn("Could not write {}: {}", file, e.getMessage());
            return false;
        }
    }

    private static String tables(List<? extends Holder.Reference<Enchantment>> enchantments, String newline) {
        StringBuilder text = new StringBuilder();
        for (Holder.Reference<Enchantment> enchantment : enchantments) {
            text.append(newline).append("[prices.\"").append(id(enchantment)).append("\"]").append(newline);
            text.append("enabled = true").append(newline);
            Enchantment value = enchantment.value();
            for (int level = value.getMinLevel(); level <= value.getMaxLevel(); level++) {
                Price price = DefaultPrices.of(enchantment, level);
                text.append("levels.\"").append(level).append("\" = { item = \"")
                        .append(BuiltInRegistries.ITEM.getKey(price.item()))
                        .append("\", count = ").append(price.count()).append(" }").append(newline);
            }
        }
        return text.toString();
    }

    static String id(Holder.Reference<Enchantment> enchantment) {
        return enchantment.key().location().toString();
    }
}
