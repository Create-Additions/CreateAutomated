package com.kotakotik.createautomated.content.blocks.oreextractor;

import com.kotakotik.createautomated.content.base.IOreExtractorBlock;
import com.kotakotik.createautomated.content.tiles.OreExtractorTile;
import com.kotakotik.createautomated.register.ModTiles;
import com.simibubi.create.content.contraptions.base.HorizontalKineticBlock;
import com.simibubi.create.content.contraptions.relays.elementary.ICogWheel;
import com.simibubi.create.foundation.block.ITE;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.PushReaction;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;

import java.util.ArrayList;
import java.util.List;

public class TopOreExtractorBlock extends HorizontalKineticBlock implements ICogWheel, ITE<OreExtractorTile>, IOreExtractorBlock {
    public TopOreExtractorBlock(Properties properties) {
        super(properties);
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
        state = checkForOther(state, direction, updatingState, world, pos, updatingPos);
        if (state.isAir(world, pos)) {
            return state;
        }
        return super.updatePostPlacement(state, direction, updatingState, world, pos, updatingPos);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder loot) {
        // TODO: breaking the top part in creative still drops it. shut i know why i just dont know how to fix it
        return new ArrayList<>();
    }
}
