package com.kotakotik.createautomated.content.kinetic.oreExtractor;

import com.google.gson.JsonObject;
import com.kotakotik.createautomated.register.ModRecipeTypes;
import com.simibubi.create.repack.registrate.util.nullness.NonNullSupplier;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.RandomUtils;

import javax.annotation.Nullable;
import java.util.Objects;

public class ExtractingRecipe implements IRecipe<IInventory> { // help
	protected final ResourceLocation id;
	public Ingredient node;
	public String output;
	public int drillDamage;
	public int requiredProgress;

	public int minOre;
	public int maxOre;

	public ExtractingRecipe node(Ingredient ingredient) {
		this.node = ingredient;
		return this;
	}

	public ExtractingRecipe node(IItemProvider item) {
		return node(Ingredient.fromItems(item));
	}

	public ExtractingRecipe node(NonNullSupplier<IItemProvider> item) {
		return node(item.get());
	}

	public ExtractingRecipe output(String output) {
		this.output = output;
		return this;
	}

	public ExtractingRecipe drillDamage(int drillDamage) {
		this.drillDamage = drillDamage;
		return this;
	}

	public ExtractingRecipe requiredProgress(int requiredProgress) {
		this.requiredProgress = requiredProgress;
		return this;
	}

	public ExtractingRecipe ore(int min, int max) {
		this.minOre = min;
		this.maxOre = max;
		return this;
	}

	public ExtractingRecipe ore(int ore) {
		return ore(ore, ore);
	}

	public ExtractingRecipe maxOre(int maxOre) {
		return ore(this.minOre, maxOre);
	}

	public ExtractingRecipe minOre(int minOre) {
		return ore(minOre, this.maxOre);
	}

	public ExtractingRecipe output(Item item) {
		return output(Objects.requireNonNull(item.getRegistryName()).toString());
	}

	public ExtractingRecipe output(NonNullSupplier<Item> item) {
		return output(item.get());
	}

	public ExtractingRecipe(ResourceLocation id, Ingredient node, String output, int drillDamage, int requiredProgress, int minOre, int maxOre) {
		this.id = id;
		this.node = node;
		this.output = output;
		this.drillDamage = drillDamage;
		this.requiredProgress = requiredProgress;
		this.minOre = minOre;
		this.maxOre = maxOre;
	}

	@Override
	public boolean matches(IInventory inv, World world) {
		return node.test(inv.getStackInSlot(0));
	}

	@Override
	public ItemStack getCraftingResult(@Nullable IInventory p_77572_1_) {
		ItemStack stack = getCraftingResult1Size();
		stack.setCount(minOre == maxOre ? minOre : RandomUtils.nextInt(minOre, maxOre));
		return stack;
	}

	public ItemStack getCraftingResult() {
		return getCraftingResult(null);
	}

	public ItemStack getCraftingResult1Size() {
		return getRecipeOutput();
	}

	@Override
	public boolean canFit(int p_194133_1_, int p_194133_2_) {
		return true;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(output)));
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return ExtractingRecipeSerializer.get();
	}

	@Override
	public IRecipeType<?> getType() {
		return ModRecipeTypes.EXTRACTING;
	}

	@Override
	public NonNullList<Ingredient> getIngredients() {
		return NonNullList.from(node);
	}

	@Override
	public String getGroup() {
		return "extracting";
	}

	public Result build() {
		return new Result(id, node, output, drillDamage, requiredProgress, minOre, maxOre);
	}

	public static class Result implements IFinishedRecipe {
		public final ResourceLocation id;
		public final Ingredient node;
		public final String output;
		public final int drillDamage;
		public final int requiredProgress;
		public final int minOre;
		public final int maxOre;

		protected Result(ResourceLocation id, Ingredient node, String output, int drillDamage, int requiredProgress, int minOre, int maxOre) {
			this.id = id;
			this.node = node;
			this.output = output;
			this.drillDamage = drillDamage;
			this.requiredProgress = requiredProgress;
			this.minOre = minOre;
			this.maxOre = maxOre;
		}

		@Override
		public void serialize(JsonObject json) {
			getSerializer().write(json, clone());
		}

		public ExtractingRecipe clone() {
			return new ExtractingRecipe(id, node, output, drillDamage, requiredProgress, minOre, maxOre);
		}

		@Override
		public ResourceLocation getID() {
			return id;
		}

		@Override
		public ExtractingRecipeSerializer getSerializer() {
			return ExtractingRecipeSerializer.get();
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
	}
}
