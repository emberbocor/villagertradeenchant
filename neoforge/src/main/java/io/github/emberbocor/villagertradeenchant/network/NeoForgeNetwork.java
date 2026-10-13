package io.github.emberbocor.villagertradeenchant.network;

import io.github.emberbocor.villagertradeenchant.client.ClientPayloadHandler;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public final class NeoForgeNetwork {
    private static final String PROTOCOL_VERSION = "1";

    private NeoForgeNetwork() {
    }

    public static void register(RegisterPayloadHandlersEvent event) {
        event.registrar(PROTOCOL_VERSION)
                .playToClient(EnchantEntriesPayload.TYPE, EnchantEntriesPayload.STREAM_CODEC,
                        (payload, context) -> ClientPayloadHandler.handleEntries(payload))
                .playToServer(SelectEnchantPayload.TYPE, SelectEnchantPayload.STREAM_CODEC,
                        (payload, context) -> {
                            if (context.player() instanceof ServerPlayer player) {
                                ServerPayloadHandler.handleSelection(player, payload);
                            }
                        });
    }
}
