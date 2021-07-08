package com.kotakotik.createautomated.content.ponder;

import com.kotakotik.createautomated.CreateAutomated;
import com.kotakotik.createautomated.content.tiles.OreExtractorTile;
import com.kotakotik.createautomated.register.RecipeItems;
import com.simibubi.create.foundation.ponder.SceneBuilder;
import com.simibubi.create.foundation.ponder.SceneBuildingUtil;
import com.simibubi.create.foundation.ponder.elements.InputWindowElement;
import com.simibubi.create.foundation.utility.Pointing;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

public class OreExtractorScenes {
    public static void intro(SceneBuilder scene, SceneBuildingUtil util) {
        // Positions
        BlockPos extractorBottom = util.grid.at(2, 1, 2);
        BlockPos extractorTop = extractorBottom.up();
        BlockPos funnel = util.grid.at(2, 2, 1);

        // Configure
        scene.title(CreateAutomated.modid + ".ore_extractor_intro", "Extracting from nodes using Ore Extractors");
        scene.world.setKineticSpeed(util.select.everywhere(), 32);
//        scene.world.hideSection(util.select.layers(1, 3), Direction.DOWN);

        // Resource nodes
        scene.showBasePlate();
        scene.idle(5);
        scene.overlay.showText(80)
                .attachKeyFrame().placeNearTarget().pointAt(util.vector.topOf(extractorBottom.down()))
                .text("Resource nodes can be found in the world, however cannot be moved");
        scene.idle(80);

        // Ore extractors
        scene.world.showSection(util.select.layersFrom(1), Direction.UP);
        scene.overlay.showText(80)
                .attachKeyFrame().placeNearTarget().pointAt(util.vector.centerOf(extractorTop))
                .text("Ore Extractors can be used to extract from resource nodes");
        scene.idle(85);
        scene.overlay.showText(80)
                .attachKeyFrame().placeNearTarget().pointAt(util.vector.topOf(extractorTop.add(-1, 0, 0)))
                .text("However they require high speed and stress capacity");
        scene.idle(20);
        scene.world.setKineticSpeed(util.select.everywhere(), 128);
        scene.effects.rotationSpeedIndicator(extractorTop.up().east());
        scene.effects.indicateSuccess(extractorBottom);
        scene.effects.indicateSuccess(extractorTop);
        scene.idle(60);

        // Drill head insertion
        scene.overlay.showControls((new InputWindowElement(util.vector.centerOf(extractorTop), Pointing.DOWN)).rightClick().withItem(new ItemStack(RecipeItems.DRILL_HEAD.get())), 60);
        scene.idle(10);
        scene.overlay.showText(160).attachKeyFrame().pointAt(util.vector.centerOf(extractorTop)).placeNearTarget().text("Extractors require drill heads to operate, which can be inserted by players by right clicking");
        scene.world.setKineticSpeed(util.select.everywhere(), 128);
        scene.world.modifyTileEntity(extractorTop, OreExtractorTile.class, (entity) -> {
            entity.maxDurability = 300;
            entity.durability = 200;
            entity.markDirty(); // dunno if i should do this... lets do it anyway!
        });

        scene.idle(60);

        scene.world.flapFunnel(funnel, true);
        scene.world.createItemEntity(util.vector.centerOf(funnel), Vector3d.ZERO, new ItemStack(RecipeItems.LAPIS_EXTRACTABLE.ORE_PIECE.get()));
    }

    public static void automation(SceneBuilder scene, SceneBuildingUtil util) {
        // TODO

        // Positions
        BlockPos extractorBottom = util.grid.at(2, 1, 2);
        BlockPos extractorTop = extractorBottom.up();

        // Configure
        scene.title(CreateAutomated.modid + ".ore_extractor_intro", "Extracting from nodes using Ore Extractors");
        scene.world.setKineticSpeed(util.select.everywhere(), 128);
        scene.rotateCameraY((90 - 35) * -1);
    }
}
