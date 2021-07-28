package com.kotakotik.createautomated.compat.kubejs.recipe;

import dev.latvian.kubejs.recipe.RecipeJS;
import dev.latvian.kubejs.util.ListJS;

public class ExtractingJS extends RecipeJS {
	// i uh
	// i dont understand anything
//	public int drillDamage;
//	public int requiredProgress;
//	public int minOre;
//	public int maxOre;
	public String output;

	@Override
	public void create(ListJS args) {
		inputItems.add(parseIngredientItem(args.get(0)));
		output = parseResultItem(args.get(1)).getId();

		// defaults
		drillDamage(1);
		requiredProgressSeconds(128, 40);
		ore(1, 4);
//		drillDamage = 1;
//		requiredProgress = 128 * 40;
//		minOre = 1;
//		maxOre = 4;
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
		inputItems.add(parseResultItem(json.get("node")));
//		drillDamage = json.get("drillDamage").getAsInt();
//		requiredProgress = json.get("requiredProgress").getAsInt();
//		minOre = json.get("minOre").getAsInt();
//		maxOre = json.get("maxOre").getAsInt();
	}

	@Override
	public void serialize() {
		json.addProperty("output", output);
		json.add("node", inputItems.get(0).toJson());
//		json.addProperty("drillDamage", drillDamage);
//		json.addProperty("requiredProgress", requiredProgress);
//		json.addProperty("minOre", minOre);
//		json.addProperty("maxOre", maxOre);
	}
}
