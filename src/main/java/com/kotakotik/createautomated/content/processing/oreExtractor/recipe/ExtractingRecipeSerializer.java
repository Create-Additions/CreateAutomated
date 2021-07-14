package com.kotakotik.createautomated.content.processing.oreExtractor.recipe;

import com.google.gson.JsonObject;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;

public class ExtractingRecipeSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<ExtractingRecipe> {
	public static ExtractingRecipeSerializer INSTANCE;

	@Override
	public ExtractingRecipe read(ResourceLocation id, JsonObject json) {
		return new ExtractingRecipe(id,
				Ingredient.deserialize(json.get("node")),
				json.get("output").getAsString(),
				json.get("drillDamage").getAsInt(),
				json.get("requiredProgress").getAsInt(),
				json.get("minOre").getAsInt(),
				json.get("maxOre").getAsInt());
	}

	@Nullable
	@Override
	public ExtractingRecipe read(ResourceLocation id, PacketBuffer buffer) {
		return new ExtractingRecipe(id,
				Ingredient.read(buffer), //node
				buffer.readString(), // output
				buffer.readInt(), // drill damage
				buffer.readInt(), // required progress
				// min ore, max ore
				buffer.readInt(),
				buffer.readInt());
	}

	public void write(JsonObject json, ExtractingRecipe recipe) {
		json.add("node", recipe.node.serialize());
		json.addProperty("output", recipe.output);
		json.addProperty("drillDamage", recipe.drillDamage);
		json.addProperty("requiredProgress", recipe.requiredProgress);
		json.addProperty("minOre", recipe.minOre);
		json.addProperty("maxOre", recipe.maxOre);
	}

	@Override
	public void write(PacketBuffer buffer, ExtractingRecipe recipe) {
		recipe.node.write(buffer);
		buffer.writeString(recipe.output);
		buffer.writeInt(recipe.drillDamage);
		buffer.writeInt(recipe.requiredProgress);
		buffer.writeInt(recipe.minOre);
		buffer.writeInt(recipe.maxOre);
	}

	static ExtractingRecipeSerializer create() {
		return INSTANCE = new ExtractingRecipeSerializer();
	}

	public static ExtractingRecipeSerializer get() {
		if (INSTANCE != null) return INSTANCE;
		return create();
	}
}
