package com.example.startermod.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.Items;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.RecipeHolder;

import net.minecraft.server.level.ServerPlayer;

import com.example.startermod.progression.PlayerFeatureId;
import com.example.startermod.progression.ProgressionService;

@Mixin(CraftingMenu.class)
public final class CraftingMenuMixin {
	@Inject(method = "slotChangedCraftingGrid", at = @At("TAIL"))
	private static void startermod$gateLectern(
			AbstractContainerMenu menu,
			ServerLevel level,
			Player player,
			CraftingContainer craftingContainer,
			ResultContainer resultContainer,
			RecipeHolder<?> recipe,
			CallbackInfo info
	) {
		if (player instanceof ServerPlayer serverPlayer && isLockedResult(resultContainer.getItem(0), serverPlayer)) {
			resultContainer.setItem(0, net.minecraft.world.item.ItemStack.EMPTY);
		}
	}

	private static boolean isLockedResult(net.minecraft.world.item.ItemStack result, ServerPlayer player) {
		var progress = ProgressionService.getPlayerProgress(player);
		if (result.is(Items.STONE_SWORD) || result.is(Items.STONE_PICKAXE) || result.is(Items.STONE_AXE)
				|| result.is(Items.STONE_SHOVEL) || result.is(Items.STONE_HOE)) {
			return !progress.hasFeature(PlayerFeatureId.STONEWORKING_RECIPES);
		}
		if (result.is(Items.IRON_SWORD) || result.is(Items.IRON_PICKAXE) || result.is(Items.IRON_AXE)
				|| result.is(Items.IRON_SHOVEL) || result.is(Items.IRON_HOE)) {
			return !progress.hasFeature(PlayerFeatureId.IRONWORKING_RECIPES);
		}
		if (result.is(Items.DIAMOND_SWORD) || result.is(Items.DIAMOND_PICKAXE) || result.is(Items.DIAMOND_AXE)
				|| result.is(Items.DIAMOND_SHOVEL) || result.is(Items.DIAMOND_HOE)) {
			return !progress.hasFeature(PlayerFeatureId.DIAMONDWORKING_RECIPES);
		}
		return false;
	}
}
