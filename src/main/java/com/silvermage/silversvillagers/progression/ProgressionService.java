package com.silvermage.silversvillagers.progression;

import java.util.Optional;
import java.util.stream.Collectors;

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
import net.minecraft.core.registries.BuiltInRegistries;

import com.silvermage.silversvillagers.item.ModItems;
import com.silvermage.silversvillagers.mixin.VillagerTradesMixin;
import com.silvermage.silversvillagers.profession.VillagerEligibility;
import com.silvermage.silversvillagers.recipe.RecipeProgression;

public final class ProgressionService {
	public static VillagerProgress getVillagerProgress(Villager villager) {
		return villager.getAttachedOrSet(ProgressAttachments.VILLAGER_PROGRESS, VillagerProgress.empty());
	}

	public static PlayerProgress getPlayerProgress(ServerPlayer player) {
		return player.getAttachedOrSet(ProgressAttachments.PLAYER_PROGRESS, PlayerProgress.empty());
	}

	public static void setVillagerProgress(Villager villager, VillagerProgress progress) {
		villager.setAttached(ProgressAttachments.VILLAGER_PROGRESS, progress);
	}

	public static void setPlayerProgress(ServerPlayer player, PlayerProgress progress) {
		player.setAttached(ProgressAttachments.PLAYER_PROGRESS, progress);
	}

	public static Optional<ProgressionStep> nextStep(Villager villager) {
		String profession = VillagerEligibility.professionName(villager);
		VillagerProgress progress = getVillagerProgress(villager);
		return ProgressionDefinitions.forProfession(profession).stream()
				.filter(step -> !progress.hasTechnology(step.technologyId()))
				.findFirst();
	}

	public static void requestNextResource(ServerPlayer player, Villager villager) {
		learnFromRank(player, villager);
		if (VillagerEligibility.isRankOnlyProfession(villager)) {
			return;
		}
		if (!VillagerEligibility.isProgressionEligible(villager)) {
			return;
		}

		VillagerProgress progress = getVillagerProgress(villager);
		Optional<ProgressionStep> next = nextStep(villager);
		if (next.isEmpty()) {
			return;
		}

		ProgressionStep step = next.get();
		if (villager.getVillagerData().level() < step.toRank()) {
			return;
		}
		if (step.requirements().values().stream().allMatch(amount -> amount == 0)) {
			completeProgression(player, villager, step);
			return;
		}
		if (!hasAnyContribution(progress, step)) {
			player.sendSystemMessage(Component.literal(
					"Bring " + requirementSummary(step) + " to this villager to unlock "
							+ displayName(step.technologyId()) + "."));
		}
	}

	public static boolean contributeResource(ServerPlayer player, Villager villager, Item material, int amount) {
		if (VillagerEligibility.isRankOnlyProfession(villager)) {
			return false;
		}
		Optional<ProgressionStep> next = nextStep(villager);
		if (next.isEmpty() || !next.get().requirements().containsKey(material)) {
			return false;
		}

		VillagerProgress progress = getVillagerProgress(villager);
		ProgressionStep step = next.get();
		if (villager.getVillagerData().level() < step.toRank()) {
			return false;
		}
		Identifier itemId = BuiltInRegistries.ITEM.getKey(material);
		int previous = contribution(progress, step, material);
		int contribution = Math.min(step.requirements().get(material), previous + amount);
		if (contribution == previous) {
			return false;
		}

		setVillagerProgress(villager, progress.withContribution(itemId, contribution));
		player.sendSystemMessage(Component.literal(displayName(BuiltInRegistries.ITEM.getKey(material))
				+ " delivered: " + contribution + " of " + step.requirements().get(material) + "."));

		if (hasAllRequirements(getVillagerProgress(villager), step)) {
			completeProgression(player, villager, step);
		}
		return true;
	}

	public static void setResourceContribution(ServerPlayer player, Villager villager, Item material, int amount) {
		if (VillagerEligibility.isRankOnlyProfession(villager)) {
			return;
		}
		Optional<ProgressionStep> next = nextStep(villager);
		if (next.isEmpty() || !next.get().requirements().containsKey(material)) {
			return;
		}
		ProgressionStep step = next.get();
		int contribution = Math.max(0, Math.min(step.requirements().get(material), amount));
		Identifier itemId = BuiltInRegistries.ITEM.getKey(material);
		setVillagerProgress(villager, getVillagerProgress(villager).withContribution(itemId, contribution));
		if (hasAllRequirements(getVillagerProgress(villager), step)) {
			completeProgression(player, villager, step);
		}
	}

	public static boolean completeScrollUnlock(ServerPlayer player, Villager villager, ItemStack translatedScroll) {
		if (!VillagerEligibility.isProgressionEligible(villager)) {
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
		if (!VillagerEligibility.isProgressionEligible(villager)
				|| !VillagerEligibility.professionName(villager).equals(profession)) {
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

	public static void completeProgression(ServerPlayer player, Villager villager) {
		Optional<ProgressionStep> step = nextStep(villager);
		if (step.isPresent() && hasAllRequirements(getVillagerProgress(villager), step.get())) {
			completeProgression(player, villager, step.get());
		}
	}

	private static void completeProgression(ServerPlayer player, Villager villager, ProgressionStep step) {
		VillagerProgress progress = getVillagerProgress(villager);
		setVillagerProgress(villager, progress.withTradeUnlock(step.toRank()).withKnowledge(step.knowledgeId())
				.withTechnology(step.technologyId()));
		addKnowledgeScrollTrades(villager);

		learnFromRank(player, villager);

		player.sendSystemMessage(Component.literal(displayName(step.technologyId()) + " unlocked at "
				+ VillagerRankRequirement.levelName(step.toRank()) + "."));
	}

	private static void learnFromRank(ServerPlayer player, Villager villager) {
		String profession = VillagerEligibility.professionName(villager);
		PlayerProgress playerProgress = getPlayerProgress(player);
		PlayerProgress updatedProgress = playerProgress;
		boolean recipesChanged = false;
		VillagerProgress villagerProgress = getVillagerProgress(villager);
		for (ProgressionStep step : ProgressionDefinitions.forProfession(profession)) {
			if (VillagerEligibility.isRankOnlyProfession(villager)
					&& villager.getVillagerData().level() >= step.toRank()
					&& !villagerProgress.hasTechnology(step.technologyId())) {
				villagerProgress = villagerProgress.withKnowledge(step.knowledgeId())
						.withTechnology(step.technologyId());
				setVillagerProgress(villager, villagerProgress);
			}
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
				.get(Identifier.fromNamespaceAndPath("silvers_villagers", name));
	}

	public static void refreshTrades(Villager villager, ServerLevel level, int previousRank) {
		int rank = villager.getVillagerData().level();
		for (int rankLevel = previousRank + 1; rankLevel <= rank; rankLevel++) {
			villager.setVillagerData(villager.getVillagerData().withLevel(rankLevel));
			((VillagerTradesMixin) villager).silversvillagers$updateTrades(level);
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
		if (VillagerEligibility.isRankOnlyProfession(villager)) {
			return false;
		}
		Optional<ProgressionStep> step = nextStep(villager);
		if (step.isEmpty() || !step.get().technologyId().equals(technologyId)) {
			return false;
		}

		ProgressionStep definition = step.get();
		VillagerProgress progress = getVillagerProgress(villager)
				.withResourceContribution(definition.requiredAmount());
		for (var requirement : definition.additionalRequirements().entrySet()) {
			progress = progress.withContribution(BuiltInRegistries.ITEM.getKey(requirement.getKey()), requirement.getValue());
		}
		setVillagerProgress(villager, progress);
		return true;
	}

	public static boolean teachKnowledge(Villager villager, Identifier knowledgeId) {
		if (!VillagerEligibility.isProgressionEligible(villager)) {
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
		if (!VillagerEligibility.isProgressionEligible(villager)
				|| VillagerEligibility.isLibrarian(villager)) {
			return;
		}
		String profession = VillagerEligibility.professionName(villager);
		VillagerProgress progress = getVillagerProgress(villager);
		for (ProgressionStep step : ProgressionDefinitions.forProfession(profession)) {
			if (!progress.hasTechnology(step.technologyId())
					|| step.technologyId().equals(TechnologyId.STONEWORKING)
					|| step.toRank() == 1) {
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

	public static void removeFletcherStickTrade(Villager villager) {
		if (!VillagerEligibility.isFletcher(villager)) {
			return;
		}
		villager.getOffers().removeIf(offer -> offer.getBaseCostA().getItem() == Items.STICK
				&& offer.getResult().getItem() == Items.EMERALD);
	}

	public static void reset(Villager villager, ServerPlayer player) {
		setVillagerProgress(villager, VillagerProgress.empty());
		setPlayerProgress(player, PlayerProgress.empty());
		RecipeProgression.refreshPlayerRecipes(player);
	}

	public static String displayName(Identifier id) {
		return displayName(id.getPath());
	}

	private static String displayName(String value) {
		return java.util.Arrays.stream(value.split("_"))
				.map(part -> Character.toUpperCase(part.charAt(0)) + part.substring(1))
				.collect(java.util.stream.Collectors.joining(" "));
	}

	private static int contribution(VillagerProgress progress, ProgressionStep step, Item material) {
		Identifier id = BuiltInRegistries.ITEM.getKey(material);
		if (progress.resourceContributions().containsKey(id)) {
			return progress.contribution(id);
		}
		return material == step.requiredMaterial() ? progress.resourceContribution() : 0;
	}

	private static boolean hasAnyContribution(VillagerProgress progress, ProgressionStep step) {
		return step.requirements().entrySet().stream().anyMatch(entry -> contribution(progress, step, entry.getKey()) > 0);
	}

	private static boolean hasAllRequirements(VillagerProgress progress, ProgressionStep step) {
		return step.requirements().entrySet().stream()
				.allMatch(entry -> contribution(progress, step, entry.getKey()) >= entry.getValue());
	}

	private static String requirementSummary(ProgressionStep step) {
		return step.requirements().entrySet().stream()
				.filter(entry -> entry.getValue() > 0)
				.map(entry -> (entry.getKey() == step.requiredMaterial() ? step.materialName()
						: displayName(BuiltInRegistries.ITEM.getKey(entry.getKey()))) + " " + entry.getValue())
				.collect(Collectors.joining(" + "));
	}
}
