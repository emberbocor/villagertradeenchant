package io.github.emberbocor.villagertradeenchant.villager;

import com.google.common.collect.ImmutableSet;

import io.github.emberbocor.villagertradeenchant.VillagerTradeEnchant;
import io.github.emberbocor.villagertradeenchant.platform.Services;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.level.block.Blocks;

public final class EnchanterProfession {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(VillagerTradeEnchant.MODID, "enchanter");
    public static final ResourceKey<PoiType> POI = ResourceKey.create(Registries.POINT_OF_INTEREST_TYPE, ID);

    private EnchanterProfession() {
    }

    public static PoiType createPoiType() {
        return new PoiType(ImmutableSet.copyOf(Blocks.ENCHANTING_TABLE.getStateDefinition().getPossibleStates()), 1, 1);
    }

    public static VillagerProfession createProfession() {
        return new VillagerProfession(ID.toString(), poi -> poi.is(POI), poi -> poi.is(POI),
                ImmutableSet.of(), ImmutableSet.of(), SoundEvents.ENCHANTMENT_TABLE_USE);
    }

    public static boolean is(VillagerData data) {
        return data.getProfession() == Services.PLATFORM.enchanterProfession();
    }
}
