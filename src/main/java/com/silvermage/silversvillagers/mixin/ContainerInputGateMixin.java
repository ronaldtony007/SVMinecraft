package com.silvermage.silversvillagers.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.silvermage.silversvillagers.recipe.RecipeProgression;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.ItemStack;

@Mixin(AbstractContainerMenu.class)
public abstract class ContainerInputGateMixin {
	@Inject(method = "clicked", at = @At("HEAD"), cancellable = true)
	private void silversvillagers$gateFurnaceInput(int slotId, int button, ContainerInput clickType, Player player,
			CallbackInfo info) {
		AbstractContainerMenu menu = (AbstractContainerMenu) (Object) this;
		if (!(menu instanceof AbstractFurnaceMenu)
				|| slotId != AbstractFurnaceMenu.INGREDIENT_SLOT
				|| !(player instanceof ServerPlayer serverPlayer)) {
			return;
		}

		ItemStack insertedStack = silversvillagers$getInsertedStack(menu, button, clickType, player);
		if (silversvillagers$isBlockedCookingInput(serverPlayer, insertedStack)) {
			info.cancel();
		}
	}

	@Unique
	private ItemStack silversvillagers$getInsertedStack(AbstractContainerMenu menu, int button,
			ContainerInput clickType, Player player) {
		return switch (clickType) {
			case PICKUP, QUICK_CRAFT -> menu.getCarried();
			case SWAP -> button >= 0 && button < Inventory.SELECTION_SIZE
					? player.getInventory().getItem(button)
					: button == Inventory.SLOT_OFFHAND ? player.getOffhandItem() : ItemStack.EMPTY;
			default -> ItemStack.EMPTY;
		};
	}

	@Unique
	private boolean silversvillagers$isBlockedCookingInput(ServerPlayer player, ItemStack stack) {
		return !stack.isEmpty()
				&& RecipeProgression.isCookingItem(player, stack)
				&& !RecipeProgression.isCookingInputUnlocked(player, stack);
	}
}
