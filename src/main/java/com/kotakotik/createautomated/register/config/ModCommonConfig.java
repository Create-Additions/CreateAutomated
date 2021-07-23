package com.kotakotik.createautomated.register.config;


import net.minecraftforge.common.ForgeConfigSpec;

public class ModCommonConfig extends ModConfig.Config {
	public static class WorldGen extends ModConfig.Config {
		public ConfigBool enabled = b(true, "enabled", "Whether or not CreateAutomated config is enabled");

		@Override
		protected void registerAll(ForgeConfigSpec.Builder builder) {
			super.registerAll(builder);
			com.kotakotik.createautomated.content.worldgen.WorldGen.entries.values().forEach((entry) -> {
				builder.push(entry.id);
				entry.addToConfig(builder);
				builder.pop();
			});
		}
	}

	public WorldGen worldGen = nested(0, WorldGen::new);
}
