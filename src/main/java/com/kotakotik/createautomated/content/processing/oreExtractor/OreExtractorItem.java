package com.kotakotik.createautomated.content.processing.oreExtractor;

import com.kotakotik.createautomated.register.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;

import javax.annotation.Nullable;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

public class OreExtractorItem extends BlockItem {
	public OreExtractorItem(TopOreExtractorBlock b, Properties p) {
		super(b, p);
	}

	@Nullable
	@Override
	public BlockState getPlacementState(BlockItemUseContext p_195945_1_) {
		BlockState blockstate = ModBlocks.ORE_EXTRACTOR_BOTTOM.get().getStateForPlacement(p_195945_1_);
		return blockstate != null && this.canPlace(p_195945_1_, blockstate) ? blockstate : null;
	}

	@Override
	protected boolean placeBlock(BlockItemUseContext ctx, BlockState state) {
		boolean p = super.placeBlock(ctx, state);
		if (p) {
			ModBlocks.ORE_EXTRACTOR_BOTTOM.get().setPlacedBy(ctx.getLevel(), ctx.getClickedPos(), state, ctx.getPlayer(), ctx.getItemInHand());
			updateCustomBlockEntityTag(ctx.getLevel(), ctx.getPlayer(), ctx.getClickedPos().above(), ctx.getItemInHand());
		}
		return p;
	}

	public static OreExtractorItemRenderer getRenderer() {
		return OreExtractorItemRenderer.INSTANCE == null ? new OreExtractorItemRenderer() : OreExtractorItemRenderer.INSTANCE;
	}

	public static Supplier<Callable<ItemStackTileEntityRenderer>> renderer() {
		return () -> OreExtractorItem::getRenderer;
	}
}
