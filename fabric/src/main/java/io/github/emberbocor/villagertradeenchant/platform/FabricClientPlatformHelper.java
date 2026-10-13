package io.github.emberbocor.villagertradeenchant.platform;

import io.github.emberbocor.villagertradeenchant.network.SelectEnchantPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.FriendlyByteBuf;

public final class FabricClientPlatformHelper implements ClientPlatformHelper {
    @Override
    public void sendSelection(SelectEnchantPayload payload) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        payload.write(buf);
        ClientPlayNetworking.send(SelectEnchantPayload.ID, buf);
    }
}
