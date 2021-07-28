package com.kotakotik.createautomated.compat;

import net.minecraftforge.fml.ModList;

import java.util.Optional;
import java.util.function.Supplier;

public enum ModDependencies {
	KUBEJS;

	// yoink
	public boolean isLoaded() {
		return ModList.get().isLoaded(asId());
	}

	public String asId() {
		return name().toLowerCase();
	}

	public <T> Optional<T> runIfInstalled(Supplier<Supplier<T>> toRun) {
		if (isLoaded())
			return Optional.of(toRun.get().get());
		return Optional.empty();
	}
}
