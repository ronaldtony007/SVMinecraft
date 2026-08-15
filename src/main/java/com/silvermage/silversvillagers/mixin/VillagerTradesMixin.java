package com.silvermage.silversvillagers.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.npc.villager.Villager;

@Mixin(Villager.class)
public interface VillagerTradesMixin {
	@Invoker("updateTrades")
	void startermod$updateTrades(ServerLevel level);
}
