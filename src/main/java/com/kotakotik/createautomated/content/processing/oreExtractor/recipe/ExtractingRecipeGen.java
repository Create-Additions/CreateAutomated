package com.kotakotik.createautomated.content.processing.oreExtractor.recipe;

import com.kotakotik.createautomated.CreateAutomated;
import com.kotakotik.createautomated.content.base.IOreExtractorBlock;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ExtractingRecipeGen extends RecipeProvider {
	protected List<ExtractingRecipe.Result> all = new ArrayList<>();

	public ExtractingRecipe.Result add(ResourceLocation id, Consumer<ExtractingRecipe> transform) {
		ExtractingRecipe r = new ExtractingRecipe(id, null, null, 1, IOreExtractorBlock.ExtractorProgressBuilder.atSpeedOfS(128).takesSeconds(20).build(), 1, 1);
		transform.accept(r);
		return add(r.build());
	}

	public ExtractingRecipe.Result add(String id, Consumer<ExtractingRecipe> transform) {
		return add(CreateAutomated.asResource("extracting/" + id), transform);
	}

	public ExtractingRecipe.Result add(ExtractingRecipe.Result built) {
		all.add(built);
		return built;
	}

	public ExtractingRecipeGen(DataGenerator p_i48262_1_) {
		super(p_i48262_1_);
	}

	// 👽
	@Override
	protected void buildShapelessRecipes(Consumer<IFinishedRecipe> c) {
		for (ExtractingRecipe.Result built : all) c.accept(built);
	}

	@Override
	public String getName() {
		return "CreateAutomated extracting recipes";
	}
}
