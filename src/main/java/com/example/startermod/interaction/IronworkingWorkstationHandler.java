package com.example.startermod.interaction;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.Blocks;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import com.example.startermod.progression.ProgressionConstants;
import com.example.startermod.progression.ProgressionService;
import com.example.startermod.progression.TechnologyId;

public final class IronworkingWorkstationHandler {
	private IronworkingWorkstationHandler() {
	}

	public static void initialize() {
		UseBlockCallback.EVENT.register((player, level, hand, hitResult) -> {
			if (!(player instanceof ServerPlayer serverPlayer)
					|| !(level instanceof ServerLevel serverLevel)
					|| !level.getBlockState(hitResult.getBlockPos()).is(Blocks.SMITHING_TABLE)) {
				return InteractionResult.PASS;
			}

			boolean unlocked = VillagerLocator.nearestBlacksmith(serverLevel, serverPlayer, ProgressionConstants.LIBRARIAN_SEARCH_RADIUS)
					.map(villager -> ProgressionService.getVillagerProgress(villager).hasTechnology(TechnologyId.IRONWORKING))
					.orElse(false);
			if (unlocked) {
				return InteractionResult.PASS;
			}

			serverPlayer.sendSystemMessage(Component.literal("Ironworking is required to use a Smithing Table."));
			return InteractionResult.FAIL;
		});
	}
}
