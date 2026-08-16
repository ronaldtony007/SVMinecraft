package com.silvermage.silversvillagers.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.npc.villager.Villager;

import com.silvermage.silversvillagers.progression.ProgressionService;

@Mixin(Villager.class)
public abstract class VillagerTradeGateMixin {
	@Inject(method = "updateTrades", at = @At("HEAD"))
	private void silversvillagers$removeScrollTrades(ServerLevel level, CallbackInfo info) {
		ProgressionService.removeKnowledgeScrollTrades((Villager) (Object) this);
	}

	@Inject(method = "updateTrades", at = @At("TAIL"))
	private void silversvillagers$appendScrollTrades(ServerLevel level, CallbackInfo info) {
		Villager villager = (Villager) (Object) this;
		ProgressionService.addKnowledgeScrollTrades(villager);
		ProgressionService.removeFletcherStickTrade(villager);
	}

	@Inject(method = "startTrading", at = @At("HEAD"))
	private void silversvillagers$normalizeScrollTrades(Player player, CallbackInfo info) {
		Villager villager = (Villager) (Object) this;
		ProgressionService.normalizeKnowledgeScrollTrades(villager);
		ProgressionService.removeFletcherStickTrade(villager);
	}
}
