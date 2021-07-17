package com.kotakotik.createautomated.compat.crafttweaker.contenttweaker;

import com.blamejared.contenttweaker.VanillaFactory;
import com.blamejared.contenttweaker.api.items.IIsCotItem;
import com.blamejared.contenttweaker.api.items.ItemTypeBuilder;
import com.blamejared.contenttweaker.api.resources.ResourceType;
import com.blamejared.contenttweaker.api.resources.WriteableResource;
import com.blamejared.contenttweaker.api.resources.WriteableResourceTemplate;
import com.blamejared.contenttweaker.items.ItemBuilder;
import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.jozufozu.flywheel.core.PartialModel;
import com.kotakotik.createautomated.CreateAutomated;
import com.kotakotik.createautomated.api.DrillPartialIndex;
import com.kotakotik.createautomated.content.simple.drillHead.DrillHeadItem;
import net.minecraft.util.ResourceLocation;
import org.openzen.zencode.java.ZenCodeType;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@ZenRegister(modDeps = {"contenttweaker"})
@ZenCodeType.Name("mods.createautomated.item.DrillHeadBuilder")
public class DrillHeadBuilder extends ItemTypeBuilder {
	public int durability;
	public boolean noPartial = false;

	public boolean generateDrillModel = true;

	public DrillHeadBuilder(ItemBuilder itemBuilder) {
		super(itemBuilder);
	}

	@Override
	public void build(ResourceLocation resourceLocation) {
		CustomDrillHead item = new CustomDrillHead(itemBuilder.getItemProperties(), durability, resourceLocation, this);
		VanillaFactory.queueItemForRegistration(item);
		if(!noPartial) {
			DrillPartialIndex.add(resourceLocation, new PartialModel(new ResourceLocation(resourceLocation.getNamespace(), "block/drills/" + resourceLocation.getPath())));

		}
	}

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

	@ZenCodeType.Method
	public DrillHeadBuilder noDrillModel() {
		generateDrillModel = false;
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
			if(builder.generateDrillModel) {
				resources.add(new WriteableResourceTemplate(ResourceType.ASSETS,
						id, "models", "block", "drills").withTemplate(ResourceType.ASSETS,
						new ResourceLocation(CreateAutomated.MODID, "models/block/drill")).setLocationProperty(id));
			}
			return resources;
		}

		@Nonnull
		@Override
		public Collection<WriteableResource> getDataPackResources() {
			return Collections.emptyList();
		}
	}
}
