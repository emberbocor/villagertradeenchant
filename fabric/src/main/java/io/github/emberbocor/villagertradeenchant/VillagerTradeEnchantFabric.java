package io.github.emberbocor.villagertradeenchant;

import io.github.emberbocor.villagertradeenchant.config.ServerConfig;
import io.github.emberbocor.villagertradeenchant.network.SelectEnchantPayload;
import io.github.emberbocor.villagertradeenchant.network.ServerPayloadHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class VillagerTradeEnchantFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        FabricContent.register();
        ServerPlayNetworking.registerGlobalReceiver(SelectEnchantPayload.ID, (server, player, handler, buf, responseSender) -> {
            SelectEnchantPayload payload = SelectEnchantPayload.read(buf);
            server.execute(() -> ServerPayloadHandler.handleSelection(player, payload));
        });
        ServerLifecycleEvents.SERVER_STARTING.register(server -> ServerConfig.load());
    }
}
