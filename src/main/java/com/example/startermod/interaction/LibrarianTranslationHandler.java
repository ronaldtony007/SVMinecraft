package com.example.startermod.interaction;

import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.item.ItemStack;

import net.minecraft.server.level.ServerPlayer;
import com.example.startermod.item.ModItems;
import com.example.startermod.profession.BlacksmithEligibility;

public final class LibrarianTranslationHandler {
	private LibrarianTranslationHandler() {
	}

	public static void initialize() {
		UseEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> {
			if (!(player instanceof ServerPlayer serverPlayer)
				|| !(entity instanceof Villager librarian)
					|| !BlacksmithEligibility.isLibrarian(librarian)
				|| !player.getItemInHand(hand).is(ModItems.KNOWLEDGE_SCROLL)) {
				return InteractionResult.PASS;
			}

			ItemStack scroll = player.getItemInHand(hand);
			String technology = ModItems.scrollTechnology(scroll);
			String profession = ModItems.scrollProfession(scroll);
			if (technology.isEmpty() || profession.isEmpty()) {
				serverPlayer.sendSystemMessage(Component.literal("This scroll has no identifiable progression knowledge."));
				return InteractionResult.SUCCESS;
			}
			if (!player.isCreative()) {
				scroll.shrink(1);
			}
			ModItems.giveTranslatedKnowledgeScroll(serverPlayer, technology, profession,
					"Portable Knowledge");
			serverPlayer.sendSystemMessage(Component.literal("The Librarian translated the scroll."));
			return InteractionResult.SUCCESS;
		});
	}
}
