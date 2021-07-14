package com.kotakotik.createautomated.register.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.function.Consumer;

public class ModConfig {
	public static void reg() {
		ModCommonConfig.register();
		ModServerConfig.register();
	}

	abstract static class Config {
		abstract protected ForgeConfigSpec.Builder getBuilder();

		protected void startCategory(String name, Consumer<ForgeConfigSpec.Builder> cat) {
			ForgeConfigSpec.Builder b = getBuilder();
			b.push(name);
			cat.accept(b);
			b.pop();
		}

		protected void startCategory(String name, Runnable cat) {
			startCategory(name, ($) -> cat.run());
		}
	}
}
