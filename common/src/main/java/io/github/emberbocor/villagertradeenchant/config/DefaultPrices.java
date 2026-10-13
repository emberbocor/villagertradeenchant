package io.github.emberbocor.villagertradeenchant.config;

import net.minecraft.util.Mth;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;

final class DefaultPrices {
    private static final int MAX_EMERALDS = 64;

    private DefaultPrices() {
    }

    static Price of(Enchantment enchantment, int level) {
        int emeralds = Mth.ceil(1.5 * (2 + 3 * level + (5 + 10 * level) / 2.0));
        if (enchantment.isTreasureOnly()) {
            emeralds *= 2;
        }
        if (emeralds <= MAX_EMERALDS) {
            return new Price(Items.EMERALD, emeralds);
        }
        return new Price(Items.EMERALD_BLOCK, Math.min(Mth.ceil(emeralds / 9.0), MAX_EMERALDS));
    }
}
