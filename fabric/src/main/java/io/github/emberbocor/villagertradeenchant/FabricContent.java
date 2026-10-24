package io.github.emberbocor.villagertradeenchant;

import io.github.emberbocor.villagertradeenchant.menu.EnchantMenu;
import io.github.emberbocor.villagertradeenchant.villager.EnchanterProfession;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;

public final class FabricContent {
    public static final VillagerProfession ENCHANTER = EnchanterProfession.createProfession();
    public static final MenuType<EnchantMenu> ENCHANT_MENU = new MenuType<>(EnchantMenu::new, FeatureFlags.VANILLA_SET);

    private FabricContent() {
    }

    static void register() {
        PointOfInterestHelper.register(EnchanterProfession.ID, EnchanterProfession.MAX_TICKETS, EnchanterProfession.VALID_RANGE,
                EnchanterProfession.jobSiteStates());
        Registry.register(BuiltInRegistries.VILLAGER_PROFESSION, EnchanterProfession.ID, ENCHANTER);
        Registry.register(BuiltInRegistries.MENU, Identifier.fromNamespaceAndPath(VillagerTradeEnchant.MODID, "enchant"), ENCHANT_MENU);
    }
}
