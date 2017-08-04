package org.mcphoton.world.generation;

import com.github.steveice10.opennbt.tag.builtin.CompoundTag;
import java.util.Random;
import org.mcphoton.world.ChunkColumnImpl;
import org.mcphoton.world.ChunkSectionImpl;
import org.mcphoton.world.ChunkColumn;
import org.mcphoton.world.ChunkGenerator;
import org.mcphoton.world.ChunkSection;
import org.mcphoton.world.World;

/**
 * @author TheElectronWill
 */
public class SimpleHeightmapBasedGenerator implements ChunkGenerator {

	private static final double DEFAULT_FACTOR = 1.0 / 40.0;
	private static final int DEFAULT_MIN = 50, DEFAULT_MAX = 200, DEFAULT_SEA_LEVEL = 70;
	private static final int ID_BEDROCK = 7 << 4, ID_WATER = 9 << 4, ID_STONE = 1 << 4,
							 ID_GRASS = 2 << 4, ID_SAND = 12	<< 4;
	private static final byte BIOME_PLAINS = 1, BIOME_OCEAN = 0;

	private final World world;
	private final SimplexNoise noise;
	private final double factor;
	private final int minValue, maxValue, seaLevel;

	public SimpleHeightmapBasedGenerator(World world, Random random, double factor, int minValue,
										 int maxValue, int seaLevel) {
		this.world = world;
		this.noise = new SimplexNoise(random);
		this.factor = factor;
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.seaLevel = seaLevel;
	}

	public SimpleHeightmapBasedGenerator(World world) {
		this(world, new Random(), DEFAULT_FACTOR, DEFAULT_MIN, DEFAULT_MAX, DEFAULT_SEA_LEVEL);
	}

	@Override
	public ChunkColumn generate(int startBlockX, int startBlockZ) {
		byte[] biomesData = new byte[256];

		int maxHeight = 150;// maximum height in this chunk
		int[][] heightmap = new int[16][16];
		for (int x = startBlockX; x < startBlockX + 16; x++) {
			for (int z = startBlockZ; z < startBlockZ + 16; z++) {
				double noiseValue = noise.generate(x * factor, z * factor);
				int height = (int)((noiseValue + 1.0) / 2.0) * (maxValue - minValue) + minValue;
				if (height > maxHeight) {
					maxHeight = height;
				}
				heightmap[x][z] = height;
			}
		}
		ChunkSectionImpl[] sections = new ChunkSectionImpl[16];
		for (int i = 0; i < sections.length; i++) {
			sections[i] = new ChunkSectionImpl(true);
		}
		// Bedrock layer
		ChunkSection zeroth = sections[0];
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				zeroth.setBlockFullId(x, 0, z, ID_BEDROCK);
			}
		}
		// Terrain
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				int height = heightmap[x][z];
				for (int y = 1; y < height; y++) {// stone ground
					sections[y / 16].setBlockFullId(x, y % 16, z, ID_STONE);
				}
				if (height > seaLevel) {// in land
					sections[height / 16].setBlockFullId(x, height, z, ID_GRASS);
					biomesData[z << 4 | x] = BIOME_PLAINS;
				} else {// in sea
					sections[height / 16].setBlockFullId(x, height, z, ID_SAND);
					for (int y = height; y <= seaLevel; y++) {
						sections[y / 16].setBlockFullId(x, y % 16, z, ID_WATER);
					}
					biomesData[z << 4 | x] = BIOME_OCEAN;
				}
			}
		}
		return new ChunkColumnImpl(startBlockX / 16, startBlockZ / 16, sections, biomesData,
								   new CompoundTag[0]);
	}
}