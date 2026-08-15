package com.silvermage.silversvillagers;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;

import net.minecraft.resources.Identifier;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import com.silvermage.silversvillagers.command.VillagerProgressCommand;
import com.silvermage.silversvillagers.interaction.VillagerContributionHandler;
import com.silvermage.silversvillagers.interaction.LibrarianTranslationHandler;
import com.silvermage.silversvillagers.interaction.VillagerMilestoneHandler;
import com.silvermage.silversvillagers.item.ModItems;
import com.silvermage.silversvillagers.persistence.ModAttachments;
import com.silvermage.silversvillagers.recipe.RecipeProgression;
import com.silvermage.silversvillagers.progression.PlayerFeatureId;
import com.silvermage.silversvillagers.progression.ProgressionService;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

public final class StarterMod implements ModInitializer {
	public static final String MOD_ID = "silvers_villagers";

	@Override
	public void onInitialize() {
		ModAttachments.initialize();
		ModItems.initialize();
		VillagerContributionHandler.initialize();
		VillagerMilestoneHandler.initialize();
		LibrarianTranslationHandler.initialize();
		VillagerProgressCommand.initialize();
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> RecipeProgression.refreshPlayerRecipes(handler.player));
		UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
			if (!(player instanceof ServerPlayer serverPlayer) || !player.getItemInHand(hand).is(Items.FLINT_AND_STEEL)
					|| !world.getBlockState(hitResult.getBlockPos()).is(Blocks.OBSIDIAN)
					|| ProgressionService.getPlayerProgress(serverPlayer).hasFeature(PlayerFeatureId.CLERIC_NETHER_ACCESS)) {
				return InteractionResult.PASS;
			}
			serverPlayer.sendSystemMessage(Component.literal("A Cleric must unlock Nether access before a portal can be lit."));
			return InteractionResult.FAIL;
		});
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
