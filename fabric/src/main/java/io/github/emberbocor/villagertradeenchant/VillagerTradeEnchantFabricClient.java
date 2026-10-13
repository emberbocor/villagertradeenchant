package io.github.emberbocor.villagertradeenchant;

import io.github.emberbocor.villagertradeenchant.client.ClientPayloadHandler;
import io.github.emberbocor.villagertradeenchant.client.EnchantScreen;
import io.github.emberbocor.villagertradeenchant.network.EnchantEntriesPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.screens.MenuScreens;

public class VillagerTradeEnchantFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        MenuScreens.register(FabricContent.ENCHANT_MENU, EnchantScreen::new);
        ClientPlayNetworking.registerGlobalReceiver(EnchantEntriesPayload.TYPE, (payload, context) -> ClientPayloadHandler.handleEntries(payload));
    }
}
