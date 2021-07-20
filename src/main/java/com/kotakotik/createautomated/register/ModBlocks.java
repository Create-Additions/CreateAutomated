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
				.properties(AbstractBlock.Properties::noOcclusion)
				.transform(AddonStressConfigDefaults.setImpact(OreExtractorTile.getDefaultStress()))
//                .blockstate(BlockStateGen.directionalBlockProvider(true))
				.blockstate((ctx, prov) -> prov.simpleBlock(ctx.get(), prov.itemModels().getExistingFile(prov.modLoc("block/ore_extractor/top"))))
				.addLayer(() -> RenderType::cutoutMipped)
				.lang("Ore Extractor")
				.item((b, p) -> new BlockItem(b, p) {
					@Nullable
					@Override
					public BlockState getPlacementState(BlockItemUseContext p_195945_1_) {
						BlockState blockstate = ORE_EXTRACTOR_BOTTOM.get().getStateForPlacement(p_195945_1_);
						return blockstate != null && this.canPlace(p_195945_1_, blockstate) ? blockstate : null;
					}
				})
				.recipe((ctx, prov) -> {
					ShapedRecipeBuilder.shaped(ctx.get())
							.pattern("cgc")
							.pattern("bsb")
							.pattern("b b")
							.define('c', AllBlocks.BRASS_CASING.get())
							.define('b', AllItems.BRASS_INGOT.get())
							.define('s', AllBlocks.SHAFT.get())
							.define('g', AllBlocks.COGWHEEL.get())
							.unlockedBy("has_brass_casing", prov.hasItem(AllBlocks.BRASS_CASING.get()))
							.save(prov);
				}).model((ctx, prov) -> prov.withExistingParent(ctx.getName(), prov.modLoc("block/ore_extractor/item"))).build()
				.register();

		ORE_EXTRACTOR_BOTTOM = registrate.block("ore_extractor_bottom", BottomOreExtractorBlock::new)
				.properties(AbstractBlock.Properties::noOcclusion)
				.lang("Ore Extractor")
				.blockstate((ctx, prov) -> prov.simpleBlock(ctx.get(), prov.itemModels().getExistingFile(prov.modLoc("block/ore_extractor/bottom"))))
				.addLayer(() -> RenderType::cutoutMipped)
				.loot((cons, block) -> cons.dropOther(block, ORE_EXTRACTOR_TOP.get()))
				.register();


		WET_SPONGE_FRAME = registrate.block("wet_sponge_frame", p -> new SpongeFrameBlock(p, InWorldProcessing.Type.SPLASHING))
				.properties(AbstractBlock.Properties::noOcclusion)
				.blockstate(($, $$) -> {
				}).tag(AllTags.AllBlockTags.FAN_TRANSPARENT.tag)
				.item().model(($, $$) -> {
				}).build()
				.register();

		LAVA_SPONGE_FRAME = registrate.block("lava_sponge_frame", p -> new SpongeFrameBlock(p, InWorldProcessing.Type.BLASTING))
				.properties(AbstractBlock.Properties::noOcclusion)
				.blockstate(($, $$) -> {
				}).tag(AllTags.AllBlockTags.FAN_TRANSPARENT.tag)
				.item().model(($, $$) -> {
				}).build()
				.register();

		SPONGE_FRAME = registrate.block("sponge_frame", p -> new SpongeFrameBlock(p, null))
				.properties(AbstractBlock.Properties::noOcclusion)
				.blockstate(($, $$) -> {
				}).tag(AllTags.AllBlockTags.FAN_TRANSPARENT.tag)
				.item().model(($, $$) -> {
				}).build().recipe((ctx, prov) -> {
					ShapelessRecipeBuilder.shapeless(WET_SPONGE_FRAME.get())
							.requires(ctx.get())
							.requires(Items.WATER_BUCKET)
							.unlockedBy("has_sponge_frame", prov.hasItem(ctx.get()))
							.save(prov);

					ShapelessRecipeBuilder.shapeless(WET_SPONGE_FRAME.get(), 4)
							.requires(Blocks.WET_SPONGE)
							.requires(AllBlocks.SAIL_FRAME.get(), 4)
							.unlockedBy("has_sponge", prov.hasItem(Blocks.SPONGE))
							.save(prov, CreateAutomated.asResource("wet_sponge_frame_from_sponge"));

					ShapelessRecipeBuilder.shapeless(LAVA_SPONGE_FRAME.get(), 4)
							.requires(Blocks.WET_SPONGE)
							.requires(AllBlocks.SAIL_FRAME.get(), 4)
							.requires(AllItems.BLAZE_CAKE.get())
							.unlockedBy("has_sponge", prov.hasItem(Blocks.SPONGE))
							.save(prov, CreateAutomated.asResource("lava_sponge_frame_from_sponge"));

					ShapelessRecipeBuilder.shapeless(ctx.get(), 4)
							.requires(Blocks.SPONGE)
							.requires(AllBlocks.SAIL_FRAME.get(), 4)
							.unlockedBy("has_sponge", prov.hasItem(Blocks.SPONGE))
							.save(prov);

					prov.smelting(DataIngredient.items(WET_SPONGE_FRAME), ctx, 0);
					RecipeItems.SPLASHING.add("wet_sponge_frame", b -> b.require(ctx.get()).output(WET_SPONGE_FRAME.get()));
					RecipeItems.SPLASHING.add("sponge_frame_from_lava", b -> b.require(LAVA_SPONGE_FRAME.get()).output(ctx.get()));
				}).register();
	}
}
