package com.kotakotik.createautomated.register;

import com.kotakotik.createautomated.CreateAutomated;
import com.kotakotik.createautomated.content.ponder.OreExtractorScenes;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.ponder.PonderRegistry;
import com.simibubi.create.foundation.ponder.content.PonderTag;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

public class ModPonder {
    public static void register(FMLClientSetupEvent event) {
        register();
    }

    protected static void register() {
        PonderRegistry.forComponents(ModBlocks.ORE_EXTRACTOR_TOP)
                .addStoryBoard("createautomated/extractor/intro", OreExtractorScenes::intro);

        PonderRegistry.tags.forTag(PonderTag.KINETIC_APPLIANCES)
                .add(ModBlocks.ORE_EXTRACTOR_TOP);
    }

    public static void generateLang(CreateRegistrate registrate, GatherDataEvent event) {
        register();
        PonderRegistry.provideLangEntries().getAsJsonObject().entrySet().forEach(e -> {
            String k = e.getKey();
            String v = e.getValue().getAsString();
            if (k.contains(CreateAutomated.modid + ".")) {
                registrate.addRawLang(k, v);
            }
        });
    }
}
