package com.kotakotik.createautomated.compat.jei.animations;

import com.kotakotik.createautomated.register.ModItems;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import com.simibubi.create.foundation.gui.GuiGameElement;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import mezz.jei.api.gui.drawable.IDrawable;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3f;

public class AnimatedPicker implements IDrawable {
	@Override
	public int getWidth() {
		return 50;
	}

	@Override
	public int getHeight() {
		return 50;
	}

	ItemStack stack = new ItemStack(ModItems.PICKER.get());

	@Override
	public void draw(MatrixStack matrixStack, int i, int i1) {
		matrixStack.push();
		matrixStack.translate(i, i1, 60);

		float offsetRange = 10;
		float speed = 2;
		float offset = offsetRange - (AnimationTickHolder.getRenderTime() * speed % (offsetRange * 2));
		if(offset < 0) offset *= -1;
		GuiGameElement.of(stack).draw(matrixStack,0, (int) offset);
		matrixStack.pop();
	}
}
