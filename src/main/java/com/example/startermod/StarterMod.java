package com.example.startermod;

import net.fabricmc.api.ModInitializer;

import net.minecraft.resources.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.startermod.command.VillagerProgressCommand;
import com.example.startermod.interaction.BlacksmithContributionHandler;
import com.example.startermod.interaction.LibrarianTranslationHandler;
import com.example.startermod.interaction.IronworkingWorkstationHandler;
import com.example.startermod.interaction.VillagerMilestoneHandler;
import com.example.startermod.item.ModItems;
import com.example.startermod.recipe.RecipeProgression;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

public class StarterMod implements ModInitializer {
	public static final String MOD_ID = "startermod";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModItems.initialize();
		BlacksmithContributionHandler.initialize();
		VillagerMilestoneHandler.initialize();
		LibrarianTranslationHandler.initialize();
		IronworkingWorkstationHandler.initialize();
		VillagerProgressCommand.initialize();
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> RecipeProgression.refreshPlayerRecipes(handler.player));
		LOGGER.info("Starter Mod progression initialized.");
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
