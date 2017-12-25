package org.tuubes.world;

import com.github.steveice10.mc.protocol.data.game.chunk.BasicChunkData;
import com.github.steveice10.mc.protocol.data.game.chunk.BasicChunkSection;
import com.github.steveice10.mc.protocol.data.game.chunk.BlockStorage;
import com.github.steveice10.mc.protocol.data.game.chunk.ChunkColumnData;
import com.github.steveice10.mc.protocol.data.game.chunk.NibbleArray3d;
import com.github.steveice10.packetlib.io.NetInput;
import com.github.steveice10.packetlib.io.NetOutput;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.tuubes.GameRegistry;
import org.tuubes.block.BlockType;
import org.tuubes.block.StandardBlocks;
import org.tuubes.entity.Entity;
import org.tuubes.world.areas.Area;

/**
 * Basic implementation of ChunkColumn. It is thread-safe.
 *
 * @author TheElectronWill
 * @see <a href="http://wiki.vg/SMP_Map_Format">wiki.vg - Protocol map format</a>
 */
public final class ChunkColumnImpl implements ChunkColumn {
	/**
	 * The chunk's world. Needed to implement the Area interface.
	 */
	private final World world;
	/**
	 * Contains the persistent chunk data.
	 */
	private final Data data;

	public ChunkColumnImpl(World world, Data data) {
		this.world = world;
		this.data = data;
	}

	public ChunkColumnImpl(World world, int x, int z, boolean skylight) {
		this.world = world;
		this.data = new Data(x, z, new ChunkSectionImpl[16], new byte[256], skylight);
	}

	public Data getData() {
		return data;
	}

	public Set<Entity> getEntities() {
		return data.entities;
	}

	public ChunkCoordinates getCoords() {
		return new ChunkCoordinates(data.x, data.z);
	}

	@Override
	public boolean contains(int x, int y, int z) {
		return (x / 16 == data.x) && (x / 16 == data.z);
	}

	@Override
	public World getWorld() {
		return world;
	}

	@Override
	public ChunkSection getSection(int index) {
		return data.sections[index];
	}

	@Override
	public void fill(BlockType blockType) {
		//TODO
	}

	@Override
	public void replace(BlockType type, BlockType replacement) {
		//TODO
	}

	@Override
	public Area subArea(int x1, int y1, int z1, int x2, int y2, int z2) {
		return null;//TODO
	}

	@Override
	public BiomeType getBiomeType(int x, int z) {
		byte typeId;
		synchronized (data.biomes) {
			typeId = data.biomes[z << 4 | x];
		}
		return GameRegistry.biome$$temp(typeId);
	}

	@Override
	public void setBiomeType(int x, int z, BiomeType biomeType) {
		int typeId = (biomeType).id();
		synchronized (data.biomes) {
			data.biomes[z << 4 | x] = (byte)typeId;
		}
	}

	@Override
	public BlockType getBlockType(int x, int y, int z) {
		ChunkSection section = data.sections[y / 16];
		if (section == null) {
			return StandardBlocks.Air();
		}
		return section.getBlockType(x, y & 15, z);

	}

	@Override
	public void setBlockType(int x, int y, int z, BlockType type) {
		ChunkSection section = data.sections[y / 16];
		if (section == null) {
			section = new ChunkSectionImpl(true);
		}
		section.setBlockType(x, y & 15, z, type);
	}

	@Override
	public Iterator<Location> iterator() {
		return null;//TODO
	}

	public static final class Data implements ChunkColumnData {
		private final Set<Entity> entities = ConcurrentHashMap.newKeySet();
		private volatile ChunkSectionImpl[] sections;
		private final byte[] biomes;
		private final int x, z;
		private final boolean skylight;
		private volatile boolean biomesChanged;

		public Data(int x, int z, ChunkSectionImpl[] sections, byte[] biomes, boolean skylight) {
			if (sections.length != 16) {
				throw new IllegalArgumentException("Invalid number of sections: need 16");
			}
			if (biomes.length != 256) {
				throw new IllegalArgumentException("Invalid size of biomes data: need 256 bytes");
			}
			this.x = x;
			this.z = z;
			this.sections = sections;
			this.biomes = biomes;
			this.skylight = skylight;
		}

		public Data(BasicChunkData basicChunkData) {
			this.x = basicChunkData.getX();
			this.z = basicChunkData.getZ();
			BasicChunkSection[] basicSections = basicChunkData.getSections();

			this.sections = new ChunkSectionImpl[basicSections.length];
			if (sections.length != 16) {
				throw new IllegalArgumentException("Invalid number of sections: need 16");
			}

			this.biomes = basicChunkData.getBiomeData();
			if (biomes.length != 256) {
				throw new IllegalArgumentException("Invalid size of biomes data: need 256 bytes");
			}
			for (int i = 0; i < basicSections.length; i++) {
				BasicChunkSection basicSection = basicSections[i];
				if (basicSection != null) {
					sections[i] = new ChunkSectionImpl(basicSection.getBlocks(),
													   basicSection.getBlockLight(),
													   basicSection.getSkyLight());
				}
			}
			this.skylight = basicChunkData.hasSkylight();
		}

		@Override
		public int getX() {
			return x;
		}

		@Override
		public int getZ() {
			return z;
		}

		@Override
		public ChunkSectionImpl[] getSections() {
			return sections;
		}

		@Override
		public byte[] getBiomeData() {
			return biomes;
		}

		@Override
		public void writeBlockEntitiesNBT(NetOutput out) throws IOException {
			out.writeByte(0);// 0 means no data
			//TODO
		}

		@Override
		public boolean hasSkylight() {
			return skylight;
		}

		public int getBiomeId(int x, int z) {
			return biomes[z << 4 | x];
		}

		public void setBiomes(int x, int z, int biomeId) {
			biomesChanged = true;
			biomes[z << 4 | x] = (byte)biomeId;
		}

		public boolean hasChanged() {
			if (biomesChanged) {
				return true;
			}
			for (ChunkSectionImpl section : sections) {
				if (section != null && section.hasChanged()) {
					return true;
				}
			}
			return false;
		}

		public void save(NetOutput out) throws IOException {
			out.writeBoolean(skylight);
			for (int i = 0; i < 16; i++) {
				ChunkSectionImpl section = sections[i];
				if (section != null) {
					out.writeByte(i);//section index
					section.getBlocks().write(out);
					section.getBlockLight().write(out);
					if (skylight) {
						section.getSkyLight().write(out);
					}
				}
			}
			out.writeBytes(biomes);
		}

		public static Data read(NetInput in, int x, int z) throws IOException {
			boolean hasSkylight = in.readBoolean();
			ChunkSectionImpl[] sections = new ChunkSectionImpl[16];
			for (int i = 0; i < 16; i++) {
				int index = in.readByte();
				BlockStorage blocks = new BlockStorage(in);
				NibbleArray3d blocklight = new NibbleArray3d(in, 4096);
				NibbleArray3d skylight = hasSkylight ? new NibbleArray3d(in, 4096) : null;
				sections[index] = new ChunkSectionImpl(blocks, blocklight, skylight);
			}
			byte[] biomes = in.readBytes(256);
			return new Data(x, z, sections, biomes, hasSkylight);
		}
	}
}