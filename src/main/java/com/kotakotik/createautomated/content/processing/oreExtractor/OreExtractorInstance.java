package com.kotakotik.createautomated.content.processing.oreExtractor;

import com.jozufozu.flywheel.backend.instancing.IDynamicInstance;
import com.jozufozu.flywheel.backend.instancing.InstanceData;
import com.jozufozu.flywheel.backend.instancing.MaterialManager;
import com.kotakotik.createautomated.api.DrillPartialIndex;
import com.kotakotik.createautomated.content.base.instances.CogInstance;
import com.simibubi.create.content.contraptions.base.KineticData;
import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import com.simibubi.create.content.contraptions.base.RotatingData;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.Field;

public class OreExtractorInstance extends CogInstance implements IDynamicInstance {
	public static final Field isRemovedField = ObfuscationReflectionHelper.findField(InstanceData.class, "removed");
	public static final Field rotationOffsetField = ObfuscationReflectionHelper.findField(KineticData.class, "rotationOffset");

	public RotatingData drill;

	public OreExtractorInstance(MaterialManager<?> modelManager, KineticTileEntity tile) {
		super(modelManager, tile);

		if (getTile().durability > 0) {
			createDrill();
		}
	}

	public OreExtractorTile getTile() {
		return (OreExtractorTile) tile;
	}

	public RotatingData createDrill() {
		drill = getRotatingMaterial().getModel(DrillPartialIndex.get(getTile().drillId), blockState).createInstance();
		drill.setRotationAxis(Direction.Axis.Y);
		updateDrillRotation();
		drill.setPosition(getInstancePosition().below());
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
			BlockPos p = getInstancePosition();
			drill.setPosition(p.getX(), p.getY() - 1 + getTile().drillPos, p.getZ());
		}
		super.update();
	}

	@Override
	public void updateLight() {
		super.updateLight();
		if (!isDrillRemoved()) {
			relight(this.pos.below(), drill);
		}
	}

	@Override
	public void remove() {
		super.remove();
		if (drill != null) drill.delete();
	}
}
