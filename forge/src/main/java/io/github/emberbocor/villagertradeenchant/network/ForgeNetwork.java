package io.github.emberbocor.villagertradeenchant.network;

import io.github.emberbocor.villagertradeenchant.VillagerTradeEnchant;
import io.github.emberbocor.villagertradeenchant.client.ClientPayloadHandler;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.Channel;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.PacketDistributor;

public final class ForgeNetwork {
    private static final int PROTOCOL_VERSION = 1;

    private static Channel<CustomPacketPayload> channel;

    private ForgeNetwork() {
    }

    public static void register() {
        channel = ChannelBuilder.named(Identifier.fromNamespaceAndPath(VillagerTradeEnchant.MODID, "main"))
                .networkProtocolVersion(PROTOCOL_VERSION)
                .payloadChannel()
                .play()
                .clientbound()
                .addMain(EnchantEntriesPayload.TYPE, EnchantEntriesPayload.STREAM_CODEC,
                        (payload, context) -> ClientPayloadHandler.handleEntries(payload))
                .serverbound()
                .addMain(SelectEnchantPayload.TYPE, SelectEnchantPayload.STREAM_CODEC.cast(), (payload, context) -> {
                    ServerPlayer player = context.getSender();
                    if (player != null) {
                        ServerPayloadHandler.handleSelection(player, payload);
                    }
                })
                .build();
    }

    public static void sendToPlayer(ServerPlayer player, CustomPacketPayload payload) {
        channel.send(payload, PacketDistributor.PLAYER.with(player));
    }

    public static void sendToServer(CustomPacketPayload payload) {
        channel.send(payload, PacketDistributor.SERVER.noArg());
    }
}
