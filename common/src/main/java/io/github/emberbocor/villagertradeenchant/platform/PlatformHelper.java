package io.github.emberbocor.villagertradeenchant.platform;

import java.nio.file.Path;

import net.minecraft.world.entity.npc.VillagerProfession;

public interface PlatformHelper {
    Path getConfigDirectory();

    VillagerProfession enchanterProfession();
}
