package com.silvermage.silversvillagers.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;

import net.minecraft.server.level.ServerPlayer;

import com.silvermage.silversvillagers.recipe.RecipeProgression;

@Mixin(CraftingMenu.class)
public final class CraftingMenuMixin {
	@Inject(method = "slotChangedCraftingGrid", at = @At("TAIL"))
	private static void silversvillagers$gateLockedRecipe(
			AbstractContainerMenu menu,
			ServerLevel level,
			Player player,
			CraftingContainer craftingContainer,
			ResultContainer resultContainer,
			RecipeHolder<?> recipe,
			CallbackInfo info
	) {
		if (!(player instanceof ServerPlayer serverPlayer)) {
			return;
		}

		Optional<RecipeHolder<CraftingRecipe>> resolvedRecipe = level.recipeAccess().getRecipeFor(
				RecipeType.CRAFTING, craftingContainer.asCraftInput(), level);
		if (resolvedRecipe.isPresent()
				&& RecipeProgression.isLocked(serverPlayer, resolvedRecipe.get())) {
			resultContainer.setItem(0, net.minecraft.world.item.ItemStack.EMPTY);
		}
	}
}
