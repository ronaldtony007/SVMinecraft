package com.example.startermod.mixin;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.Recipe;

import com.example.startermod.recipe.RecipeProgression;

@Mixin(ServerPlayer.class)
public abstract class RecipeBookGateMixin {
	@ModifyVariable(method = "awardRecipesByKey", at = @At("HEAD"), argsOnly = true)
	private List<ResourceKey<Recipe<?>>> startermod$filterLockedRecipes(List<ResourceKey<Recipe<?>>> recipes) {
		ServerPlayer player = (ServerPlayer) (Object) this;
		return recipes.stream()
				.filter(recipe -> !RecipeProgression.isGated(recipe.identifier())
						|| RecipeProgression.isUnlocked(player, recipe.identifier()))
				.toList();
	}
}
