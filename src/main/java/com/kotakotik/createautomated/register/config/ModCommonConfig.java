package com.kotakotik.createautomated.register.config;

import com.kotakotik.createautomated.content.worldgen.WorldGen;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ModCommonConfig {
	public static final HashMap<String, ForgeConfigSpec.IntValue> worldGenVeinSizes = new HashMap<>();
	public static final HashMap<String, ForgeConfigSpec.IntValue> worldGenMinHeights = new HashMap<>();
	public static final HashMap<String, ForgeConfigSpec.IntValue> worldGenMaxHeights = new HashMap<>();
	public static final HashMap<String, ForgeConfigSpec.IntValue> worldGenFrequencies = new HashMap<>();
	public static final HashMap<String, ForgeConfigSpec.BooleanValue> worldGenEnabled = new HashMap<>();
	protected static final List<WorldGen.FeatureToRegister> worldGenFeatures = new ArrayList<>();
	protected static ForgeConfigSpec.Builder BUILDER_COMMON;

	public static void register() {
		BUILDER_COMMON = new ForgeConfigSpec.Builder();

		BUILDER_COMMON.push("worldgen");
		worldGen();
		BUILDER_COMMON.pop();

		ModLoadingContext.get().registerConfig(net.minecraftforge.fml.config.ModConfig.Type.COMMON, BUILDER_COMMON.build());
	}

	protected static void worldGen() {
		worldGenFeatures.forEach(f -> {
			BUILDER_COMMON.push(f.name);
			String b = f.name;
			worldGenVeinSizes.put(b, BUILDER_COMMON.defineInRange("veinSize", f.veinSize, 1, Integer.MAX_VALUE));
			worldGenMinHeights.put(b, BUILDER_COMMON.defineInRange("minHeight", f.minHeight, 0, Integer.MAX_VALUE));
			worldGenMaxHeights.put(b, BUILDER_COMMON.defineInRange("maxHeight", f.maxHeight, 1, Integer.MAX_VALUE));
			worldGenFrequencies.put(b, BUILDER_COMMON.defineInRange("frequency", f.frequency, 0, Integer.MAX_VALUE));
			worldGenEnabled.put(b, BUILDER_COMMON.define("enabled", true));
			BUILDER_COMMON.pop();
		});
	}

	public static void addWorldGen(WorldGen.FeatureToRegister f) {
		worldGenFeatures.add(f);
	}
}
