package com.silvermage.silversvillagers.interaction;

import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.server.level.ServerPlayer;

import com.silvermage.silversvillagers.progression.ProgressionService;

public final class VillagerMilestoneHandler {
	public static void initialize() {
		UseEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> {
			if (player instanceof ServerPlayer serverPlayer && entity instanceof Villager villager) {
				ProgressionService.requestNextResource(serverPlayer, villager);
			}
			return InteractionResult.PASS;
		});
	}
}
