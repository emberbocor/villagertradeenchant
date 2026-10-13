package io.github.emberbocor.villagertradeenchant.enchant;

import io.github.emberbocor.villagertradeenchant.config.Price;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public record EnchantEntry(Holder<Enchantment> enchantment, int level, Price price) {
    public boolean isSameOption(EnchantEntry other) {
        return enchantment.equals(other.enchantment) && level == other.level;
    }

    public boolean isPaidBy(ItemStack payment) {
        return payment.is(price.item()) && payment.getCount() >= price.count();
    }
}
