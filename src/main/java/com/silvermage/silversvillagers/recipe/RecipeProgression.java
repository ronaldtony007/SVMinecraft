package com.silvermage.silversvillagers.recipe;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;

import com.silvermage.silversvillagers.progression.PlayerProgressionRecipeId;
import com.silvermage.silversvillagers.progression.ProgressionDefinitions;
import com.silvermage.silversvillagers.progression.ProgressionService;
import com.silvermage.silversvillagers.progression.ProgressionStep;
import com.silvermage.silversvillagers.progression.TechnologyId;

public final class RecipeProgression {
	private RecipeProgression() {
	}

	public static Identifier featureFor(ProgressionStep step) {
		boolean stone = step.technologyId().equals(TechnologyId.STONEWORKING);
		boolean copper = step.technologyId().equals(TechnologyId.COPPERWORKING);
		boolean iron = step.technologyId().equals(TechnologyId.IRONWORKING);
		return switch (step.profession()) {
			case "toolsmith" -> stone ? PlayerProgressionRecipeId.TOOLSMITH_STONE_RECIPES
					: copper ? PlayerProgressionRecipeId.TOOLSMITH_COPPER_RECIPES
							: iron ? PlayerProgressionRecipeId.TOOLSMITH_IRON_RECIPES : PlayerProgressionRecipeId.TOOLSMITH_DIAMOND_RECIPES;
			case "weaponsmith" -> stone ? PlayerProgressionRecipeId.WEAPONSMITH_STONE_RECIPES
					: copper ? PlayerProgressionRecipeId.WEAPONSMITH_COPPER_RECIPES
							: iron ? PlayerProgressionRecipeId.WEAPONSMITH_IRON_RECIPES : PlayerProgressionRecipeId.WEAPONSMITH_DIAMOND_RECIPES;
			case "armorer" -> stone ? PlayerProgressionRecipeId.ARMORER_STONE_RECIPES
					: copper ? PlayerProgressionRecipeId.ARMORER_COPPER_RECIPES
							: iron ? PlayerProgressionRecipeId.ARMORER_IRON_RECIPES : PlayerProgressionRecipeId.ARMORER_DIAMOND_RECIPES;
			case "farmer" -> step.technologyId().equals(TechnologyId.FARMER_APPRENTICE)
					? PlayerProgressionRecipeId.FARMER_APPRENTICE_FOOD
					: step.technologyId().equals(TechnologyId.FARMER_JOURNEYMAN)
							? PlayerProgressionRecipeId.FARMER_JOURNEYMAN_FOOD : PlayerProgressionRecipeId.FARMER_MASTER_FOOD;
			case "butcher" -> step.technologyId().equals(TechnologyId.BUTCHER_APPRENTICE)
					? PlayerProgressionRecipeId.BUTCHER_APPRENTICE_FOOD : PlayerProgressionRecipeId.BUTCHER_JOURNEYMAN_FOOD;
			case "fisherman" -> PlayerProgressionRecipeId.FISHERMAN_APPRENTICE_FOOD;
			case "shepherd" -> step.technologyId().equals(TechnologyId.SHEPHERD_COLORED_BEDS)
					? PlayerProgressionRecipeId.SHEPHERD_COLORED_BEDS
					: step.technologyId().equals(TechnologyId.SHEPHERD_APPRENTICE)
							? PlayerProgressionRecipeId.SHEPHERD_APPRENTICE_DYES
							: step.technologyId().equals(TechnologyId.SHEPHERD_JOURNEYMAN)
									? PlayerProgressionRecipeId.SHEPHERD_JOURNEYMAN_DYES
									: step.technologyId().equals(TechnologyId.SHEPHERD_EXPERT)
									? PlayerProgressionRecipeId.SHEPHERD_EXPERT_DYES : PlayerProgressionRecipeId.SHEPHERD_MASTER_PAINTING;
			case "fletcher" -> step.technologyId().equals(TechnologyId.FLETCHER_BOW_AND_ARROWS)
					? PlayerProgressionRecipeId.FLETCHER_BOW_AND_ARROWS
					: step.technologyId().equals(TechnologyId.FLETCHER_CROSSBOW)
							? PlayerProgressionRecipeId.FLETCHER_CROSSBOW : PlayerProgressionRecipeId.FLETCHER_TIPPED_ARROWS;
			case "librarian" -> step.technologyId().equals(TechnologyId.LIBRARIAN_BOOKS)
					? PlayerProgressionRecipeId.LIBRARIAN_BOOKS : PlayerProgressionRecipeId.ENCHANTING;
			case "cleric" -> step.technologyId().equals(TechnologyId.CLERIC_NETHER_ACCESS)
					? PlayerProgressionRecipeId.CLERIC_NETHER_ACCESS : PlayerProgressionRecipeId.CLERIC_ENDER_CRAFTING;
			case "leatherworker" -> step.technologyId().equals(TechnologyId.LEATHERWORKER_EXPERT_ARMOR)
					? PlayerProgressionRecipeId.LEATHERWORKER_EXPERT_ARMOR : PlayerProgressionRecipeId.LEATHERWORKER_MASTER_SADDLE;
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

	public static boolean isCookingItem(ServerPlayer player, ItemStack input) {
		return isCookingItem(player, input, RecipeType.SMELTING)
				|| isCookingItem(player, input, RecipeType.SMOKING);
	}

	private static <T extends AbstractCookingRecipe> boolean isCookingItem(ServerPlayer player, ItemStack input,
			RecipeType<T> recipeType) {
		var recipes = ((net.minecraft.server.level.ServerLevel) player.level()).recipeAccess().getAllOfType(recipeType);
		return recipes.stream().anyMatch(recipe -> recipe.value().matches(new SingleRecipeInput(input), player.level()));
	}

	private static <T extends AbstractCookingRecipe> boolean isCookingInputUnlocked(ServerPlayer player, ItemStack input,
			RecipeType<T> recipeType) {
		var recipes = ((net.minecraft.server.level.ServerLevel) player.level()).recipeAccess().getAllOfType(recipeType);
		return recipes.stream()
				.filter(recipe -> recipe.value().matches(new SingleRecipeInput(input), player.level()))
				.allMatch(recipe -> !isGated(recipe.id().identifier()) || isUnlocked(player, recipe.id().identifier()));
	}

}
