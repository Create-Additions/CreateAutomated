package com.kotakotik.createautomated.content.base.instances;

import com.jozufozu.flywheel.backend.instancing.Instancer;
import com.jozufozu.flywheel.backend.material.MaterialManager;
import com.kotakotik.createautomated.register.ModBlockPartials;
import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import com.simibubi.create.content.contraptions.base.RotatingData;
import com.simibubi.create.content.contraptions.base.ShaftlessCogInstance;
import com.simibubi.create.foundation.render.AllMaterialSpecs;

public class CogInstance extends ShaftlessCogInstance {
	public CogInstance(MaterialManager<?> modelManager, KineticTileEntity tile) {
		super(modelManager, tile);
	}

	@Override
	public Instancer<RotatingData> getModel() {
		return this.materialManager.getMaterial(AllMaterialSpecs.ROTATING).getModel(ModBlockPartials.COGWHEEL, tile.getBlockState());
	}
}
