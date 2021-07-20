package com.kotakotik.createautomated.register.config;

import com.kotakotik.createautomated.content.worldgen.WorldGen;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ModCommonConfig extends ModConfig.Config {
	public static final HashMap<String, ForgeConfigSpec.IntValue> worldGenVeinSizes = new HashMap<>();
	public static final HashMap<String, ForgeConfigSpec.IntValue> worldGenMinHeights = new HashMap<>();
	public static final HashMap<String, ForgeConfigSpec.IntValue> worldGenMaxHeights = new HashMap<>();
	public static final HashMap<String, ForgeConfigSpec.IntValue> worldGenFrequencies = new HashMap<>();
	public static final HashMap<String, ForgeConfigSpec.BooleanValue> worldGenEnabled = new HashMap<>();
	protected static final List<WorldGen.FeatureToRegister> worldGenFeatures = new ArrayList<>();
	protected static ForgeConfigSpec.Builder BUILDER_COMMON;
	public static ForgeConfigSpec SPEC;

	@Override
	protected ForgeConfigSpec.Builder getBuilder() {
		return BUILDER_COMMON;
	}

	public static void register() {
		new ModCommonConfig().reg();
	}

	public void reg() {
		BUILDER_COMMON = new ForgeConfigSpec.Builder();

		startCategory("worldgen", this::worldGen);

		SPEC = BUILDER_COMMON.build();
		ModLoadingContext.get().registerConfig(net.minecraftforge.fml.config.ModConfig.Type.COMMON, SPEC);
	}

	protected void worldGen() {
		worldGenFeatures.forEach(f -> {
			String b = f.name;
			startCategory(b, () -> {
				worldGenVeinSizes.put(b, BUILDER_COMMON.defineInRange("veinSize", f.veinSize, 1, Integer.MAX_VALUE));
				worldGenMinHeights.put(b, BUILDER_COMMON.defineInRange("minHeight", f.minHeight, 0, Integer.MAX_VALUE));
				worldGenMaxHeights.put(b, BUILDER_COMMON.defineInRange("maxHeight", f.maxHeight, 1, Integer.MAX_VALUE));
				worldGenFrequencies.put(b, BUILDER_COMMON.defineInRange("frequency", f.frequency, 0, Integer.MAX_VALUE));
				worldGenEnabled.put(b, BUILDER_COMMON.define("enabled", true));
			});
		});
	}

	public static void addWorldGen(WorldGen.FeatureToRegister f) {
		worldGenFeatures.add(f);
	}
}
