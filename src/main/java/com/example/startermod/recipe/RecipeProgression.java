package com.example.startermod.recipe;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.RecipeType;

import com.example.startermod.progression.PlayerFeatureId;
import com.example.startermod.progression.ProgressionDefinitions;
import com.example.startermod.progression.ProgressionService;
import com.example.startermod.progression.ProgressionStep;
import com.example.startermod.progression.TechnologyId;

public final class RecipeProgression {
	private RecipeProgression() {
	}

	public static Identifier featureFor(ProgressionStep step) {
		if (step.technologyId().equals(TechnologyId.STONEWORKING)) {
			return PlayerFeatureId.STONEWORKING_RECIPES;
		}
		if (step.technologyId().equals(TechnologyId.IRONWORKING)) {
			return PlayerFeatureId.IRONWORKING_RECIPES;
		}
		if (step.technologyId().equals(TechnologyId.DIAMONDWORKING)) {
			return PlayerFeatureId.DIAMONDWORKING_RECIPES;
		}
		return null;
	}

	public static void refreshPlayerRecipes(ServerPlayer player) {
		List<net.minecraft.world.item.crafting.RecipeHolder<?>> allRecipes = new ArrayList<>();
		((net.minecraft.server.level.ServerLevel) player.level()).recipeAccess().getAllOfType(RecipeType.CRAFTING)
				.forEach(recipe -> allRecipes.add((net.minecraft.world.item.crafting.RecipeHolder<?>) recipe));
		List<net.minecraft.world.item.crafting.RecipeHolder<?>> toUnlock = allRecipes.stream()
				.filter(recipe -> isUnlocked(player, recipe.id().identifier()))
				.toList();
		List<net.minecraft.world.item.crafting.RecipeHolder<?>> toRemove = allRecipes.stream()
				.filter(recipe -> isGated(recipe.id().identifier()) && !isUnlocked(player, recipe.id().identifier()))
				.toList();
		player.resetRecipes(toRemove);
		player.awardRecipes(toUnlock);
	}

	public static boolean isGated(Identifier recipeId) {
		return ProgressionDefinitions.allSteps().stream()
				.anyMatch(step -> step.playerRecipes().contains(recipeId));
	}

	public static boolean isUnlocked(ServerPlayer player, Identifier recipeId) {
		var progress = ProgressionService.getPlayerProgress(player);
		return ProgressionDefinitions.allSteps().stream().anyMatch(step -> {
			Identifier feature = featureFor(step);
			return feature != null && step.playerRecipes().contains(recipeId) && progress.hasFeature(feature);
		});
	}
}
