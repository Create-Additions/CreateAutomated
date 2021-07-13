package com.kotakotik.createautomated.content.kinetic.picker;

import com.kotakotik.createautomated.CreateAutomated;
import com.kotakotik.createautomated.register.ModItems;
import com.kotakotik.createautomated.register.RecipeItems;
import com.simibubi.create.content.contraptions.processing.ProcessingOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class PickingRecipeGen extends RecipeProvider {
	public List<PickingRecipe> all = new ArrayList<>(); // made public in case someone for some reason wants to add a picking recipe without a deploying recipe

	public PickingRecipeGen(DataGenerator p_i48262_1_) {
		super(p_i48262_1_);
	}

	public PickingRecipe add(ResourceLocation id, Consumer<PickingRecipe> transform) {
		PickingRecipe r = new PickingRecipe(id);
		transform.accept(r);
		return add(r);
	}

	public PickingRecipe add(String id, Consumer<PickingRecipe> transform) {
		return add(CreateAutomated.asResource("picking/" + id), transform);
	}

	public PickingRecipe add(PickingRecipe built) {
		all.add(built);
		NonNullList<ProcessingOutput> outputs = NonNullList.create();
		outputs.addAll(built.output);
		RecipeItems.DEPLOYING.add(built.id.getPath() + "_deploying", b -> b.require(built.input).require(ModItems.PICKER.get()).withItemOutputs(outputs));
		return built;
	}

	@Override
	protected void registerRecipes(Consumer<IFinishedRecipe> c) {
		for (PickingRecipe built : all) c.accept(built);
	}

	@Override
	public String getName() {
		return "CreateAutomated picking recipes";
	}
}
