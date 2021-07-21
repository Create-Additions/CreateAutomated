package com.kotakotik.createautomated.content.processing.oreExtractor;

import com.jozufozu.flywheel.backend.instancing.IDynamicInstance;
import com.jozufozu.flywheel.backend.instancing.InstanceData;
import com.jozufozu.flywheel.backend.instancing.Instancer;
import com.jozufozu.flywheel.backend.instancing.MaterialManager;
import com.kotakotik.createautomated.api.DrillPartialIndex;
import com.kotakotik.createautomated.register.ModBlockPartials;
import com.simibubi.create.content.contraptions.base.KineticData;
import com.simibubi.create.content.contraptions.base.KineticTileInstance;
import com.simibubi.create.content.contraptions.base.RotatingData;
import com.simibubi.create.foundation.render.AllMaterialSpecs;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.Field;

public class OreExtractorInstance extends KineticTileInstance<OreExtractorTile> implements IDynamicInstance {
	public static final Field isRemovedField = ObfuscationReflectionHelper.findField(InstanceData.class, "removed");
	public static final Field rotationOffsetField = ObfuscationReflectionHelper.findField(KineticData.class, "rotationOffset");

	public RotatingData drill;

	public Instancer<RotatingData> shortModel = materialManager.getMaterial(AllMaterialSpecs.ROTATING).getModel(ModBlockPartials.HALF_SHAFT_COGWHEEL, tile.getBlockState());
	public Instancer<RotatingData> model = materialManager.getMaterial(AllMaterialSpecs.ROTATING).getModel(ModBlockPartials.COGWHEEL, tile.getBlockState());

	protected RotatingData rotatingModel = this.setup(model.createInstance());

	protected BlockState getRenderedBlockState() {
		return this.blockState;
	}

	public OreExtractorInstance(MaterialManager<?> modelManager, OreExtractorTile tile) {
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
		rotatingModel.delete();
		rotatingModel = createShortCogwheel();
		rotatingModel.setPosition(getInstancePosition());
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

	public void deleteDrill() {
		drill.delete();
		rotatingModel.delete();
		rotatingModel = createCogwheel();
		rotatingModel.setPosition(getInstancePosition());
		updateLight();
	}

	public RotatingData createShortCogwheel() {
		return shortModel.createInstance();
	}

	public RotatingData createCogwheel() {
		return model.createInstance();
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
				deleteDrill();
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
		this.updateRotation(this.rotatingModel);
		super.update();
	}

	@Override
	public void updateLight() {
		super.updateLight();
		if (!isDrillRemoved()) {
			relight(this.pos.below(), drill);
		}
		relight(this.pos, this.rotatingModel);
	}

	@Override
	public void remove() {
		this.rotatingModel.delete();
		if (drill != null) drill.delete();
	}
}
