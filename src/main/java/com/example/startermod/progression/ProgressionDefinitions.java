package com.example.startermod.progression;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;

public final class ProgressionDefinitions {
	private static final Set<String> BLACKSMITH_PROFESSIONS = Set.of("toolsmith", "weaponsmith", "armorer");

	private static final Set<Identifier> TOOLSMITH_STONE_RECIPES = recipes(
			"stone_pickaxe", "stone_axe", "stone_shovel", "stone_hoe");
	private static final Set<Identifier> TOOLSMITH_COPPER_RECIPES = recipes(
			"copper_pickaxe", "copper_axe", "copper_shovel", "copper_hoe");
	private static final Set<Identifier> TOOLSMITH_IRON_RECIPES = recipes(
			"iron_pickaxe", "iron_axe", "iron_shovel", "iron_hoe",
			"golden_pickaxe", "golden_axe", "golden_shovel", "golden_hoe",
			"bucket", "shears");
	private static final Set<Identifier> TOOLSMITH_DIAMOND_RECIPES = recipes(
			"diamond_pickaxe", "diamond_axe", "diamond_shovel", "diamond_hoe");
	private static final Set<Identifier> WEAPONSMITH_STONE_RECIPES = recipes("stone_sword", "stone_spear");
	private static final Set<Identifier> WEAPONSMITH_COPPER_RECIPES = recipes("copper_sword", "copper_spear");
	private static final Set<Identifier> WEAPONSMITH_IRON_RECIPES = recipes(
			"iron_sword", "iron_spear", "golden_sword", "golden_spear", "shield");
	private static final Set<Identifier> WEAPONSMITH_DIAMOND_RECIPES = recipes("diamond_sword", "diamond_spear");
	private static final Set<Identifier> ARMORER_STONE_RECIPES = recipes(
			"leather_helmet", "leather_chestplate", "leather_leggings", "leather_boots");
	private static final Set<Identifier> ARMORER_COPPER_RECIPES = recipes(
			"copper_helmet", "copper_chestplate", "copper_leggings", "copper_boots");
	private static final Set<Identifier> ARMORER_IRON_RECIPES = recipes(
			"iron_helmet", "iron_chestplate", "iron_leggings", "iron_boots",
			"golden_helmet", "golden_chestplate", "golden_leggings", "golden_boots");
	private static final Set<Identifier> ARMORER_DIAMOND_RECIPES = recipes(
			"diamond_helmet", "diamond_chestplate", "diamond_leggings", "diamond_boots");

	private static final List<ProgressionStep> STEPS = BLACKSMITH_PROFESSIONS.stream()
			.flatMap(profession -> List.of(
					new ProgressionStep(TechnologyId.STONEWORKING, KnowledgeId.STONEWORKING, profession,
							1, 1, Items.STONE, "Stone", 0, recipesFor(profession, "stone")),
					new ProgressionStep(TechnologyId.COPPERWORKING, KnowledgeId.COPPERWORKING, profession,
							1, 2, Items.COPPER_INGOT, "Copper", 32, recipesFor(profession, "copper")),
					new ProgressionStep(TechnologyId.IRONWORKING, KnowledgeId.IRONWORKING, profession,
							2, 3, Items.IRON_INGOT, "Iron", 32, recipesFor(profession, "iron")),
					new ProgressionStep(TechnologyId.DIAMONDWORKING, KnowledgeId.DIAMONDWORKING, profession,
							3, 5, Items.DIAMOND, "Diamond", 16, recipesFor(profession, "diamond"))
			).stream())
			.toList();

	private ProgressionDefinitions() {
	}

	public static Optional<ProgressionStep> forVillager(String profession, int unlockedRank) {
		return STEPS.stream().filter(step -> step.matches(profession, unlockedRank)).findFirst();
	}

	public static List<ProgressionStep> forProfession(String profession) {
		return STEPS.stream().filter(step -> step.profession().equals(profession)).toList();
	}

	public static List<ProgressionStep> allSteps() {
		return STEPS;
	}

	private static Set<Identifier> recipes(String... names) {
		return java.util.Arrays.stream(names).map(Identifier::withDefaultNamespace).collect(java.util.stream.Collectors.toUnmodifiableSet());
	}

	private static Set<Identifier> recipesFor(String profession, String tier) {
		return switch (profession) {
			case "toolsmith" -> switch (tier) {
				case "stone" -> TOOLSMITH_STONE_RECIPES;
				case "copper" -> TOOLSMITH_COPPER_RECIPES;
				case "iron" -> TOOLSMITH_IRON_RECIPES;
				default -> TOOLSMITH_DIAMOND_RECIPES;
			};
			case "weaponsmith" -> switch (tier) {
				case "stone" -> WEAPONSMITH_STONE_RECIPES;
				case "copper" -> WEAPONSMITH_COPPER_RECIPES;
				case "iron" -> WEAPONSMITH_IRON_RECIPES;
				default -> WEAPONSMITH_DIAMOND_RECIPES;
			};
		default -> switch (tier) {
				case "stone" -> ARMORER_STONE_RECIPES;
				case "copper" -> ARMORER_COPPER_RECIPES;
				case "iron" -> ARMORER_IRON_RECIPES;
				default -> ARMORER_DIAMOND_RECIPES;
			};
		};
	}
}
