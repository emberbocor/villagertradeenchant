package io.github.emberbocor.villagertradeenchant.platform;

import io.github.emberbocor.villagertradeenchant.network.SelectEnchantPayload;

public interface ClientPlatformHelper {
    void sendSelection(SelectEnchantPayload payload);
}
