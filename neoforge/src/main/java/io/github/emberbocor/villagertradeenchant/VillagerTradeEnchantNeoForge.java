package io.github.emberbocor.villagertradeenchant;

import io.github.emberbocor.villagertradeenchant.config.ServerConfig;
import io.github.emberbocor.villagertradeenchant.network.NeoForgeNetwork;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;

@Mod(VillagerTradeEnchant.MODID)
public class VillagerTradeEnchantNeoForge {
    public VillagerTradeEnchantNeoForge(IEventBus modEventBus) {
        NeoForgeContent.register(modEventBus);
        modEventBus.addListener(NeoForgeNetwork::register);
        NeoForge.EVENT_BUS.addListener((ServerAboutToStartEvent event) -> ServerConfig.load(event.getServer().registryAccess()));
    }
}
