package com.kotakotik.createautomated.register.config;

import com.simibubi.create.content.contraptions.base.IRotate;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class ModServerConfig extends com.kotakotik.createautomated.register.config.ModConfig.Config {
	protected static ForgeConfigSpec.Builder BUILDER_SERVER;
	public static ForgeConfigSpec SPEC;

	public abstract static class Extractor {
		public static ForgeConfigSpec.BooleanValue armCanInsertDrills;
		public static ForgeConfigSpec.BooleanValue armCanExtractOrePieces;
		public static ForgeConfigSpec.BooleanValue allowInsertDrills;
		public static ForgeConfigSpec.BooleanValue allowExtractOrePieces;
		public static ForgeConfigSpec.BooleanValue extractorAllowToggleRedstone;
		public static ForgeConfigSpec.BooleanValue allowBreakOres;
		public static ForgeConfigSpec.BooleanValue allowBreakBlocks;
		public static ForgeConfigSpec.BooleanValue unbreakableDrills;
		public static ForgeConfigSpec.IntValue drillDurability;
		public static ForgeConfigSpec.EnumValue<IRotate.SpeedLevel> requiredSpeed;

		protected Extractor() {
		}
	}

	public abstract static class Picker {
		public static ForgeConfigSpec.IntValue useTime;
		public static ForgeConfigSpec.IntValue durability;

		protected Picker() {
		}
	}

	@Override
	protected ForgeConfigSpec.Builder getBuilder() {
		return BUILDER_SERVER;
	}

	public static void register() {
		new ModServerConfig().reg();
	}

	public void reg() {
		BUILDER_SERVER = new ForgeConfigSpec.Builder();

		startCategory("machines", this::machines);

		SPEC = BUILDER_SERVER.build();
		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SPEC);
	}

	protected void machines(ForgeConfigSpec.Builder builder) {
		startCategory("ore_extractor", this::extractor);
		startCategory("picker", this::picker);
	}

	protected void picker() {
		Picker.useTime = BUILDER_SERVER
				.comment("How long it takes to pick in ticks")
				.defineInRange("useTime", 16, 0, Integer.MAX_VALUE);

		Picker.durability = BUILDER_SERVER
				.comment("How many items you can pick before the picker breaks")
				.defineInRange("durability", 32, 0, Integer.MAX_VALUE);
	}

	protected void extractor() {
		Extractor.armCanInsertDrills = BUILDER_SERVER
				.comment("Whether or not mechanical arms can insert drills into ore extractors")
				.define("canArmInsertDrills", true);

		Extractor.armCanExtractOrePieces = BUILDER_SERVER
				.comment("Whether or not mechanical arms can extract ore pieces from ore extractors")
				.define("canArmExtractOrePieces", false);

		Extractor.allowInsertDrills = BUILDER_SERVER
				.comment("Whether or not things like hoppers and funnels can insert drills into ore extractors")
				.define("allowInsertDrills", true);

		Extractor.allowExtractOrePieces = BUILDER_SERVER
				.comment("Whether or not things like hoppers and funnels can extract ore pieces from ore extractors")
				.define("allowExtractOrePieces", true);

		Extractor.extractorAllowToggleRedstone = BUILDER_SERVER
				.comment("Whether or not the extractor can be toggled with redstone")
				.define("allowToggleRedstone", true);

		Extractor.allowBreakOres = BUILDER_SERVER
				.comment("Whether or not the extractor can break ores")
				.define("allowBreakOres", false);

		Extractor.allowBreakBlocks = BUILDER_SERVER
				.comment("Whether or not the extractor can break any block")
				.define("allowBreakBlocks", false);

		Extractor.drillDurability = BUILDER_SERVER
				.comment("The durability of the default drill head")
				.defineInRange("drillDurability", 200, 0, Integer.MAX_VALUE);

		Extractor.unbreakableDrills = BUILDER_SERVER
				.comment("Makes drill heads not loose durability", "(why would you use this??)")
				.define("unbreakableDrills", false);

		Extractor.requiredSpeed = BUILDER_SERVER
				.comment("The speed requirement for the extractor. The exact number has to be configured in Create's config: ",
						"Server/Gameplay -> Kinetics -> Stress Values -> Stats")
				.defineEnum("requiredSpeed", IRotate.SpeedLevel.FAST);
	}
}
