package com.kotakotik.createautomated.jei.categories;

import com.kotakotik.createautomated.CreateAutomated;
import com.kotakotik.createautomated.content.recipe.extracting.ExtractingRecipe;
import com.kotakotik.createautomated.jei.animations.AnimatedOreExtractor;
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
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class OreExtractionCategory implements IRecipeCategory<ExtractingRecipe> {
    protected final IDrawable background;
    protected final IDrawable icon;
    private final IGuiHelper helper;

    public static final ResourceLocation id = new ResourceLocation(CreateAutomated.modid, "extraction");

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
        return "Ore Extraction"; // TODO: use translation keys
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
        iIngredients.setInput(VanillaTypes.ITEM, new ItemStack(recipe.node.getMatchingStacks()[0].getItem()));// not sure whether i actually need to create a new itemstack but *idc*

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
                    // TODO: use translation keys
                    tooltip.add(new StringTextComponent("From " + recipe.minOre + " to " + recipe.maxOre).formatted(TextFormatting.GOLD));
                } else {
                    stack.setCount(recipe.minOre);
                }
            }
        });
    }

    @Override
    public void draw(ExtractingRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
        oreExtractor.drawWithBlock(matrixStack, 30, 30, ((BlockItem) recipe.node.getMatchingStacks()[0].getItem()).getBlock().getDefaultState());
        AllGuiTextures.JEI_DOWN_ARROW.draw(matrixStack, 80, 30);

        (recipe.minOre > 0 ? AllGuiTextures.JEI_SLOT : AllGuiTextures.JEI_CHANCE_SLOT)
                .draw(matrixStack, 85, 45);
    }

    public static List<IRecipe<?>> getRecipes() {
        return Minecraft.getInstance().world.getRecipeManager()
                .getRecipes()
                .stream()
                .filter(r -> r.getType() == ModRecipeTypes.EXTRACTING)
                .collect(Collectors.toList());
    }

    public static List<ItemStack> getCatalysts() {
        return Arrays.asList(new ItemStack(ModBlocks.ORE_EXTRACTOR_TOP.get()));
    }
}
