package com.example.startermod.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.npc.villager.Villager;

import com.example.startermod.progression.ProgressionService;

@Mixin(Villager.class)
public abstract class VillagerTradeGateMixin {
	@Inject(method = "updateTrades", at = @At("HEAD"))
	private void startermod$removeScrollTrades(ServerLevel level, CallbackInfo info) {
		ProgressionService.removeKnowledgeScrollTrades((Villager) (Object) this);
	}

	@Inject(method = "updateTrades", at = @At("TAIL"))
	private void startermod$appendScrollTrades(ServerLevel level, CallbackInfo info) {
		ProgressionService.addKnowledgeScrollTrades((Villager) (Object) this);
	}

	@Inject(method = "startTrading", at = @At("HEAD"))
	private void startermod$normalizeScrollTrades(Player player, CallbackInfo info) {
		ProgressionService.normalizeKnowledgeScrollTrades((Villager) (Object) this);
	}
}
