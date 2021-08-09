package com.kotakotik.createautomated.compat.crafttweaker.contenttweaker;

import com.blamejared.contenttweaker.VanillaFactory;
import com.blamejared.contenttweaker.api.items.IIsCotItem;
import com.blamejared.contenttweaker.api.items.ItemTypeBuilder;
import com.blamejared.contenttweaker.api.resources.WriteableResource;
import com.blamejared.contenttweaker.items.ItemBuilder;
import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import com.jozufozu.flywheel.core.PartialModel;
import com.kotakotik.createautomated.api.DrillPartialIndex;
import com.kotakotik.createautomated.content.simple.drillHead.DrillHeadItem;
import net.minecraft.util.ResourceLocation;
import org.openzen.zencode.java.ZenCodeType;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Test
 */
@ZenRegister(modDeps = {"contenttweaker"})
@ZenCodeType.Name("mods.createautomated.item.DrillHeadBuilder")
@Document("mods/createautomated/DrillHeadBuilder")
public class DrillHeadBuilder extends ItemTypeBuilder {
	public int durability;
	public boolean noPartial = false;

	public DrillHeadBuilder(ItemBuilder itemBuilder) {
		super(itemBuilder);
	}

	@Override
	public void build(ResourceLocation resourceLocation) {
		CustomDrillHead item = new CustomDrillHead(itemBuilder.getItemProperties(), durability, resourceLocation, this);
		VanillaFactory.queueItemForRegistration(item);
		if (!noPartial) {
			DrillPartialIndex.add(resourceLocation, new PartialModel(new ResourceLocation(resourceLocation.getNamespace(), "block/drills/" + resourceLocation.getPath())));
		}
	}

	/**
	 * ee
	 *
	 * @param newDurability Eeeeeeee
	 * @return eeeeee
	 */
	@ZenCodeType.Method
	public DrillHeadBuilder durability(int newDurability) {
		this.durability = newDurability;
		return this;
	}

	@ZenCodeType.Method
	public DrillHeadBuilder noPartial() {
		noPartial = true;
		return this;
	}

	public static class CustomDrillHead extends DrillHeadItem implements IIsCotItem {
		public int durability;
		protected DrillHeadBuilder builder;

		public CustomDrillHead(Properties p_i48487_1_, int durability, ResourceLocation id, DrillHeadBuilder builder) {
			super(p_i48487_1_);
			this.durability = durability;
			this.builder = builder;
			setRegistryName(id);
		}

		@Override
		public int getDurability() {
			return durability;
		}

		@Nonnull
		@Override
		public Collection<WriteableResource> getResourcePackResources() {
			List<WriteableResource> resources = new ArrayList<>();
			ResourceLocation id = getRegistryNameNonNull();
			return resources;
		}

		@Nonnull
		@Override
		public Collection<WriteableResource> getDataPackResources() {
			return Collections.emptyList();
		}
	}
}
