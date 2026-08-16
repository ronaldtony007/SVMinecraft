package com.silvermage.silversvillagers.progression;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.resources.Identifier;

import com.silvermage.silversvillagers.Mod;

public final class ProgressAttachments {
	public static final AttachmentType<VillagerProgress> VILLAGER_PROGRESS = AttachmentRegistry.create(
			Identifier.fromNamespaceAndPath(Mod.MOD_ID, "villager_progress"),
			builder -> builder.initializer(VillagerProgress::empty).persistent(VillagerProgress.CODEC)
	);

	public static final AttachmentType<PlayerProgress> PLAYER_PROGRESS = AttachmentRegistry.create(
			Identifier.fromNamespaceAndPath(Mod.MOD_ID, "player_progress"),
			builder -> builder.initializer(PlayerProgress::empty).persistent(PlayerProgress.CODEC)
	);

	public static void initialize() {
		// Force attachment registration before saved entities are loaded.
	}
}
