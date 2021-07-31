package com.kotakotik.createautomated.content.processing.oreExtractor;

import com.jozufozu.flywheel.core.PartialModel;
import com.kotakotik.createautomated.api.DrillPartialIndex;
import com.kotakotik.createautomated.register.ModBlockPartials;
import com.kotakotik.createautomated.register.ModBlocks;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.simibubi.create.foundation.render.PartialBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

public class OreExtractorItemRenderer extends ItemStackTileEntityRenderer {
	public static OreExtractorItemRenderer INSTANCE;

	public OreExtractorItemRenderer() {
		INSTANCE = this;
	}

	public BlockState state = ModBlocks.ORE_EXTRACTOR_TOP.getDefaultState();

	float offset = 1.2f;

	@Override
	public void renderByItem(ItemStack stack, ItemCameraTransforms.TransformType p_239207_2_, MatrixStack ms, IRenderTypeBuffer buffer, int p_239207_5_, int p_239207_6_) {
		offset = 1.4f;
		IVertexBuilder vb = buffer.getBuffer(RenderType.cutout());
		PartialBufferer.get(ModBlockPartials.ORE_EXTRACTOR_TOP, state)
				.translate(0, offset, 0)
				.renderInto(ms, vb);
		PartialBufferer.get(ModBlockPartials.ORE_EXTRACTOR_BOTTOM, state)
				.translate(0, -1 + offset, 0)
				.renderInto(ms, vb);
		PartialModel cogModel = ModBlockPartials.COGWHEEL;
		if (!stack.isEmpty()) {
			CompoundNBT tag = stack.getOrCreateTag();
			if (tag.contains("BlockEntityTag")) {
				CompoundNBT tileData = tag.getCompound("BlockEntityTag");
				if (tileData.contains("DrillId") && tileData.getInt("Durability") != 0) {
					ResourceLocation drillId = new ResourceLocation(tileData.getString("DrillId"));
					SuperByteBuffer headRender = PartialBufferer.get(DrillPartialIndex.get(drillId), state);
					headRender.translate(0, -1 + offset, 0).renderInto(ms, vb);
					cogModel = ModBlockPartials.HALF_SHAFT_COGWHEEL;
				}
			}
		}
		PartialBufferer.get(cogModel, state)
				.translate(0, offset, 0)
				.renderInto(ms, vb);
		super.renderByItem(stack, p_239207_2_, ms, buffer, p_239207_5_, p_239207_6_);
	}
}
