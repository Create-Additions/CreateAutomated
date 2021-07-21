package com.kotakotik.createautomated.register.config;

import com.kotakotik.createautomated.content.worldgen.WorldGen.FeatureToRegister;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ModCommonConfig extends ModConfig.Config {
	public static final HashMap<String, ConfigInt> worldGenVeinSizes = new HashMap<String, ConfigInt>();
	public static final HashMap<String, ConfigInt> worldGenMinHeights = new HashMap<String, ConfigInt>();
	public static final HashMap<String, ConfigInt> worldGenMaxHeights = new HashMap<String, ConfigInt>();
	public static final HashMap<String, ConfigInt> worldGenFrequencies = new HashMap<String, ConfigInt>();
	public static final HashMap<String, ConfigBool> worldGenEnabled = new HashMap<String, ConfigBool>();
	protected static final List<FeatureToRegister> worldGenFeatures = new ArrayList<>();

	public static class WorldGen extends ModConfig.Config {
		public ArrayList<WorldGenFeature> features = new ArrayList<>();

		public WorldGen() {
			for (FeatureToRegister f : worldGenFeatures) {
				features.add(nested(1, () -> new WorldGenFeature(f)));
			}
		}

		public static class WorldGenFeature extends ModConfig.Config {
			public final FeatureToRegister feature;

			@Override
			public String getName() {
				return feature.name;
			}

			public WorldGenFeature(FeatureToRegister f) {
				this.feature = f;
				veinSize = i(feature.veinSize, 1, "veinSize");
				minHeight = i(feature.minHeight, 0, "minHeight");
				maxHeight = i(feature.maxHeight, 1, "maxHeight");
				frequency = i(feature.frequency, 0, "frequency");
				enabled = b(true, "enabled");
				worldGenVeinSizes.put(f.name, veinSize);
				worldGenMinHeights.put(f.name, minHeight);
				worldGenMaxHeights.put(f.name, maxHeight);
				worldGenFrequencies.put(f.name, frequency);
				worldGenEnabled.put(f.name, enabled);
			}

			public ConfigInt veinSize;
			public ConfigInt minHeight;
			public ConfigInt maxHeight;
			public ConfigInt frequency;
			public ConfigBool enabled;
		}
	}

	public WorldGen worldGen = nested(0, WorldGen::new);

	public static void addWorldGen(FeatureToRegister f) {
		worldGenFeatures.add(f);
	}
}
