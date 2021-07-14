package com.kotakotik.createautomated.api;

import com.jozufozu.flywheel.core.PartialModel;
import com.kotakotik.createautomated.register.ModBlockPartials;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;

public class DrillPartialIndex {
	protected static HashMap<ResourceLocation, PartialModel> index = new HashMap<>();

	public static PartialModel getDefault() {
		return ModBlockPartials.DRILL_ORE_EXTRACTOR;
	}

	public static PartialModel get(ResourceLocation id) {
		return index.getOrDefault(id, getDefault());
	}

	public static void add(ResourceLocation id, PartialModel model) {
		index.put(id, model);
	}
}
