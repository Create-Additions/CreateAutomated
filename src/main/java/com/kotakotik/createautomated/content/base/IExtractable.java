package com.kotakotik.createautomated.content.base;

import com.kotakotik.createautomated.content.recipe.extracting.ExtractingRecipe;
import com.kotakotik.createautomated.content.tiles.OreExtractorTile;

import java.util.Optional;

public interface IExtractable {
    void extractTick(OreExtractorTile oreExtractorTile, Optional<ExtractingRecipe> recipe);
}
