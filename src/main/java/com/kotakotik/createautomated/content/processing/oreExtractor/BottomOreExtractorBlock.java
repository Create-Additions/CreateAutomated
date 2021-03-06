package com.kotakotik.createautomated.content.processing.oreExtractor;

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
import net.minecraft.item.ItemUseContext;
import net.minecraft.loot.LootContext;
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
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class BottomOreExtractorBlock extends Block implements IOreExtractorBlock {
	public static final VoxelShape shape = Stream.of(
			Block.box(0, 0, 0, 3, 16, 3),
			Block.box(0, 0, 13, 3, 16, 16),
			Block.box(13, 0, 13, 16, 16, 16),
			Block.box(13, 0, 0, 16, 16, 3)
	).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).get();

	public BottomOreExtractorBlock(Properties p_i48440_1_) {
		super(p_i48440_1_);
		registerDefaultState(defaultBlockState().setValue(BlockStateProperties.POWERED, false));
	}

	@Override
	public boolean isTop() {
		return false;
	}

	@Override
	public PushReaction getPistonPushReaction(BlockState state) {
		return pushReaction(state);
	}

	@Override
	public BlockState updateShape(BlockState state, Direction direction, BlockState updatingState, IWorld world, BlockPos pos, BlockPos updatingPos) {
		state = checkForOther(state, direction, updatingState, world, pos, updatingPos, false);
		if (state.isAir(world, pos)) {
			return state;
		}
		return super.updateShape(state, direction, updatingState, world, pos, updatingPos);
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> b) {
		b.add(BlockStateProperties.POWERED);
		super.createBlockStateDefinition(b);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext ctx) {
		BlockPos bottom = ctx.getClickedPos();
		BlockPos top = bottom.above();
		return ctx.getLevel().getBlockState(top).canBeReplaced(ctx) ? super.getStateForPlacement(ctx) : null;
	}

	@Override
	public void setPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		super.setPlacedBy(world, pos, state, placer, stack);
		if (!world.isClientSide) {
			BlockPos top = pos.above();
			world.setBlock(top, ModBlocks.ORE_EXTRACTOR_TOP.getDefaultState(), 3);
			world.blockUpdated(pos, Blocks.AIR);
			state.updateNeighbourShapes(world, pos, 3);
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
	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity plr, Hand hand, BlockRayTraceResult rayTraceResult) {
		// redirect to top block's onUse method
		BlockState upState = world.getBlockState(pos.above());
		return upState.getBlock().use(upState, world, pos.above(), plr, hand, rayTraceResult);
	}

	public TopOreExtractorBlock getTop() {
		return ModBlocks.ORE_EXTRACTOR_TOP.get();
	}

	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
		return getTop().getPickBlock(state, target, world, pos.above(), player);
	}

	@Override
	public ActionResultType onSneakWrenched(BlockState state, ItemUseContext context) {
		World world = context.getLevel();
		BlockPos pos = context.getClickedPos();
		PlayerEntity player = context.getPlayer();
		if (world instanceof ServerWorld) {
			Block.getDrops(world.getBlockState(pos.above()), (ServerWorld) world, pos.above(), world.getBlockEntity(pos.above()), player, context.getItemInHand()).forEach((stack) -> {
				player.inventory.placeItemBackInInventory(world, fillStackNbt(stack, world, pos));
			});

			state.spawnAfterBreak((ServerWorld) world, pos, ItemStack.EMPTY);
			world.destroyBlock(pos, false);
			this.playRemoveSound(world, pos);
		}

		return ActionResultType.SUCCESS;
	}

	public ItemStack fillStackNbt(ItemStack stack, World world, BlockPos pos) {
		if (stack.getItem().getRegistryName().equals(ModBlocks.ORE_EXTRACTOR_TOP.getId())) {
			stack.setTag(ModBlocks.ORE_EXTRACTOR_TOP.get().fillDropNbt(stack.getOrCreateTag(), world, pos.above()));
		}
		return stack;
	}

	@Override
	public void playerWillDestroy(World world, BlockPos pos, BlockState state, PlayerEntity plr) {
		Direction d = IOreExtractorBlock.getDirectionToOther(false);
		BlockPos updatingPos = pos.relative(d);
		checkForOther(state, d, world.getBlockState(updatingPos), world, pos, updatingPos, !plr.isCreative());
		if (!plr.isCreative() && world instanceof ServerWorld) {
			BlockPos pos2 = pos.above();
			BlockState state2 = world.getBlockState(pos2);
			TileEntity tile = world.getBlockEntity(pos2);
			Block.getDrops(state2, (ServerWorld) world, pos2, tile).forEach(s -> {
				popResource(world, pos, fillStackNbt(s, world, pos));
			});
			// 36 56 71
		}
		super.playerWillDestroy(world, pos, state, plr);
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder loot) {
		return new ArrayList<>();
	}

	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		if (!worldIn.isClientSide) {
			boolean previouslyPowered = state.getValue(BlockStateProperties.POWERED);
			if (previouslyPowered != worldIn.hasNeighborSignal(pos)) {
				worldIn.setBlock(pos, state.cycle(BlockStateProperties.POWERED), 2);
			}

		}
		super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
	}
}
