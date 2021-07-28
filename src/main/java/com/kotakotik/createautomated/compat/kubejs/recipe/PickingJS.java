package com.kotakotik.createautomated.compat.kubejs.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.recipe.RecipeJS;
import dev.latvian.kubejs.util.ListJS;

public class PickingJS extends RecipeJS {
	@Override
	public void create(ListJS args) {
		inputItems.add(parseIngredientItem(args.get(0)));
		for (Object o : ListJS.orSelf(args.get(1))) {
			outputItems.add(parseResultItem(o));
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
}
