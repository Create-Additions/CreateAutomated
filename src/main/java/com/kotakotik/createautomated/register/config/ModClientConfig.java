package com.kotakotik.createautomated.register.config;

public class ModClientConfig extends ModConfig.Config {
	public static class Machines extends ModConfig.Config {
		public static class Extractor extends ModConfig.Config {
			public ConfigBool renderDrillInItem = b(true, "renderDrillInItem",
					"Whether or not to render the drill head on the item",
					"Turning this off can slightly improve performance");
		}

		public Extractor extractor = nested(1, Extractor::new);
	}

	public Machines machines = nested(0, Machines::new);
}
