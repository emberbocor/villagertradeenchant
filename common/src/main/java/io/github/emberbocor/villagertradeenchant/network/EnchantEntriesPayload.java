package io.github.emberbocor.villagertradeenchant.network;

import java.util.List;

import io.github.emberbocor.villagertradeenchant.VillagerTradeEnchant;
import io.github.emberbocor.villagertradeenchant.config.Price;
import io.github.emberbocor.villagertradeenchant.enchant.EnchantEntry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.enchantment.Enchantment;

public record EnchantEntriesPayload(int containerId, List<EnchantEntry> entries, int selection) implements CustomPacketPayload {
    public static final Type<EnchantEntriesPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(VillagerTradeEnchant.MODID, "enchant_entries"));
    private static final StreamCodec<RegistryFriendlyByteBuf, Price> PRICE_CODEC = StreamCodec.composite(
            ByteBufCodecs.registry(Registries.ITEM), Price::item,
            ByteBufCodecs.VAR_INT, Price::count,
            Price::new);
    private static final StreamCodec<RegistryFriendlyByteBuf, EnchantEntry> ENTRY_CODEC = StreamCodec.composite(
            Enchantment.STREAM_CODEC, EnchantEntry::enchantment,
            ByteBufCodecs.VAR_INT, EnchantEntry::level,
            PRICE_CODEC, EnchantEntry::price,
            EnchantEntry::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, EnchantEntriesPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, EnchantEntriesPayload::containerId,
            ENTRY_CODEC.apply(ByteBufCodecs.list()), EnchantEntriesPayload::entries,
            ByteBufCodecs.VAR_INT, EnchantEntriesPayload::selection,
            EnchantEntriesPayload::new);

    @Override
    public Type<EnchantEntriesPayload> type() {
        return TYPE;
    }
}
