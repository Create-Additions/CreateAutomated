package com.kotakotik.createautomated.register.recipes;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.contraptions.components.crusher.CrushingRecipe;
import com.simibubi.create.content.contraptions.processing.ProcessingRecipeBuilder;
import com.simibubi.create.content.contraptions.processing.ProcessingRecipeSerializer;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;

public class ModCrushingRecipes extends CAProcessingRecipeWrapper<CrushingRecipe> {
	public ModCrushingRecipes(DataGenerator datagen) {
		super(datagen);
	}

	@Override
	public ProcessingRecipeBuilder<CrushingRecipe> createBuilder(ResourceLocation id) {
		return new ProcessingRecipeBuilder<>(((ProcessingRecipeSerializer<CrushingRecipe>) AllRecipeTypes.CRUSHING.serializer).getFactory(), id);
	}

	@Override
	public String getName() {
		return "CreateAutomated crushing recipes";
	}
}
