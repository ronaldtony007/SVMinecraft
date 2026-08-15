package com.example.startermod.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.example.startermod.progression.PlayerFeatureId;
import com.example.startermod.progression.ProgressionService;
import com.example.startermod.mixinaccess.ItemCombinerMenuAccess;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

@Mixin(AnvilMenu.class)
public abstract class LibrarianAnvilGateMixin {
	@Inject(method = "createResult", at = @At("TAIL"))
	private void startermod$clearLockedEnchantedBookResult(CallbackInfo info) {
		AnvilMenu menu = (AnvilMenu) (Object) this;
		if (menu.getSlot(1).getItem().is(Items.ENCHANTED_BOOK)
				&& !startermod$isEnchantingUnlocked(menu)) {
			menu.getSlot(2).set(ItemStack.EMPTY);
		}
	}

	private boolean startermod$isEnchantingUnlocked(AnvilMenu menu) {
		if (!(((ItemCombinerMenuAccess) (Object) menu).startermod$getPlayer() instanceof ServerPlayer serverPlayer)) {
			return true;
		}
		return ProgressionService.getPlayerProgress(serverPlayer).hasFeature(PlayerFeatureId.ENCHANTING);
	}
}
