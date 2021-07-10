package com.kotakotik.createautomated.register.recipes;

import com.google.gson.JsonObject;
import com.kotakotik.createautomated.CreateAutomated;
import com.simibubi.create.content.contraptions.processing.ProcessingRecipe;
import com.simibubi.create.content.contraptions.processing.ProcessingRecipeBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

public abstract class CAProcessingRecipeWrapper<T extends ProcessingRecipe<?>> extends RecipeProvider {
	public List<ProcessingRecipeBuilder<T>> recipes = new ArrayList<>();
	public static final Method saveRecipeMethod = ObfuscationReflectionHelper.findMethod(RecipeProvider.class, "saveRecipe", DirectoryCache.class, JsonObject.class, Path.class);

	public CAProcessingRecipeWrapper(DataGenerator datagen) {
		super(datagen);
	}

	@Override
	protected void registerRecipes(Consumer<IFinishedRecipe> r) {
		recipes.forEach(builder -> builder.build(r));
	}

	public void add(String name, UnaryOperator<ProcessingRecipeBuilder<T>> transform) {
		ResourceLocation id = new ResourceLocation(CreateAutomated.modid, name);
		ProcessingRecipeBuilder<T> builder = createBuilder(id);
		transform.apply(builder);
		recipes.add(builder);
	}

	public abstract ProcessingRecipeBuilder<T> createBuilder(ResourceLocation id);
}
