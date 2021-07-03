package com.kotakotik.createautomated.register;

import com.kotakotik.createautomated.content.tiles.OreExtractorTile;
import com.simibubi.create.content.contraptions.base.KineticTileEntityRenderer;
import com.simibubi.create.content.contraptions.base.ShaftlessCogInstance;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.repack.registrate.util.entry.TileEntityEntry;

public class ModTiles {
    public static TileEntityEntry<OreExtractorTile> ORE_EXTRACTOR;

    public static void register(CreateRegistrate registrate) {
        ORE_EXTRACTOR = registrate.tileEntity("ore_extractor", OreExtractorTile::new)
//                .instance(() -> ShaftInstance::new)
                .instance(() -> ShaftlessCogInstance::new)
                .validBlocks(ModBlocks.ORE_EXTRACTOR)
                .renderer(() -> KineticTileEntityRenderer::new)
                .register();
    }
}
