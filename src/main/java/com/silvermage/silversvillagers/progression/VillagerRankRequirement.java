package com.silvermage.silversvillagers.progression;

public final class VillagerRankRequirement {
	public static String levelName(int level) {
		return switch (level) {
			case 1 -> "Novice";
			case 2 -> "Apprentice";
			case 3 -> "Journeyman";
			case 4 -> "Expert";
			case 5 -> "Master";
			default -> "Level " + level;
		};
	}
}
