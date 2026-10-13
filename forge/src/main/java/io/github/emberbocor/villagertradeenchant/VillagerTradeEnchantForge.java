package io.github.emberbocor.villagertradeenchant;

import io.github.emberbocor.villagertradeenchant.config.ServerConfig;
import io.github.emberbocor.villagertradeenchant.network.ForgeNetwork;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(VillagerTradeEnchant.MODID)
public class VillagerTradeEnchantForge {
    @SuppressWarnings("removal")
    public VillagerTradeEnchantForge() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ForgeContent.register(modEventBus);
        ForgeNetwork.register();
        MinecraftForge.EVENT_BUS.addListener((ServerAboutToStartEvent event) -> ServerConfig.load(event.getServer().registryAccess()));
        if (FMLEnvironment.dist == Dist.CLIENT) {
            VillagerTradeEnchantForgeClient.register(modEventBus);
        }
    }
}
