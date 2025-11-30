# Terrain Transition Tiles Generator

This module automatically generates transition tiles for smooth terrain blending in your procedurally generated maps.

## What It Does

The `TransitionTileGenerator` creates edge and corner transition tiles by applying alpha gradients to your base terrain textures. These tiles are automatically:
1. Generated from your source textures
2. Named according to the transition type
3. Packed into the atlas alongside your base terrain tiles

## Usage

### Basic Usage (8 Essential Transitions per Texture)

```kotlin
// In your main or test function
repackTerrainTextures(
    generateTransitions = true,      // Enable transition generation
    generateInnerCorners = false     // Only generate edges and outer corners (8 tiles)
)
```

This generates **8 transition tiles** per terrain texture:
- 4 edge tiles: North, East, South, West
- 4 outer corner tiles: NE, SE, SW, NW

### Advanced Usage (12 Complete Transitions per Texture)

```kotlin
repackTerrainTextures(
    generateTransitions = true,      // Enable transition generation
    generateInnerCorners = true      // Generate all 12 transition types
)
```

This generates **12 transition tiles** per terrain texture:
- 4 edge tiles
- 4 outer corner tiles
- 4 inner corner tiles (for concave corners)

### Disable Transitions

```kotlin
repackTerrainTextures(
    generateTransitions = false      // No transition generation
)
```

## Generated Tile Names

For each terrain texture (e.g., "BeachSand2"), the following tiles are generated:

### Essential Transitions (8 tiles)
- `BeachSand2_Edge_N` - North edge
- `BeachSand2_Edge_E` - East edge
- `BeachSand2_Edge_S` - South edge
- `BeachSand2_Edge_W` - West edge
- `BeachSand2_Corner_Outer_NE` - Northeast outer corner
- `BeachSand2_Corner_Outer_SE` - Southeast outer corner
- `BeachSand2_Corner_Outer_SW` - Southwest outer corner
- `BeachSand2_Corner_Outer_NW` - Northwest outer corner

### Additional Inner Corners (4 tiles)
- `BeachSand2_Corner_Inner_NE` - Northeast inner corner
- `BeachSand2_Corner_Inner_SE` - Southeast inner corner
- `BeachSand2_Corner_Inner_SW` - Southwest inner corner
- `BeachSand2_Corner_Inner_NW` - Northwest inner corner

## How It Works

### Edge Transitions
Edge tiles use linear gradients:
- **Edge_N**: Fades from opaque at bottom to transparent at top
- **Edge_S**: Fades from opaque at top to transparent at bottom
- **Edge_E**: Fades from opaque at left to transparent at right
- **Edge_W**: Fades from opaque at right to transparent at left

### Outer Corners
Outer corner tiles use diagonal gradients for convex corners where two different terrains meet at a 90-degree angle.

### Inner Corners
Inner corner tiles use inverted diagonal gradients for concave corners where the same terrain wraps around.

## Integration with ProceduralMapGenerator

The `ProceduralMapGenerator` in the terrain module automatically looks for these transition tiles in your atlas using the naming convention above. The `TileTransitionResolver` will:

1. Detect terrain boundaries
2. Calculate the appropriate transition type
3. Look for the matching tile in the atlas
4. Apply the transition if found, or fall back to the base tile if not

## Adding More Biomes

To add transition generation for new biomes, simply add them to the `textureAssets` list in `RepackTerrainTextures.kt`:

```kotlin
private val textureAssets = listOf(
    TerrainTextureData(
        "Water",
        "Assets/Game Buffs/Stylized Beach & Desert Textures/Textures",
        listOf("Beach_Sand_2", "Water_1", ...)
    ),
    TerrainTextureData(
        "Grass",  // New biome
        "Assets/Your/Path/To/Grass/Textures",
        listOf("Grass_1", "Grass_2", ...)
    )
)
```

## Performance Notes

- Transition generation happens at **build/pack time**, not runtime
- For a biome with 10 textures:
  - With `generateInnerCorners = false`: 80 transition tiles generated
  - With `generateInnerCorners = true`: 120 transition tiles generated
- All generated tiles are packed into the same atlas as the base textures

## Customization

To customize the transition gradient patterns, modify the `calculateTransitionAlpha` function in `TransitionTileGenerator.kt`.

## Example Output

After running `repackTerrainTextures()`, you'll see:

```
╔════════════════════════════════════════════════════════════════╗
║          GENERATING TERRAIN TRANSITION TILES                  ║
╚════════════════════════════════════════════════════════════════╝

=== Generating transitions for biome: Water ===
Generating 8 transition tiles for BeachSand2...
  Generated: BeachSand2_Edge_N.png
  Generated: BeachSand2_Edge_E.png
  Generated: BeachSand2_Edge_S.png
  Generated: BeachSand2_Edge_W.png
  Generated: BeachSand2_Corner_Outer_NE.png
  Generated: BeachSand2_Corner_Outer_SE.png
  Generated: BeachSand2_Corner_Outer_SW.png
  Generated: BeachSand2_Corner_Outer_NW.png
Finished generating transitions for BeachSand2
...
=== Finished transitions for Water ===

╔════════════════════════════════════════════════════════════════╗
║          TRANSITION TILE GENERATION COMPLETE                  ║
╚════════════════════════════════════════════════════════════════╝
```