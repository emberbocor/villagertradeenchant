package io.github.emberbocor.villagertradeenchant;

import io.github.emberbocor.villagertradeenchant.menu.EnchantMenu;
import io.github.emberbocor.villagertradeenchant.villager.EnchanterProfession;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class ForgeContent {
    private static final DeferredRegister<PoiType> POI_TYPES = DeferredRegister.create(Registries.POINT_OF_INTEREST_TYPE, VillagerTradeEnchant.MODID);
    private static final DeferredRegister<VillagerProfession> PROFESSIONS = DeferredRegister.create(Registries.VILLAGER_PROFESSION, VillagerTradeEnchant.MODID);
    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, VillagerTradeEnchant.MODID);

    static final RegistryObject<PoiType> ENCHANTER_POI =
            POI_TYPES.register(EnchanterProfession.ID.getPath(), EnchanterProfession::createPoiType);
    public static final RegistryObject<VillagerProfession> ENCHANTER =
            PROFESSIONS.register(EnchanterProfession.ID.getPath(), EnchanterProfession::createProfession);
    public static final RegistryObject<MenuType<EnchantMenu>> ENCHANT_MENU =
            MENUS.register("enchant", () -> new MenuType<>(EnchantMenu::new, FeatureFlags.VANILLA_SET));

    private ForgeContent() {
    }

    static void register(IEventBus modEventBus) {
        POI_TYPES.register(modEventBus);
        PROFESSIONS.register(modEventBus);
        MENUS.register(modEventBus);
    }
}
