package com.kotakotik.createautomated.register.config;


import com.simibubi.create.foundation.worldgen.ConfigDrivenFeatureEntry;
import net.minecraftforge.common.ForgeConfigSpec;

public class ModCommonConfig extends ModConfig.Config {
	public static class WorldGen extends ModConfig.Config {
		public ConfigBool enabled = b(true, "enabled", "Whether or not CreateAutomated worldgen is enabled");

		@Override
		protected void registerAll(ForgeConfigSpec.Builder builder) {
			super.registerAll(builder);
			for (ConfigDrivenFeatureEntry entry : com.kotakotik.createautomated.content.worldgen.WorldGen.entries.values()) {
				builder.push(entry.id);
				entry.addToConfig(builder);
				builder.pop();
			}
		}
	}

	public WorldGen worldGen = nested(0, WorldGen::new);
}
