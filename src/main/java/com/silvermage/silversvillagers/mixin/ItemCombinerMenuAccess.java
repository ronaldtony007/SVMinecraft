package com.silvermage.silversvillagers.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ItemCombinerMenu;

@Mixin(ItemCombinerMenu.class)
public interface ItemCombinerMenuAccess {
	@Accessor("player")
	Player silversvillagers$getPlayer();
}
