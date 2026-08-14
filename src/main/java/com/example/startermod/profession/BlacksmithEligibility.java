package com.example.startermod.profession;

import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.villager.VillagerProfession;

public final class BlacksmithEligibility {
	private BlacksmithEligibility() {
	}

	public static boolean isBlacksmithingEligible(Villager villager) {
		return isBlacksmithingProfession(villager);
	}

	public static boolean isLibrarian(Villager villager) {
		return villager.getVillagerData().profession().is(VillagerProfession.LIBRARIAN);
	}

	public static boolean isBlacksmithingProfession(Villager villager) {
		return villager.getVillagerData().profession().is(VillagerProfession.TOOLSMITH)
				|| villager.getVillagerData().profession().is(VillagerProfession.WEAPONSMITH)
				|| villager.getVillagerData().profession().is(VillagerProfession.ARMORER);
	}

	public static String professionName(Villager villager) {
		return villager.getVillagerData().profession().unwrapKey()
				.map(key -> key.identifier().getPath())
				.orElse("unemployed");
	}
}
