package io.github.emberbocor.villagertradeenchant.platform;

import io.github.emberbocor.villagertradeenchant.network.ForgeNetwork;
import io.github.emberbocor.villagertradeenchant.network.SelectEnchantPayload;

public final class ForgeClientPlatformHelper implements ClientPlatformHelper {
    @Override
    public void sendSelection(SelectEnchantPayload payload) {
        ForgeNetwork.sendToServer(payload);
    }
}
