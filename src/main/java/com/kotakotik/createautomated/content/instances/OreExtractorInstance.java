package com.kotakotik.createautomated.content.instances;

import com.kotakotik.createautomated.content.tiles.OreExtractorTile;
import com.kotakotik.createautomated.register.ModBlockPartials;
import com.simibubi.create.content.contraptions.base.RotatingData;
import com.simibubi.create.foundation.render.backend.instancing.IDynamicInstance;
import com.simibubi.create.foundation.render.backend.instancing.InstancedTileRenderer;
import net.minecraft.util.Direction;

public class OreExtractorInstance extends CogInstance implements IDynamicInstance {
    public RotatingData drill;

    public OreExtractorTile getTile() {
        return (OreExtractorTile) tile;
    }

    public OreExtractorInstance(InstancedTileRenderer<?> modelManager, OreExtractorTile tile) {
        super(modelManager, tile);

        createDrill();
    }

    public RotatingData createDrill() {
        drill = getRotatingMaterial().getModel(ModBlockPartials.DRILL_ORE_EXTRACTOR, blockState).createInstance();
        drill.setRotationAxis(Direction.Axis.Y);
        drill.setPosition(getInstancePosition().down());
        return drill;
    }

    boolean drillRemoved = false;

    @Override
    public void beginFrame() {

    }

    @Override
    public void update() {
        drill.setRotationalSpeed(tile.getSpeed());
        drill.setRotationAxis(super.getRotationAxis());
        if (getTile().durability <= 0) {
            if (!drillRemoved) {
                drill.delete();
            }
            drillRemoved = true;
        } else {
            if (drillRemoved) {
                createDrill();
            }
            drillRemoved = false;
        }
        super.update();
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
