package com.kotakotik.createautomated.api;

import net.minecraft.util.ResourceLocation;

import java.util.HashMap;

public class NodeInfo {
	public interface Entry {
		boolean isInfinite();

		int getCount();

		boolean randomizeDamage();
	}

	public static final HashMap<ResourceLocation, Entry> info = new HashMap<>();
}
