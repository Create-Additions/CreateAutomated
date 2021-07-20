package com.kotakotik.createautomated.register.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class ModServerConfig extends com.kotakotik.createautomated.register.config.ModConfig.Config {
	protected static ForgeConfigSpec.Builder BUILDER_SERVER;
	public static ForgeConfigSpec SPEC;

	public static ForgeConfigSpec.BooleanValue armCanInsertDrills;
	public static ForgeConfigSpec.BooleanValue armCanExtractOrePieces;
	public static ForgeConfigSpec.BooleanValue allowInsertDrills;
	public static ForgeConfigSpec.BooleanValue allowExtractOrePieces;
	public static ForgeConfigSpec.BooleanValue extractorAllowToggleRedstone;
	public static ForgeConfigSpec.BooleanValue allowBreakOres;
	public static ForgeConfigSpec.BooleanValue allowBreakBlocks;

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
	}

	protected void extractor() {
		armCanInsertDrills = BUILDER_SERVER
				.comment("Whether or not mechanical arms can insert drills into ore extractors")
				.define("canArmInsertDrills", true);

		armCanExtractOrePieces = BUILDER_SERVER
				.comment("Whether or not mechanical arms can extract ore pieces from ore extractors")
				.define("canArmExtractOrePieces", false);

		allowInsertDrills = BUILDER_SERVER
				.comment("Whether or not things like hoppers and funnels can insert drills into ore extractors")
				.define("allowInsertDrills", true);

		allowExtractOrePieces = BUILDER_SERVER
				.comment("Whether or not things like hoppers and funnels can extract ore pieces from ore extractors")
				.define("allowExtractOrePieces", true);

		extractorAllowToggleRedstone = BUILDER_SERVER
				.comment("Whether or not the extractor can be toggled with redstone")
				.define("allowToggleRedstone", true);

		allowBreakOres = BUILDER_SERVER
				.comment("Whether or not the extractor can break ores")
				.define("allowBreakOres", false);

		allowBreakBlocks = BUILDER_SERVER
				.comment("Whether or not the extractor can break any block")
				.define("allowBreakBlocks", false);
	}
}
