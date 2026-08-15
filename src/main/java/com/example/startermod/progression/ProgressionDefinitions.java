package com.example.startermod.progression;

import java.util.List;
import java.util.Set;
import java.util.Map;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;

public final class ProgressionDefinitions {
	private static final Set<String> BLACKSMITH_PROFESSIONS = Set.of("toolsmith", "weaponsmith", "armorer");

	private static final Set<Identifier> TOOLSMITH_STONE_RECIPES = recipes(
			"stone_pickaxe", "stone_axe", "stone_shovel", "stone_hoe", "furnace");
	private static final Set<Identifier> TOOLSMITH_COPPER_RECIPES = recipes(
			"copper_pickaxe", "copper_axe", "copper_shovel", "copper_hoe");
	private static final Set<Identifier> TOOLSMITH_IRON_RECIPES = recipes(
			"iron_pickaxe", "iron_axe", "iron_shovel", "iron_hoe",
			"golden_pickaxe", "golden_axe", "golden_shovel", "golden_hoe",
			"bucket", "shears", "blast_furnace");
	private static final Set<Identifier> TOOLSMITH_DIAMOND_RECIPES = recipes(
			"diamond_pickaxe", "diamond_axe", "diamond_shovel", "diamond_hoe");
	private static final Set<Identifier> WEAPONSMITH_STONE_RECIPES = recipes("stone_sword", "stone_spear", "furnace");
	private static final Set<Identifier> WEAPONSMITH_COPPER_RECIPES = recipes("copper_sword", "copper_spear");
	private static final Set<Identifier> WEAPONSMITH_IRON_RECIPES = recipes(
			"iron_sword", "iron_spear", "golden_sword", "golden_spear", "shield", "blast_furnace");
	private static final Set<Identifier> WEAPONSMITH_DIAMOND_RECIPES = recipes("diamond_sword", "diamond_spear");
	private static final Set<Identifier> ARMORER_STONE_RECIPES = recipes(
			"leather_helmet", "leather_chestplate", "leather_leggings", "leather_boots", "furnace");
	private static final Set<Identifier> ARMORER_COPPER_RECIPES = recipes(
			"copper_helmet", "copper_chestplate", "copper_leggings", "copper_boots");
	private static final Set<Identifier> ARMORER_IRON_RECIPES = recipes(
			"iron_helmet", "iron_chestplate", "iron_leggings", "iron_boots",
			"golden_helmet", "golden_chestplate", "golden_leggings", "golden_boots", "blast_furnace");
	private static final Set<Identifier> ARMORER_DIAMOND_RECIPES = recipes(
			"diamond_helmet", "diamond_chestplate", "diamond_leggings", "diamond_boots");

	private static final List<ProgressionStep> STEPS = java.util.stream.Stream.concat(
			BLACKSMITH_PROFESSIONS.stream()
			.flatMap(profession -> List.of(
					new ProgressionStep(TechnologyId.STONEWORKING, KnowledgeId.STONEWORKING, profession,
							1, 1, Items.STONE, "Stone", 0, recipesFor(profession, "stone")),
					new ProgressionStep(TechnologyId.COPPERWORKING, KnowledgeId.COPPERWORKING, profession,
							1, 2, Items.COPPER_INGOT, "Copper", 32, recipesFor(profession, "copper")),
					new ProgressionStep(TechnologyId.IRONWORKING, KnowledgeId.IRONWORKING, profession,
							2, 3, Items.IRON_INGOT, "Iron", 32, recipesFor(profession, "iron")),
					new ProgressionStep(TechnologyId.DIAMONDWORKING, KnowledgeId.DIAMONDWORKING, profession,
							3, 5, Items.DIAMOND, "Diamond", 16, recipesFor(profession, "diamond"))
			).stream()),
			foodSteps().stream())
			.toList();

	private static List<ProgressionStep> foodSteps() {
		return List.of(
				new ProgressionStep(TechnologyId.FARMER_APPRENTICE, KnowledgeId.FARMER_APPRENTICE, "farmer",
						1, 2, Items.WHEAT, "Wheat", 32,
						recipes("bread", "baked_potato", "baked_potato_from_smoking", "baked_potato_from_campfire_cooking",
								"beetroot_soup", "mushroom_stew")),
				new ProgressionStep(TechnologyId.FARMER_JOURNEYMAN, KnowledgeId.FARMER_JOURNEYMAN, "farmer",
						2, 3, Items.PUMPKIN, "Pumpkin", 16,
						recipes("cookie", "pumpkin_pie", "cake")),
				new ProgressionStep(TechnologyId.FARMER_MASTER, KnowledgeId.FARMER_MASTER, "farmer",
						3, 5, Items.GOLD_NUGGET, "Gold Nuggets and Carrots", 8,
						recipes("golden_apple", "golden_carrot",
								"suspicious_stew_from_dandelion", "suspicious_stew_from_pink_tulip",
								"suspicious_stew_from_red_tulip", "suspicious_stew_from_open_eyeblossom",
								"suspicious_stew_from_white_tulip", "suspicious_stew_from_allium",
								"suspicious_stew_from_golden_dandelion", "suspicious_stew_from_closed_eyeblossom",
								"suspicious_stew_from_azure_bluet", "suspicious_stew_from_oxeye_daisy",
								"suspicious_stew_from_cornflower", "suspicious_stew_from_torchflower",
								"suspicious_stew_from_blue_orchid", "suspicious_stew_from_lily_of_the_valley",
								"suspicious_stew_from_poppy",
								"suspicious_stew_from_wither_rose", "suspicious_stew_from_orange_tulip"),
						Map.of(Items.CARROT, 8)),
				new ProgressionStep(TechnologyId.BUTCHER_APPRENTICE, KnowledgeId.BUTCHER_APPRENTICE, "butcher",
						1, 2, Items.EGG, "Eggs", 32,
						recipes("cooked_chicken", "cooked_chicken_from_smoking", "cooked_chicken_from_campfire_cooking",
								"cooked_mutton", "cooked_mutton_from_smoking", "cooked_mutton_from_campfire_cooking",
								"cooked_rabbit", "cooked_rabbit_from_smoking", "cooked_rabbit_from_campfire_cooking")),
				new ProgressionStep(TechnologyId.BUTCHER_JOURNEYMAN, KnowledgeId.BUTCHER_JOURNEYMAN, "butcher",
						2, 3, Items.BEEF, "Raw Beef", 32,
						recipes("cooked_beef", "cooked_beef_from_smoking", "cooked_beef_from_campfire_cooking",
								"cooked_porkchop", "cooked_porkchop_from_smoking", "cooked_porkchop_from_campfire_cooking",
								"rabbit_stew_from_red_mushroom", "rabbit_stew_from_brown_mushroom")),
				new ProgressionStep(TechnologyId.FISHERMAN_APPRENTICE, KnowledgeId.FISHERMAN_APPRENTICE, "fisherman",
						1, 2, Items.KELP, "Kelp", 32,
						recipes("dried_kelp", "dried_kelp_from_smelting", "dried_kelp_from_smoking", "dried_kelp_from_campfire_cooking",
								"cooked_cod", "cooked_cod_from_smoking", "cooked_cod_from_campfire_cooking",
								"cooked_salmon", "cooked_salmon_from_smoking", "cooked_salmon_from_campfire_cooking"))
		);
	}

	private ProgressionDefinitions() {
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
