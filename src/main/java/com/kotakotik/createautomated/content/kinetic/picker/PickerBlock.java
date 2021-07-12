package com.kotakotik.createautomated.content.kinetic.picker;

import com.kotakotik.createautomated.register.ModTiles;
import com.simibubi.create.content.contraptions.base.HorizontalKineticBlock;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.world.IBlockReader;

public class PickerBlock extends HorizontalKineticBlock {
	public PickerBlock(Properties properties) {
		super(properties);
	}

	@Override
	public TileEntity createTileEntity(BlockState blockState, IBlockReader iBlockReader) {
		return ModTiles.PICKER.create();
	}

	@Override
	public Direction.Axis getRotationAxis(BlockState blockState) {
		return blockState.get(HORIZONTAL_FACING).getAxis();
	}
}
