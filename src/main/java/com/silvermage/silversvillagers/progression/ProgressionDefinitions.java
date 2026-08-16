package com.silvermage.silversvillagers.progression;

import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.stream.Stream;

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
			"bucket", "shears", "flint_and_steel", "blast_furnace", "crafter");
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
	private static final Set<Identifier> SHEPHERD_COLORED_BED_RECIPES = recipes(
			"black_bed", "blue_bed", "brown_bed", "cyan_bed", "gray_bed", "green_bed",
			"light_blue_bed", "light_gray_bed", "lime_bed", "magenta_bed", "orange_bed",
			"pink_bed", "purple_bed", "red_bed", "yellow_bed",
			"dye_black_bed", "dye_blue_bed", "dye_brown_bed", "dye_cyan_bed", "dye_gray_bed",
			"dye_green_bed", "dye_light_blue_bed", "dye_light_gray_bed", "dye_lime_bed",
			"dye_magenta_bed", "dye_orange_bed", "dye_pink_bed", "dye_purple_bed",
			"dye_red_bed", "dye_yellow_bed");
	private static final Set<Identifier> SHEPHERD_APPRENTICE_DYE_RECIPES = recipes(
			"white_dye", "white_dye_from_lily_of_the_valley",
			"gray_dye", "gray_dye_from_closed_eyeblossom",
			"black_dye_from_ink_sac", "black_dye_from_wither_rose",
			"black_dye", "light_blue_dye_from_blue_orchid", "light_blue_dye_from_blue_white_dye",
			"lime_dye", "lime_dye_from_smelting");
	private static final Set<Identifier> SHEPHERD_JOURNEYMAN_DYE_RECIPES = recipes(
			"yellow_dye_from_dandelion", "yellow_dye_from_wildflowers", "yellow_dye_from_golden_dandelion",
			"yellow_dye_from_sunflower",
			"light_gray_dye_from_azure_bluet", "light_gray_dye_from_oxeye_daisy",
			"light_gray_dye_from_white_tulip", "light_gray_dye_from_black_white_dye",
			"light_gray_dye_from_gray_white_dye", "orange_dye_from_orange_tulip",
			"orange_dye_from_red_yellow", "orange_dye_from_torchflower",
			"orange_dye_from_open_eyeblossom",
			"red_dye_from_poppy", "red_dye_from_beetroot", "red_dye_from_rose_bush",
			"red_dye_from_tulip", "pink_dye_from_peony", "pink_dye_from_pink_tulip",
			"pink_dye_from_red_white_dye", "pink_dye_from_cactus_flower", "pink_dye_from_pink_petals");
	private static final Set<Identifier> SHEPHERD_EXPERT_DYE_RECIPES = recipes(
			"brown_dye", "purple_dye", "blue_dye", "blue_dye_from_cornflower", "green_dye",
			"magenta_dye_from_allium", "magenta_dye_from_lilac", "magenta_dye_from_purple_dye",
			"magenta_dye_from_blue_red_pink", "magenta_dye_from_blue_red_white_dye",
			"magenta_dye_from_purple_and_pink", "cyan_dye", "cyan_dye_from_pitcher_plant",
			"white_banner", "orange_banner", "magenta_banner", "light_blue_banner", "yellow_banner",
			"lime_banner", "pink_banner", "gray_banner", "light_gray_banner", "cyan_banner",
			"purple_banner", "blue_banner", "brown_banner", "green_banner", "red_banner",
			"black_banner", "white_banner_duplicate", "orange_banner_duplicate", "magenta_banner_duplicate",
			"light_blue_banner_duplicate", "yellow_banner_duplicate", "lime_banner_duplicate",
			"pink_banner_duplicate", "gray_banner_duplicate", "light_gray_banner_duplicate",
			"cyan_banner_duplicate", "purple_banner_duplicate", "blue_banner_duplicate",
			"brown_banner_duplicate", "green_banner_duplicate", "red_banner_duplicate", "black_banner_duplicate");
	private static final Set<Identifier> SHEPHERD_MASTER_RECIPES = recipes("painting");
	private static final Set<Identifier> FLETCHER_BOW_AND_ARROW_RECIPES = recipes("bow", "arrow");
	private static final Set<Identifier> FLETCHER_CROSSBOW_RECIPES = recipes("crossbow");
	private static final Set<Identifier> FLETCHER_TIPPED_ARROW_RECIPES = recipes("tipped_arrow");
	private static final Set<Identifier> LIBRARIAN_BOOK_RECIPES = recipes("paper", "book", "bookshelf");
	private static final Set<Identifier> ENCHANTING_RECIPES = recipes("enchanting_table");
	private static final Set<Identifier> CLERIC_ENDER_RECIPES = recipes("ender_eye", "ender_chest");
	private static final Set<Identifier> LEATHERWORKER_EXPERT_RECIPES = recipes("leather_horse_armor", "wolf_armor");
	private static final Set<Identifier> LEATHERWORKER_MASTER_RECIPES = recipes("saddle");

	private static final List<ProgressionStep> STEPS = java.util.stream.Stream.concat(
			BLACKSMITH_PROFESSIONS.stream()
			.flatMap(profession -> Stream.of(
					new ProgressionStep(TechnologyId.STONEWORKING, KnowledgeId.STONEWORKING, profession,
							1, 1, Items.STONE, "Stone", 0, recipesFor(profession, "stone")),
					new ProgressionStep(TechnologyId.COPPERWORKING, KnowledgeId.COPPERWORKING, profession,
							1, 2, Items.COPPER_INGOT, "Copper", 32, recipesFor(profession, "copper")),
					new ProgressionStep(TechnologyId.IRONWORKING, KnowledgeId.IRONWORKING, profession,
							2, 3, Items.IRON_INGOT, "Iron", 32, recipesFor(profession, "iron")),
					new ProgressionStep(TechnologyId.DIAMONDWORKING, KnowledgeId.DIAMONDWORKING, profession,
							3, 5, Items.DIAMOND, "Diamond", 16, recipesFor(profession, "diamond"))
			)),
			java.util.stream.Stream.concat(foodSteps().stream(),
					java.util.stream.Stream.concat(shepherdSteps().stream(),
							java.util.stream.Stream.concat(fletcherSteps().stream(),
									java.util.stream.Stream.concat(librarianSteps().stream(),
											java.util.stream.Stream.concat(clericSteps().stream(), leatherworkerSteps().stream()))))))
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
						1, 2, Items.EGG, "Eggs", 8,
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

	private static List<ProgressionStep> shepherdSteps() {
		return List.of(
				new ProgressionStep(TechnologyId.SHEPHERD_COLORED_BEDS, KnowledgeId.SHEPHERD_COLORED_BEDS,
						"shepherd", 1, 1, Items.AIR, "None", 0, SHEPHERD_COLORED_BED_RECIPES),
				new ProgressionStep(TechnologyId.SHEPHERD_APPRENTICE, KnowledgeId.SHEPHERD_APPRENTICE,
						"shepherd", 1, 2, Items.AIR, "None", 0, SHEPHERD_APPRENTICE_DYE_RECIPES),
				new ProgressionStep(TechnologyId.SHEPHERD_JOURNEYMAN, KnowledgeId.SHEPHERD_JOURNEYMAN,
						"shepherd", 2, 3, Items.AIR, "None", 0, SHEPHERD_JOURNEYMAN_DYE_RECIPES),
				new ProgressionStep(TechnologyId.SHEPHERD_EXPERT, KnowledgeId.SHEPHERD_EXPERT,
						"shepherd", 3, 4, Items.AIR, "None", 0, SHEPHERD_EXPERT_DYE_RECIPES),
				new ProgressionStep(TechnologyId.SHEPHERD_MASTER, KnowledgeId.SHEPHERD_MASTER,
						"shepherd", 4, 5, Items.AIR, "None", 0, SHEPHERD_MASTER_RECIPES)
		);
	}

	private static List<ProgressionStep> fletcherSteps() {
		return List.of(
				new ProgressionStep(TechnologyId.FLETCHER_BOW_AND_ARROWS, KnowledgeId.FLETCHER_BOW_AND_ARROWS,
						"fletcher", 1, 2, Items.STRING, "String", 3, FLETCHER_BOW_AND_ARROW_RECIPES,
						Map.of(Items.STICK, 3)),
				new ProgressionStep(TechnologyId.FLETCHER_CROSSBOW, KnowledgeId.FLETCHER_CROSSBOW,
						"fletcher", 2, 3, Items.AIR, "None", 0, FLETCHER_CROSSBOW_RECIPES),
				new ProgressionStep(TechnologyId.FLETCHER_TIPPED_ARROWS, KnowledgeId.FLETCHER_TIPPED_ARROWS,
						"fletcher", 3, 5, Items.AIR, "None", 0, FLETCHER_TIPPED_ARROW_RECIPES)
		);
	}

	private static List<ProgressionStep> librarianSteps() {
		return List.of(
				new ProgressionStep(TechnologyId.LIBRARIAN_BOOKS, KnowledgeId.LIBRARIAN_BOOKS,
						"librarian", 1, 1, Items.AIR, "None", 0, LIBRARIAN_BOOK_RECIPES),
				new ProgressionStep(TechnologyId.ENCHANTING, KnowledgeId.ENCHANTING,
						"librarian", 1, 2, Items.OBSIDIAN, "Obsidian Blocks", 4, ENCHANTING_RECIPES));
	}

	private static List<ProgressionStep> clericSteps() {
		return List.of(
				new ProgressionStep(TechnologyId.CLERIC_NETHER_ACCESS, KnowledgeId.CLERIC_NETHER_ACCESS,
						"cleric", 1, 2, Items.AIR, "None", 0, Set.of()),
				new ProgressionStep(TechnologyId.CLERIC_ENDER_CRAFTING, KnowledgeId.CLERIC_ENDER_CRAFTING,
						"cleric", 3, 4, Items.AIR, "None", 0, CLERIC_ENDER_RECIPES));
	}

	private static List<ProgressionStep> leatherworkerSteps() {
		return List.of(
				new ProgressionStep(TechnologyId.LEATHERWORKER_EXPERT_ARMOR, KnowledgeId.LEATHERWORKER_EXPERT_ARMOR,
						"leatherworker", 3, 4, Items.AIR, "None", 0, LEATHERWORKER_EXPERT_RECIPES),
				new ProgressionStep(TechnologyId.LEATHERWORKER_MASTER_SADDLE, KnowledgeId.LEATHERWORKER_MASTER_SADDLE,
						"leatherworker", 4, 5, Items.AIR, "None", 0, LEATHERWORKER_MASTER_RECIPES));
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
