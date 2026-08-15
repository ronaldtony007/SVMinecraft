package com.silvermage.silversvillagers.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.silvermage.silversvillagers.progression.PlayerFeatureId;
import com.silvermage.silversvillagers.progression.ProgressionService;
import com.silvermage.silversvillagers.mixinaccess.ItemCombinerMenuAccess;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.Items;

@Mixin(ItemCombinerMenu.class)
public abstract class ItemCombinerMenuMixin implements ItemCombinerMenuAccess {
	@Shadow @Final protected Container inputSlots;
	@Shadow @Final protected ResultContainer resultSlots;
	@Shadow @Final protected Player player;

	@Override
	public Player startermod$getPlayer() {
		return player;
	}

	@Inject(method = "mayPickup", at = @At("HEAD"), cancellable = true)
	private void startermod$blockLockedEnchantedBookPickup(
			Player player, boolean present, CallbackInfoReturnable<Boolean> cir) {
		if (inputSlots.getItem(1).is(Items.ENCHANTED_BOOK)
				&& player instanceof ServerPlayer serverPlayer
				&& !ProgressionService.getPlayerProgress(serverPlayer).hasFeature(PlayerFeatureId.ENCHANTING)) {
			cir.setReturnValue(false);
		}
	}
}
