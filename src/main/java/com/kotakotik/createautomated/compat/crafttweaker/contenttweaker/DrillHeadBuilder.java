package com.kotakotik.createautomated.compat.crafttweaker.contenttweaker;

import com.blamejared.contenttweaker.VanillaFactory;
import com.blamejared.contenttweaker.api.items.IIsCotItem;
import com.blamejared.contenttweaker.api.items.ItemTypeBuilder;
import com.blamejared.contenttweaker.api.resources.WriteableResource;
import com.blamejared.contenttweaker.blocks.BlockBuilder;
import com.blamejared.contenttweaker.items.ItemBuilder;
import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.impl.actions.tags.ActionTagAdd;
import com.blamejared.crafttweaker.impl.tag.MCTag;
import com.blamejared.crafttweaker.impl.tag.manager.TagManager;
import com.blamejared.crafttweaker.impl.tag.manager.TagManagerItem;
import com.kotakotik.createautomated.CreateAutomated;
import com.kotakotik.createautomated.content.simple.drillHead.DrillHeadItem;
import com.kotakotik.createautomated.register.RecipeItems;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import org.openzen.zencode.java.ZenCodeType;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

@ZenRegister(modDeps = {"contenttweaker"})
@ZenCodeType.Name("mods.createautomated.item.DrillHeadBuilder")
public class DrillHeadBuilder extends ItemTypeBuilder {
	public String tier;
	public int durability;
	public boolean addTag;

	public DrillHeadBuilder(ItemBuilder itemBuilder) {
		super(itemBuilder);
	}

	@Override
	public void build(ResourceLocation resourceLocation) {
		CustomDrillHead item = new CustomDrillHead(itemBuilder.getItemProperties(), durability, resourceLocation);
		VanillaFactory.queueItemForRegistration(item);
		if(addTag) {
			TagManagerItem.INSTANCE.addElements(new MCTag<>(CreateAutomated.asResource("drill_heads"), TagManagerItem.INSTANCE), Arrays.asList(item));
			if(tier != null) TagManagerItem.INSTANCE.addElements(new MCTag<>(CreateAutomated.asResource("drill_heads/"+tier), TagManagerItem.INSTANCE), Arrays.asList(item));
		}
	}

	@ZenCodeType.Method
	public DrillHeadBuilder tier(String newTier) {
		this.tier = newTier;
		return this;
	}

	@ZenCodeType.Method
	public DrillHeadBuilder durability(int newDurability) {
		this.durability = newDurability;
		return this;
	}

	@ZenCodeType.Method
	public DrillHeadBuilder noTag() {
		addTag = false;
		return this;
	}

	public static class CustomDrillHead extends DrillHeadItem implements IIsCotItem {
		public int durability;

		public CustomDrillHead(Properties p_i48487_1_, int durability, ResourceLocation id) {
			super(p_i48487_1_);
			this.durability = durability;
			setRegistryName(id);
		}

		@Override
		public int getDurability() {
			return durability;
		}

		@Nonnull
		@Override
		public Collection<WriteableResource> getResourcePackResources() {
			return Collections.emptyList();
		}

		@Nonnull
		@Override
		public Collection<WriteableResource> getDataPackResources() {
			return Collections.emptyList();
		}
	}
}
