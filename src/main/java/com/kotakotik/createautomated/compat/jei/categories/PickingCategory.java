package com.kotakotik.createautomated.compat.jei.categories;

import com.kotakotik.createautomated.CALocalization;
import com.kotakotik.createautomated.CreateAutomated;
import com.kotakotik.createautomated.compat.jei.animations.AnimatedOreExtractor;
import com.kotakotik.createautomated.compat.jei.animations.AnimatedPicker;
import com.kotakotik.createautomated.content.kinetic.picker.recipe.PickingRecipe;
import com.kotakotik.createautomated.register.ModBlocks;
import com.kotakotik.createautomated.register.ModItems;
import com.kotakotik.createautomated.register.ModRecipeTypes;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.simibubi.create.compat.jei.EmptyBackground;
import com.simibubi.create.compat.jei.category.CrushingCategory;
import com.simibubi.create.content.contraptions.processing.ProcessingOutput;
import com.simibubi.create.content.curiosities.tools.SandPaperPolishingRecipe;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.GuiGameElement;
import com.simibubi.create.foundation.utility.Lang;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class PickingCategory implements IRecipeCategory<PickingRecipe> {
	protected final IDrawable background;
	protected final IDrawable icon;
	public static final ResourceLocation id = CreateAutomated.asResource("picking");
	public static PickingCategory INSTANCE;
	private IGuiHelper guiHelper;
	protected AnimatedPicker picker = new AnimatedPicker();

	public PickingCategory(IGuiHelper guiHelper) {
		INSTANCE = this;
		this.icon = guiHelper.createDrawableIngredient(new ItemStack(ModItems.PICKER.get()));
		this.background = new EmptyBackground(177, 55);
		this.guiHelper = guiHelper;
	}

	public static List<IRecipe<?>> getRecipes() {
		return Minecraft.getInstance().world.getRecipeManager()
				.getRecipes()
				.stream()
				.filter(r -> r.getType() == ModRecipeTypes.PICKING)
				.collect(Collectors.toList());
	}

	public static List<ItemStack> getCatalysts() {
		return Arrays.asList(new ItemStack(ModItems.PICKER.get()));
	}

	@Override
	public ResourceLocation getUid() {
		return id;
	}

	@Override
	public Class<? extends PickingRecipe> getRecipeClass() {
		return PickingRecipe.class;
	}

	@Override
	public String getTitle() {
		return CALocalization.JEI_PICKER_TITLE.translate();
	}

	@Override
	public IDrawable getBackground() {
		return background;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void setIngredients(PickingRecipe pickingRecipe, IIngredients iIngredients) {
		iIngredients.setInputIngredients(pickingRecipe.getIngredients());
		iIngredients.setOutputs(VanillaTypes.ITEM, pickingRecipe.output.stream().map(o -> o.getStack().copy()).collect(Collectors.toList()));
	}

	@Override
	public void setRecipe(IRecipeLayout iRecipeLayout, PickingRecipe pickingRecipe, IIngredients iIngredients) {
		IGuiItemStackGroup itemStacks = iRecipeLayout.getItemStacks();
		List<ProcessingOutput> results = pickingRecipe.output;

		itemStacks.init(0, true, 26, 28);
		itemStacks.set(0, Arrays.asList(pickingRecipe.input
				.getMatchingStacks()));
		itemStacks.init(1, false, 131, 28);
		itemStacks.set(1, results.get(0)
				.getStack());

		itemStacks.addTooltipCallback((i, input, stack, tooltip) -> {
			if (!input) {
				ProcessingOutput output = results.get(i - 1);
				float chance = output.getChance();
				if (chance != 1) {
					tooltip.add(1, Lang.translate("recipe.processing.chance", (double)chance < 0.01f ? "<1" : (int)(chance * 100)).formatted(TextFormatting.GOLD));
				}
			}
		});
	}

	@Override
	public void draw(PickingRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
		AllGuiTextures.JEI_SLOT.draw(matrixStack, 26, 28);
		(recipe.output.stream().noneMatch(o -> o.getChance() != 1) ? AllGuiTextures.JEI_SLOT : AllGuiTextures.JEI_CHANCE_SLOT)
				.draw(matrixStack, 131, 28);
		AllGuiTextures.JEI_SHADOW.draw(matrixStack, 61, 21);
		AllGuiTextures.JEI_LONG_ARROW.draw(matrixStack, 52, 32);

		ItemStack[] matchingStacks = recipe.input
				.getMatchingStacks();
		if (matchingStacks.length == 0)
			return;

		ItemStack s = matchingStacks[0];

		matrixStack.push();
		matrixStack.scale(1.4f, 1.4f, 1.4f);
		GuiGameElement.of(s).draw(matrixStack,55, 3);
		matrixStack.pop();

		picker.draw(matrixStack, 80, -10);
	}
}
