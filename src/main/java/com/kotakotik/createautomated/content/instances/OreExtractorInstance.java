package com.kotakotik.createautomated.content.instances;

import com.kotakotik.createautomated.content.tiles.OreExtractorTile;
import com.kotakotik.createautomated.register.ModBlockPartials;
import com.simibubi.create.content.contraptions.base.RotatingData;
import com.simibubi.create.foundation.render.backend.instancing.IDynamicInstance;
import com.simibubi.create.foundation.render.backend.instancing.InstancedTileRenderer;
import net.minecraft.util.Direction;

public class OreExtractorInstance extends HalfShaftCogInstance implements IDynamicInstance {
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
        return drill;
    }

    boolean drillRemoved = false;

    @Override
    public void beginFrame() {
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
            drill.setRotationalSpeed(getTileSpeed());
            drill.setPosition(getInstancePosition());
        }
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
