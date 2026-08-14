package com.example.startermod.recipe;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.RecipeType;

import com.example.startermod.progression.PlayerFeatureId;
import com.example.startermod.progression.ProgressionService;
import com.example.startermod.progression.ProgressionStep;

public final class RecipeProgression {
	private static final Set<Identifier> GATED_RECIPES = Set.of(
			Identifier.withDefaultNamespace("stone_sword"),
			Identifier.withDefaultNamespace("stone_pickaxe"),
			Identifier.withDefaultNamespace("stone_axe"),
			Identifier.withDefaultNamespace("stone_shovel"),
			Identifier.withDefaultNamespace("stone_hoe"),
			Identifier.withDefaultNamespace("iron_sword"),
			Identifier.withDefaultNamespace("iron_pickaxe"),
			Identifier.withDefaultNamespace("iron_axe"),
			Identifier.withDefaultNamespace("iron_shovel"),
			Identifier.withDefaultNamespace("iron_hoe"),
			Identifier.withDefaultNamespace("diamond_sword"),
			Identifier.withDefaultNamespace("diamond_pickaxe"),
			Identifier.withDefaultNamespace("diamond_axe"),
			Identifier.withDefaultNamespace("diamond_shovel"),
			Identifier.withDefaultNamespace("diamond_hoe"));

	private RecipeProgression() {
	}

	public static Identifier featureFor(ProgressionStep step) {
		if (step.technologyId().equals(com.example.startermod.progression.TechnologyId.STONEWORKING)) {
			return PlayerFeatureId.STONEWORKING_RECIPES;
		}
		if (step.technologyId().equals(com.example.startermod.progression.TechnologyId.IRONWORKING)) {
			return PlayerFeatureId.IRONWORKING_RECIPES;
		}
		if (step.technologyId().equals(com.example.startermod.progression.TechnologyId.DIAMONDWORKING)) {
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
				.filter(recipe -> GATED_RECIPES.contains(recipe.id().identifier()) && !isUnlocked(player, recipe.id().identifier()))
				.toList();
		player.resetRecipes(toRemove);
		player.awardRecipes(toUnlock);
	}

	public static boolean isGated(Identifier recipeId) {
		return GATED_RECIPES.contains(recipeId);
	}

	public static boolean isUnlocked(ServerPlayer player, Identifier recipeId) {
		var progress = ProgressionService.getPlayerProgress(player);
		return progress.hasFeature(PlayerFeatureId.STONEWORKING_RECIPES) && recipeId.getPath().startsWith("stone_")
				|| progress.hasFeature(PlayerFeatureId.IRONWORKING_RECIPES) && recipeId.getPath().startsWith("iron_")
				|| progress.hasFeature(PlayerFeatureId.DIAMONDWORKING_RECIPES) && recipeId.getPath().startsWith("diamond_");
	}
}
