package com.kotakotik.createautomated.compat.crafttweaker;


import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.item.IIngredient;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.api.managers.IRecipeManager;
import com.blamejared.crafttweaker.impl.actions.recipes.ActionAddRecipe;
import com.blamejared.crafttweaker.impl.item.MCWeightedItemStack;
import com.kotakotik.createautomated.CreateAutomated;
import com.kotakotik.createautomated.content.processing.oreExtractor.recipe.ExtractingRecipe;
import com.kotakotik.createautomated.content.processing.picker.recipe.PickingRecipe;
import com.kotakotik.createautomated.register.ModRecipeTypes;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.contraptions.components.deployer.DeployerApplicationRecipe;
import com.simibubi.create.content.contraptions.processing.ProcessingRecipeBuilder;
import com.simibubi.create.content.contraptions.processing.ProcessingRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import org.openzen.zencode.java.ZenCodeType;

@ZenRegister
@ZenCodeType.Name("mods.createautomated.PickingManager")
public class PickingManager implements IRecipeManager {
	@Override
	public IRecipeType getRecipeType() {
		return ModRecipeTypes.PICKING;
	}

	@ZenCodeType.Method
	public void addRecipe(String name, IIngredient input, MCWeightedItemStack[] outputs, @ZenCodeType.OptionalBoolean(true) boolean addDeployingRecipe) {
		name = fixRecipeName(name);
		String deployingName = name + "_deploying";
		ResourceLocation resourceLocation = new ResourceLocation("crafttweaker", name);
		ResourceLocation deployingRes = new ResourceLocation("crafttweaker", deployingName);
		PickingRecipe r = new PickingRecipe(resourceLocation).require(input.asVanillaIngredient());
		ProcessingRecipeBuilder<DeployerApplicationRecipe> deployingRecipe = new ProcessingRecipeBuilder<>(((ProcessingRecipeSerializer<DeployerApplicationRecipe>) AllRecipeTypes.DEPLOYING.serializer).getFactory(), deployingRes)
				.require(input.asVanillaIngredient());
		for(MCWeightedItemStack output : outputs) {
			r.output((float) output.getWeight(), output.getItemStack().getInternal());
			deployingRecipe.output((float) output.getWeight(), output.getItemStack().getInternal());
		}
		CraftTweakerAPI.apply(new ActionAddRecipe(this, r, ""));
		if(addDeployingRecipe) {
			CraftTweakerAPI.apply(new ActionAddRecipe(this, deployingRecipe.build(), ""));
		}
	}
}
