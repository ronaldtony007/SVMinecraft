package com.example.startermod.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.RecipeHolder;

import net.minecraft.server.level.ServerPlayer;

import com.example.startermod.recipe.RecipeProgression;

@Mixin(CraftingMenu.class)
public final class CraftingMenuMixin {
	@Inject(method = "slotChangedCraftingGrid", at = @At("TAIL"))
	private static void startermod$gateLectern(
			AbstractContainerMenu menu,
			ServerLevel level,
			Player player,
			CraftingContainer craftingContainer,
			ResultContainer resultContainer,
			RecipeHolder<?> recipe,
			CallbackInfo info
	) {
		if (player instanceof ServerPlayer serverPlayer && isLockedRecipe(recipe, serverPlayer)) {
			resultContainer.setItem(0, net.minecraft.world.item.ItemStack.EMPTY);
		}
	}

	private static boolean isLockedRecipe(RecipeHolder<?> recipe, ServerPlayer player) {
		return recipe != null && RecipeProgression.isGated(recipe.id().identifier())
				&& !RecipeProgression.isUnlocked(player, recipe.id().identifier());
	}
}
