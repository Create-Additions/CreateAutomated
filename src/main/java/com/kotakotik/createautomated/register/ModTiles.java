package com.kotakotik.createautomated.register;

import com.kotakotik.createautomated.content.processing.oreExtractor.BottomOreExtractorTile;
import com.kotakotik.createautomated.content.processing.oreExtractor.OreExtractorInstance;
import com.kotakotik.createautomated.content.processing.oreExtractor.OreExtractorRenderer;
import com.kotakotik.createautomated.content.processing.oreExtractor.OreExtractorTile;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.repack.registrate.util.entry.TileEntityEntry;

public class ModTiles {
	public static TileEntityEntry<OreExtractorTile> ORE_EXTRACTOR;
	public static TileEntityEntry<BottomOreExtractorTile> BOTTOM_ORE_EXTRACTOR;

	public static void register(CreateRegistrate registrate) {
		ORE_EXTRACTOR = registrate.tileEntity("ore_extractor", OreExtractorTile::new)
//                .instance(() -> ShaftInstance::new)
				.instance(() -> OreExtractorInstance::new)
				.validBlocks(ModBlocks.ORE_EXTRACTOR_TOP)
				.renderer(() -> OreExtractorRenderer::new)
				.register();

		BOTTOM_ORE_EXTRACTOR = registrate.tileEntity("ore_extractor_bottom", BottomOreExtractorTile::new)
				.validBlocks(ModBlocks.ORE_EXTRACTOR_BOTTOM)
				.register();
	}
}
