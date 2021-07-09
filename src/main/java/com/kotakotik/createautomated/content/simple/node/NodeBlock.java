package com.kotakotik.createautomated.content.simple.node;

import com.kotakotik.createautomated.content.base.INode;
import com.kotakotik.createautomated.content.kinetic.oreExtractor.ExtractingRecipe;
import com.simibubi.create.foundation.config.AllConfigs;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.PushReaction;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class NodeBlock extends Block implements INode {
    public NodeBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean randomOrePieceCount(World world, BlockPos pos, BlockPos drillPos, Random random, Optional<ExtractingRecipe> recipe) {
        return recipe.get().maxOre == recipe.get().minOre;
    }

    @Override
    public ItemStack getOrePieceStack(World world, BlockPos pos, BlockPos drillPos, Random random, Optional<ExtractingRecipe> recipe) {
        return recipe.get().getCraftingResult();
    }

    @Override
    public int getOreToRemove(World world, BlockPos pos, BlockPos drillPos, Random random, Optional<ExtractingRecipe> recipe) {
        return 1; // wait isnt this supposed to be removed
    }

    @Override
    public int getRequiredProgress(World world, BlockPos pos, BlockPos drillPos, Optional<ExtractingRecipe> recipe) {
        return recipe.get().requiredProgress;
    }

    @Override
    public int getDrillDamage(World world, BlockPos pos, BlockPos drillPos, Optional<ExtractingRecipe> recipe) {
        return recipe.get().drillDamage;
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
