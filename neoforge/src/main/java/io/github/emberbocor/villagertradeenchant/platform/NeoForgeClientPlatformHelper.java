package io.github.emberbocor.villagertradeenchant.platform;

import io.github.emberbocor.villagertradeenchant.network.SelectEnchantPayload;
import net.neoforged.neoforge.network.PacketDistributor;

public final class NeoForgeClientPlatformHelper implements ClientPlatformHelper {
    @Override
    public void sendSelection(SelectEnchantPayload payload) {
        PacketDistributor.sendToServer(payload);
    }
}
