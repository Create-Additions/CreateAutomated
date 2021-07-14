package com.kotakotik.createautomated.register.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class ModServerConfig extends com.kotakotik.createautomated.register.config.ModConfig.Config {
	protected static ForgeConfigSpec.Builder BUILDER_SERVER;

	public static ForgeConfigSpec.BooleanValue armCanInsertDrills;
	public static ForgeConfigSpec.BooleanValue armCanExtractOrePieces;
	public static ForgeConfigSpec.BooleanValue allowInsertDrills;
	public static ForgeConfigSpec.BooleanValue allowExtractOrePieces;

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

		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, BUILDER_SERVER.build());
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
	}
}
