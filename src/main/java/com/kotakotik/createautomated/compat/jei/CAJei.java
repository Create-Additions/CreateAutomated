package com.kotakotik.createautomated.compat.jei;

import com.kotakotik.createautomated.CreateAutomated;
import com.kotakotik.createautomated.compat.jei.categories.OreExtractionCategory;
import com.kotakotik.createautomated.compat.jei.categories.PickingCategory;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.util.ResourceLocation;

@JeiPlugin
public class CAJei implements IModPlugin {
	public static final ResourceLocation id = CreateAutomated.asResource("jeicompat");

	@Override
	public ResourceLocation getPluginUid() {
		return id;
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		OreExtractionCategory.getCatalysts().forEach(e -> registration.addRecipeCatalyst(e, OreExtractionCategory.id));
		PickingCategory.getCatalysts().forEach(e -> registration.addRecipeCatalyst(e, PickingCategory.id));
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		registration.addRecipes(OreExtractionCategory.getRecipes(), OreExtractionCategory.id);
		registration.addRecipes(PickingCategory.getRecipes(), PickingCategory.id);
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {
		registration.addRecipeCategories(new OreExtractionCategory(registration.getJeiHelpers().getGuiHelper()));
		registration.addRecipeCategories(new PickingCategory(registration.getJeiHelpers().getGuiHelper()));
	}
}
