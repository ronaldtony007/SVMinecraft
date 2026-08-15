package com.silvermage.silversvillagers.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.item.trading.MerchantOffer;

import com.silvermage.silversvillagers.progression.ProgressionService;

@Mixin(AbstractVillager.class)
public abstract class VillagerProgressionMixin {
	@Inject(method = "notifyTrade", at = @At("HEAD"))
	private void startermod$progressAfterTrade(MerchantOffer offer, CallbackInfo info) {
		if (!((Object) this instanceof Villager villager)) {
			return;
		}
		if (villager.getTradingPlayer() instanceof ServerPlayer player) {
			ProgressionService.recordTrade(player, villager);
		}
	}
}
