package com.kotakotik.createautomated.register;

import com.jozufozu.flywheel.core.PartialModel;
import com.kotakotik.createautomated.CreateAutomated;
import com.simibubi.create.Create;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public abstract class ModBlockPartials {
	public static PartialModel COGWHEEL = getCreate("cogwheel");
	public static PartialModel HALF_SHAFT_COGWHEEL = get("half_shaft_cogwheel");
	public static PartialModel DRILL_ORE_EXTRACTOR = get("ore_extractor/drill");

	public static PartialModel get(String name) {
		return new PartialModel(new ResourceLocation(CreateAutomated.modid, "block/" + name));
	}

	public static PartialModel getCreate(String name) {
		return new PartialModel(Create.asResource("block/" + name));
	}

	public static void register(FMLClientSetupEvent event) {
		load();
	}

	public static void load() {

	}
}
