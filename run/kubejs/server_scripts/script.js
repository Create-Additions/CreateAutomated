// priority: 0

settings.logAddedRecipes = true
settings.logRemovedRecipes = true
settings.logSkippedRecipes = false
settings.logErroringRecipes = true

console.info('Hello, World! (You will see this line every time server resources reload)')

onEvent('recipes', event => {
	event.recipes.createautomated.extracting("kubejs:floppa_node", "kubejs:floppa_bit")
	    .drillDamage(0)
	    .ore(2)
	    .requiredProgressSeconds(128, 3)

  event.custom({
    type: 'create:splashing',
    ingredients: [
      Ingredient.of('kubejs:floppa_bit').toJson()
    ],
    results: [
      Item.of('kubejs:dripping_floppa').toResultJson(),
    ]
  })

  event.shaped("kubejs:floppa_node", [
  "yyy",
  "yfy",
  "yyy"
  ], {
    "y": "kubejs:floppa_bit",
    "f": "minecraft:leather"
  })

  event.shapeless("kubejs:floppa_bit", "9x minecraft:diamond_block")

  event.shapeless("9x minecraft:diamond_block", "kubejs:floppa_bit")

  event.shapeless("minecraft:netherite_block", "kubejs:dripping_floppa")

  event.shaped("kubejs:engineer_floppa", [
  " gb",
  "wft",
  " n "
  ], {
  "g": "create:goggles",
  "b": "create:copper_backtank",
  "w": "create:wrench",
  "f": "kubejs:floppa_bit",
  "t": "create:builders_tea",
  "n": "kubejs:floppa_node"
  })

  event.shapeless("minecraft:bedrock", ["minecraft:stone", "kubejs:dripping_floppa"])

  event.shaped("kubejs:floppa_drill", [
  "nnn",
  "ini",
  " i "
  ], {
    "n": "kubejs:floppa_node",
    "i": "kubejs:floppa_bit"
  })
})
