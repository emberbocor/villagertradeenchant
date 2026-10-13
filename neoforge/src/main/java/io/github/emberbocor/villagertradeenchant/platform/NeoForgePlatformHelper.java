package io.github.emberbocor.villagertradeenchant.platform;

import io.github.emberbocor.villagertradeenchant.NeoForgeContent;
import net.minecraft.world.entity.npc.VillagerProfession;

public final class NeoForgePlatformHelper implements PlatformHelper {
    @Override
    public VillagerProfession enchanterProfession() {
        return NeoForgeContent.ENCHANTER.get();
    }
}
