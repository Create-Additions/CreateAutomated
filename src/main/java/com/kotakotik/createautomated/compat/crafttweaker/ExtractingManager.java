package com.kotakotik.createautomated.compat.crafttweaker;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.item.IIngredient;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.api.managers.IRecipeManager;
import com.blamejared.crafttweaker.impl.actions.recipes.ActionAddRecipe;
import com.kotakotik.createautomated.content.base.IOreExtractorBlock;
import com.kotakotik.createautomated.content.kinetic.oreExtractor.OreExtractorTile;
import com.kotakotik.createautomated.content.kinetic.oreExtractor.recipe.ExtractingRecipe;
import com.kotakotik.createautomated.register.ModRecipeTypes;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import org.openzen.zencode.java.ZenCodeType;

@ZenRegister
@ZenCodeType.Name("mods.createautomated.ExtractingManager")
public class ExtractingManager implements IRecipeManager {
	@Override
	public IRecipeType getRecipeType() {
		return ModRecipeTypes.EXTRACTING;
	}

	@ZenCodeType.Method
	public void addRecipe(String name, IIngredient node, IItemStack output, int drillDamage, int requiredProgress, int minOre, int maxOre) {

		name = fixRecipeName(name);
		ResourceLocation resourceLocation = new ResourceLocation("crafttweaker", name);
		CraftTweakerAPI.apply(new ActionAddRecipe(this,
				new ExtractingRecipe(resourceLocation, node.asVanillaIngredient(),
						output.getRegistryName().getPath(), drillDamage, requiredProgress, minOre, maxOre)
				, ""));
	}

	@ZenCodeType.Method
	public void addRecipe(String name, IIngredient node, IItemStack output, int drillDamage, int requiredProgress, int ore) {
		addRecipe(name, node, output, drillDamage, requiredProgress, ore, ore);
	}

	@ZenCodeType.Method
	public void addRecipe(String name, IIngredient node, IItemStack output, int drillDamage, int speedOf, int takesTicks, int minOre, int maxOre) {
		addRecipe(name, node, output, drillDamage, IOreExtractorBlock.ExtractorProgressBuilder.atSpeedOfS(speedOf).takesTicks(takesTicks).build(), minOre, maxOre);
	}
}
