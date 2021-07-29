package com.kotakotik.createautomated.compat.kubejs.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kotakotik.createautomated.register.ModItems;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.contraptions.components.deployer.DeployerApplicationRecipe;
import com.simibubi.create.content.contraptions.processing.ProcessingRecipeBuilder;
import com.simibubi.create.content.contraptions.processing.ProcessingRecipeSerializer;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.recipe.RecipeEventJS;
import dev.latvian.kubejs.recipe.RecipeJS;
import dev.latvian.kubejs.util.ListJS;
import net.minecraft.util.ResourceLocation;

public class PickingJS extends RecipeJS {
	@Override
	public void create(ListJS args) {
		inputItems.add(parseIngredientItem(args.get(0)));
		for (Object o : ListJS.orSelf(args.get(1))) {
			outputItems.add(parseResultItem(o));
		}
		if (args.size() >= 3) {
			Object event = args.get(2);
			if (event instanceof RecipeEventJS) {
				addDeploying((RecipeEventJS) event);
			}
		}
	}

	@Override
	public void deserialize() {
		inputItems.add(parseIngredientItem(json.get("ingredient")));

		for (JsonElement e : json.get("results").getAsJsonArray()) {
			outputItems.add(parseResultItem(e));
		}
	}

	@Override
	public void serialize() {
		json.add("ingredient", inputItems.get(0).toJson());

		JsonArray jsonOutputs = new JsonArray();
		for (ItemStackJS is : outputItems) {
			jsonOutputs.add(is.toResultJson());
		}
		json.add("results", jsonOutputs);
	}

	public PickingJS addDeploying(RecipeEventJS event) {
		// omg this sucks
		ProcessingRecipeBuilder<DeployerApplicationRecipe> b = new ProcessingRecipeBuilder<>(((ProcessingRecipeSerializer<DeployerApplicationRecipe>) AllRecipeTypes.DEPLOYING.serializer).getFactory(), new ResourceLocation("deploying_recipe"))
				.require(inputItems.get(0).createVanillaIngredient())
				.require(ModItems.PICKER.get())
				.output((float) outputItems.get(0).getChance(), outputItems.get(0).getItemStack());
		DeployerApplicationRecipe r = b.build();
		JsonObject json = new JsonObject();
		((ProcessingRecipeSerializer) AllRecipeTypes.DEPLOYING.serializer).write(json, r);
		json.addProperty("type", "create:deploying");
		event.custom(json);
		return this;
	}
}
