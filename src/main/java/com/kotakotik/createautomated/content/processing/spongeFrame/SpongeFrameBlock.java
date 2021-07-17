package com.kotakotik.createautomated.content.processing.spongeFrame;

import com.kotakotik.createautomated.api.ISpongeFrame;
import com.simibubi.create.content.contraptions.processing.InWorldProcessing;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;

import javax.annotation.Nullable;

public class SpongeFrameBlock extends DirectionalBlock implements ISpongeFrame {
	@Nullable
	public final InWorldProcessing.Type type;

	public SpongeFrameBlock(Properties p_i48415_1_, @Nullable InWorldProcessing.Type type) {
		super(p_i48415_1_);
		this.type = type;
		registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH));
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> b) {
		b.add(FACING);
		super.createBlockStateDefinition(b);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext ctx) {
		return super.getStateForPlacement(ctx).setValue(FACING, ctx.getNearestLookingDirection());
	}

	@Nullable
	@Override
	public InWorldProcessing.Type getType() {
		return type;
	}
}
