package io.github.emberbocor.villagertradeenchant.platform;

import java.nio.file.Path;

import io.github.emberbocor.villagertradeenchant.menu.EnchantMenu;
import io.github.emberbocor.villagertradeenchant.network.EnchantEntriesPayload;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public interface PlatformHelper {
    Path getConfigDirectory();

    VillagerProfession enchanterProfession();

    MenuType<EnchantMenu> enchantMenuType();

    boolean canApply(ItemStack stack, Holder<Enchantment> enchantment);

    void sendEntries(ServerPlayer player, EnchantEntriesPayload payload);
}
