package io.github.emberbocor.villagertradeenchant.client;

import io.github.emberbocor.villagertradeenchant.menu.EnchantMenu;
import io.github.emberbocor.villagertradeenchant.network.EnchantEntriesPayload;
import net.minecraft.client.Minecraft;

public final class ClientPayloadHandler {
    private ClientPayloadHandler() {
    }

    public static void handleEntries(EnchantEntriesPayload payload) {
        if (Minecraft.getInstance().player != null
                && Minecraft.getInstance().player.containerMenu instanceof EnchantMenu menu
                && menu.containerId == payload.containerId()) {
            menu.setEntries(payload.entries(), payload.selection());
        }
    }
}
