package com.kotakotik.createautomated.api;

import com.kotakotik.createautomated.content.processing.oreExtractor.OreExtractorTile;
import com.kotakotik.createautomated.content.processing.oreExtractor.recipe.ExtractingRecipe;
import com.kotakotik.createautomated.register.ModRecipeTypes;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.extensions.IForgeBlock;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;

import java.util.Objects;
import java.util.Optional;

public interface IExtractable extends IForgeBlock {
	void extractTick(OreExtractorTile oreExtractorTile);

	static void tryExtract(OreExtractorTile tile) {
		if (getRecipe(Objects.requireNonNull(tile.getLevel()), tile.getBreakingPos()).isPresent()) {
			if (tile.getBlockToMine() instanceof IExtractable) {
				((IExtractable) tile.getBlockToMine()).extractTick(tile);
			} else {
				extractFromRecipe(tile);
			}
		}
	}

	static boolean isExtractable(World world, BlockPos pos) {
		return world.getBlockState(pos).getBlock() instanceof IExtractable;
	}

	static Optional<ExtractingRecipe> getRecipe(World world, BlockPos pos) {
		return world.getRecipeManager().getRecipeFor(ModRecipeTypes.EXTRACTING, new RecipeWrapper(new ItemStackHandler(1)) {
			@Override
			public ItemStack getItem(int slot) {
				return new ItemStack(world.getBlockState(pos).getBlock()); // probably not the best way of doing this but idc
			}
		}, world);
	}

	static void extractFromRecipe(OreExtractorTile tile) {
		Block below = tile.getBlockToMine();
		BlockPos belowBlock = tile.getBreakingPos();
		Optional<ExtractingRecipe> recipe = IExtractable.getRecipe(Objects.requireNonNull(tile.getLevel()), belowBlock);
		ExtractingRecipe r = recipe.get();
		int progress = (int) (tile.extractProgress + Math.abs(tile.getSpeed()));
		if (progress >= r.requiredProgress) {
			progress = 0;
//			tile.getDrillHeadItem().takeDamage(tile, r.drillDamage);
			tile.takeDamage(r.drillDamage);
			ItemStack stack = tile.inventory.getStackInSlot(0);
			ItemStack toAdd = r.getCraftingResult();
			if (stack.getItem().getRegistryName().equals(toAdd.getItem().getRegistryName())) {
				stack.setCount(Math.min(stack.getMaxStackSize(), stack.getCount() + toAdd.getCount()));
			} else if (stack.isEmpty()) {
				tile.inventory.setStackInSlot(0, toAdd);
			}
		}
		tile.extractProgress = progress;
		tile.notifyUpdate();
	}
}
