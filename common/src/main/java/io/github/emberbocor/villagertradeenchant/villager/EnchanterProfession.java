package io.github.emberbocor.villagertradeenchant.villager;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import io.github.emberbocor.villagertradeenchant.VillagerTradeEnchant;
import io.github.emberbocor.villagertradeenchant.platform.Services;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.villager.VillagerData;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public final class EnchanterProfession {
    public static final Identifier ID = Identifier.fromNamespaceAndPath(VillagerTradeEnchant.MODID, "enchanter");
    public static final ResourceKey<PoiType> POI = ResourceKey.create(Registries.POINT_OF_INTEREST_TYPE, ID);
    public static final int MAX_TICKETS = 1;
    public static final int VALID_RANGE = 1;

    private EnchanterProfession() {
    }

    public static Set<BlockState> jobSiteStates() {
        return ImmutableSet.copyOf(Blocks.ENCHANTING_TABLE.getStateDefinition().getPossibleStates());
    }

    public static PoiType createPoiType() {
        return new PoiType(jobSiteStates(), MAX_TICKETS, VALID_RANGE);
    }

    public static VillagerProfession createProfession() {
        return new VillagerProfession(Component.translatable("entity." + ID.getNamespace() + ".villager." + ID.getPath()),
                poi -> poi.is(POI), poi -> poi.is(POI), ImmutableSet.of(), ImmutableSet.of(), SoundEvents.ENCHANTMENT_TABLE_USE);
    }

    public static boolean is(VillagerData data) {
        return data.profession().value() == Services.PLATFORM.enchanterProfession();
    }
}
