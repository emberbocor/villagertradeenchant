package io.github.emberbocor.villagertradeenchant.config;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

import io.github.emberbocor.villagertradeenchant.platform.Services;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.enchantment.Enchantment;

public final class ServerConfig {
    private static volatile Settings settings = new Settings(false, true, Map.of());

    private ServerConfig() {
    }

    public static void load() {
        List<Enchantment> enchantments = BuiltInRegistries.ENCHANTMENT.stream()
                .sorted(Comparator.comparing(ServerConfigFile::id))
                .toList();
        Path file = Services.PLATFORM.getConfigDirectory().resolve(ServerConfigFile.FILE_NAME);
        settings = new ServerConfigReader(file).read(ServerConfigFile.load(file, enchantments).orElse(null), enchantments);
    }

    public static boolean allowConflicts() {
        return settings.allowConflicts();
    }

    public static boolean allowBooks() {
        return settings.allowBooks();
    }

    public static boolean isEnabled(Enchantment enchantment) {
        EnchantmentPrices prices = settings.enchantments().get(enchantment);
        return prices != null && prices.enabled();
    }

    @Nullable
    public static Price price(Enchantment enchantment, int level) {
        EnchantmentPrices prices = settings.enchantments().get(enchantment);
        return prices == null ? null : prices.levels().get(level);
    }

    record Settings(boolean allowConflicts, boolean allowBooks, Map<Enchantment, EnchantmentPrices> enchantments) {
    }

    record EnchantmentPrices(boolean enabled, Int2ObjectMap<Price> levels) {
    }
}
