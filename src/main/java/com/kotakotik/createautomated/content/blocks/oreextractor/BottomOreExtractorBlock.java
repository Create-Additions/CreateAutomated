package com.kotakotik.createautomated.content.blocks.oreextractor;

import com.kotakotik.createautomated.content.base.IOreExtractorBlock;
import com.kotakotik.createautomated.register.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class BottomOreExtractorBlock extends Block implements IOreExtractorBlock {
    public BottomOreExtractorBlock(Properties p_i48440_1_) {
        super(p_i48440_1_);
    }

    @Override
    public boolean isTop() {
        return false;
    }

    @Override
    public PushReaction getPushReaction(BlockState state) {
        return pushReaction(state);
    }

    @Override
    public BlockState updatePostPlacement(BlockState state, Direction direction, BlockState updatingState, IWorld world, BlockPos pos, BlockPos updatingPos) {
        state = checkForOther(state, direction, updatingState, world, pos, updatingPos);
        if (state.isAir(world, pos)) {
            return state;
        }
        return super.updatePostPlacement(state, direction, updatingState, world, pos, updatingPos);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext ctx) {
        BlockPos bottom = ctx.getPos();
        BlockPos top = bottom.up();
        return ctx.getWorld().getBlockState(top).isReplaceable(ctx) ? super.getStateForPlacement(ctx) : null;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);
        if (!world.isRemote) {
            BlockPos top = pos.up();
            world.setBlockState(top, ModBlocks.ORE_EXTRACTOR_TOP.getDefaultState(), 3);
            world.updateNeighbors(pos, Blocks.AIR);
            state.updateNeighbors(world, pos, 3);
        }
    }

    @Override
    public VoxelShape getCollisionShape(BlockState p_220071_1_, IBlockReader p_220071_2_, BlockPos p_220071_3_, ISelectionContext p_220071_4_) {
        return VoxelShapes.empty(); //TODO: better shape
    }
}
