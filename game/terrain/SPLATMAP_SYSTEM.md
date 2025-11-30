# Splatmap-Based Terrain Rendering System

This document explains the complete splatmap-based terrain rendering system that provides smooth, shader-based transitions between different terrain types.

## Overview

The splatmap system uses GPU shaders to blend multiple terrain textures based on weight maps (splatmaps), creating natural-looking transitions between different terrain types in your procedurally generated world.

## Components

### 1. ProceduralMapGenerator
**Location:** `game/terrain/src/main/kotlin/org/roldy/terrain/ProceduralMapGenerator.kt`

Generates terrain using multi-layered noise (elevation, temperature, moisture) and assigns appropriate biomes and terrains to each tile.

**Key Method:**
- `getTerrainData()`: Returns a 2D array of terrain assignments for splatmap generation

### 2. SplatMapGenerator
**Location:** `game/terrain/src/main/kotlin/org/roldy/terrain/splatmap/SplatMapGenerator.kt`

Generates splatmaps (weight maps) that define how much each terrain type should be visible at each pixel.

**How it works:**
- Takes the 2D terrain data from ProceduralMapGenerator
- For each pixel, calculates weights based on distance to neighboring terrains
- Uses exponential falloff for smooth transitions
- Packs up to 4 terrain weights into RGBA channels of a texture

**Key Features:**
- Distance-based blending for natural transitions
- Supports unlimited terrains (creates multiple splatmaps, 4 terrains per splatmap)
- Normalized weights ensure terrains always sum to 100%

### 3. SplatMapTerrainRenderer
**Location:** `game/terrain/src/main/kotlin/org/roldy/terrain/splatmap/SplatMapTerrainRenderer.kt`

Renders the terrain using shader-based texture blending.

**How it works:**
- Creates a full-screen quad covering the entire terrain
- Binds splatmap texture (contains terrain weights)
- Binds up to 4 terrain textures per pass
- Shader samples all textures and blends them based on splatmap weights

### 4. Shaders
**Location:** `game/terrain/src/main/resources/shader/terrain/`

**Vertex Shader (`vert.glsl`):**
- Simple pass-through shader
- Transforms vertices and passes texture coordinates

**Fragment Shader (`frag.glsl`):**
- Samples splatmap to get terrain weights (RGBA = weights for 4 terrains)
- Samples all 4 terrain textures with tiled coordinates
- Blends textures using weights: `finalColor = terrain0 * weight.r + terrain1 * weight.g + ...`

## Usage

### Basic Setup

```kotlin
// 1. Generate procedural terrain
val generator = ProceduralMapGenerator(
    seed = 1,
    width = 100,
    height = 100,
    tileSize = 400,
    elevationScale = 0.001f,
    moistureScale = 0.003f,
    temperatureScale = 0.05f
)

// 2. Get terrain assignments
val terrainData = generator.getTerrainData()

// 3. Generate splatmaps
val splatMapGenerator = SplatMapGenerator(100, 100, 400, terrainData)
val splatMaps = splatMapGenerator.generate()

// 4. Create renderer
val renderer = SplatMapTerrainRenderer(
    widthInTiles = 100,
    heightInTiles = 100,
    tileSize = 400,
    splatMaps = splatMaps,
    tileScale = 1.0f  // Controls texture repeat
)

// 5. Render
fun render(camera: OrthographicCamera) {
    renderer.render(camera)
}
```

### Parameters

**ProceduralMapGenerator:**
- `seed`: Random seed for reproducible generation
- `width/height`: Map dimensions in tiles
- `tileSize`: Size of each tile in pixels
- `elevationScale`: Lower = smoother elevation (try 0.001-0.005)
- `moistureScale`: Lower = larger moisture zones (try 0.003-0.01)
- `temperatureScale`: Lower = larger temperature zones (try 0.05-0.2)

**SplatMapTerrainRenderer:**
- `tileScale`: Texture repetition factor
  - `1.0` = texture covers entire map once
  - `10.0` = texture repeats 10 times (more detail)
  - Adjust based on your terrain texture resolution

## How Splatmaps Work

### Weight Maps
A splatmap is a texture where each color channel stores the "weight" or "influence" of a terrain at that location:

```
R channel = Weight of Terrain 0 (e.g., Water)
G channel = Weight of Terrain 1 (e.g., Sand)
B channel = Weight of Terrain 2 (e.g., Grass)
A channel = Weight of Terrain 3 (e.g., Rock)
```

**Example pixel value:**
- RGBA = (0.7, 0.3, 0.0, 0.0)
- Means: 70% Water, 30% Sand, 0% Grass, 0% Rock
- Shader blends these textures accordingly

### Blending Process

1. **Splatmap Generation:**
   - For each pixel, calculate distance to nearby terrain tiles
   - Closer terrains get higher weights
   - Weights are normalized to sum to 1.0

2. **Shader Rendering:**
   ```glsl
   vec4 weights = texture2D(u_splatmap, texCoord);  // Get weights
   vec4 water = texture2D(u_terrain0, tiledCoords); // Sample textures
   vec4 sand = texture2D(u_terrain1, tiledCoords);
   vec4 grass = texture2D(u_terrain2, tiledCoords);
   vec4 rock = texture2D(u_terrain3, tiledCoords);

   // Blend based on weights
   finalColor = water * weights.r +
                sand * weights.g +
                grass * weights.b +
                rock * weights.a;
   ```

## Advantages Over Tile-Based Transitions

### Tile-Based Approach (Previous)
❌ Requires pre-made transition tiles for every terrain pair
❌ Limited to specific transitions (e.g., Water→Sand only)
❌ Large atlas size with many transition variations
❌ Hard edges between incompatible terrain pairs

### Splatmap Approach (Current)
✅ Smooth transitions between ANY terrain types
✅ GPU-accelerated blending (fast)
✅ Natural-looking gradients
✅ Works with procedural generation
✅ Small memory footprint (just weight maps)
✅ Supports unlimited terrain types

## Performance Characteristics

**Memory:**
- Splatmap texture: `width * tileSize * height * tileSize * 4 bytes`
- Example: 100x100 tiles at 400px = 16,000x16,000 px = ~1 GB (can be optimized)

**GPU:**
- Multiple texture samples per pixel (1 splatmap + 4 terrains per pass)
- Modern GPUs handle this easily
- Larger maps may need LOD system

## Optimization Tips

1. **Reduce Splatmap Resolution:**
   - Splatmap doesn't need to be as high-res as terrain textures
   - Consider generating at lower resolution

2. **Texture Tiling:**
   - Use smaller, tileable terrain textures
   - Increase `tileScale` for more detail

3. **LOD (Level of Detail):**
   - Generate multiple splatmap resolutions
   - Use lower resolution for distant terrain

4. **Limit Terrain Types:**
   - Fewer unique terrains = fewer splatmap passes
   - Group similar terrains together

## Debugging

**Check if splatmaps are generated:**
```kotlin
println("Generated ${splatMaps.size} splatmaps")
splatMaps.forEach { splatMap ->
    println("Splatmap has ${splatMap.terrainLayers.size} terrain layers")
}
```

**Visualize splatmap weights:**
Modify the fragment shader temporarily:
```glsl
void main() {
    vec4 weights = texture2D(u_splatmap, v_texCoord);
    gl_FragColor = weights;  // Shows weight distribution as colors
}
```

**Check terrain assignments:**
```kotlin
val terrainData = generator.getTerrainData()
val uniqueTerrains = terrainData.flatten().map { it.data.name }.toSet()
println("Unique terrains in map: $uniqueTerrains")
```

## Troubleshooting

**Issue: Black screen**
- Check shader compilation log
- Verify terrain textures are loaded
- Ensure camera is positioned correctly

**Issue: Blocky transitions**
- Increase blend radius in SplatMapGenerator
- Adjust exponential falloff parameter
- Check splatmap resolution

**Issue: Performance problems**
- Reduce map size or tile size
- Lower splatmap resolution
- Use texture compression

## Future Enhancements

- **Multi-pass rendering** for >4 terrains per location
- **Normal map blending** for better visual quality
- **Height-based blending** for cliff transitions
- **Texture splatting on GPU** for dynamic updates
- **Erosion simulation** for natural terrain features