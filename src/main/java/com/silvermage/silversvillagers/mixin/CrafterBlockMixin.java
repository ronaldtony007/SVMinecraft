package com.silvermage.silversvillagers.mixin;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.CrafterBlock;

import com.silvermage.silversvillagers.recipe.RecipeProgression;

@Mixin(CrafterBlock.class)
public abstract class CrafterBlockMixin {
	@Inject(method = "getPotentialResults", at = @At("RETURN"), cancellable = true)
	private static void silversvillagers$filterLockedRecipe(
			ServerLevel level,
			CraftingInput input,
			CallbackInfoReturnable<Optional<RecipeHolder<CraftingRecipe>>> cir) {
		Optional<RecipeHolder<CraftingRecipe>> result = cir.getReturnValue();
		if (result.isPresent() && RecipeProgression.isGated(result.get().id().identifier())) {
			cir.setReturnValue(Optional.empty());
		}
	}
}
