package com.kotakotik.createautomated.register.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class ModServerConfig {
	protected static ForgeConfigSpec.Builder BUILDER_SERVER;

	public static ForgeConfigSpec.BooleanValue armCanInsertDrills;
	public static ForgeConfigSpec.BooleanValue armCanExtractOrePieces;

	public static void register() {
		BUILDER_SERVER = new ForgeConfigSpec.Builder();

		BUILDER_SERVER.push("machines");
		machines();
		BUILDER_SERVER.pop();

		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, BUILDER_SERVER.build());
	}

	protected static void machines() {
		BUILDER_SERVER.push("ore_extractor");
		extractor();
		BUILDER_SERVER.pop();
	}

	protected static void extractor() {
		armCanInsertDrills = BUILDER_SERVER
				.comment("Whether or not mechanical arms can insert drills into ore extractors")
				.define("canArmInsertDrills", true);

		armCanExtractOrePieces = BUILDER_SERVER
				.comment("Whether or not mechanical arms can extract ore pieces into ore extractors")
				.define("canArmExtractOrePieces", false);
	}
}
