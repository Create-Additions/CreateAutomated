package com.kotakotik.createautomated.content.worldgen;

import com.kotakotik.createautomated.CreateAutomated;
import com.simibubi.create.foundation.worldgen.ConfigDrivenFeatureEntry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.GenerationStage;
import net.minecraftforge.event.world.BiomeLoadingEvent;

import java.util.HashMap;

public class WorldGen {
	public static HashMap<String, ConfigDrivenFeatureEntry> entries = new HashMap<>();

	public static ConfigDrivenFeatureEntry register(String id, ConfigDrivenFeatureEntry entry) {
		entries.put(id, entry);
		return entry;
	}

	public static ConfigDrivenFeatureEntry register(ConfigDrivenFeatureEntry entry) {
		return register(entry.id, entry);
	}

	public static void register() {
	}

	public static void gen(BiomeLoadingEvent e) {
		entries.values().forEach((entry) -> {
			boolean shouldReg = true;
			if (entry instanceof DimensionalConfigDrivenFeatureEntry) {
				shouldReg = ((DimensionalConfigDrivenFeatureEntry) entry).shouldRegister(e);
			}
			if (shouldReg) {
				e.getGeneration().addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, entry.getFeature());
			}
		});
	}

	public static void reg() {
		entries.forEach((key, value) -> Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, CreateAutomated.MODID + ":" + key, value.getFeature()));
	}
}
