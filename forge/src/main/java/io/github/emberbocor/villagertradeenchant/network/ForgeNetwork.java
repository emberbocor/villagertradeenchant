package io.github.emberbocor.villagertradeenchant.network;

import io.github.emberbocor.villagertradeenchant.VillagerTradeEnchant;
import io.github.emberbocor.villagertradeenchant.client.ClientPayloadHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public final class ForgeNetwork {
    private static final String PROTOCOL_VERSION = "1";

    private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(VillagerTradeEnchant.MODID, "main"),
            () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

    private ForgeNetwork() {
    }

    public static void register() {
        CHANNEL.messageBuilder(EnchantEntriesPayload.class, 0, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(EnchantEntriesPayload::write)
                .decoder(EnchantEntriesPayload::read)
                .consumerNetworkThread((payload, contextSupplier) -> {
                    NetworkEvent.Context context = contextSupplier.get();
                    context.enqueueWork(() -> ClientPayloadHandler.handleEntries(payload));
                    context.setPacketHandled(true);
                })
                .add();
        CHANNEL.messageBuilder(SelectEnchantPayload.class, 1, NetworkDirection.PLAY_TO_SERVER)
                .encoder(SelectEnchantPayload::write)
                .decoder(SelectEnchantPayload::read)
                .consumerNetworkThread((payload, contextSupplier) -> {
                    NetworkEvent.Context context = contextSupplier.get();
                    context.enqueueWork(() -> {
                        ServerPlayer player = context.getSender();
                        if (player != null) {
                            ServerPayloadHandler.handleSelection(player, payload);
                        }
                    });
                    context.setPacketHandled(true);
                })
                .add();
    }

    public static void sendToPlayer(ServerPlayer player, EnchantEntriesPayload payload) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), payload);
    }

    public static void sendToServer(SelectEnchantPayload payload) {
        CHANNEL.sendToServer(payload);
    }
}
