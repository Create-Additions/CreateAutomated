package com.kotakotik.createautomated.content.blocks;

import com.kotakotik.createautomated.content.base.INode;
import com.simibubi.create.foundation.config.AllConfigs;
import com.simibubi.create.repack.registrate.util.nullness.NonNullSupplier;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.PushReaction;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class NodeBlock extends Block implements INode {
    public final NonNullSupplier<Item> orePiece;
    public final int maxOre;
    public final int minOre;
    public final int requiredProgress;
    public final int drillDamage;

    public NodeBlock(Properties properties, NonNullSupplier<Item> orePiece, int maxOre, int minOre, int requiredProgress, int drillDamage) {
        super(properties);
        this.orePiece = orePiece;
        this.maxOre = maxOre;
        this.minOre = minOre;
        this.requiredProgress = requiredProgress;
        this.drillDamage = drillDamage;
    }

    public NodeBlock(Properties properties, NonNullSupplier<Item> orePiece, int ore, int progressRequired, int drillDamage) {
        this(properties, orePiece, ore, ore, progressRequired, drillDamage);
    }

    @Override
    public boolean randomOrePieceCount() {
        return maxOre == minOre;
    }

    @Override
    public ItemStack getOrePieceStack(World world, BlockPos pos, BlockPos drillPos, Random random) {
        return new ItemStack(orePiece.get(), minOre == maxOre ? minOre : random.nextInt((maxOre - minOre) + 1) + minOre);
    }

    @Override
    public int getOreToRemove(World world, BlockPos pos, BlockPos drillPos, Random random) {
        return 1;
    }

    @Override
    public int getRequiredProgress(World world, BlockPos pos, BlockPos drillPos) {
        return requiredProgress;
    }

    @Override
    public int getDrillDamage(World world, BlockPos pos, BlockPos drillPos) {
        return drillDamage;
    }

    @Override
    public PushReaction getPushReaction(BlockState state) {
        return PushReaction.DESTROY;
    }

    @Override
    public void addInformation(ItemStack p_190948_1_, @Nullable IBlockReader p_190948_2_, List<ITextComponent> p_190948_3_, ITooltipFlag p_190948_4_) {
        p_190948_3_.add(new StringTextComponent(String.valueOf(AllConfigs.SERVER.kinetics.stressValues.getImpactOf(this))));
        super.addInformation(p_190948_1_, p_190948_2_, p_190948_3_, p_190948_4_);
    }
}
