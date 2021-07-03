package com.kotakotik.createautomated.register.recipes;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.contraptions.processing.ProcessingRecipe;
import com.simibubi.create.content.contraptions.processing.ProcessingRecipeBuilder;
import com.simibubi.create.foundation.data.recipe.ProcessingRecipeGen;
import net.minecraft.data.DataGenerator;

import java.util.function.UnaryOperator;

public class ModMixingRecipes extends ProcessingRecipeGen {
    public ModMixingRecipes(DataGenerator p_i48262_1_) {
        super(p_i48262_1_);
    }

    @Override
    protected AllRecipeTypes getRecipeType() {
        return AllRecipeTypes.MIXING;
    }

    public <T extends ProcessingRecipe<?>> void add(String name, UnaryOperator<ProcessingRecipeBuilder<T>> transform) {
        super.create(name, transform);
    }
}
