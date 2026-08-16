package com.silvermage.silversvillagers.profession;

import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.villager.VillagerProfession;

public final class VillagerEligibility {
	public static String professionName(Villager villager) {
		return villager.getVillagerData().profession().unwrapKey()
				.map(key -> key.identifier().getPath())
				.orElse("unemployed");
	}

	public static boolean isProgressionEligible(Villager villager) {
		return isBlacksmithProfession(villager) || isFoodProfession(villager)
				|| isFletcher(villager) || isLibrarian(villager) || isCleric(villager) || isLeatherworker(villager);
	}

	public static boolean isRankOnlyProfession(Villager villager) {
		return villager.getVillagerData().profession().is(VillagerProfession.SHEPHERD);
	}

	public static boolean isBlacksmithProfession(Villager villager) {
		return villager.getVillagerData().profession().is(VillagerProfession.TOOLSMITH)
				|| villager.getVillagerData().profession().is(VillagerProfession.WEAPONSMITH)
				|| villager.getVillagerData().profession().is(VillagerProfession.ARMORER);
	}

	public static boolean isFoodProfession(Villager villager) {
		return villager.getVillagerData().profession().is(VillagerProfession.FARMER)
				|| villager.getVillagerData().profession().is(VillagerProfession.BUTCHER)
				|| villager.getVillagerData().profession().is(VillagerProfession.FISHERMAN);
	}

	public static boolean isLibrarian(Villager villager) {
		return villager.getVillagerData().profession().is(VillagerProfession.LIBRARIAN);
	}

	public static boolean isCleric(Villager villager) {
		return villager.getVillagerData().profession().is(VillagerProfession.CLERIC);
	}

	public static boolean isLeatherworker(Villager villager) {
		return villager.getVillagerData().profession().is(VillagerProfession.LEATHERWORKER);
	}

	public static boolean isFletcher(Villager villager) {
		return villager.getVillagerData().profession().is(VillagerProfession.FLETCHER);
	}
}
