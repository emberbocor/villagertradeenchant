package io.github.emberbocor.villagertradeenchant;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;

@Mod(VillagerTradeEnchant.MODID)
public class VillagerTradeEnchant {
    public static final String MODID = "villagertradeenchant";
    public static final Logger LOGGER = LogUtils.getLogger();

    public VillagerTradeEnchant(IEventBus modEventBus, ModContainer modContainer) {
    }
}
