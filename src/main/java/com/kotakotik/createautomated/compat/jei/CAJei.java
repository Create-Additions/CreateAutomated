package com.kotakotik.createautomated.compat.jei;

import com.kotakotik.createautomated.CreateAutomated;
import com.kotakotik.createautomated.compat.jei.categories.OreExtractionCategory;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.util.ResourceLocation;

@JeiPlugin
public class CAJei implements IModPlugin {
    public static final ResourceLocation id = new ResourceLocation(CreateAutomated.modid, "jeicompat");

    @Override
    public ResourceLocation getPluginUid() {
        return id;
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        OreExtractionCategory.getCatalysts().forEach(e -> registration.addRecipeCatalyst(e, OreExtractionCategory.id));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(OreExtractionCategory.getRecipes(), OreExtractionCategory.id);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new OreExtractionCategory(registration.getJeiHelpers().getGuiHelper()));
    }
}
