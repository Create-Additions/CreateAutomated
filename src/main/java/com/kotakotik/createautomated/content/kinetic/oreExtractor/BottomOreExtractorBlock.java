package com.kotakotik.createautomated.content.kinetic.oreExtractor;

import com.kotakotik.createautomated.content.base.IOreExtractorBlock;
import com.kotakotik.createautomated.register.ModBlocks;
import com.kotakotik.createautomated.register.ModTiles;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.stream.Stream;

public class BottomOreExtractorBlock extends Block implements IOreExtractorBlock {
	public static final VoxelShape shape = Stream.of(
			Block.makeCuboidShape(0, 0, 0, 3, 16, 3),
			Block.makeCuboidShape(0, 0, 13, 3, 16, 16),
			Block.makeCuboidShape(13, 0, 13, 16, 16, 16),
			Block.makeCuboidShape(13, 0, 0, 16, 16, 3)
	).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get();

	public BottomOreExtractorBlock(Properties p_i48440_1_) {
		super(p_i48440_1_);
		setDefaultState(getDefaultState().with(BlockStateProperties.POWERED, false));
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
		state = checkForOther(state, direction, updatingState, world, pos, updatingPos, false);
		if (state.isAir(world, pos)) {
			return state;
		}
		return super.updatePostPlacement(state, direction, updatingState, world, pos, updatingPos);
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> b) {
		b.add(BlockStateProperties.POWERED);
		super.fillStateContainer(b);
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
		return shape;
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return ModTiles.BOTTOM_ORE_EXTRACTOR.create();
	}

	@Override
	public ActionResultType onUse(BlockState state, World world, BlockPos pos, PlayerEntity plr, Hand hand, BlockRayTraceResult rayTraceResult) {
		// redirect to top block's onUse method
		BlockState upState = world.getBlockState(pos.up());
		return upState.getBlock().onUse(upState, world, pos.up(), plr, hand, rayTraceResult);
	}

	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
		return new ItemStack(ModBlocks.ORE_EXTRACTOR_TOP.get().asItem());
	}

	@Override
	public void onBlockHarvested(World world, BlockPos pos, BlockState state, PlayerEntity plr) {
		Direction d = IOreExtractorBlock.getDirectionToOther(false);
		BlockPos updatingPos = pos.offset(d);
		checkForOther(state, d, world.getBlockState(updatingPos), world, pos, updatingPos, !plr.isCreative());
		super.onBlockHarvested(world, pos, state, plr);
	}

	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		if (!worldIn.isRemote) {
			boolean previouslyPowered = state.get(BlockStateProperties.POWERED);
			if (previouslyPowered != worldIn.isBlockPowered(pos)) {
				worldIn.setBlockState(pos, state.cycle(BlockStateProperties.POWERED), 2);
			}

		}
		super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
	}
}
