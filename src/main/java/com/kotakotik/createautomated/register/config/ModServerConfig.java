package com.kotakotik.createautomated.register.config;

import com.kotakotik.createautomated.content.processing.oreExtractor.OreExtractorTile;
import com.simibubi.create.content.contraptions.base.IRotate;

public class ModServerConfig extends com.kotakotik.createautomated.register.config.ModConfig.Config {
	public static class Extractor extends com.kotakotik.createautomated.register.config.ModConfig.Config {
		public ConfigBool armCanInsertDrills = b(true, "canArmInsertDrills", "Whether or not mechanical arms can insert drills into ore extractors");
		public ConfigBool armCanExtractOrePieces = b(false, "canArmExtractOrePieces", "Whether or not mechanical arms can extract ore pieces from ore extractors");
		public ConfigBool allowInsertDrills = b(true, "allowInsertDrills", "Whether or not things like hoppers and funnels can insert drills into ore extractors", "Note: This will also disable the ability to insert drills using mechanical arms");
		public ConfigBool allowExtractOrePieces = b(true, "allowExtractOrePieces", "Whether or not things like hoppers and funnels can extract ore pieces from ore extractors", "Note: This will also disable the ability to extract items using mechanical arms");
		public ConfigBool extractorAllowToggleRedstone = b(true, "allowToggleRedstone", "Whether or not the extractor can be toggled with redstone");
		public ConfigBool unbreakableDrills = b(false, "unbreakableDrills", "Makes drill heads not loose durability", "(why would you use this??)");
		public ConfigInt drillDurability = i(300, 0, "drillDurability", "The durability of the default drill head");
		public ConfigEnum<IRotate.SpeedLevel> requiredSpeed = e(IRotate.SpeedLevel.FAST, "requiredSpeed", "The speed requirement for the extractor. The exact number has to be configured in Create's config");
		public ConfigEnum<OreExtractorTile.MiningAbility> miningAbility = e(OreExtractorTile.MiningAbility.NONE, "miningAbility", "What types of blocks the extractor can mine. NONE if you want it to only be able to break blocks, ORES for ores only, and ANY for any block");
	}

	public static class Picker extends com.kotakotik.createautomated.register.config.ModConfig.Config {
		public ConfigInt useTime = i(16, 0, "useTime", "[in ticks]", "How long it takes to pick");
		public ConfigInt durability = i(32, 0, "durability", "How many items you can pick before the picker breaks");
	}

	public static class Machines extends com.kotakotik.createautomated.register.config.ModConfig.Config {
		public Picker picker = nested(1, Picker::new);
		public Extractor extractor = nested(1, Extractor::new);
	}

	public Machines machines = nested(0, Machines::new);
}
