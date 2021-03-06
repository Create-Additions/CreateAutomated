package com.kotakotik.createautomated.content.processing.picker.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.simibubi.create.content.contraptions.processing.ProcessingOutput;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;

public class PickingRecipeSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<PickingRecipe> {
	@Override
	public PickingRecipe fromJson(ResourceLocation id, JsonObject json) {
		PickingRecipe recipe = new PickingRecipe(id);
		for (JsonElement je : JSONUtils.getAsJsonArray(json, "results"))
			recipe.output.add(ProcessingOutput.deserialize(je));
		recipe.input = Ingredient.fromJson(json.get("ingredient"));
		return recipe;
	}

	@Nullable
	@Override
	public PickingRecipe fromNetwork(ResourceLocation id, PacketBuffer buffer) {
		PickingRecipe recipe = new PickingRecipe(id);
		int size = buffer.readVarInt();
		for (int i = 0; i < size; i++)
			recipe.output.add(ProcessingOutput.read(buffer));
		recipe.input = Ingredient.fromNetwork(buffer);
		return recipe;
	}

	@Override
	public void toNetwork(PacketBuffer buffer, PickingRecipe recipe) {
		buffer.writeVarInt(recipe.output.size());
		recipe.output.forEach((o) -> o.write(buffer));
		recipe.input.toNetwork(buffer);
	}

	public void write(JsonObject json, PickingRecipe recipe) {
		JsonArray jsonOutputs = new JsonArray();
		recipe.output.forEach((o) -> jsonOutputs.add(o.serialize()));
		json.add("results", jsonOutputs);
		json.add("ingredient", recipe.input.toJson());
	}
}
