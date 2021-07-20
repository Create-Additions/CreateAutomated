package com.kotakotik.createautomated.compat.jei.animations;

import com.kotakotik.createautomated.register.ModBlockPartials;
import com.kotakotik.createautomated.register.ModBlocks;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.GuiGameElement;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.vector.Vector3f;

import javax.annotation.Nullable;

public class AnimatedOreExtractor extends AnimatedKinetics {
	public static float getCurrentAngle() {
		return AnimationTickHolder.getRenderTime() * 32 % 360;
	}

	@Override
	public void draw(MatrixStack matrixStack, int xOffset, int yOffset) {
		drawWithBlock(matrixStack, xOffset, yOffset, null);
	}

	public void drawWithBlock(MatrixStack matrixStack, int xOffset, int yOffset, @Nullable BlockState block) {
		matrixStack.pushPose();

		float shadowScale = 1.3f;
		matrixStack.scale(shadowScale, shadowScale, shadowScale);

		AllGuiTextures.JEI_SHADOW.draw(matrixStack, (int) (xOffset / shadowScale - 11), (int) (yOffset / shadowScale + 38));

		matrixStack.popPose();
		matrixStack.pushPose();
		matrixStack.translate(xOffset, yOffset, 200);
		matrixStack.mulPose(Vector3f.XP.rotationDegrees(-15.5f));
		matrixStack.mulPose(Vector3f.YP.rotationDegrees(22.5f));

//        matrixStack.scale(0.5f,0.5f,0.5f);

		int scale = 28;

		GuiGameElement.of(AllBlocks.COGWHEEL.getDefaultState()) // default cogwheel() method returns a shaftless one
				.rotateBlock(0, getCurrentAngle(), 0)
				.atLocal(0, 0, 0)
				.scale(scale)
				.render(matrixStack);

		GuiGameElement.of(ModBlocks.ORE_EXTRACTOR_BOTTOM.getDefaultState())
				.atLocal(0, 1, 0)
				.scale(scale)
				.render(matrixStack);

		GuiGameElement.of(ModBlocks.ORE_EXTRACTOR_TOP.getDefaultState())
				.atLocal(0, 0, 0)
				.scale(scale)
				.render(matrixStack);

		GuiGameElement.of(ModBlockPartials.DRILL_ORE_EXTRACTOR)
				.rotateBlock(0, getCurrentAngle(), 0)
				.atLocal(0, 1, 0)
				.scale(scale)
				.render(matrixStack);

		if (block != null) {
			GuiGameElement.of(block)
					.atLocal(0, 2, 0)
					.scale(scale)
					.render(matrixStack);
		}


		matrixStack.popPose();
	}
}
