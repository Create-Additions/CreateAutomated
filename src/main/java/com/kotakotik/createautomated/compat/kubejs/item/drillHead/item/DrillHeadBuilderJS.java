package com.kotakotik.createautomated.compat.kubejs.item.drillHead.item;

import dev.latvian.kubejs.item.ItemBuilder;

public class DrillHeadBuilderJS extends ItemBuilder {
	public int durability;
	public boolean ignoreDamage = false;

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

	public DrillHeadBuilderJS ignoreDamage(boolean value) {
		ignoreDamage = value;
		return this;
	}

	public DrillHeadBuilderJS ignoreDamage() {
		return ignoreDamage(true);
	}

	public DrillHeadBuilderJS takeDamage(boolean value) {
		return ignoreDamage(!value);
	}

	public DrillHeadBuilderJS takeDamage() {
		return takeDamage(true);
	}

	public DrillHeadBuilderJS infinite(boolean value) {
		return (ignoreDamage(value));
	}

	public DrillHeadBuilderJS infinite() {
		return infinite((true));
	}
}
