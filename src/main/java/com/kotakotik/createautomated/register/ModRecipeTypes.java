package com.kotakotik.createautomated.register;

import com.kotakotik.createautomated.CreateAutomated;
import com.kotakotik.createautomated.content.kinetic.oreExtractor.recipe.ExtractingRecipeSerializer;
import com.kotakotik.createautomated.content.kinetic.oreExtractor.recipe.ExtractingRecipeType;
import com.kotakotik.createautomated.content.kinetic.picker.recipe.PickingRecipeSerializer;
import com.kotakotik.createautomated.content.kinetic.picker.recipe.PickingRecipeType;
import com.simibubi.create.foundation.data.CreateRegistrate;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.event.RegistryEvent;

public class ModRecipeTypes {
	protected static ResourceLocation ExtractingLoc = CreateAutomated.asResource("extracting");
	public static ExtractingRecipeType EXTRACTING;

	protected static ResourceLocation PickingLoc = CreateAutomated.asResource("picking");
	public static PickingRecipeType PICKING;
	public static PickingRecipeSerializer PICKING_SERIALIZER;

	public static void reg(CreateRegistrate registrate) {
		EXTRACTING = Registry.register(Registry.RECIPE_TYPE, ExtractingLoc, new ExtractingRecipeType());
		PICKING = Registry.register(Registry.RECIPE_TYPE, PickingLoc, new PickingRecipeType());
	}

	public static void register(RegistryEvent.Register<IRecipeSerializer<?>> event) {
		event.getRegistry()
				.register(ExtractingRecipeSerializer.get().setRegistryName(ExtractingLoc));
		PICKING_SERIALIZER = new PickingRecipeSerializer();
		event.getRegistry()
				.register(PICKING_SERIALIZER.setRegistryName(PickingLoc));
//        AllRecipeTypes
//        Registry.register(Registry.RECIPE_TYPE, new ResourceLocation(CreateAutomated.modid, "extracting"), ExtractingRecipeType.class);
	}
}
