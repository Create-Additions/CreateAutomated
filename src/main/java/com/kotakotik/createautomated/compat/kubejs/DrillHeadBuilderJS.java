package com.kotakotik.createautomated.compat.kubejs;

import dev.latvian.kubejs.item.ItemBuilder;

public class DrillHeadBuilderJS extends ItemBuilder {
	public int durability;

	public DrillHeadBuilderJS(String i) {
		super(i);
	}

	public DrillHeadBuilderJS withDurability(int durability) {
		this.durability = durability;
		return this;
	}

	// just to be safe
	public DrillHeadBuilderJS durability(int durability) {
		return withDurability(durability);
	}
}
