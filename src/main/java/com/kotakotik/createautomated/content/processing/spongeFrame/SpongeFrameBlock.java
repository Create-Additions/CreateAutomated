package com.kotakotik.createautomated.content.processing.spongeFrame;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class SpongeFrameBlock extends DirectionalBlock {
	public final boolean isWet;

	public SpongeFrameBlock(Properties p_i48415_1_, boolean isWet) {
		super(p_i48415_1_);
		this.isWet = isWet;
		setDefaultState(getDefaultState().with(FACING, Direction.NORTH));
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> b) {
		b.add(FACING);
		super.fillStateContainer(b);
	}

	@Override
	public ActionResultType onUse(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
		return super.onUse(p_225533_1_, p_225533_2_, p_225533_3_, p_225533_4_, p_225533_5_, p_225533_6_);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext ctx) {
		return super.getStateForPlacement(ctx).with(FACING, ctx.getNearestLookingDirection());
	}
}
