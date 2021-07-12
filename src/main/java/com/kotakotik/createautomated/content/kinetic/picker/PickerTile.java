package com.kotakotik.createautomated.content.kinetic.picker;

import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import net.minecraft.tileentity.TileEntityType;

public class PickerTile extends KineticTileEntity {
	public PickerTile(TileEntityType<?> typeIn) {
		super(typeIn);
	}

	public static int getDefaultStress() {
		return 8;
	}
}
