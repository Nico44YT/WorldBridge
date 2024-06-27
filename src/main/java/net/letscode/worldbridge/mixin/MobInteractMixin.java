package net.letscode.worldbridge.mixin;

import net.letscode.worldbridge.item.custom.SoulCrystal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(net.minecraft.entity.mob.MobEntity.class)
public class MobInteractMixin {

    @Inject(method = "interact", at = @At("HEAD"), cancellable = true)
    private void interact(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if(!player.getWorld().isClient) {
            MobEntity mob = (MobEntity)(Object)this;
            if(player.getStackInHand(hand).getItem() instanceof SoulCrystal soulCrystal) {
                soulCrystal.useOnEntity(player.getStackInHand(hand), player, mob, hand);
                cir.setReturnValue(ActionResult.SUCCESS);
            }
        }
    }

}