package com.kotakotik.createautomated.register;

import com.kotakotik.createautomated.CreateAutomated;
import com.kotakotik.createautomated.content.ponder.OreExtractorScenes;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.ponder.PonderLocalization;
import com.simibubi.create.foundation.ponder.PonderRegistry;
import com.simibubi.create.foundation.ponder.content.PonderTag;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

public class ModPonder {
	public static void register(FMLClientSetupEvent event) {
		event.enqueueWork((Runnable) ModPonder::register);
	}

	protected static void register() {
		PonderRegistry.startRegistration(CreateAutomated.MODID);
		PonderRegistry.forComponents(ModBlocks.ORE_EXTRACTOR_TOP)
				.addStoryBoard("extractor/intro", OreExtractorScenes::intro)
				.addStoryBoard("extractor/automation", OreExtractorScenes::automation);

		PonderRegistry.TAGS.forTag(PonderTag.KINETIC_APPLIANCES)
				.add(ModBlocks.ORE_EXTRACTOR_TOP);
		PonderRegistry.endRegistration();
	}

	public static void generateLang(CreateRegistrate registrate, GatherDataEvent event) {
		register();
		PonderLocalization.provideRegistrateLang(registrate);
	}
}
