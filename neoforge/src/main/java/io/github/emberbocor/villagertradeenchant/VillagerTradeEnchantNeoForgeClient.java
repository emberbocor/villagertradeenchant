package io.github.emberbocor.villagertradeenchant;

import io.github.emberbocor.villagertradeenchant.client.EnchantScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@Mod(value = VillagerTradeEnchant.MODID, dist = Dist.CLIENT)
public class VillagerTradeEnchantNeoForgeClient {
    public VillagerTradeEnchantNeoForgeClient(IEventBus modEventBus) {
        modEventBus.addListener((RegisterMenuScreensEvent event) -> event.register(NeoForgeContent.ENCHANT_MENU.get(), EnchantScreen::new));
    }
}
