package com.silvermage.silversvillagers.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.Recipe;

import com.silvermage.silversvillagers.recipe.RecipeProgression;

@Mixin(ServerPlayer.class)
public abstract class RecipeBookGateMixin {
	@ModifyVariable(method = "awardRecipesByKey", at = @At("HEAD"), argsOnly = true, name = "recipeIds")
	private List<ResourceKey<Recipe<?>>> silversvillagers$filterLockedRecipes(List<ResourceKey<Recipe<?>>> recipeIds) {
		ServerPlayer player = (ServerPlayer) (Object) this;
		return recipeIds.stream()
				.filter(recipe -> !RecipeProgression.isGated(recipe.identifier())
						|| RecipeProgression.isUnlocked(player, recipe.identifier()))
				.toList();
	}
}
