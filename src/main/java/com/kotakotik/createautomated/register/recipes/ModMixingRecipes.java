package com.kotakotik.createautomated.register.recipes;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.contraptions.components.mixer.MixingRecipe;
import com.simibubi.create.content.contraptions.processing.ProcessingRecipeBuilder;
import com.simibubi.create.content.contraptions.processing.ProcessingRecipeSerializer;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;

public class ModMixingRecipes extends CAProcessingRecipeWrapper<MixingRecipe> {
    public ModMixingRecipes(DataGenerator gen) {
        super(gen);
    }

    @Override
    public ProcessingRecipeBuilder<MixingRecipe> createBuilder(ResourceLocation id) {
        return new ProcessingRecipeBuilder<>(((ProcessingRecipeSerializer<MixingRecipe>) AllRecipeTypes.MIXING.serializer).getFactory(), id);
    }

    @Override
    public String getName() {
        return "CreateAutomated mixing recipes";
    }
}
