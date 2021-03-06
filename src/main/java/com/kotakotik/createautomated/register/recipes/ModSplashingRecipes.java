package com.kotakotik.createautomated.register.recipes;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.contraptions.components.fan.SplashingRecipe;
import com.simibubi.create.content.contraptions.processing.ProcessingRecipeBuilder;
import com.simibubi.create.content.contraptions.processing.ProcessingRecipeSerializer;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;

public class ModSplashingRecipes extends CAProcessingRecipeWrapper<SplashingRecipe> {
	{
		add("basalt", b -> b.require(Blocks.BASALT).output(Blocks.BLACKSTONE));
	}

	public ModSplashingRecipes(DataGenerator datagen) {
		super(datagen);
	}

	@Override
	public ProcessingRecipeBuilder<SplashingRecipe> createBuilder(ResourceLocation id) {
		return new ProcessingRecipeBuilder<>(((ProcessingRecipeSerializer<SplashingRecipe>) AllRecipeTypes.SPLASHING.getSerializer()).getFactory(), id);
	}

	@Override
	public String getName() {
		return "CreateAutomated splashing recipes";
	}
}
