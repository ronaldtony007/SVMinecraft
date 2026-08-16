package com.silvermage.silversvillagers.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;

import net.minecraft.server.level.ServerPlayer;

import com.silvermage.silversvillagers.recipe.RecipeProgression;

@Mixin(CampfireBlockEntity.class)
public final class CampfireBlockEntityMixin {
	@Inject(method = "placeFood", at = @At("HEAD"), cancellable = true)
	private void silversvillagers$gateLockedFood(ServerLevel level, LivingEntity entity, ItemStack stack,
												 CallbackInfoReturnable<Boolean> cir) {
		if (entity instanceof ServerPlayer player && !RecipeProgression.isCampfireInputUnlocked(player, stack)) {
			cir.setReturnValue(false);
		}
	}
}
