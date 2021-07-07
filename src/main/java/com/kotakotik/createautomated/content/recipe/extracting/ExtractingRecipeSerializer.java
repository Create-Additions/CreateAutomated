package com.kotakotik.createautomated.content.recipe.extracting;

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
                json.get("drill_damage").getAsInt(),
                json.get("required_progress").getAsInt(),
                json.get("min_ore").getAsInt(),
                json.get("max_ore").getAsInt());
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
        json.addProperty("drill_damage", recipe.drillDamage);
        json.addProperty("required_progress", recipe.requiredProgress);
        json.addProperty("min_ore", recipe.minOre);
        json.addProperty("max_ore", recipe.maxOre);
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
