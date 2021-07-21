package com.kotakotik.createautomated;

import com.kotakotik.createautomated.content.worldgen.WorldGen;
import com.kotakotik.createautomated.register.*;
import com.kotakotik.createautomated.register.config.ModConfig;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.repack.registrate.util.NonNullLazyValue;
import com.simibubi.create.repack.registrate.util.OneTimeEventReceiver;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DatagenModLoader;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(CreateAutomated.MODID)
public class CreateAutomated {

	// Directly reference a log4j logger.
	public static final Logger LOGGER = LogManager.getLogger();

	public static final String MODID = BuildConfig.MODID;

	public static IEventBus modEventBus;

	public static final NonNullLazyValue<CreateRegistrate> registrate = CreateRegistrate.lazy(MODID);

	public CreateAutomated() {
		modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		CreateRegistrate r = registrate.get();
		ModRecipeTypes.reg(r);
		ModItems.register(r);
		ModBlocks.register(r);
		ModEntities.register(r);
		ModTiles.register(r);
		WorldGen.register();
		ModFluids.register(r);
		modEventBus.addGenericListener(IRecipeSerializer.class, ModRecipeTypes::register);
		OneTimeEventReceiver.addListener(modEventBus, FMLCommonSetupEvent.class, (e) -> {
			WorldGen.reg(e);
			ModActors.register();
		});
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
				() -> ModBlockPartials::load);
		if (DatagenModLoader.isRunningDataGen()) {
			modEventBus.addListener((GatherDataEvent g) -> ModPonder.generateLang(r, g));
			CALocalization.register(r);
		}
		modEventBus.addListener(ModPonder::register);
		MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH, WorldGen::gen);
		// uses a new item group so its last not to put any other items in the item group
		RecipeItems.register(r);
		ModConfig.reg();
	}

	public static ResourceLocation asResource(String path) {
		return new ResourceLocation(MODID, path);
	}
}
