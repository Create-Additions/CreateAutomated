package com.kotakotik.createautomated.content.ponder;

import com.kotakotik.createautomated.CreateAutomated;
import com.kotakotik.createautomated.content.processing.oreExtractor.OreExtractorTile;
import com.kotakotik.createautomated.content.simple.drillHead.DrillHeadItem;
import com.kotakotik.createautomated.register.RecipeItems;
import com.simibubi.create.content.contraptions.components.deployer.DeployerTileEntity;
import com.simibubi.create.foundation.ponder.ElementLink;
import com.simibubi.create.foundation.ponder.SceneBuilder;
import com.simibubi.create.foundation.ponder.SceneBuildingUtil;
import com.simibubi.create.foundation.ponder.elements.EntityElement;
import com.simibubi.create.foundation.ponder.elements.InputWindowElement;
import com.simibubi.create.foundation.utility.Pointing;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

public class OreExtractorScenes {
	public static void intro(SceneBuilder scene, SceneBuildingUtil util) {
		// Positions
		BlockPos extractorBottom = util.grid.at(1, 1, 1);
		BlockPos extractorTop = extractorBottom.above();
		BlockPos funnel = util.grid.at(1, 2, 0);

		// Configure
		scene.title(CreateAutomated.MODID + ".ore_extractor.intro", "Extracting from nodes using Ore Extractors");
		scene.world.setKineticSpeed(util.select.everywhere(), 32);
//        scene.world.hideSection(util.select.layers(1, 3), Direction.DOWN);

		// Resource nodes
		scene.showBasePlate();
		scene.idle(5);
		scene.overlay.showText(80)
				.attachKeyFrame().placeNearTarget().pointAt(util.vector.topOf(extractorBottom.below()))
				.text("Resource nodes can be found in the world, however cannot be moved");
		scene.idle(80);

		// Ore extractors
		scene.world.showSection(util.select.layersFrom(1), Direction.DOWN);
		scene.overlay.showText(80)
				.attachKeyFrame().placeNearTarget().pointAt(util.vector.centerOf(extractorTop))
				.text("Ore Extractors can be used to extract from resource nodes");
		scene.idle(85);
		scene.overlay.showText(80)
				.attachKeyFrame().placeNearTarget().pointAt(util.vector.topOf(extractorTop.offset(-1, 0, 0)))
				.text("However they require high speed and stress capacity");
		scene.idle(20);
		scene.world.setKineticSpeed(util.select.everywhere(), 128);
		scene.effects.rotationSpeedIndicator(extractorTop.above().east());
		scene.effects.indicateSuccess(extractorBottom);
		scene.effects.indicateSuccess(extractorTop);
		scene.idle(60);

		// Drill head insertion
		scene.overlay.showControls((new InputWindowElement(util.vector.centerOf(extractorTop), Pointing.DOWN)).rightClick().withItem(new ItemStack(RecipeItems.DRILL_HEAD.item.get())), 60);
		scene.idle(10);
		scene.overlay.showText(160).attachKeyFrame().pointAt(util.vector.centerOf(extractorTop)).placeNearTarget().text("Extractors require drill heads to operate, which can be inserted by players by right clicking");
		scene.world.setKineticSpeed(util.select.everywhere(), 128);
		scene.world.modifyTileEntity(extractorTop, OreExtractorTile.class, (entity) -> {
			entity.setDrill(RecipeItems.DRILL_HEAD.item.get());
			entity.setChanged(); // dunno if i should do this... lets do it anyway!
		});

		scene.idle(60);

		scene.world.flapFunnel(funnel, true);
		ElementLink<EntityElement> piece = scene.world.createItemEntity(util.vector.centerOf(funnel), Vector3d.ZERO, new ItemStack(RecipeItems.LAPIS_EXTRACTABLE.ORE_PIECE.get()));

		scene.idle(40);
		scene.world.modifyEntity(piece, Entity::remove);
	}

	public static void automation(SceneBuilder scene, SceneBuildingUtil util) {
		// Variables
		BlockPos extractorBottom = util.grid.at(3, 1, 1);
		BlockPos extractorTop = extractorBottom.above();
		BlockPos deployer = util.grid.at(3, 1, 3);
		BlockPos hopper = util.grid.at(3, 2, 2);
		BlockPos funnelIn = util.grid.at(2, 2, 1);
		BlockPos funnelOut = funnelIn.below();
		BlockPos chute = extractorTop.above();
		ItemStack drill = new ItemStack(RecipeItems.DRILL_HEAD.item.get());

		// Configure
		scene.title(CreateAutomated.MODID + ".ore_extractor.automation", "Automating ore extraction");
		scene.world.setKineticSpeed(util.select.everywhere(), 128);
		scene.showBasePlate();
		scene.world.showSection(util.select.fromTo(extractorBottom, extractorTop), Direction.UP);
//        scene.world.showSection(util.select.fromTo(4, 1, 1, 4, 2, 3), Direction.UP);

		// Deployer
		scene.idle(5);
		scene.world.showSection(util.select.position(deployer), Direction.UP);
		scene.idle(5);
		scene.overlay.showControls((new InputWindowElement(util.vector.centerOf(deployer), Pointing.DOWN)).rightClick()
				.withItem(new ItemStack(RecipeItems.DRILL_HEAD.item.get())), 60);
		scene.overlay.showText(60)
				.placeNearTarget().pointAt(util.vector.centerOf(deployer))
				.text("Deployers can insert drill heads into Ore Extractors");
		scene.world.modifyTileNBT(util.select.position(deployer), DeployerTileEntity.class, (nbt) -> {
			nbt.put("HeldItem", new ItemStack(RecipeItems.DRILL_HEAD.item.get()).serializeNBT());
		});
		scene.idle(61);
		scene.world.moveDeployer(deployer, .7f, 25);
		scene.idle(26);
		scene.world.modifyTileEntity(extractorTop, OreExtractorTile.class, tile -> {
			tile.setDrill((DrillHeadItem) drill.getItem());
		});
		scene.idle(81);
		scene.world.hideSection(util.select.position(deployer), Direction.UP);
		scene.world.modifyTileEntity(extractorTop, OreExtractorTile.class, tile -> {
			tile.durability = 0;
			tile.maxDurability = 0;
		});

		// Funnel input
		scene.idle(5);
		scene.rotateCameraY(-75);
		scene.world.showSection(util.select.position(funnelIn), Direction.WEST.getOpposite());
		scene.world.showSection(util.select.position(hopper), Direction.SOUTH.getOpposite());
		scene.world.showSection(util.select.position(chute), Direction.UP.getOpposite());
		scene.idle(5);
		scene.overlay.showControls((new InputWindowElement(util.vector.centerOf(funnelIn), Pointing.UP))
				.withItem(drill), 60);
		scene.overlay.showControls((new InputWindowElement(util.vector.centerOf(hopper), Pointing.DOWN))
				.withItem(drill), 60);
		scene.overlay.showControls((new InputWindowElement(util.vector.centerOf(chute), Pointing.DOWN))
				.withItem(drill), 60);
		scene.overlay.showText(60)
				.attachKeyFrame().placeNearTarget().pointAt(util.vector.centerOf(extractorTop))
				.text("Funnels, hoppers, and chutes can also all be used to input drill heads");
		scene.idle(81);
		scene.world.hideSection(util.select.position(funnelIn), Direction.WEST);
		scene.world.hideSection(util.select.position(hopper), Direction.SOUTH);
		scene.world.hideSection(util.select.position(chute), Direction.UP);

		// Funnel output
		scene.idle(5);
		scene.world.modifyTileEntity(extractorTop, OreExtractorTile.class, tile -> {
			tile.setDrill((DrillHeadItem) drill.getItem());
		});
		scene.rotateCameraY(75);
		scene.world.showSection(util.select.position(funnelOut), Direction.WEST);
		scene.idle(5);
		scene.overlay.showText(60)
				.attachKeyFrame().placeNearTarget().pointAt(util.vector.centerOf(funnelOut))
				.text("Funnels and fan chutes can extract any output items");
		scene.idle(20);
		ElementLink<EntityElement> piece = scene.world.createItemEntity(util.vector.topOf(funnelOut.below()), Vector3d.ZERO, new ItemStack(RecipeItems.GOLD_EXTRACTABLE.ORE_PIECE.get(), 34));
		scene.world.flapFunnel(funnelOut, false);
		scene.idle(81);

		// Finish
		scene.world.modifyEntity(piece, Entity::remove);
		scene.world.showSection(util.select.position(funnelIn), Direction.EAST);
		scene.world.showSection(util.select.position(chute), Direction.DOWN);
		scene.world.showSection(util.select.position(hopper), Direction.NORTH);
		scene.world.showSection(util.select.position(deployer), Direction.DOWN);
		scene.rotateCameraY(-75);
	}
}
