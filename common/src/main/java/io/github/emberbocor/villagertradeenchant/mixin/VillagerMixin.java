package io.github.emberbocor.villagertradeenchant.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.emberbocor.villagertradeenchant.menu.EnchantMenu;
import io.github.emberbocor.villagertradeenchant.villager.EnchanterProfession;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;

@Mixin(Villager.class)
public abstract class VillagerMixin {
    @Inject(method = "updateTrades", at = @At("HEAD"), cancellable = true)
    private void villagertradeenchant$skipEnchanterTrades(CallbackInfo ci) {
        if (EnchanterProfession.is(((Villager) (Object) this).getVillagerData())) {
            ci.cancel();
        }
    }

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void villagertradeenchant$openEnchantScreen(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        Villager villager = (Villager) (Object) this;
        if (!EnchanterProfession.is(villager.getVillagerData())
                || player.getItemInHand(hand).is(Items.VILLAGER_SPAWN_EGG)
                || !villager.isAlive()
                || villager.isTrading()
                || villager.isSleeping()
                || villager.isBaby()
                || player.isSecondaryUseActive()) {
            return;
        }
        if (!villager.level().isClientSide()) {
            if (hand == InteractionHand.MAIN_HAND) {
                player.awardStat(Stats.TALKED_TO_VILLAGER);
            }
            EnchantMenu.open(player, villager);
        }
        cir.setReturnValue(InteractionResult.SUCCESS);
    }
}
