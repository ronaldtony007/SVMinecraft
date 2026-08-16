package com.silvermage.silversvillagers.interaction;

import com.silvermage.silversvillagers.progression.ProgressionStep;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.item.ItemStack;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;

import com.silvermage.silversvillagers.progression.ProgressionService;
import com.silvermage.silversvillagers.item.ModItems;

import java.util.Optional;

public final class VillagerContributionHandler {
	public static void initialize() {
		UseEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> {
			if (!(player instanceof ServerPlayer serverPlayer)
					|| !(level instanceof ServerLevel)
					|| !(entity instanceof Villager villager)) {
				return InteractionResult.PASS;
			}

			if (player.getItemInHand(hand).is(ModItems.TRANSLATED_KNOWLEDGE_SCROLL)) {
				ItemStack scroll = player.getItemInHand(hand);
				if (!ProgressionService.completeScrollUnlock(serverPlayer, villager, scroll)) {
					player.sendSystemMessage(Component.literal("This villager cannot learn that knowledge yet, or already knows it."));
					return InteractionResult.SUCCESS;
				}

				if (!player.isCreative()) {
					player.getItemInHand(hand).shrink(1);
				}
				player.sendSystemMessage(Component.literal("The villager accepted the translated scroll."));
				return InteractionResult.SUCCESS;
			}

			Optional<ProgressionStep> step = ProgressionService.nextStep(villager);
			if (step.isPresent() && step.get().requirements().values().stream().allMatch(amount -> amount == 0)) {
				ProgressionService.requestNextResource(serverPlayer, villager);
				step = ProgressionService.nextStep(villager);
			}
			ItemStack stack = player.getItemInHand(hand);
			if (step.isEmpty() || step.get().requirements().values().stream().allMatch(amount -> amount == 0)
					|| step.get().requirements().keySet().stream().noneMatch(stack::is)) {
				return InteractionResult.PASS;
			}

			if (!ProgressionService.contributeResource(serverPlayer, villager, stack.getItem(), 1)) {
				player.sendSystemMessage(Component.literal("This villager is not requesting that material right now."));
				return InteractionResult.SUCCESS;
			}

			if (!player.isCreative()) {
				stack.shrink(1);
			}

			return InteractionResult.SUCCESS;
		});
	}
}
