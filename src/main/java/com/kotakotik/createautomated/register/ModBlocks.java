package com.kotakotik.createautomated.register;

import com.kotakotik.createautomated.content.processing.oreExtractor.BottomOreExtractorBlock;
import com.kotakotik.createautomated.content.processing.oreExtractor.OreExtractorTile;
import com.kotakotik.createautomated.content.processing.oreExtractor.TopOreExtractorBlock;
import com.kotakotik.createautomated.util.AddonStressConfigDefaults;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.repack.registrate.util.entry.BlockEntry;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;

import javax.annotation.Nullable;

public class ModBlocks {
	public static BlockEntry<TopOreExtractorBlock> ORE_EXTRACTOR_TOP;
	public static BlockEntry<BottomOreExtractorBlock> ORE_EXTRACTOR_BOTTOM;

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

//        LAPIS_NODE = registrate.block("lapis_node", p -> new NodeBlock(p, ModItems.LAPIS_ORE_PIECE, 1, OreExtractorBlock.ExtractorProgressBuilder.atSpeedOfS(128).takesSeconds(10).build())).tag(ModTags.Blocks.NODES).simpleItem().register();
	}
}
