package com.kotakotik.createautomated.content.processing.oreExtractor;

import com.kotakotik.createautomated.api.IDrillHead;
import com.kotakotik.createautomated.content.base.IOreExtractorBlock;
import com.kotakotik.createautomated.register.ModTiles;
import com.kotakotik.createautomated.register.config.ModConfig;
import com.simibubi.create.content.contraptions.base.KineticBlock;
import com.simibubi.create.content.contraptions.relays.elementary.ICogWheel;
import com.simibubi.create.foundation.block.ITE;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;

public class TopOreExtractorBlock extends KineticBlock implements ICogWheel, ITE<OreExtractorTile>, IOreExtractorBlock {
	@Override
	public boolean isLargeCog() {
		return !isSmallCog();
	}

	@Override
	public boolean isSmallCog() {
		return true;
	}

	public TopOreExtractorBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(BlockStateProperties.POWERED, false));
	}

	@Override
	public Direction.Axis getRotationAxis(BlockState blockState) {
		return Direction.Axis.Y;
	}

	@Override
	public boolean hasShaftTowards(IWorldReader world, BlockPos pos, BlockState state, Direction face) {
		return face.equals(Direction.UP);
	}

	@Override
	public TileEntity createTileEntity(BlockState blockState, IBlockReader iBlockReader) {
		return ModTiles.ORE_EXTRACTOR.create();
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> b) {
		b.add(BlockStateProperties.POWERED);
		super.createBlockStateDefinition(b);
	}

	@Override
	public SpeedLevel getMinimumRequiredSpeedLevel() {
		return ModConfig.SERVER.machines.extractor.requiredSpeed.get();
	}

	@Override
	public Class<OreExtractorTile> getTileEntityClass() {
		return OreExtractorTile.class;
	}

	@Override
	public PushReaction getPistonPushReaction(BlockState state) {
		return pushReaction(state);
//        return super.getPushReaction(p_149656_1_);
//        return PushReaction.BLOCK;
	}

	@Override
	public boolean isTop() {
		return true;
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
	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity plr, Hand hand, BlockRayTraceResult rayTraceResult) {
		if (plr.getItemInHand(hand).getItem() instanceof IDrillHead) {
			IDrillHead drill = (IDrillHead) plr.getItemInHand(hand).getItem();
			OreExtractorTile tile = ((OreExtractorTile) world.getBlockEntity(pos));
			if (!(plr instanceof FakePlayer) || tile.durability == 0) { // if fakeplayer, only use when durability is ran out, like the blaze burner
				tile.setDrill(drill, plr.getItemInHand(hand).getItem().getRegistryName());
				if (!plr.isCreative()) {
					plr.getItemInHand(hand).shrink(1);
				}
				tile.sendData();
				return ActionResultType.SUCCESS; // dunno if i should use CONSUME but iirc it disables the animation and i dont want that
			}
		}
		return super.use(state, world, pos, plr, hand, rayTraceResult);
	}

	@Override
	public void playerWillDestroy(World world, BlockPos pos, BlockState state, PlayerEntity plr) {
		Direction d = IOreExtractorBlock.getDirectionToOther(false);
		BlockPos updatingPos = pos.relative(d);
		checkForOther(state, d, world.getBlockState(updatingPos), world, pos, updatingPos, !plr.isCreative());
		super.playerWillDestroy(world, pos, state, plr);
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

	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
		// pain moment
		ItemStack stack = new ItemStack(this);
		CompoundNBT nbt = stack.getOrCreateTag();
		CompoundNBT nbtTile = nbt.getCompound("BlockEntityTag");
		withTileEntityDo(world, pos, t -> {
			// i dont just do t.write(nbtTile, false) because then it would write all the data, i only want the data in nbtList to be written
			CompoundNBT tileNbt = new CompoundNBT();
			t.write(tileNbt, false);
			for (String nbtKey : nbtList()) {
				INBT value = tileNbt.get(nbtKey);
				if (value != null) {
					nbtTile.put(nbtKey, value);
				}
			}
		});
		nbt.put("BlockEntityTag", nbtTile);
		stack.setTag(nbt);
		return stack;
	}
}
