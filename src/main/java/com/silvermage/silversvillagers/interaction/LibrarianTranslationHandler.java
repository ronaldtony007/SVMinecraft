package com.silvermage.silversvillagers.interaction;

import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.item.ItemStack;

import net.minecraft.server.level.ServerPlayer;
import com.silvermage.silversvillagers.item.ModItems;
import com.silvermage.silversvillagers.profession.VillagerEligibility;

public final class LibrarianTranslationHandler {
	public static void initialize() {
		UseEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> {
			if (!(player instanceof ServerPlayer serverPlayer)
				|| !(entity instanceof Villager librarian)
					|| !VillagerEligibility.isLibrarian(librarian)
				|| !player.getItemInHand(hand).is(ModItems.KNOWLEDGE_SCROLL)) {
				return InteractionResult.PASS;
			}

			ItemStack scroll = player.getItemInHand(hand);
			String technology = ModItems.scrollTechnology(scroll);
			String profession = ModItems.scrollProfession(scroll);
			String rank = ModItems.scrollRank(scroll);
			if (technology.isEmpty() || profession.isEmpty() || rank.isEmpty()) {
				player.sendSystemMessage(Component.literal("This scroll has no identifiable progression knowledge."));
				return InteractionResult.SUCCESS;
			}
			if (!player.isCreative()) {
				scroll.shrink(1);
			}
			ModItems.giveTranslatedKnowledgeScroll(serverPlayer, technology, profession, rank);
			player.sendSystemMessage(Component.literal("The Librarian translated the scroll."));
			return InteractionResult.SUCCESS;
		});
	}
}
