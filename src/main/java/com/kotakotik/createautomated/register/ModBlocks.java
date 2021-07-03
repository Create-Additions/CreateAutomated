package com.kotakotik.createautomated.register;

import com.kotakotik.createautomated.content.blocks.NodeBlock;
import com.kotakotik.createautomated.content.blocks.OreExtractorBlock;
import com.simibubi.create.foundation.config.StressConfigDefaults;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.simibubi.create.repack.registrate.util.entry.BlockEntry;
import net.minecraft.client.renderer.RenderType;

public class ModBlocks {
    public static BlockEntry<OreExtractorBlock> ORE_EXTRACTOR;

    public static BlockEntry<NodeBlock> LAPIS_NODE;

    public static void register(CreateRegistrate registrate) {
        ORE_EXTRACTOR = registrate.block("ore_extractor", OreExtractorBlock::new)
                .initialProperties(SharedProperties::stone)
//                .blockstate(BlockStateGen.directionalBlockProvider(true))
                .blockstate(($, $$) -> {
                })
                .addLayer(() -> RenderType::getCutoutMipped)
                .transform(StressConfigDefaults.setCapacity(16.0))
                .transform(StressConfigDefaults.setImpact(2.0))
                .item().model(($, $$) -> {
                }).build()
                .register();

//        LAPIS_NODE = registrate.block("lapis_node", p -> new NodeBlock(p, ModItems.LAPIS_ORE_PIECE, 1, OreExtractorBlock.ExtractorProgressBuilder.atSpeedOfS(128).takesSeconds(10).build())).tag(ModTags.Blocks.NODES).simpleItem().register();
    }
}
