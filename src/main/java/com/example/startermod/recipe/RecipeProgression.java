package com.example.startermod.recipe;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;

import com.example.startermod.progression.PlayerFeatureId;
import com.example.startermod.progression.ProgressionDefinitions;
import com.example.startermod.progression.ProgressionService;
import com.example.startermod.progression.ProgressionStep;
import com.example.startermod.progression.TechnologyId;

public final class RecipeProgression {
	private RecipeProgression() {
	}

	public static Identifier featureFor(ProgressionStep step) {
		boolean stone = step.technologyId().equals(TechnologyId.STONEWORKING);
		boolean copper = step.technologyId().equals(TechnologyId.COPPERWORKING);
		boolean iron = step.technologyId().equals(TechnologyId.IRONWORKING);
		return switch (step.profession()) {
			case "toolsmith" -> stone ? PlayerFeatureId.TOOLSMITH_STONE_RECIPES
					: copper ? PlayerFeatureId.TOOLSMITH_COPPER_RECIPES
							: iron ? PlayerFeatureId.TOOLSMITH_IRON_RECIPES : PlayerFeatureId.TOOLSMITH_DIAMOND_RECIPES;
			case "weaponsmith" -> stone ? PlayerFeatureId.WEAPONSMITH_STONE_RECIPES
					: copper ? PlayerFeatureId.WEAPONSMITH_COPPER_RECIPES
							: iron ? PlayerFeatureId.WEAPONSMITH_IRON_RECIPES : PlayerFeatureId.WEAPONSMITH_DIAMOND_RECIPES;
			case "armorer" -> stone ? PlayerFeatureId.ARMORER_STONE_RECIPES
					: copper ? PlayerFeatureId.ARMORER_COPPER_RECIPES
							: iron ? PlayerFeatureId.ARMORER_IRON_RECIPES : PlayerFeatureId.ARMORER_DIAMOND_RECIPES;
			case "farmer" -> step.technologyId().equals(TechnologyId.FARMER_APPRENTICE)
					? PlayerFeatureId.FARMER_APPRENTICE_FOOD
					: step.technologyId().equals(TechnologyId.FARMER_JOURNEYMAN)
							? PlayerFeatureId.FARMER_JOURNEYMAN_FOOD : PlayerFeatureId.FARMER_MASTER_FOOD;
			case "butcher" -> step.technologyId().equals(TechnologyId.BUTCHER_APPRENTICE)
					? PlayerFeatureId.BUTCHER_APPRENTICE_FOOD : PlayerFeatureId.BUTCHER_JOURNEYMAN_FOOD;
			case "fisherman" -> PlayerFeatureId.FISHERMAN_APPRENTICE_FOOD;
			case "shepherd" -> step.technologyId().equals(TechnologyId.SHEPHERD_COLORED_BEDS)
					? PlayerFeatureId.SHEPHERD_COLORED_BEDS
					: step.technologyId().equals(TechnologyId.SHEPHERD_APPRENTICE)
							? PlayerFeatureId.SHEPHERD_APPRENTICE_DYES
							: step.technologyId().equals(TechnologyId.SHEPHERD_JOURNEYMAN)
									? PlayerFeatureId.SHEPHERD_JOURNEYMAN_DYES
									: step.technologyId().equals(TechnologyId.SHEPHERD_EXPERT)
											? PlayerFeatureId.SHEPHERD_EXPERT_DYES : PlayerFeatureId.SHEPHERD_MASTER_PAINTING;
			default -> null;
		};
	}

	public static void refreshPlayerRecipes(ServerPlayer player) {
		List<net.minecraft.world.item.crafting.RecipeHolder<?>> allRecipes = new ArrayList<>(
				((net.minecraft.server.level.ServerLevel) player.level()).recipeAccess().getRecipes());
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

	public static boolean isCampfireInputUnlocked(ServerPlayer player, ItemStack input) {
		return true;
	}

	public static boolean isCookingInputUnlocked(ServerPlayer player, ItemStack input) {
		return isCookingInputUnlocked(player, input, RecipeType.SMELTING)
				&& isCookingInputUnlocked(player, input, RecipeType.SMOKING);
	}

	private static <T extends AbstractCookingRecipe> boolean isCookingInputUnlocked(ServerPlayer player, ItemStack input,
			RecipeType<T> recipeType) {
		var recipes = ((net.minecraft.server.level.ServerLevel) player.level()).recipeAccess().getAllOfType(recipeType);
		return recipes.stream()
				.filter(recipe -> recipe.value().matches(new SingleRecipeInput(input), player.level()))
				.allMatch(recipe -> !isGated(recipe.id().identifier()) || isUnlocked(player, recipe.id().identifier()));
	}

}
