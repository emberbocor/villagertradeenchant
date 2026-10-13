package io.github.emberbocor.villagertradeenchant.platform;

import java.nio.file.Path;

import io.github.emberbocor.villagertradeenchant.FabricContent;
import io.github.emberbocor.villagertradeenchant.menu.EnchantMenu;
import io.github.emberbocor.villagertradeenchant.network.EnchantEntriesPayload;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public final class FabricPlatformHelper implements PlatformHelper {
    @Override
    public Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public VillagerProfession enchanterProfession() {
        return FabricContent.ENCHANTER;
    }

    @Override
    public MenuType<EnchantMenu> enchantMenuType() {
        return FabricContent.ENCHANT_MENU;
    }

    @Override
    public boolean canApply(ItemStack stack, Enchantment enchantment) {
        return enchantment.canEnchant(stack);
    }

    @Override
    public void sendEntries(ServerPlayer player, EnchantEntriesPayload payload) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        payload.write(buf);
        ServerPlayNetworking.send(player, EnchantEntriesPayload.ID, buf);
    }
}
