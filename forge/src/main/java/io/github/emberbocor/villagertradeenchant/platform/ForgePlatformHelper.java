package io.github.emberbocor.villagertradeenchant.platform;

import java.nio.file.Path;

import io.github.emberbocor.villagertradeenchant.ForgeContent;
import io.github.emberbocor.villagertradeenchant.menu.EnchantMenu;
import io.github.emberbocor.villagertradeenchant.network.EnchantEntriesPayload;
import io.github.emberbocor.villagertradeenchant.network.ForgeNetwork;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.fml.loading.FMLPaths;

public final class ForgePlatformHelper implements PlatformHelper {
    @Override
    public Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public VillagerProfession enchanterProfession() {
        return ForgeContent.ENCHANTER.get();
    }

    @Override
    public MenuType<EnchantMenu> enchantMenuType() {
        return ForgeContent.ENCHANT_MENU.get();
    }

    @Override
    public boolean canApply(ItemStack stack, Holder<Enchantment> enchantment) {
        return enchantment.value().canEnchant(stack);
    }

    @Override
    public void sendEntries(ServerPlayer player, EnchantEntriesPayload payload) {
        ForgeNetwork.sendToPlayer(player, payload);
    }
}
