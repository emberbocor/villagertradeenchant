package io.github.emberbocor.villagertradeenchant.network;

import io.github.emberbocor.villagertradeenchant.VillagerTradeEnchant;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;

public record SelectEnchantPayload(int containerId, ResourceKey<Enchantment> enchantment, int level) implements CustomPacketPayload {
    public static final Type<SelectEnchantPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(VillagerTradeEnchant.MODID, "select_enchant"));
    public static final StreamCodec<ByteBuf, SelectEnchantPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SelectEnchantPayload::containerId,
            ResourceKey.streamCodec(Registries.ENCHANTMENT), SelectEnchantPayload::enchantment,
            ByteBufCodecs.VAR_INT, SelectEnchantPayload::level,
            SelectEnchantPayload::new);

    @Override
    public Type<SelectEnchantPayload> type() {
        return TYPE;
    }
}
