package com.kotakotik.createautomated.compat.jei.categories;

import com.google.common.collect.Lists;
import com.kotakotik.createautomated.CALocalization;
import com.kotakotik.createautomated.CreateAutomated;
import com.kotakotik.createautomated.compat.jei.animations.AnimatedOreExtractor;
import com.kotakotik.createautomated.content.processing.oreExtractor.recipe.ExtractingRecipe;
import com.kotakotik.createautomated.register.ModBlocks;
import com.kotakotik.createautomated.register.ModRecipeTypes;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.simibubi.create.compat.jei.EmptyBackground;
import com.simibubi.create.foundation.gui.AllGuiTextures;
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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class OreExtractionCategory implements IRecipeCategory<ExtractingRecipe> {
	protected final IDrawable background;
	protected final IDrawable icon;
	private final IGuiHelper helper;

	public static final ResourceLocation id = CreateAutomated.asResource("extraction");

	public static OreExtractionCategory INSTANCE;
	protected AnimatedOreExtractor oreExtractor = new AnimatedOreExtractor();

	public OreExtractionCategory(IGuiHelper helper) {
		INSTANCE = this;
		this.icon = helper.createDrawableIngredient(new ItemStack(ModBlocks.ORE_EXTRACTOR_TOP.get()));
		this.background = new EmptyBackground(177, 100);
		this.helper = helper;
	}

	@Override
	public ResourceLocation getUid() {
		return id;
	}

	@Override
	public Class<? extends ExtractingRecipe> getRecipeClass() {
		return ExtractingRecipe.class;
	}

	@Override
	public String getTitle() {
		return CALocalization.JEI_ORE_EXTRACTOR_TITLE.translate();
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
	public void setIngredients(ExtractingRecipe recipe, IIngredients iIngredients) {
		iIngredients.setInput(VanillaTypes.ITEM, new ItemStack(recipe.node.getItems()[0].getItem()));// not sure whether i actually need to create a new itemstack but *idc*

		iIngredients.setOutput(VanillaTypes.ITEM, recipe.getCraftingResult1Size());
	}

	@Override
	public void setRecipe(IRecipeLayout iRecipeLayout, ExtractingRecipe recipe, IIngredients iIngredients) {
		IGuiItemStackGroup guiItemStacks = iRecipeLayout.getItemStacks();
		guiItemStacks.init(1, false, 85, 45);

		guiItemStacks.set(1, iIngredients.getOutputs(VanillaTypes.ITEM).get(0));

		guiItemStacks.addTooltipCallback((i, input, stack, tooltip) -> {
			if (!input) {
				if (recipe.minOre != recipe.maxOre) {
					tooltip.add(CALocalization.JEI_ORE_EXTRACTOR_BETWEEN.getComponent(recipe.minOre, recipe.maxOre).withStyle(TextFormatting.GOLD));
//                    tooltip.add(new StringTextComponent("From " + recipe.minOre + " to " + recipe.maxOre).formatted(TextFormatting.GOLD));
				} else {
					stack.setCount(recipe.minOre);
				}
			}
		});
	}

	@Override
	public List<ITextComponent> getTooltipStrings(ExtractingRecipe recipe, double mouseX, double mouseY) {
//        System.out.println("x" + mouseX);
//        System.out.println("y" + mouseY);
		ItemStack node = recipe.node.getItems()[0];
		// from x 30
		// from y 86
		// to x 60
		// to y 56
		if (mouseX > 30 && mouseX < 60 && mouseY > 56 && mouseX < 86) {
			return Lists.newArrayList(
					new TranslationTextComponent(node.getItem().getDescriptionId()),
					CALocalization.JEI_ORE_EXTRACTOR_DRILL_DAMAGE.getComponent(recipe.drillDamage).withStyle(TextFormatting.GOLD),
					CALocalization.JEI_ORE_EXTRACTOR_TIME.getComponent(recipe.requiredProgress / 128f / 20f).withStyle(TextFormatting.GOLD) // dunno if i actually need to include that they are float but shouldnt hurt right?
			);
		}
		return IRecipeCategory.super.getTooltipStrings(recipe, mouseX, mouseY);
	}

	@Override
	public void draw(ExtractingRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
		oreExtractor.drawWithBlock(matrixStack, 30, 30, ((BlockItem) recipe.node.getItems()[0].getItem()).getBlock().defaultBlockState());
		AllGuiTextures.JEI_DOWN_ARROW.draw(matrixStack, 80, 30);

		(recipe.minOre > 0 ? AllGuiTextures.JEI_SLOT : AllGuiTextures.JEI_CHANCE_SLOT)
				.draw(matrixStack, 85, 45);
	}

	public static List<IRecipe<?>> getRecipes() {
		return Minecraft.getInstance().level.getRecipeManager()
				.getRecipes()
				.stream()
				.filter(r -> r.getType() == ModRecipeTypes.EXTRACTING)
				.collect(Collectors.toList());
	}

	public static List<ItemStack> getCatalysts() {
		return Arrays.asList(new ItemStack(ModBlocks.ORE_EXTRACTOR_TOP.get()));
	}
}
