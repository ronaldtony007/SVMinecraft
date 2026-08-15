package com.example.startermod.interaction;

import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.item.ItemStack;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;

import com.example.startermod.progression.ProgressionService;
import com.example.startermod.item.ModItems;

public final class BlacksmithContributionHandler {
	private BlacksmithContributionHandler() {
	}

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
					serverPlayer.sendSystemMessage(Component.literal("This villager cannot learn that knowledge yet, or already knows it."));
					return InteractionResult.SUCCESS;
				}

				if (!player.isCreative()) {
					player.getItemInHand(hand).shrink(1);
				}
				serverPlayer.sendSystemMessage(Component.literal("The Blacksmith accepted the translated scroll."));
				return InteractionResult.SUCCESS;
			}

			var step = ProgressionService.nextStep(villager);
			if (step.isEmpty() || !player.getItemInHand(hand).is(step.get().requiredMaterial())) {
				return InteractionResult.PASS;
			}

			if (!ProgressionService.contributeResource(serverPlayer, villager, step.get().requiredMaterial(), 1)) {
				serverPlayer.sendSystemMessage(Component.literal("This villager is not requesting that material right now."));
				return InteractionResult.SUCCESS;
			}

			ItemStack stack = player.getItemInHand(hand);
			if (!player.isCreative()) {
				stack.shrink(1);
			}

			return InteractionResult.SUCCESS;
		});
	}
}
