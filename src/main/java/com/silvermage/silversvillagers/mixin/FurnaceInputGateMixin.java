package com.silvermage.silversvillagers.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;

import com.silvermage.silversvillagers.recipe.RecipeProgression;

@Mixin(AbstractFurnaceMenu.class)
public abstract class FurnaceInputGateMixin {
	@Inject(method = "quickMoveStack", at = @At("HEAD"), cancellable = true)
	private void silversvillagers$gateQuickMoveIntoFurnace(Player player, int slotId,
			CallbackInfoReturnable<ItemStack> cir) {
		if (slotId > AbstractFurnaceMenu.RESULT_SLOT && player instanceof ServerPlayer serverPlayer) {
			ItemStack stack = ((AbstractFurnaceMenu) (Object) this).getSlot(slotId).getItem();
			if (isBlockedCookingInput(serverPlayer, stack)) {
				cir.setReturnValue(ItemStack.EMPTY);
			}
		}
	}

	@Inject(method = "handlePlacement", at = @At("HEAD"), cancellable = true)
	private void silversvillagers$gateRecipeBookPlacement(boolean craftAll, boolean placeAll,
			RecipeHolder<?> recipe, ServerLevel level, Inventory inventory,
			CallbackInfoReturnable<RecipeBookMenu.PostPlaceAction> cir) {
		if (!(recipe.value() instanceof AbstractCookingRecipe cookingRecipe)
				|| !(inventory.player instanceof ServerPlayer serverPlayer)) {
			return;
		}

		AbstractFurnaceMenu furnaceMenu = (AbstractFurnaceMenu) (Object) this;
		if (isBlockedCookingInput(serverPlayer, furnaceMenu.getSlot(0).getItem())
				|| inventory.getNonEquipmentItems().stream()
						.anyMatch(stack -> cookingRecipe.input().test(stack) && isBlockedCookingInput(serverPlayer, stack))) {
			cir.setReturnValue(RecipeBookMenu.PostPlaceAction.NOTHING);
		}
	}

	@Unique
	private boolean isBlockedCookingInput(ServerPlayer player, ItemStack stack) {
		return !stack.isEmpty()
				&& RecipeProgression.isCookingItem(player, stack)
				&& !RecipeProgression.isCookingInputUnlocked(player, stack);
	}
}
