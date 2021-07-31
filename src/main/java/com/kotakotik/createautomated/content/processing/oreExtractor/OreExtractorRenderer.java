package com.kotakotik.createautomated.content.processing.oreExtractor;

import com.jozufozu.flywheel.backend.Backend;
import com.kotakotik.createautomated.api.DrillPartialIndex;
import com.kotakotik.createautomated.register.ModBlockPartials;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import com.simibubi.create.content.contraptions.base.KineticTileEntityRenderer;
import com.simibubi.create.foundation.render.PartialBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.BlockPos;

public class OreExtractorRenderer extends KineticTileEntityRenderer {
	public OreExtractorRenderer(TileEntityRendererDispatcher dispatcher) {
		super(dispatcher);
	}

	public boolean shouldRenderOffScreen(KineticTileEntity te) {
		return true;
	}

	protected void renderSafe(KineticTileEntity te, float partialTicks, MatrixStack ms, IRenderTypeBuffer buffer, int light, int overlay) {
		if (!Backend.getInstance().canUseInstancing(te.getLevel())) {
			BlockState blockState = te.getBlockState();
			OreExtractorTile tile = (OreExtractorTile) te;
			BlockPos pos = te.getBlockPos();
			IVertexBuilder vb = buffer.getBuffer(RenderType.cutout());
			SuperByteBuffer superBuffer = PartialBufferer.get(tile.durability > 0 ? ModBlockPartials.HALF_SHAFT_COGWHEEL : ModBlockPartials.COGWHEEL, blockState);
			standardKineticRotationTransform(superBuffer, te, light).renderInto(ms, vb);
			if (tile.durability > 0) {
				int packedLightmapCoords = WorldRenderer.getLightColor(te.getLevel(), blockState, pos);
				SuperByteBuffer headRender = PartialBufferer.get(DrillPartialIndex.get(tile.drillId), blockState);
				// dunno if i can use standardKineticRotationTransform here?
				standardKineticRotationTransform(headRender, te, packedLightmapCoords).translate(0, -1 + tile.drillPos, 0).renderInto(ms, vb);
			}
		}
	}
}
