package com.example.startermod.persistence;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.resources.Identifier;

import com.example.startermod.StarterMod;
import com.example.startermod.progression.PlayerProgress;
import com.example.startermod.progression.VillagerProgress;

public final class ModAttachments {
	public static final AttachmentType<VillagerProgress> VILLAGER_PROGRESS = AttachmentRegistry.create(
			Identifier.fromNamespaceAndPath(StarterMod.MOD_ID, "villager_progress"),
			builder -> builder.initializer(VillagerProgress::empty).persistent(VillagerProgress.CODEC)
	);

	public static final AttachmentType<PlayerProgress> PLAYER_PROGRESS = AttachmentRegistry.create(
			Identifier.fromNamespaceAndPath(StarterMod.MOD_ID, "player_progress"),
			builder -> builder.initializer(PlayerProgress::empty).persistent(PlayerProgress.CODEC)
	);

	private ModAttachments() {
	}

	public static void initialize() {
		// Force attachment registration before saved entities are loaded.
	}
}
