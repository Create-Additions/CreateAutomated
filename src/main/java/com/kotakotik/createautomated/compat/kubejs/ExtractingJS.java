package com.kotakotik.createautomated.compat.kubejs;

import com.blamejared.crafttweaker.api.item.IIngredient;
import dev.latvian.kubejs.item.ItemJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.kubejs.recipe.RecipeJS;
import dev.latvian.kubejs.util.ListJS;

public class ExtractingJS extends RecipeJS {
	public String output;
	public IngredientJS node;
	public int drillDamage;
	public int requiredProgress;
	public int minOre;
	public int maxOre;

	@Override
	public void create(ListJS args) {
		json.add("node", ((IngredientJS) args.get(0)).toJson());
		Object output = args.get(1);
		String outputOut;
		if(output instanceof String) outputOut = (String) output;
		else if(output instanceof ItemJS) outputOut = ((ItemJS) output).getRegistryName().toString();
		else if(output instanceof ItemStackJS) outputOut = ((ItemStackJS) output).getItem().getRegistryName().toString();
		else {
			throw new RecipeExceptionJS("Recieved extracting output isn't a string, item, or item stack\n" + output + "\nReceived class: " + output.getClass().getName());
		}
		json.addProperty("output", outputOut);

		// defaults
		drillDamage(1);
		requiredProgressSeconds(128, 40);
		ore(1, 4);
	}

	public ExtractingJS drillDamage(int damage) {
		json.addProperty("drillDamage", damage);
		save();
		return this;
	}

	public ExtractingJS requiredProgress(int progress) {
		json.addProperty("requiredProgress", progress);
		save();
		return this;
	}

	public ExtractingJS requiredProgressTicks(int speed, int ticks) {
		return requiredProgress(speed * ticks);
	}

	public ExtractingJS requiredProgressSeconds(int speed, int seconds) {
		return requiredProgressTicks(speed, seconds * 20);
	}

	public ExtractingJS requiredProgressMinutes(int speed, int minutes) {
		return requiredProgressSeconds(speed, minutes * 60);
	}

	public ExtractingJS ore(int min, int max) {
		json.addProperty("minOre", min);
		json.addProperty("maxOre", max);
		save();
		return this;
	}

	public ExtractingJS ore(int ore) {
		// someone really loves the word ore
		return ore(ore, ore);
	}

//	public ExtractingJS requires(IngredientJS node) {
//		json.add("node", node.toJson());
//		save();
//		return this;
//	}
//
//	public ExtractingJS output(ItemJS output) {
//		json.addProperty("output", output.getRegistryName().toString());
//		save();
//		return this;
//	}
//
//	public ExtractingJS drill

	@Override
	public void deserialize() {
		output = json.get("output").getAsString();
		node = parseResultItem(json.get("node"));
		drillDamage = json.get("drillDamage").getAsInt();
		requiredProgress = json.get("requiredProgress").getAsInt();
		minOre = json.get("minOre").getAsInt();
		maxOre = json.get("maxOre").getAsInt();
	}

	@Override
	public void serialize() {
		json.addProperty("output", output);
		json.add("node", node.toJson());
		json.addProperty("drillDamage", drillDamage);
		json.addProperty("requiredProgress", requiredProgress);
		json.addProperty("minOre", minOre);
		json.addProperty("maxOre", maxOre);
	}
}
