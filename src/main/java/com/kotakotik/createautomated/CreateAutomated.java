package com.kotakotik.createautomated;

import com.kotakotik.createautomated.content.worldgen.WorldGen;
import com.kotakotik.createautomated.register.*;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.repack.registrate.util.NonNullLazyValue;
import com.simibubi.create.repack.registrate.util.OneTimeEventReceiver;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// TODO: rename this class!
@Mod(CreateAutomated.modid)
public class CreateAutomated {

    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public static final String modid = "createautomated";

    public static IEventBus modEventBus;

    public static final NonNullLazyValue<CreateRegistrate> registrate = CreateRegistrate.lazy(modid);

    public CreateAutomated() {
        modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        CreateRegistrate r = registrate.get();
        modEventBus.addListener(RecipeItems::gatherData);
        ModItems.register(r);
        ModBlocks.register(r);
        ModEntities.register(r);
        ModTiles.register(r);
        WorldGen.register();
        OneTimeEventReceiver.addListener(modEventBus, FMLCommonSetupEvent.class, (e) -> {
            WorldGen.reg(e);
            ModActors.register();
        });
        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH, WorldGen::gen);
    }
}
