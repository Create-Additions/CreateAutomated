package com.kotakotik.createautomated.content.processing.spongeFrame;

import com.kotakotik.createautomated.api.ISpongeFrame;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.contraptions.processing.InWorldProcessing;
import com.simibubi.create.foundation.utility.VoxelShaper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class SpongeFrameBlock extends DirectionalBlock implements ISpongeFrame {
	public static VoxelShaper shape = new AllShapes.Builder(Block.box(0, 5, 0, 16, 10, 16)).forDirectional();

	@Nullable
	public final InWorldProcessing.Type type;

	public SpongeFrameBlock(Properties p_i48415_1_, @Nullable InWorldProcessing.Type type) {
		super(p_i48415_1_);
		this.type = type;
		registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH));
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader blockReader, BlockPos pos, ISelectionContext ctx) {
		return shape.get(state.getValue(FACING));
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> b) {
		b.add(FACING);
		super.createBlockStateDefinition(b);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext ctx) {
		return super.getStateForPlacement(ctx).setValue(FACING, ctx.getNearestLookingDirection().getOpposite());
	}

	@Nullable
	@Override
	public InWorldProcessing.Type getType() {
		return type;
	}
}
