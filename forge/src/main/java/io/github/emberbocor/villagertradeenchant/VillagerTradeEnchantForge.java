package io.github.emberbocor.villagertradeenchant;

import io.github.emberbocor.villagertradeenchant.config.ServerConfig;
import io.github.emberbocor.villagertradeenchant.network.ForgeNetwork;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(VillagerTradeEnchant.MODID)
public class VillagerTradeEnchantForge {
    public VillagerTradeEnchantForge(FMLJavaModLoadingContext context) {
        BusGroup modBusGroup = context.getModBusGroup();
        ForgeContent.register(modBusGroup);
        ForgeNetwork.register();
        ServerAboutToStartEvent.BUS.addListener(event -> ServerConfig.load(event.getServer().registryAccess()));
        if (FMLEnvironment.dist == Dist.CLIENT) {
            VillagerTradeEnchantForgeClient.register(modBusGroup);
        }
    }
}
