package com.kotakotik.createautomated.register;

import com.kotakotik.createautomated.CreateAutomated;
import com.kotakotik.createautomated.content.kinetic.oreExtractor.ExtractingRecipeSerializer;
import com.kotakotik.createautomated.content.kinetic.oreExtractor.ExtractingRecipeType;
import com.simibubi.create.foundation.data.CreateRegistrate;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.event.RegistryEvent;

public class ModRecipeTypes {
	protected static ResourceLocation ExtractingLoc = new ResourceLocation(CreateAutomated.modid, "extracting");
	public static ExtractingRecipeType EXTRACTING;

	public static void reg(CreateRegistrate registrate) {
		EXTRACTING = Registry.register(Registry.RECIPE_TYPE, ExtractingLoc, new ExtractingRecipeType());
	}

	public static void register(RegistryEvent.Register<IRecipeSerializer<?>> event) {
		event.getRegistry()
				.register(ExtractingRecipeSerializer.get().setRegistryName(ExtractingLoc));
//        AllRecipeTypes
//        Registry.register(Registry.RECIPE_TYPE, new ResourceLocation(CreateAutomated.modid, "extracting"), ExtractingRecipeType.class);
	}
}
