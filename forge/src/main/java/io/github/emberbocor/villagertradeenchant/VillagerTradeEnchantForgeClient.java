package io.github.emberbocor.villagertradeenchant;

import io.github.emberbocor.villagertradeenchant.client.EnchantScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

final class VillagerTradeEnchantForgeClient {
    private VillagerTradeEnchantForgeClient() {
    }

    static void register(BusGroup modBusGroup) {
        FMLClientSetupEvent.getBus(modBusGroup).addListener(event ->
                event.enqueueWork(() -> MenuScreens.register(ForgeContent.ENCHANT_MENU.get(), EnchantScreen::new)));
    }
}
