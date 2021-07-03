package com.kotakotik.createautomated.content.blocks;

import com.kotakotik.createautomated.content.base.INode;
import com.simibubi.create.repack.registrate.util.nullness.NonNullSupplier;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class NodeBlock extends Block implements INode {
    public final NonNullSupplier<Item> orePiece;
    public final int maxOre;
    public final int minOre;
    public final int requiredProgress;

    public NodeBlock(Properties properties, NonNullSupplier<Item> orePiece, int maxOre, int minOre, int requiredProgress) {
        super(properties);
        this.orePiece = orePiece;
        this.maxOre = maxOre;
        this.minOre = minOre;
        this.requiredProgress = requiredProgress;
    }

    public NodeBlock(Properties properties, NonNullSupplier<Item> orePiece, int ore, int progressRequired) {
        this(properties, orePiece, ore, ore, progressRequired);
    }

    @Override
    public boolean randomOrePieceCount() {
        return maxOre == minOre;
    }

    @Override
    public ItemStack getOrePieceStack(World world, BlockPos pos, BlockPos drillPos, Random random) {
        return new ItemStack(orePiece.get(), minOre == maxOre ? minOre : random.nextInt(minOre + maxOre) - minOre);
    }

    @Override
    public int getOreToRemove(World world, BlockPos pos, BlockPos drillPos, Random random) {
        return 1;
    }

    @Override
    public int getRequiredProgress(World world, BlockPos pos, BlockPos drillPos) {
        return requiredProgress;
    }
}
