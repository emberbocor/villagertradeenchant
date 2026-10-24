package io.github.emberbocor.villagertradeenchant;

import io.github.emberbocor.villagertradeenchant.config.ServerConfig;
import io.github.emberbocor.villagertradeenchant.network.EnchantEntriesPayload;
import io.github.emberbocor.villagertradeenchant.network.SelectEnchantPayload;
import io.github.emberbocor.villagertradeenchant.network.ServerPayloadHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class VillagerTradeEnchantFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        FabricContent.register();
        PayloadTypeRegistry.clientboundPlay().register(EnchantEntriesPayload.TYPE, EnchantEntriesPayload.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(SelectEnchantPayload.TYPE, SelectEnchantPayload.STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(SelectEnchantPayload.TYPE,
                (payload, context) -> ServerPayloadHandler.handleSelection(context.player(), payload));
        ServerLifecycleEvents.SERVER_STARTING.register(server -> ServerConfig.load(server.registryAccess()));
    }
}
