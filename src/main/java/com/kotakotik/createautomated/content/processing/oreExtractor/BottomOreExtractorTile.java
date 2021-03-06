package com.kotakotik.createautomated.content.processing.oreExtractor;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;

public class BottomOreExtractorTile extends TileEntity {
	public BottomOreExtractorTile(TileEntityType<?> p_i48289_1_) {
		super(p_i48289_1_);
	}

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, Direction side) {
		TileEntity tile = level.getBlockEntity(worldPosition.above());
		if (tile != null) return tile.getCapability(cap, side);
		return super.getCapability(cap, side);
	}
}
