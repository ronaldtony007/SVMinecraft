package com.silvermage.silversvillagers.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.item.ItemStack;

import com.silvermage.silversvillagers.recipe.RecipeProgression;

@Mixin(AbstractFurnaceMenu.class)
public abstract class FurnaceQuickMoveGateMixin {
	@Inject(method = "quickMoveStack", at = @At("HEAD"), cancellable = true)
	private void startermod$gateShiftClickIntoFurnace(Player player, int slotId,
			CallbackInfoReturnable<ItemStack> cir) {
		if (slotId > 2 && player instanceof ServerPlayer serverPlayer) {
			ItemStack stack = ((AbstractFurnaceMenu) (Object) this).getSlot(slotId).getItem();
			if (!stack.isEmpty() && !RecipeProgression.isCookingInputUnlocked(serverPlayer, stack)) {
				cir.setReturnValue(ItemStack.EMPTY);
			}
		}
	}
}
