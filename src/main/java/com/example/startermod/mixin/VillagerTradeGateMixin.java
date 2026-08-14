package com.example.startermod.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.npc.villager.Villager;

import com.example.startermod.profession.BlacksmithEligibility;
import com.example.startermod.progression.ProgressionService;
import com.example.startermod.progression.TechnologyId;
import com.example.startermod.progression.ProgressionService;

@Mixin(Villager.class)
public abstract class VillagerTradeGateMixin {
	@Inject(method = "updateTrades", at = @At("HEAD"), cancellable = true)
	private void startermod$gateTrades(ServerLevel level, CallbackInfo info) {
		Villager villager = (Villager) (Object) this;
		ProgressionService.syncRankMilestones(villager);
		if (BlacksmithEligibility.isBlacksmithingEligible(villager)
				&& villager.getVillagerData().level() > ProgressionService.getVillagerProgress(villager).unlockedTradeLevel()) {
			villager.getOffers().clear();
			info.cancel();
		}
	}
}
