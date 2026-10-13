package io.github.emberbocor.villagertradeenchant.platform;

import io.github.emberbocor.villagertradeenchant.network.SelectEnchantPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public final class FabricClientPlatformHelper implements ClientPlatformHelper {
    @Override
    public void sendSelection(SelectEnchantPayload payload) {
        ClientPlayNetworking.send(payload);
    }
}
