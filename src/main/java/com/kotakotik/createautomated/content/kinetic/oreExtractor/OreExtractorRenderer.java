package com.kotakotik.createautomated.content.kinetic.oreExtractor;

import com.kotakotik.createautomated.register.ModBlockPartials;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import com.simibubi.create.content.contraptions.base.KineticTileEntityRenderer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import com.simibubi.create.foundation.render.backend.FastRenderDispatcher;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

public class OreExtractorRenderer extends KineticTileEntityRenderer {
	public OreExtractorRenderer(TileEntityRendererDispatcher dispatcher) {
		super(dispatcher);
	}

	public boolean isGlobalRenderer(KineticTileEntity te) {
		return true;
	}

	protected void renderSafe(KineticTileEntity te, float partialTicks, MatrixStack ms, IRenderTypeBuffer buffer, int light, int overlay) {
		if (!FastRenderDispatcher.available(te.getWorld())) {
			BlockState blockState = te.getBlockState();
			OreExtractorTile tile = (OreExtractorTile) te;
			BlockPos pos = te.getPos();
			IVertexBuilder vb = buffer.getBuffer(RenderType.getSolid());
			SuperByteBuffer superBuffer = ModBlockPartials.COGWHEEL.renderOn(blockState);
			standardKineticRotationTransform(superBuffer, te, light).renderInto(ms, vb);
			if (tile.extractProgress > 0) {
				int packedLightmapCoords = WorldRenderer.getLightmapCoordinates(te.getWorld(), blockState, pos);
				float speed = Math.abs(tile.getSpeed());
				float time = AnimationTickHolder.getRenderTime(te.getWorld());
				float angle = ((time * speed * 6 / 10f) % 360) / 180 * (float) Math.PI;
				SuperByteBuffer headRender = ModBlockPartials.DRILL_ORE_EXTRACTOR.renderOn(blockState);
				// dunno if i can use standardKineticRotationTransform here?
				headRender.rotateCentered(Direction.UP, angle).translate(0, -1, 0).light(packedLightmapCoords).renderInto(ms, vb);
			}
		}
	}
}
