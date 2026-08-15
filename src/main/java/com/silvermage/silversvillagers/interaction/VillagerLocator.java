package com.silvermage.silversvillagers.interaction;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.villager.Villager;

import com.silvermage.silversvillagers.profession.BlacksmithEligibility;

public final class VillagerLocator {
	private VillagerLocator() {
	}

	public static Optional<Villager> nearestBlacksmith(ServerLevel level, Entity origin, double radius) {
		List<Villager> villagers = level.getEntitiesOfClass(Villager.class,
				origin.getBoundingBox().inflate(radius), BlacksmithEligibility::isBlacksmithingEligible);

		return villagers.stream()
				.filter(villager -> villager != origin)
				.min(Comparator.comparingDouble((Villager villager) -> origin.distanceToSqr(villager))
						.thenComparing(villager -> villager.getUUID().toString()));
	}
}
