package com.example.startermod.interaction;

import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.item.ItemStack;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;

import com.example.startermod.item.ModItems;
import com.example.startermod.profession.BlacksmithEligibility;
import com.example.startermod.progression.ProgressionConstants;
import com.example.startermod.progression.ProgressionService;

public final class LibrarianTranslationHandler {
	private LibrarianTranslationHandler() {
	}

	public static void initialize() {
		UseEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> {
			if (!(player instanceof ServerPlayer serverPlayer)
					|| !(level instanceof ServerLevel serverLevel)
					|| !(entity instanceof Villager librarian)
					|| !BlacksmithEligibility.isLibrarian(librarian)
				|| !player.getItemInHand(hand).is(ModItems.KNOWLEDGE_SCROLL)) {
				return InteractionResult.PASS;
			}

			Villager target = VillagerLocator.nearestBlacksmith(serverLevel, librarian, ProgressionConstants.LIBRARIAN_SEARCH_RADIUS).orElse(null);
			if (target == null) {
				serverPlayer.sendSystemMessage(Component.literal("The Librarian cannot find an eligible Blacksmith nearby."));
				return InteractionResult.SUCCESS;
			}
			if (ProgressionService.getVillagerProgress(target).pendingScrollRank() == 0) {
				serverPlayer.sendSystemMessage(Component.literal("No Blacksmith nearby is waiting for a translated scroll."));
				return InteractionResult.SUCCESS;
			}
			int rank = ProgressionService.getVillagerProgress(target).pendingScrollRank();

			ItemStack scroll = player.getItemInHand(hand);
			if (!player.isCreative()) {
				scroll.shrink(1);
			}
			String profession = com.example.startermod.profession.BlacksmithEligibility.professionName(target);
			profession = Character.toUpperCase(profession.charAt(0)) + profession.substring(1);
			String technology = ProgressionService.nextStep(target)
					.map(step -> ProgressionService.displayName(step.technologyId()))
					.orElse("Knowledge");
			ModItems.giveTranslatedKnowledgeScroll(serverPlayer, technology, profession,
					com.example.startermod.progression.VillagerRankRequirement.levelName(rank));
			serverPlayer.sendSystemMessage(Component.literal("The Librarian translated the scroll."));
			return InteractionResult.SUCCESS;
		});
	}
}
