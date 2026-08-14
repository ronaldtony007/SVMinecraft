package com.example.startermod.progression;

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
		Set<Identifier> playerRecipes
) {
	public ProgressionStep {
		playerRecipes = Set.copyOf(playerRecipes);
	}

	public boolean matches(String professionName, int unlockedRank) {
		return this.profession.equals(professionName) && this.fromRank == unlockedRank;
	}
}
