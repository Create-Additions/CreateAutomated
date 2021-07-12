package com.kotakotik.createautomated.content.kinetic.picker;

import com.google.gson.JsonObject;
import com.kotakotik.createautomated.register.ModRecipeTypes;
import com.simibubi.create.content.contraptions.processing.ProcessingOutput;
import com.simibubi.create.foundation.utility.Pair;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PickingRecipe implements IRecipe<IInventory>, IFinishedRecipe {
	public List<ProcessingOutput> output = new ArrayList<>();
	public Ingredient input;
	public final ResourceLocation id;

	public PickingRecipe(ResourceLocation id) {
		this.id = id;
	}

	@Override
	public void serialize(JsonObject json) {
		((PickingRecipeSerializer) getSerializer()).write(json, this);
	}

	@Override
	public ResourceLocation getID() {
		return id;
	}

	@Nullable
	@Override
	public JsonObject getAdvancementJson() {
		return null;
	}

	@Nullable
	@Override
	public ResourceLocation getAdvancementID() {
		return null;
	}

	@Override
	public boolean matches(IInventory inv, World world) {
		return input.test(inv.getStackInSlot(0));
	}

	@Override
	public ItemStack getCraftingResult(IInventory p_77572_1_) {
		return getRecipeOutput();
	}

	@Override
	public boolean canFit(int p_194133_1_, int p_194133_2_) {
		return true;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return output.get(0).rollOutput();
	}

	public List<ItemStack> generateOutputs() {
		return output.stream().map(ProcessingOutput::rollOutput).filter(p -> !p.isEmpty()).collect(Collectors.toList());
	}

	@Override
	public ResourceLocation getId() {
		return getID();
	}

	@Override
	public IRecipeSerializer<PickingRecipe> getSerializer() {
		return ModRecipeTypes.PICKING_SERIALIZER;
	}

	@Override
	public IRecipeType<?> getType() {
		return ModRecipeTypes.PICKING;
	}

	public PickingRecipe require(ITag.INamedTag<Item> tag) {
		return this.require(Ingredient.fromTag(tag));
	}

	public PickingRecipe require(IItemProvider item) {
		return this.require(Ingredient.fromItems(item));
	}

	public PickingRecipe require(Ingredient ingredient) {
		input = ingredient;
		return this;
	}

	public PickingRecipe output(IItemProvider item) {
		return this.output(item, 1);
	}

	public PickingRecipe output(float chance, IItemProvider item) {
		return this.output(chance, item, 1);
	}

	public PickingRecipe output(IItemProvider item, int amount) {
		return this.output(1.0F, item, amount);
	}

	public PickingRecipe output(float chance, IItemProvider item, int amount) {
		return this.output(chance, new ItemStack(item, amount));
	}

	public PickingRecipe output(ItemStack output) {
		return this.output(1.0F, output);
	}

	public PickingRecipe output(float chance, ItemStack output) {
		this.output.add(new ProcessingOutput(output, chance));
		return this;
	}

	public PickingRecipe output(float chance, ResourceLocation registryName, int amount) {
		this.output.add(new ProcessingOutput(Pair.of(registryName, amount), chance));
		return this;
	}
}
