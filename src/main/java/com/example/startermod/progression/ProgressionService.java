package com.example.startermod.progression;

import java.util.Optional;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;

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
		String profession = BlacksmithEligibility.professionName(villager);
		VillagerProgress progress = getVillagerProgress(villager);
		return ProgressionDefinitions.forProfession(profession).stream()
				.filter(step -> !progress.hasTechnology(step.technologyId()))
				.findFirst();
	}

	public static boolean requestNextResource(ServerPlayer player, Villager villager) {
		learnFromRank(player, villager);
		if (!BlacksmithEligibility.isBlacksmithingEligible(villager)) {
			return false;
		}

		VillagerProgress progress = getVillagerProgress(villager);
		Optional<ProgressionStep> next = nextStep(villager);
		if (next.isEmpty()) {
			return false;
		}

		ProgressionStep step = next.get();
		if (villager.getVillagerData().level() < step.toRank()) {
			return false;
		}
		if (step.requiredAmount() == 0) {
			completeProgression(player, villager, step);
			return true;
		}
		if (progress.resourceContribution() == 0) {
			player.sendSystemMessage(Component.literal(
					"Provide " + step.requiredAmount() + " " + step.materialName()
							+ " to upgrade to the next level (0/" + step.requiredAmount() + ")."));
		}
		return true;
	}

	public static boolean contributeResource(ServerPlayer player, Villager villager, Item material, int amount) {
		Optional<ProgressionStep> next = nextStep(villager);
		if (next.isEmpty() || next.get().requiredMaterial() != material) {
			return false;
		}

		VillagerProgress progress = getVillagerProgress(villager);
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

		if (contribution == step.requiredAmount()) {
			completeProgression(player, villager, step);
		}
		return true;
	}

	public static boolean setResourceContribution(ServerPlayer player, Villager villager, int amount) {
		Optional<ProgressionStep> next = nextStep(villager);
		if (next.isEmpty()) {
			return false;
		}
		ProgressionStep step = next.get();
		int contribution = Math.max(0, Math.min(step.requiredAmount(), amount));
		setVillagerProgress(villager, getVillagerProgress(villager).withResourceContribution(contribution));
		if (contribution == step.requiredAmount()) {
			completeProgression(player, villager, step);
		}
		return true;
	}

	public static boolean completeScrollUnlock(ServerPlayer player, Villager villager, ItemStack translatedScroll) {
		if (!BlacksmithEligibility.isBlacksmithingEligible(villager)) {
			return false;
		}

		String technologyName = ModItems.scrollTechnology(translatedScroll);
		Identifier technology = Identifier.tryParse(technologyName);
		if (technology == null || technology.equals(TechnologyId.STONEWORKING)) {
			return false;
		}
		String profession = ModItems.scrollProfession(translatedScroll);
		VillagerProgress progress = getVillagerProgress(villager);
		if (progress.hasTechnology(technology)) {
			return false;
		}
		Optional<ProgressionStep> next = nextTransferStep(villager, profession);
		if (next.isEmpty() || !next.get().technologyId().equals(technology)
				|| !next.get().profession().equals(profession)) {
			return false;
		}

		ProgressionStep step = next.get();
		completeTranslatedProgression(player, villager, step);
		return true;
	}

	private static Optional<ProgressionStep> nextTransferStep(Villager villager, String profession) {
		if (!BlacksmithEligibility.isBlacksmithingEligible(villager)
				|| !BlacksmithEligibility.professionName(villager).equals(profession)) {
			return Optional.empty();
		}

		VillagerProgress progress = getVillagerProgress(villager);
		return ProgressionDefinitions.forProfession(profession).stream()
				.filter(step -> !progress.hasTechnology(step.technologyId()))
				.findFirst();
	}

	private static void completeTranslatedProgression(ServerPlayer player, Villager villager, ProgressionStep step) {
		int previousRank = villager.getVillagerData().level();
		VillagerProgress progress = getVillagerProgress(villager);
		setVillagerProgress(villager, progress.withTradeUnlock(step.toRank()).withKnowledge(step.knowledgeId())
				.withTechnology(step.technologyId()));
		villager.setVillagerData(villager.getVillagerData().withLevel(Math.max(previousRank, step.toRank())));
		refreshTrades(villager, (ServerLevel) villager.level(), previousRank);
		learnFromRank(player, villager);
	}

	public static boolean completeProgression(ServerPlayer player, Villager villager) {
		Optional<ProgressionStep> step = nextStep(villager);
		return step.isPresent() && getVillagerProgress(villager).resourceContribution() >= step.get().requiredAmount()
				&& completeProgression(player, villager, step.get());
	}

	private static boolean completeProgression(ServerPlayer player, Villager villager, ProgressionStep step) {
		VillagerProgress progress = getVillagerProgress(villager);
		setVillagerProgress(villager, progress.withTradeUnlock(step.toRank()).withKnowledge(step.knowledgeId())
				.withTechnology(step.technologyId()));
		addKnowledgeScrollTrades(villager);

		learnFromRank(player, villager);

		player.sendSystemMessage(Component.literal(displayName(step.technologyId()) + " unlocked at "
				+ VillagerRankRequirement.levelName(step.toRank()) + "."));
		return true;
	}

	private static void learnFromRank(ServerPlayer player, Villager villager) {
		String profession = BlacksmithEligibility.professionName(villager);
		PlayerProgress playerProgress = getPlayerProgress(player);
		PlayerProgress updatedProgress = playerProgress;
		boolean recipesChanged = false;
		VillagerProgress villagerProgress = getVillagerProgress(villager);
		for (ProgressionStep step : ProgressionDefinitions.forProfession(profession)) {
			if (!villagerProgress.hasTechnology(step.technologyId())) {
				continue;
			}
			Identifier feature = RecipeProgression.featureFor(step);
			if (feature != null && !updatedProgress.hasFeature(feature)) {
				updatedProgress = updatedProgress.withFeature(feature);
				recipesChanged = true;
				player.sendSystemMessage(Component.literal(displayName(step.technologyId()) + " recipes unlocked."));
			}
			awardAgeAdvancement(player, step);
		}
		if (updatedProgress != playerProgress) {
			setPlayerProgress(player, updatedProgress);
		}
		if (recipesChanged) {
			RecipeProgression.refreshPlayerRecipes(player);
		}
	}

	private static void awardAgeAdvancement(ServerPlayer player, ProgressionStep step) {
		String age = switch (step.technologyId().getPath()) {
			case "stoneworking" -> "stone";
			case "copperworking" -> "copper";
			case "ironworking" -> "iron";
			case "diamondworking" -> "diamond";
			default -> null;
		};
		if (age == null) return;

		ServerLevel level = (ServerLevel) player.level();
		AdvancementHolder root = getAdvancement(level, "villager_progression");
		if (root != null) {
			player.getAdvancements().award(root, "progression_started");
		}

		AdvancementHolder advancement = getAdvancement(level, step.profession() + "_" + age);
		if (advancement != null) {
			player.getAdvancements().award(advancement, "complete_progression");
		}
	}

	public static void recordTrade(ServerPlayer player, Villager villager) {
		awardRootAdvancement(player);
		requestNextResource(player, villager);
	}

	private static void awardRootAdvancement(ServerPlayer player) {
		AdvancementHolder root = getAdvancement((ServerLevel) player.level(), "villager_progression");
		if (root != null) {
			player.getAdvancements().award(root, "progression_started");
		}
	}

	private static AdvancementHolder getAdvancement(ServerLevel level, String name) {
		return level.getServer().getAdvancements()
				.get(Identifier.fromNamespaceAndPath("startermod", name));
	}

	public static void refreshTrades(Villager villager, ServerLevel level, int previousRank) {
		int rank = villager.getVillagerData().level();
		for (int rankLevel = previousRank + 1; rankLevel <= rank; rankLevel++) {
			villager.setVillagerData(villager.getVillagerData().withLevel(rankLevel));
			((VillagerTradesMixin) villager).startermod$updateTrades(level);
		}
		villager.setVillagerData(villager.getVillagerData().withLevel(rank));
	}

	public static void debugSetRank(ServerPlayer player, Villager villager, int rank) {
		int previousRank = villager.getVillagerData().level();
		villager.setVillagerData(villager.getVillagerData().withLevel(rank));

		refreshTrades(villager, (ServerLevel) villager.level(), previousRank);
		learnFromRank(player, villager);
	}

	public static boolean unlockTechnology(Villager villager, Identifier technologyId) {
		Optional<ProgressionStep> step = nextStep(villager);
		if (step.isEmpty() || !step.get().technologyId().equals(technologyId)) {
			return false;
		}

		ProgressionStep definition = step.get();
		VillagerProgress progress = getVillagerProgress(villager)
				.withResourceContribution(definition.requiredAmount());
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

	public static void removeKnowledgeScrollTrades(Villager villager) {
		villager.getOffers().removeIf(offer -> offer.getResult().is(ModItems.KNOWLEDGE_SCROLL));
	}

	public static void addKnowledgeScrollTrades(Villager villager) {
		if (!BlacksmithEligibility.isBlacksmithingEligible(villager)) {
			return;
		}
		String profession = BlacksmithEligibility.professionName(villager);
		VillagerProgress progress = getVillagerProgress(villager);
		for (ProgressionStep step : ProgressionDefinitions.forProfession(profession)) {
			if (!progress.hasTechnology(step.technologyId())
					|| step.technologyId().equals(TechnologyId.STONEWORKING)) {
				continue;
			}
			ItemStack scroll = ModItems.knowledgeScroll(step.technologyId(), profession,
					VillagerRankRequirement.levelName(step.toRank()));
			if (villager.getOffers().stream().anyMatch(offer -> offer.getResult().is(scroll.getItem())
					&& ModItems.scrollTechnology(offer.getResult()).equals(ModItems.scrollTechnology(scroll)))) {
				continue;
			}
			villager.getOffers().add(new MerchantOffer(
					new ItemCost(Items.EMERALD, ProgressionConstants.KNOWLEDGE_SCROLL_EMERALD_COST), scroll,
					12, 1, 0.05F));
		}
	}

	public static void normalizeKnowledgeScrollTrades(Villager villager) {
		removeKnowledgeScrollTrades(villager);
		addKnowledgeScrollTrades(villager);
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
