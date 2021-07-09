package com.kotakotik.createautomated.content.base;

import com.kotakotik.createautomated.content.kinetic.oreExtractor.ExtractingRecipe;
import com.kotakotik.createautomated.content.kinetic.oreExtractor.OreExtractorTile;

import java.util.Optional;

public interface IExtractable {
    void extractTick(OreExtractorTile oreExtractorTile, Optional<ExtractingRecipe> recipe);
}
