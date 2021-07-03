package com.kotakotik.createaddontemplate.register.recipes;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.foundation.data.recipe.ProcessingRecipeGen;
import net.minecraft.data.DataGenerator;

public class ModMixingRecipes extends ProcessingRecipeGen {
    public ModMixingRecipes(DataGenerator p_i48262_1_) {
        super(p_i48262_1_);
    }

    @Override
    protected AllRecipeTypes getRecipeType() {
        return AllRecipeTypes.MIXING;
    }

}
