package com.kotakotik.createautomated.register;

import com.kotakotik.createautomated.content.processing.oreExtractor.BottomOreExtractorTile;
import com.kotakotik.createautomated.content.processing.oreExtractor.OreExtractorInstance;
import com.kotakotik.createautomated.content.processing.oreExtractor.OreExtractorRenderer;
import com.kotakotik.createautomated.content.processing.oreExtractor.OreExtractorTile;
import com.kotakotik.createautomated.content.simple.node.NodeTile;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.CreateTileEntityBuilder;
import com.simibubi.create.repack.registrate.util.entry.TileEntityEntry;
import com.simibubi.create.repack.registrate.util.nullness.NonNullSupplier;
import net.minecraft.block.Block;

import java.util.ArrayList;
import java.util.List;

public class ModTiles {
	public static TileEntityEntry<OreExtractorTile> ORE_EXTRACTOR;
	public static TileEntityEntry<BottomOreExtractorTile> BOTTOM_ORE_EXTRACTOR;
	public static TileEntityEntry<NodeTile> NODE;

	public static List<NonNullSupplier<Block>> allowedNodeBlocks = new ArrayList<>();

	public static void register(CreateRegistrate registrate) {
		ORE_EXTRACTOR = registrate.tileEntity("ore_extractor", OreExtractorTile::new)
				.instance(() -> OreExtractorInstance::new)
				.validBlocks(ModBlocks.ORE_EXTRACTOR_TOP)
				.renderer(() -> OreExtractorRenderer::new)
				.register();

		BOTTOM_ORE_EXTRACTOR = registrate.tileEntity("ore_extractor_bottom", BottomOreExtractorTile::new)
				.validBlocks(ModBlocks.ORE_EXTRACTOR_BOTTOM)
				.register();

		CreateTileEntityBuilder<NodeTile, CreateRegistrate> nodeBuilder = registrate.tileEntity("node", NodeTile::new);
		for (RecipeItems.ExtractableResource extractable : RecipeItems.EXTRACTABLES) {
			nodeBuilder.validBlock(extractable.NODE);
		}
		for (NonNullSupplier<Block> block : allowedNodeBlocks) {
			nodeBuilder.validBlock(block);
		}
		nodeBuilder.validBlocks(allowedNodeBlocks.toArray(new NonNullSupplier[0]));
		NODE = nodeBuilder.register();
	}
}
