package com.kotakotik.createautomated.content.instances;

import com.kotakotik.createautomated.content.tiles.OreExtractorTile;
import com.kotakotik.createautomated.register.ModBlockPartials;
import com.simibubi.create.content.contraptions.base.RotatingData;
import com.simibubi.create.foundation.render.backend.instancing.IDynamicInstance;
import com.simibubi.create.foundation.render.backend.instancing.InstancedTileRenderer;
import net.minecraft.util.Direction;

public class OreExtractorInstance extends HalfShaftCogInstance implements IDynamicInstance {
    public final RotatingData drill;

    public OreExtractorTile getTile() {
        return (OreExtractorTile) tile;
    }

    public OreExtractorInstance(InstancedTileRenderer<?> modelManager, OreExtractorTile tile) {
        super(modelManager, tile);

        drill = getRotatingMaterial().getModel(ModBlockPartials.DRILL_ORE_EXTRACTOR, blockState).createInstance();
        drill.setRotationAxis(Direction.Axis.Y);
    }

    @Override
    public void beginFrame() {
        drill.setRotationalSpeed(getTileSpeed());
        drill.setPosition(getInstancePosition());
    }

    @Override
    public void updateLight() {
        super.updateLight();
        relight(this.pos.down(), drill);
    }

    @Override
    public void remove() {
        super.remove();
        drill.delete();
    }
}
