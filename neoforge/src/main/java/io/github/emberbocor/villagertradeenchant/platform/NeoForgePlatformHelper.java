package io.github.emberbocor.villagertradeenchant.platform;

import java.nio.file.Path;

import io.github.emberbocor.villagertradeenchant.NeoForgeContent;
import io.github.emberbocor.villagertradeenchant.menu.EnchantMenu;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.fml.loading.FMLPaths;

public final class NeoForgePlatformHelper implements PlatformHelper {
    @Override
    public Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public VillagerProfession enchanterProfession() {
        return NeoForgeContent.ENCHANTER.get();
    }

    @Override
    public MenuType<EnchantMenu> enchantMenuType() {
        return NeoForgeContent.ENCHANT_MENU.get();
    }

    @Override
    public boolean canApply(ItemStack stack, Holder<Enchantment> enchantment) {
        return stack.supportsEnchantment(enchantment);
    }
}
