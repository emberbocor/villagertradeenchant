package io.github.emberbocor.villagertradeenchant.platform;

import io.github.emberbocor.villagertradeenchant.network.SelectEnchantPayload;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

public final class NeoForgeClientPlatformHelper implements ClientPlatformHelper {
    @Override
    public void sendSelection(SelectEnchantPayload payload) {
        ClientPacketDistributor.sendToServer(payload);
    }
}
