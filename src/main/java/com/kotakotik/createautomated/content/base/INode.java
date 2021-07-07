package com.kotakotik.createautomated.content.base;

import com.kotakotik.createautomated.content.recipe.extracting.ExtractingRecipe;
import com.kotakotik.createautomated.content.tiles.OreExtractorTile;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.Random;

public interface INode extends IExtractable {
    boolean randomOrePieceCount(World world, BlockPos pos, BlockPos drillPos, Random random, Optional<ExtractingRecipe> recipe);

    ItemStack getOrePieceStack(World world, BlockPos pos, BlockPos drillPos, Random random, Optional<ExtractingRecipe> recipe);

    int getOreToRemove(World world, BlockPos pos, BlockPos drillPos, Random random, Optional<ExtractingRecipe> recipe);

    int getRequiredProgress(World world, BlockPos pos, BlockPos drillPos, Optional<ExtractingRecipe> recipe);

    int getDrillDamage(World world, BlockPos pos, BlockPos drillPos, Optional<ExtractingRecipe> recipe);

    default int getProgressToAdd(World world, BlockPos pos, BlockPos drillPos, int drillSpeed, Optional<ExtractingRecipe> recipe) {
        return Math.abs(drillSpeed);
    }

    @Override
    default void extractTick(OreExtractorTile oreExtractorTile, Optional<ExtractingRecipe> recipe) {
    }
}
