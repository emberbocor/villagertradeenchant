package io.github.emberbocor.villagertradeenchant;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(VillagerTradeEnchant.MODID)
public class VillagerTradeEnchantNeoForge {
    public VillagerTradeEnchantNeoForge(IEventBus modEventBus) {
        NeoForgeContent.register(modEventBus);
    }
}
