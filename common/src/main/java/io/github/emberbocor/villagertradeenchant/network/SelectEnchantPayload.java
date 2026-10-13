package io.github.emberbocor.villagertradeenchant.network;

import io.github.emberbocor.villagertradeenchant.VillagerTradeEnchant;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record SelectEnchantPayload(int containerId, ResourceLocation enchantment, int level) {
    public static final ResourceLocation ID = new ResourceLocation(VillagerTradeEnchant.MODID, "select_enchant");

    public static SelectEnchantPayload read(FriendlyByteBuf buf) {
        return new SelectEnchantPayload(buf.readVarInt(), buf.readResourceLocation(), buf.readVarInt());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(containerId);
        buf.writeResourceLocation(enchantment);
        buf.writeVarInt(level);
    }
}
