package com.kotakotik.createautomated.register;

import com.kotakotik.createautomated.content.kinetic.oreExtractor.BottomOreExtractorBlock;
import com.kotakotik.createautomated.content.kinetic.oreExtractor.OreExtractorTile;
import com.kotakotik.createautomated.content.kinetic.oreExtractor.TopOreExtractorBlock;
import com.kotakotik.createautomated.content.simple.node.NodeBlock;
import com.kotakotik.createautomated.util.AddonStressConfigDefaults;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.repack.registrate.util.entry.BlockEntry;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;

import javax.annotation.Nullable;

public class ModBlocks {
    public static BlockEntry<TopOreExtractorBlock> ORE_EXTRACTOR_TOP;
    public static BlockEntry<BottomOreExtractorBlock> ORE_EXTRACTOR_BOTTOM;

    public static BlockEntry<NodeBlock> LAPIS_NODE;

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
                }).model((ctx, prov) -> prov.withExistingParent(ctx.getName(), prov.modLoc("block/ore_extractor/item"))).build()
                .register();

        ORE_EXTRACTOR_BOTTOM = registrate.block("ore_extractor_bottom", BottomOreExtractorBlock::new)
                .properties(AbstractBlock.Properties::nonOpaque)
                .lang("Ore Extractor")
                .blockstate((ctx, prov) -> prov.simpleBlock(ctx.get(), prov.itemModels().getExistingFile(prov.modLoc("block/ore_extractor/bottom"))))
                .addLayer(() -> RenderType::getCutoutMipped)
                .loot((cons, block) -> cons.registerDropping(block, ORE_EXTRACTOR_TOP.get()))
                .register();

//        LAPIS_NODE = registrate.block("lapis_node", p -> new NodeBlock(p, ModItems.LAPIS_ORE_PIECE, 1, OreExtractorBlock.ExtractorProgressBuilder.atSpeedOfS(128).takesSeconds(10).build())).tag(ModTags.Blocks.NODES).simpleItem().register();
    }
}
