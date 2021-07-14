package com.kotakotik.createautomated.content.kinetic.oreExtractor;

import com.kotakotik.createautomated.content.base.IDrillHead;
import com.kotakotik.createautomated.content.base.IOreExtractorBlock;
import com.kotakotik.createautomated.content.simple.drillHead.DrillHeadItem;
import com.kotakotik.createautomated.register.ModTiles;
import com.simibubi.create.content.contraptions.base.KineticBlock;
import com.simibubi.create.content.contraptions.relays.elementary.ICogWheel;
import com.simibubi.create.foundation.block.ITE;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
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
		setDefaultState(getDefaultState().with(BlockStateProperties.POWERED, false));
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
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> b) {
		b.add(BlockStateProperties.POWERED);
		super.fillStateContainer(b);
	}

	@Override
	public SpeedLevel getMinimumRequiredSpeedLevel() {
		return SpeedLevel.FAST;
	}

	@Override
	public Class<OreExtractorTile> getTileEntityClass() {
		return OreExtractorTile.class;
	}

	@Override
	public PushReaction getPushReaction(BlockState state) {
		return pushReaction(state);
//        return super.getPushReaction(p_149656_1_);
//        return PushReaction.BLOCK;
	}

	@Override
	public boolean isTop() {
		return true;
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
	public ActionResultType onUse(BlockState state, World world, BlockPos pos, PlayerEntity plr, Hand hand, BlockRayTraceResult rayTraceResult) {
		if (plr.getHeldItem(hand).getItem() instanceof IDrillHead) {
			IDrillHead drill = (IDrillHead) plr.getHeldItem(hand).getItem();
			OreExtractorTile tile = ((OreExtractorTile) world.getTileEntity(pos));
			if (!(plr instanceof FakePlayer) || tile.durability == 0) { // if fakeplayer, only use when durability is ran out, like the blaze burner
				tile.setDrill(drill, plr.getHeldItem(hand).getItem().getRegistryName());
				if (!plr.isCreative()) {
					plr.getHeldItem(hand).shrink(1);
				}
				tile.sendData();
				return ActionResultType.SUCCESS; // dunno if i should use CONSUME but iirc it disables the animation and i dont want that
			}
		}
		return super.onUse(state, world, pos, plr, hand, rayTraceResult);
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
