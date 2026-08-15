package com.example.startermod.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.ContainerInput;

import com.example.startermod.recipe.RecipeProgression;

@Mixin(AbstractContainerMenu.class)
public abstract class FurnaceInputGateMixin {
	@Inject(method = "clicked", at = @At("HEAD"), cancellable = true)
	private void startermod$gateFurnaceInput(int slotId, int button, ContainerInput clickType, Player player,
			CallbackInfo info) {
		if (slotId == 0 && (Object) this instanceof AbstractFurnaceMenu furnaceMenu
				&& player instanceof ServerPlayer serverPlayer
				&& !furnaceMenu.getCarried().isEmpty()
				&& !RecipeProgression.isCookingInputUnlocked(serverPlayer, furnaceMenu.getCarried())) {
			info.cancel();
		}
	}
}
