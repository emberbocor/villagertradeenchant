package io.github.emberbocor.villagertradeenchant.network;

import io.github.emberbocor.villagertradeenchant.menu.EnchantMenu;
import net.minecraft.server.level.ServerPlayer;

public final class ServerPayloadHandler {
    private ServerPayloadHandler() {
    }

    public static void handleSelection(ServerPlayer player, SelectEnchantPayload payload) {
        if (player.containerMenu instanceof EnchantMenu menu && menu.containerId == payload.containerId() && menu.stillValid(player)) {
            menu.select(payload.enchantment(), payload.level());
        }
    }
}
