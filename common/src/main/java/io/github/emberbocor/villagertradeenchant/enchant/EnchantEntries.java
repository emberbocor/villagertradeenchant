package io.github.emberbocor.villagertradeenchant.enchant;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.github.emberbocor.villagertradeenchant.config.Price;
import io.github.emberbocor.villagertradeenchant.config.ServerConfig;
import io.github.emberbocor.villagertradeenchant.platform.Services;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;

public final class EnchantEntries {
    private EnchantEntries() {
    }

    public static List<EnchantEntry> forItem(ItemStack stack) {
        if (stack.isEmpty()) {
            return List.of();
        }
        boolean book = isBook(stack);
        if (book && !ServerConfig.allowBooks()) {
            return List.of();
        }

        Map<Enchantment, Integer> current = EnchantmentHelper.getEnchantments(stack);
        List<EnchantEntry> entries = new ArrayList<>();
        for (Enchantment enchantment : BuiltInRegistries.ENCHANTMENT) {
            if (!ServerConfig.isEnabled(enchantment) || !book && !Services.PLATFORM.canApply(stack, enchantment)) {
                continue;
            }
            int currentLevel = current.getOrDefault(enchantment, 0);
            if (currentLevel == 0 && !ServerConfig.allowConflicts() && conflicts(enchantment, current)) {
                continue;
            }
            for (int level = Math.max(enchantment.getMinLevel(), currentLevel + 1); level <= enchantment.getMaxLevel(); level++) {
                Price price = ServerConfig.price(enchantment, level);
                if (price != null) {
                    entries.add(new EnchantEntry(enchantment, level, price));
                }
            }
        }
        return List.copyOf(entries);
    }

    public static ItemStack apply(ItemStack stack, Enchantment enchantment, int level) {
        ItemStack result;
        if (stack.is(Items.BOOK)) {
            result = new ItemStack(Items.ENCHANTED_BOOK);
            CompoundTag tag = stack.getTag();
            if (tag != null) {
                result.setTag(tag.copy());
            }
        } else {
            result = stack.copyWithCount(1);
        }
        if (result.is(Items.ENCHANTED_BOOK)) {
            EnchantedBookItem.addEnchantment(result, new EnchantmentInstance(enchantment, level));
        } else {
            Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(result);
            enchantments.put(enchantment, level);
            EnchantmentHelper.setEnchantments(enchantments, result);
        }
        return result;
    }

    private static boolean isBook(ItemStack stack) {
        return stack.is(Items.BOOK) || stack.is(Items.ENCHANTED_BOOK);
    }

    private static boolean conflicts(Enchantment enchantment, Map<Enchantment, Integer> current) {
        for (Enchantment other : current.keySet()) {
            if (!enchantment.isCompatibleWith(other)) {
                return true;
            }
        }
        return false;
    }
}
