package com.kotakotik.createautomated.register;

import com.kotakotik.createautomated.CreateAutomated;
import com.kotakotik.createautomated.content.base.IOreExtractorBlock;
import com.kotakotik.createautomated.content.processing.oreExtractor.BottomOreExtractorBlock;
import com.kotakotik.createautomated.content.processing.oreExtractor.OreExtractorItem;
import com.kotakotik.createautomated.content.processing.oreExtractor.OreExtractorTile;
import com.kotakotik.createautomated.content.processing.oreExtractor.TopOreExtractorBlock;
import com.kotakotik.createautomated.content.processing.spongeFrame.SpongeFrameBlock;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllTags;
import com.simibubi.create.content.contraptions.processing.InWorldProcessing;
import com.simibubi.create.foundation.block.BlockStressDefaults;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.simibubi.create.repack.registrate.providers.DataGenContext;
import com.simibubi.create.repack.registrate.providers.RegistrateBlockstateProvider;
import com.simibubi.create.repack.registrate.providers.loot.RegistrateBlockLootTables;
import com.simibubi.create.repack.registrate.util.DataIngredient;
import com.simibubi.create.repack.registrate.util.entry.BlockEntry;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.item.Items;
import net.minecraft.loot.functions.CopyNbt;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

public abstract class ModBlocks extends BlockLootTables {
	public static BlockEntry<TopOreExtractorBlock> ORE_EXTRACTOR_TOP;
	public static BlockEntry<BottomOreExtractorBlock> ORE_EXTRACTOR_BOTTOM;
	public static BlockEntry<SpongeFrameBlock> WET_SPONGE_SAIL;
	public static BlockEntry<SpongeFrameBlock> SPONGE_SAIL;
	public static BlockEntry<SpongeFrameBlock> LAVA_SPONGE_SAIL;
	public static void register(CreateRegistrate registrate) {
		ORE_EXTRACTOR_TOP = registrate.block("ore_extractor", TopOreExtractorBlock::new)
				.properties(AbstractBlock.Properties::noOcclusion)
				.transform(BlockStressDefaults.setImpact(OreExtractorTile.getDefaultStress()))
//                .blockstate(BlockStateGen.directionalBlockProvider(true))
				.blockstate((ctx, prov) -> prov.simpleBlock(ctx.get(), prov.itemModels().getExistingFile(prov.modLoc("block/ore_extractor/top"))))
				.addLayer(() -> RenderType::cutoutMipped)
				.lang("Ore Extractor")
				.item(OreExtractorItem::new)
				.properties(p -> {
					DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> p.setISTER(OreExtractorItem.renderer()));
					return p;
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
				.loot(ModBlocks::oreExtractorLootTable)
				.initialProperties(SharedProperties::stone)
				.register();

		ORE_EXTRACTOR_BOTTOM = registrate.block("ore_extractor_bottom", BottomOreExtractorBlock::new)
				.properties(AbstractBlock.Properties::noOcclusion)
				.lang("Ore Extractor")
				.blockstate((ctx, prov) -> prov.simpleBlock(ctx.get(), prov.itemModels().getExistingFile(prov.modLoc("block/ore_extractor/bottom"))))
				.addLayer(() -> RenderType::cutoutMipped)
				// P A I N
				.loot(ModBlocks::oreExtractorLootTable)
				.initialProperties(SharedProperties::stone)
				.register();

		WET_SPONGE_SAIL = registrate.block("wet_sponge_sail", p -> new SpongeFrameBlock(p, InWorldProcessing.Type.SPLASHING))
				.properties(AbstractBlock.Properties::noOcclusion)
				.tag(AllTags.AllBlockTags.FAN_TRANSPARENT.tag)
				.blockstate(ModBlocks::spongeSailBlockstate)
				.simpleItem()
				.initialProperties(SharedProperties::wooden)
				.register();

		LAVA_SPONGE_SAIL = registrate.block("lava_sponge_sail", p -> new SpongeFrameBlock(p, InWorldProcessing.Type.BLASTING))
				.properties(AbstractBlock.Properties::noOcclusion)
				.tag(AllTags.AllBlockTags.FAN_TRANSPARENT.tag)
				.blockstate(ModBlocks::spongeSailBlockstate)
				.simpleItem()
				.initialProperties(SharedProperties::wooden)
				.register();

		SPONGE_SAIL = registrate.block("sponge_sail", p -> new SpongeFrameBlock(p, InWorldProcessing.Type.NONE))
				.properties(AbstractBlock.Properties::noOcclusion)
				.tag(AllTags.AllBlockTags.FAN_TRANSPARENT.tag)
				.blockstate(ModBlocks::spongeSailBlockstate)
				.simpleItem()
				.initialProperties(SharedProperties::wooden)
				.recipe((ctx, prov) -> {
					ShapelessRecipeBuilder.shapeless(WET_SPONGE_SAIL.get())
							.requires(ctx.get())
							.requires(Items.WATER_BUCKET)
							.unlockedBy("has_sponge_frame", prov.hasItem(ctx.get()))
							.save(prov);

					ShapelessRecipeBuilder.shapeless(WET_SPONGE_SAIL.get(), 4)
							.requires(Blocks.WET_SPONGE)
							.requires(AllBlocks.SAIL_FRAME.get(), 4)
							.unlockedBy("has_sponge", prov.hasItem(Blocks.SPONGE))
							.save(prov, CreateAutomated.asResource("wet_sponge_frame_from_sponge"));

					ShapelessRecipeBuilder.shapeless(LAVA_SPONGE_SAIL.get(), 4)
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

					prov.smelting(DataIngredient.items(WET_SPONGE_SAIL), ctx, 0);
					RecipeItems.SPLASHING.add("wet_sponge_frame", b -> b.require(ctx.get()).output(WET_SPONGE_SAIL.get()));
					RecipeItems.SPLASHING.add("sponge_frame_from_lava", b -> b.require(LAVA_SPONGE_SAIL.get()).output(ctx.get()));
				}).register();
	}

	protected static <T extends Block & IOreExtractorBlock> void oreExtractorLootTable(RegistrateBlockLootTables cons, T block) {
		CopyNbt.Builder nbt = CopyNbt.copyData(CopyNbt.Source.BLOCK_ENTITY);
		for (String nbtKey : block.nbtList()) {
			nbt.copy(nbtKey, "BlockEntityTag." + nbtKey);
		}
		cons.add(block, createSingleItemTable(ORE_EXTRACTOR_TOP.get()).apply(nbt));
	}

	public static <T extends Block> void spongeSailBlockstate(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov) {
		prov.directionalBlock(ctx.get(), prov.models().getExistingFile(prov.modLoc(ctx.getName())));
	}

}
