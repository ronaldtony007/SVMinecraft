package com.example.startermod.progression;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;

public final class ProgressionDefinitions {
	private static final Set<String> BLACKSMITH_PROFESSIONS = Set.of("toolsmith", "weaponsmith", "armorer");

	private static final Set<Identifier> STONE_RECIPES = recipes(
			"stone_sword", "stone_pickaxe", "stone_axe", "stone_shovel", "stone_hoe");
	private static final Set<Identifier> IRON_RECIPES = recipes(
			"iron_sword", "iron_pickaxe", "iron_axe", "iron_shovel", "iron_hoe");
	private static final Set<Identifier> DIAMOND_RECIPES = recipes(
			"diamond_sword", "diamond_pickaxe", "diamond_axe", "diamond_shovel", "diamond_hoe");

	private static final List<ProgressionStep> STEPS = BLACKSMITH_PROFESSIONS.stream()
			.flatMap(profession -> List.of(
					new ProgressionStep(TechnologyId.STONEWORKING, KnowledgeId.STONEWORKING, profession,
							1, 2, Items.STONE, "Stone", 32, STONE_RECIPES),
					new ProgressionStep(TechnologyId.IRONWORKING, KnowledgeId.IRONWORKING, profession,
							2, 3, Items.IRON_INGOT, "Iron", 32, IRON_RECIPES),
					new ProgressionStep(TechnologyId.DIAMONDWORKING, KnowledgeId.DIAMONDWORKING, profession,
							4, 5, Items.DIAMOND, "Diamond", 16, DIAMOND_RECIPES)
			).stream())
			.toList();

	private ProgressionDefinitions() {
	}

	public static Optional<ProgressionStep> forVillager(String profession, int unlockedRank) {
		return STEPS.stream().filter(step -> step.matches(profession, unlockedRank)).findFirst();
	}

	private static Set<Identifier> recipes(String... names) {
		return java.util.Arrays.stream(names).map(Identifier::withDefaultNamespace).collect(java.util.stream.Collectors.toUnmodifiableSet());
	}
}
