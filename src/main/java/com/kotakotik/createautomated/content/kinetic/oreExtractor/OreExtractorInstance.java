package com.kotakotik.createautomated.content.kinetic.oreExtractor;

import com.kotakotik.createautomated.content.base.instances.CogInstance;
import com.kotakotik.createautomated.register.ModBlockPartials;
import com.simibubi.create.content.contraptions.base.KineticData;
import com.simibubi.create.content.contraptions.base.RotatingData;
import com.simibubi.create.foundation.render.backend.instancing.IDynamicInstance;
import com.simibubi.create.foundation.render.backend.instancing.InstanceData;
import com.simibubi.create.foundation.render.backend.instancing.InstancedTileRenderer;
import net.minecraft.util.Direction;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.Field;

public class OreExtractorInstance extends CogInstance implements IDynamicInstance {
    public static final Field isRemovedField = ObfuscationReflectionHelper.findField(InstanceData.class, "removed");
    public static final Field rotationOffsetField = ObfuscationReflectionHelper.findField(KineticData.class, "rotationOffset");

    public RotatingData drill;

    public OreExtractorTile getTile() {
        return (OreExtractorTile) tile;
    }

    public OreExtractorInstance(InstancedTileRenderer<?> modelManager, OreExtractorTile tile) {
        super(modelManager, tile);

//        createDrill();
    }

    public RotatingData createDrill() {
        drill = getRotatingMaterial().getModel(ModBlockPartials.DRILL_ORE_EXTRACTOR, blockState).createInstance();
        drill.setRotationAxis(Direction.Axis.Y);
        updateDrillRotation();
        drill.setPosition(getInstancePosition().down());
        return drill;
    }

    public void updateDrillRotation() {
        drill.setRotationalSpeed(tile.getSpeed());
        try {
            drill.setRotationOffset((Float) rotationOffsetField.get(rotatingModel));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        drill.setRotationAxis(super.getRotationAxis());
    }

    @Override
    public void beginFrame() {

    }

    public boolean isDrillRemoved() {
        if (drill == null) return true;
        try {
            return (boolean) isRemovedField.get(drill);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void update() {
        if (getTile().durability <= 0) {
            if (!isDrillRemoved()) {
                drill.delete();
            }
        } else {
            if (isDrillRemoved()) {
                createDrill();
            }
        }
        if (!isDrillRemoved()) {
            updateDrillRotation();
            updateLight();
        }
        super.update();
    }

    @Override
    public void updateLight() {
        super.updateLight();
        if (!isDrillRemoved()) {
            relight(this.pos.down(), drill);
        }
    }

    @Override
    public void remove() {
        super.remove();
        if (drill != null) drill.delete();
    }
}
