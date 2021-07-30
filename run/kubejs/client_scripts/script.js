// priority: 0

console.info('Hello, World! (You will see this line every time client resources reload)')

onEvent("partial.registry.drillhead", event => {
    event.create("kubejs:floppa_drill", "kubejs:item/floppa_drill")
})
