package com.example.startermod.progression;

import java.util.Map;
import java.util.Set;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

public record ProgressionStep(
		Identifier technologyId,
		Identifier knowledgeId,
		String profession,
		int fromRank,
		int toRank,
		Item requiredMaterial,
		String materialName,
		int requiredAmount,
		Set<Identifier> playerRecipes,
		Map<Item, Integer> additionalRequirements
) {
	public ProgressionStep {
		playerRecipes = Set.copyOf(playerRecipes);
		additionalRequirements = Map.copyOf(additionalRequirements);
	}

	public ProgressionStep(Identifier technologyId, Identifier knowledgeId, String profession, int fromRank,
			int toRank, Item requiredMaterial, String materialName, int requiredAmount, Set<Identifier> playerRecipes) {
		this(technologyId, knowledgeId, profession, fromRank, toRank, requiredMaterial, materialName, requiredAmount,
				playerRecipes, Map.of());
	}

	public Map<Item, Integer> requirements() {
		Map<Item, Integer> requirements = new java.util.LinkedHashMap<>();
		requirements.put(requiredMaterial, requiredAmount);
		requirements.putAll(additionalRequirements);
		return Map.copyOf(requirements);
	}

}
