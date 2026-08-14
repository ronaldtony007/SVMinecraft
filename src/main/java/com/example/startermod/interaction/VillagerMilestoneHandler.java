package com.example.startermod.interaction;

import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.server.level.ServerPlayer;

import com.example.startermod.progression.ProgressionService;

/** Checks rank milestones independently from the villager's trade UI lifecycle. */
public final class VillagerMilestoneHandler {
	private VillagerMilestoneHandler() {
	}

	public static void initialize() {
		UseEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> {
			if (player instanceof ServerPlayer serverPlayer && entity instanceof Villager villager) {
				ProgressionService.requestNextResource(serverPlayer, villager);
			}
			return InteractionResult.PASS;
		});
	}
}
