package com.kotakotik.createautomated.content.base;

import com.kotakotik.createautomated.content.tiles.OreExtractorTile;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public interface INode extends IExtractable {
    boolean randomOrePieceCount();

    ItemStack getOrePieceStack(World world, BlockPos pos, BlockPos drillPos, Random random);

    int getOreToRemove(World world, BlockPos pos, BlockPos drillPos, Random random);

    int getRequiredProgress(World world, BlockPos pos, BlockPos drillPos);

    default int getProgressToAdd(World world, BlockPos pos, BlockPos drillPos, int drillSpeed) {
        return Math.abs(drillSpeed);
    }

    @Override
    default void extractTick(OreExtractorTile oreExtractorTile) {
    }
}
