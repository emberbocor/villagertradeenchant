package io.github.emberbocor.villagertradeenchant.network;

import java.util.List;

import io.github.emberbocor.villagertradeenchant.VillagerTradeEnchant;
import io.github.emberbocor.villagertradeenchant.config.Price;
import io.github.emberbocor.villagertradeenchant.enchant.EnchantEntry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record EnchantEntriesPayload(int containerId, List<EnchantEntry> entries, int selection) {
    public static final ResourceLocation ID = new ResourceLocation(VillagerTradeEnchant.MODID, "enchant_entries");

    public static EnchantEntriesPayload read(FriendlyByteBuf buf) {
        return new EnchantEntriesPayload(buf.readVarInt(), buf.readList(EnchantEntriesPayload::readEntry), buf.readVarInt());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(containerId);
        buf.writeCollection(entries, EnchantEntriesPayload::writeEntry);
        buf.writeVarInt(selection);
    }

    private static EnchantEntry readEntry(FriendlyByteBuf buf) {
        return new EnchantEntry(buf.readById(BuiltInRegistries.ENCHANTMENT), buf.readVarInt(),
                new Price(buf.readById(BuiltInRegistries.ITEM), buf.readVarInt()));
    }

    private static void writeEntry(FriendlyByteBuf buf, EnchantEntry entry) {
        buf.writeId(BuiltInRegistries.ENCHANTMENT, entry.enchantment());
        buf.writeVarInt(entry.level());
        buf.writeId(BuiltInRegistries.ITEM, entry.price().item());
        buf.writeVarInt(entry.price().count());
    }
}
