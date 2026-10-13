package io.github.emberbocor.villagertradeenchant.enchant;

import java.util.ArrayList;
import java.util.List;

import io.github.emberbocor.villagertradeenchant.config.Price;
import io.github.emberbocor.villagertradeenchant.config.ServerConfig;
import io.github.emberbocor.villagertradeenchant.platform.Services;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public final class EnchantEntries {
    private EnchantEntries() {
    }

    public static List<EnchantEntry> forItem(ItemStack stack, RegistryAccess registries) {
        if (stack.isEmpty()) {
            return List.of();
        }
        boolean book = isBook(stack);
        if (book ? !ServerConfig.allowBooks() : !EnchantmentHelper.canStoreEnchantments(stack)) {
            return List.of();
        }

        ItemEnchantments current = EnchantmentHelper.getEnchantmentsForCrafting(stack);
        List<EnchantEntry> entries = new ArrayList<>();
        registries.registryOrThrow(Registries.ENCHANTMENT).holders().forEach(enchantment -> {
            if (!ServerConfig.isEnabled(enchantment) || !book && !Services.PLATFORM.canApply(stack, enchantment)) {
                return;
            }
            int currentLevel = current.getLevel(enchantment);
            if (currentLevel == 0 && !ServerConfig.allowConflicts() && conflicts(enchantment, current)) {
                return;
            }
            Enchantment value = enchantment.value();
            for (int level = Math.max(value.getMinLevel(), currentLevel + 1); level <= value.getMaxLevel(); level++) {
                Price price = ServerConfig.price(enchantment, level);
                if (price != null) {
                    entries.add(new EnchantEntry(enchantment, level, price));
                }
            }
        });
        return List.copyOf(entries);
    }

    public static ItemStack apply(ItemStack stack, Holder<Enchantment> enchantment, int level) {
        ItemStack result = stack.is(Items.BOOK) ? stack.transmuteCopy(Items.ENCHANTED_BOOK, 1) : stack.copyWithCount(1);
        EnchantmentHelper.updateEnchantments(result, enchantments -> enchantments.set(enchantment, level));
        return result;
    }

    private static boolean isBook(ItemStack stack) {
        return stack.is(Items.BOOK) || stack.is(Items.ENCHANTED_BOOK);
    }

    private static boolean conflicts(Holder<Enchantment> enchantment, ItemEnchantments current) {
        for (Holder<Enchantment> other : current.keySet()) {
            if (!Enchantment.areCompatible(enchantment, other)) {
                return true;
            }
        }
        return false;
    }
}
