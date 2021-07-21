package com.kotakotik.createautomated.content.processing.picker;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

public class PickerRenderer extends ItemStackTileEntityRenderer {
	@Override
	public void renderByItem(ItemStack stack, ItemCameraTransforms.TransformType transformType, MatrixStack ms, IRenderTypeBuffer buffer, int light, int overlay) {
		ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
		ClientPlayerEntity player = Minecraft.getInstance().player;
		PickerRenderer.PickerModel mainModel = (PickerModel) itemRenderer.getModel(stack, Minecraft.getInstance().level, null);

		boolean leftHand = transformType == ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND;
		boolean firstPerson = leftHand || transformType == ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND;

		ms.pushPose();
		ms.translate(.5f, .5f, .5f);

		CompoundNBT tag = stack.getOrCreateTag();

		if (tag.contains("Picking")) {
			ms.pushPose();

			if (transformType == ItemCameraTransforms.TransformType.GUI) {
				ms.translate(0.0F, .2f, 1.0F);
				ms.scale(.75f, .75f, .75f);
			} else {
				int modifier = leftHand ? -1 : 1;
				ms.mulPose(Vector3f.YP.rotationDegrees(modifier * 40));
			}

			// Reverse bobbing
			float time = (float) player.getUseItemRemainingTicks();
			if (time / (float) stack.getUseDuration() < 0.8F) {
				float bobbing = -MathHelper.abs(MathHelper.cos(time / 4.0F * (float) Math.PI) * 0.1F);

				if (transformType == ItemCameraTransforms.TransformType.GUI)
					ms.translate(bobbing, bobbing, 0.0F);
				else
					ms.translate(0.0f, bobbing, 0.0F);
			}

			ItemStack toPolish = ItemStack.of(tag.getCompound("Picking"));
			ms.pushPose();
			float s = .5f;
			ms.scale(s, s, s);
			ms.mulPose(Vector3f.XP.rotationDegrees(-15.5f));
			ms.mulPose(Vector3f.YP.rotationDegrees(22.5f));
			itemRenderer.renderStatic(toPolish, ItemCameraTransforms.TransformType.NONE, light, overlay, ms, buffer);

			// why is there no popPose(int count) 🤔
			ms.popPose();
			ms.popPose();
		}

		if (firstPerson) {
			int itemInUseCount = player.getUseItemRemainingTicks();
			if (itemInUseCount > 0) {
				int modifier = leftHand ? -1 : 1;
				ms.translate(modifier * .5f, 0, -.25f);
				ms.mulPose(Vector3f.ZP.rotationDegrees(modifier * 40));
				ms.mulPose(Vector3f.XP.rotationDegrees(modifier * 10));
				ms.mulPose(Vector3f.YP.rotationDegrees(modifier * 90));
			}
		}

		itemRenderer.render(stack, ItemCameraTransforms.TransformType.NONE, false, ms, buffer, light, overlay, mainModel.getOriginalModel());

		ms.popPose();
	}

	public static class PickerModel extends CustomRenderedItemModel {

		public PickerModel(IBakedModel template) {
			super(template, "");
		}

		@Override
		public PickerRenderer createRenderer() {
			return new PickerRenderer();
		}

	}
}
