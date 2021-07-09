package com.kotakotik.createautomated.content.blocks;

import com.kotakotik.createautomated.content.tiles.KineticFurnaceTile;
import com.kotakotik.createautomated.register.ModTiles;
import com.simibubi.create.content.contraptions.base.HorizontalKineticBlock;
import com.simibubi.create.content.contraptions.components.press.MechanicalPressBlock;
import com.simibubi.create.content.contraptions.components.press.MechanicalPressTileEntity;
import com.simibubi.create.foundation.block.ITE;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;

public class KineticFurnaceBlock extends HorizontalKineticBlock implements ITE<KineticFurnaceTile> {
    public KineticFurnaceBlock(Properties properties) {
        super(properties);
    }

    @Override
    public TileEntity createTileEntity(BlockState blockState, IBlockReader iBlockReader) {
        return ModTiles.KINETIC_FURNACE.create();
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState blockState) {
        return blockState.get(HORIZONTAL_FACING).getAxis();
    }

    @Override
    public boolean hasShaftTowards(IWorldReader world, BlockPos pos, BlockState state, Direction face) {
        return state.get(HORIZONTAL_FACING) == face.getOpposite();
    }

    @Override
    public Class<KineticFurnaceTile> getTileEntityClass() {
        return KineticFurnaceTile.class;
    }
}
