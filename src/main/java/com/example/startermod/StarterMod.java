package com.example.startermod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;

import net.minecraft.resources.Identifier;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.startermod.command.VillagerProgressCommand;
import com.example.startermod.interaction.VillagerContributionHandler;
import com.example.startermod.interaction.LibrarianTranslationHandler;
import com.example.startermod.interaction.VillagerMilestoneHandler;
import com.example.startermod.item.ModItems;
import com.example.startermod.persistence.ModAttachments;
import com.example.startermod.recipe.RecipeProgression;
import com.example.startermod.progression.PlayerFeatureId;
import com.example.startermod.progression.ProgressionService;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

public final class StarterMod implements ModInitializer {
	public static final String MOD_ID = "startermod";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

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
		LOGGER.info("Starter Mod progression initialized.");
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
