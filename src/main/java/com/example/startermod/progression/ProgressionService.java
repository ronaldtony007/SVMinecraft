package com.example.startermod.progression;

import java.util.List;
import java.util.Optional;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.item.Item;

import com.example.startermod.item.ModItems;
import com.example.startermod.mixin.VillagerTradesMixin;
import com.example.startermod.persistence.ModAttachments;
import com.example.startermod.profession.BlacksmithEligibility;
import com.example.startermod.recipe.RecipeProgression;

public final class ProgressionService {
	private ProgressionService() {
	}

	public static VillagerProgress getVillagerProgress(Villager villager) {
		return villager.getAttachedOrSet(ModAttachments.VILLAGER_PROGRESS, VillagerProgress.empty());
	}

	public static PlayerProgress getPlayerProgress(ServerPlayer player) {
		return player.getAttachedOrSet(ModAttachments.PLAYER_PROGRESS, PlayerProgress.empty());
	}

	public static void setVillagerProgress(Villager villager, VillagerProgress progress) {
		villager.setAttached(ModAttachments.VILLAGER_PROGRESS, progress);
	}

	public static void setPlayerProgress(ServerPlayer player, PlayerProgress progress) {
		player.setAttached(ModAttachments.PLAYER_PROGRESS, progress);
	}

	public static Optional<ProgressionStep> nextStep(Villager villager) {
		syncRankMilestones(villager);
		return ProgressionDefinitions.forVillager(
				BlacksmithEligibility.professionName(villager), getVillagerProgress(villager).unlockedTradeLevel());
	}

	public static boolean requestNextResource(ServerPlayer player, Villager villager) {
		if (!BlacksmithEligibility.isBlacksmithingEligible(villager)) {
			return false;
		}

		VillagerProgress progress = getVillagerProgress(villager);
		Optional<ProgressionStep> next = nextStep(villager);
		if (next.isEmpty() || progress.pendingScrollRank() != 0) {
			return false;
		}

		ProgressionStep step = next.get();
		if (villager.getVillagerData().level() < step.toRank()) {
			return false;
		}
		if (progress.resourceContribution() == 0) {
			player.sendSystemMessage(Component.literal(
					step.materialName() + " required: " + step.requiredAmount() + " (0/" + step.requiredAmount() + ")"));
		}
		return true;
	}

	public static boolean contributeResource(ServerPlayer player, Villager villager, Item material, int amount) {
		Optional<ProgressionStep> next = nextStep(villager);
		if (next.isEmpty() || next.get().requiredMaterial() != material) {
			return false;
		}

		VillagerProgress progress = getVillagerProgress(villager);
		if (progress.pendingScrollRank() != 0) {
			return false;
		}

		ProgressionStep step = next.get();
		if (villager.getVillagerData().level() < step.toRank()) {
			return false;
		}
		int contribution = Math.min(step.requiredAmount(), progress.resourceContribution() + amount);
		if (contribution == progress.resourceContribution()) {
			return false;
		}

		setVillagerProgress(villager, progress.withResourceContribution(contribution));
		player.sendSystemMessage(Component.literal(step.materialName() + " contribution: "
				+ contribution + "/" + step.requiredAmount()));

		generateScrollIfReady(player, villager, step);
		return true;
	}

	public static boolean setResourceContribution(ServerPlayer player, Villager villager, int amount) {
		Optional<ProgressionStep> next = nextStep(villager);
		if (next.isEmpty()) {
			return false;
		}
		ProgressionStep step = next.get();
		setVillagerProgress(villager, getVillagerProgress(villager).withResourceContribution(
				Math.max(0, Math.min(step.requiredAmount(), amount))));
		generateScrollIfReady(player, villager, step);
		return true;
	}

	public static boolean completeScrollUnlock(ServerPlayer player, Villager villager) {
		if (!BlacksmithEligibility.isBlacksmithingEligible(villager)) {
			return false;
		}

		VillagerProgress progress = getVillagerProgress(villager);
		Optional<ProgressionStep> next = nextStep(villager);
		if (next.isEmpty() || progress.pendingScrollRank() != next.get().toRank()
				|| villager.getVillagerData().level() < next.get().toRank()) {
			return false;
		}

		ProgressionStep step = next.get();
		setVillagerProgress(villager, progress.withTradeUnlock(step.toRank()).withKnowledge(step.knowledgeId())
				.withTechnology(step.technologyId()));
		refreshTrades(villager, (ServerLevel) villager.level());

		PlayerProgress playerProgress = getPlayerProgress(player);
		Identifier feature = RecipeProgression.featureFor(step);
		if (feature != null && !playerProgress.hasFeature(feature)) {
			setPlayerProgress(player, playerProgress.withFeature(feature));
			RecipeProgression.refreshPlayerRecipes(player);
			player.sendSystemMessage(Component.literal(displayName(step.technologyId()) + " recipes unlocked."));
		}

		player.sendSystemMessage(Component.literal("The " + displayName(villager) + " advanced to "
				+ VillagerRankRequirement.levelName(step.toRank()) + "."));
		requestNextResource(player, villager);
		return true;
	}

	public static void refreshTrades(Villager villager, ServerLevel level) {
		var offers = villager.getOffers();
		offers.clear();
		VillagerProgress progress = getVillagerProgress(villager);
		int rank = villager.getVillagerData().level();
		int unlockedRank = BlacksmithEligibility.isBlacksmithingEligible(villager)
				? Math.min(rank, progress.unlockedTradeLevel()) : rank;
		for (int rankLevel = 1; rankLevel <= unlockedRank; rankLevel++) {
			villager.setVillagerData(villager.getVillagerData().withLevel(rankLevel));
			((VillagerTradesMixin) villager).startermod$updateTrades(level);
		}
		villager.setVillagerData(villager.getVillagerData().withLevel(rank));
	}

	public static boolean unlockTechnology(ServerPlayer player, Villager villager, Identifier technologyId) {
		Optional<ProgressionStep> step = ProgressionDefinitions.forVillager(
				BlacksmithEligibility.professionName(villager), getVillagerProgress(villager).unlockedTradeLevel());
		if (step.isEmpty() || !step.get().technologyId().equals(technologyId)) {
			return false;
		}

		ProgressionStep definition = step.get();
		VillagerProgress progress = getVillagerProgress(villager)
				.withResourceContribution(definition.requiredAmount())
				.withPendingScroll(definition.toRank());
		setVillagerProgress(villager, progress);
		return true;
	}

	public static boolean teachKnowledge(Villager villager, Identifier knowledgeId) {
		if (!BlacksmithEligibility.isBlacksmithingEligible(villager)) {
			return false;
		}
		VillagerProgress progress = getVillagerProgress(villager);
		if (progress.knows(knowledgeId)) {
			return false;
		}
		setVillagerProgress(villager, progress.withKnowledge(knowledgeId));
		return true;
	}

	private static void generateScrollIfReady(ServerPlayer player, Villager villager, ProgressionStep step) {
		VillagerProgress progress = getVillagerProgress(villager);
		if (progress.resourceContribution() != step.requiredAmount() || progress.pendingScrollRank() != 0) {
			return;
		}
		if (ModItems.giveKnowledgeScroll(player, displayName(step.technologyId()), displayName(villager),
				VillagerRankRequirement.levelName(step.toRank()))) {
			setVillagerProgress(villager, progress.withPendingScroll(step.toRank()));
			player.sendSystemMessage(Component.literal(displayName(step.technologyId()) + " Scroll generated. Take it to a Librarian."));
		}
	}

	public static void syncRankMilestones(Villager villager) {
		if (!BlacksmithEligibility.isBlacksmithingEligible(villager)) {
			return;
		}
		VillagerProgress progress = getVillagerProgress(villager);
		if (progress.unlockedTradeLevel() == 3 && villager.getVillagerData().level() >= 4) {
			setVillagerProgress(villager, progress.withTradeUnlock(4));
		}
	}

	public static void reset(Villager villager, ServerPlayer player) {
		setVillagerProgress(villager, VillagerProgress.empty());
		setPlayerProgress(player, PlayerProgress.empty());
		RecipeProgression.refreshPlayerRecipes(player);
	}

	private static String displayName(Villager villager) {
		return displayName(BlacksmithEligibility.professionName(villager));
	}

	public static String displayName(Identifier id) {
		return displayName(id.getPath());
	}

	private static String displayName(String value) {
		return java.util.Arrays.stream(value.split("_"))
				.map(part -> Character.toUpperCase(part.charAt(0)) + part.substring(1))
				.collect(java.util.stream.Collectors.joining(" "));
	}
}
