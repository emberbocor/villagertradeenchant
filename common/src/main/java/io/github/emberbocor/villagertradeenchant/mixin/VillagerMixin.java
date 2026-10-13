package io.github.emberbocor.villagertradeenchant.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.emberbocor.villagertradeenchant.villager.EnchanterProfession;
import net.minecraft.world.entity.npc.Villager;

@Mixin(Villager.class)
public abstract class VillagerMixin {
    @Inject(method = "updateTrades", at = @At("HEAD"), cancellable = true)
    private void villagertradeenchant$skipEnchanterTrades(CallbackInfo ci) {
        if (EnchanterProfession.is(((Villager) (Object) this).getVillagerData())) {
            ci.cancel();
        }
    }
}
