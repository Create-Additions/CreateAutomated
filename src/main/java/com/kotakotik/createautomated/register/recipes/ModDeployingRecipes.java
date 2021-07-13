package com.kotakotik.createautomated.register.recipes;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.contraptions.components.deployer.DeployerApplicationRecipe;
import com.simibubi.create.content.contraptions.processing.ProcessingRecipeBuilder;
import com.simibubi.create.content.contraptions.processing.ProcessingRecipeSerializer;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;

public class ModDeployingRecipes extends CAProcessingRecipeWrapper<DeployerApplicationRecipe> {
	public ModDeployingRecipes(DataGenerator datagen) {
		super(datagen);
	}

	@Override
	public ProcessingRecipeBuilder<DeployerApplicationRecipe> createBuilder(ResourceLocation id) {
		return new ProcessingRecipeBuilder<>(((ProcessingRecipeSerializer<DeployerApplicationRecipe>) AllRecipeTypes.DEPLOYING.serializer).getFactory(), id);
	}

	@Override
	public String getName() {
		return "CreateAutomated deploying recipes";
	}
}
