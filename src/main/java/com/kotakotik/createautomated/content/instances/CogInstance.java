package com.kotakotik.createautomated.content.instances;

import com.kotakotik.createautomated.register.ModBlockPartials;
import com.simibubi.create.content.contraptions.base.KineticRenderMaterials;
import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import com.simibubi.create.content.contraptions.base.RotatingData;
import com.simibubi.create.content.contraptions.base.ShaftlessCogInstance;
import com.simibubi.create.foundation.render.backend.instancing.InstancedModel;
import com.simibubi.create.foundation.render.backend.instancing.InstancedTileRenderer;

public class CogInstance extends ShaftlessCogInstance {
    public CogInstance(InstancedTileRenderer<?> modelManager, KineticTileEntity tile) {
        super(modelManager, tile);
    }

    @Override
    public InstancedModel<RotatingData> getModel() {
        return this.renderer.getMaterial(KineticRenderMaterials.ROTATING).getModel(ModBlockPartials.COGWHEEL, tile.getBlockState());
    }
}
