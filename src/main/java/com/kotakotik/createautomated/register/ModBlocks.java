package com.kotakotik.createautomated.register;

import com.kotakotik.createautomated.CreateAutomated;
import com.kotakotik.createautomated.content.processing.oreExtractor.BottomOreExtractorBlock;
import com.kotakotik.createautomated.content.processing.oreExtractor.OreExtractorTile;
import com.kotakotik.createautomated.content.processing.oreExtractor.TopOreExtractorBlock;
import com.kotakotik.createautomated.content.processing.spongeFrame.SpongeFrameBlock;
import com.kotakotik.createautomated.util.AddonStressConfigDefaults;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllTags;
import com.simibubi.create.content.contraptions.processing.InWorldProcessing;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.repack.registrate.util.DataIngredient;
import com.simibubi.create.repack.registrate.util.entry.BlockEntry;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Items;

import javax.annotation.Nullable;

public class ModBlocks {
	public static BlockEntry<TopOreExtractorBlock> ORE_EXTRACTOR_TOP;
	public static BlockEntry<BottomOreExtractorBlock> ORE_EXTRACTOR_BOTTOM;
	public static BlockEntry<SpongeFrameBlock> WET_SPONGE_FRAME;
	public static BlockEntry<SpongeFrameBlock> SPONGE_FRAME;
	public static BlockEntry<SpongeFrameBlock> LAVA_SPONGE_FRAME;

	public static void register(CreateRegistrate registrate) {
		ORE_EXTRACTOR_TOP = registrate.block("ore_extractor", TopOreExtractorBlock::new)
				.properties(AbstractBlock.Properties::nonOpaque)
				.transform(AddonStressConfigDefaults.setImpact(OreExtractorTile.getDefaultStress()))
//                .blockstate(BlockStateGen.directionalBlockProvider(true))
				.blockstate((ctx, prov) -> prov.simpleBlock(ctx.get(), prov.itemModels().getExistingFile(prov.modLoc("block/ore_extractor/top"))))
				.addLayer(() -> RenderType::getCutoutMipped)
				.lang("Ore Extractor")
				.item((b, p) -> new BlockItem(b, p) {
					@Nullable
					@Override
					public BlockState getStateForPlacement(BlockItemUseContext p_195945_1_) {
						BlockState blockstate = ORE_EXTRACTOR_BOTTOM.get().getStateForPlacement(p_195945_1_);
						return blockstate != null && this.canPlace(p_195945_1_, blockstate) ? blockstate : null;
					}
				})
				.recipe((ctx, prov) -> {
					ShapedRecipeBuilder.shapedRecipe(ctx.get())
							.patternLine("cgc")
							.patternLine("bsb")
							.patternLine("b b")
							.key('c', AllBlocks.BRASS_CASING.get())
							.key('b', AllItems.BRASS_INGOT.get())
							.key('s', AllBlocks.SHAFT.get())
							.key('g', AllBlocks.COGWHEEL.get())
							.addCriterion("has_brass_casing", prov.hasItem(AllBlocks.BRASS_CASING.get()))
							.build(prov);
				}).model((ctx, prov) -> prov.withExistingParent(ctx.getName(), prov.modLoc("block/ore_extractor/item"))).build()
				.register();

		ORE_EXTRACTOR_BOTTOM = registrate.block("ore_extractor_bottom", BottomOreExtractorBlock::new)
				.properties(AbstractBlock.Properties::nonOpaque)
				.lang("Ore Extractor")
				.blockstate((ctx, prov) -> prov.simpleBlock(ctx.get(), prov.itemModels().getExistingFile(prov.modLoc("block/ore_extractor/bottom"))))
				.addLayer(() -> RenderType::getCutoutMipped)
				.loot((cons, block) -> cons.registerDropping(block, ORE_EXTRACTOR_TOP.get()))
				.register();


		WET_SPONGE_FRAME = registrate.block("wet_sponge_frame", p -> new SpongeFrameBlock(p, InWorldProcessing.Type.SPLASHING))
				.properties(AbstractBlock.Properties::nonOpaque)
				.blockstate(($, $$) -> {
				}).tag(AllTags.AllBlockTags.FAN_TRANSPARENT.tag)
				.item().model(($, $$) -> {
				}).build()
				.register();

		LAVA_SPONGE_FRAME = registrate.block("lava_sponge_frame", p -> new SpongeFrameBlock(p, InWorldProcessing.Type.BLASTING))
				.properties(AbstractBlock.Properties::nonOpaque)
				.blockstate(($, $$) -> {
				}).tag(AllTags.AllBlockTags.FAN_TRANSPARENT.tag)
				.item().model(($, $$) -> {
				}).build()
				.register();

		SPONGE_FRAME = registrate.block("sponge_frame", p -> new SpongeFrameBlock(p, null))
				.properties(AbstractBlock.Properties::nonOpaque)
				.blockstate(($, $$) -> {
				}).tag(AllTags.AllBlockTags.FAN_TRANSPARENT.tag)
				.item().model(($, $$) -> {
				}).build().recipe((ctx, prov) -> {
					ShapelessRecipeBuilder.shapelessRecipe(WET_SPONGE_FRAME.get())
							.addIngredient(ctx.get())
							.addIngredient(Items.WATER_BUCKET)
							.addCriterion("has_sponge_frame", prov.hasItem(ctx.get()))
							.build(prov);

					ShapelessRecipeBuilder.shapelessRecipe(WET_SPONGE_FRAME.get(), 4)
							.addIngredient(Blocks.WET_SPONGE)
							.addIngredient(AllBlocks.SAIL_FRAME.get(), 4)
							.addCriterion("has_sponge", prov.hasItem(Blocks.SPONGE))
							.build(prov, CreateAutomated.asResource("wet_sponge_frame_from_sponge"));

					ShapelessRecipeBuilder.shapelessRecipe(LAVA_SPONGE_FRAME.get(), 4)
							.addIngredient(Blocks.WET_SPONGE)
							.addIngredient(AllBlocks.SAIL_FRAME.get(), 4)
							.addIngredient(AllItems.BLAZE_CAKE.get())
							.addCriterion("has_sponge", prov.hasItem(Blocks.SPONGE))
							.build(prov, CreateAutomated.asResource("lava_sponge_frame_from_sponge"));

					ShapelessRecipeBuilder.shapelessRecipe(ctx.get(), 4)
							.addIngredient(Blocks.SPONGE)
							.addIngredient(AllBlocks.SAIL_FRAME.get(), 4)
							.addCriterion("has_sponge", prov.hasItem(Blocks.SPONGE))
							.build(prov);

					prov.smelting(DataIngredient.items(WET_SPONGE_FRAME), ctx, 0);
					RecipeItems.SPLASHING.add("wet_sponge_frame", b -> b.require(ctx.get()).output(WET_SPONGE_FRAME.get()));
					RecipeItems.SPLASHING.add("sponge_frame_from_lava", b -> b.require(LAVA_SPONGE_FRAME.get()).output(ctx.get()));
				}).register();
	}
}
