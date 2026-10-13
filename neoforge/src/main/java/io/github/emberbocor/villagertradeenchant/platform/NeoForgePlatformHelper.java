package io.github.emberbocor.villagertradeenchant.platform;

import java.nio.file.Path;

import io.github.emberbocor.villagertradeenchant.NeoForgeContent;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.neoforged.fml.loading.FMLPaths;

public final class NeoForgePlatformHelper implements PlatformHelper {
    @Override
    public Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public VillagerProfession enchanterProfession() {
        return NeoForgeContent.ENCHANTER.get();
    }
}
