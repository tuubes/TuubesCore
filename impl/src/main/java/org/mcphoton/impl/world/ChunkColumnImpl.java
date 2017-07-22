package org.mcphoton.impl.world;

import com.github.steveice10.mc.protocol.data.game.chunk.ChunkColumnData;
import com.github.steveice10.packetlib.io.NetInput;
import com.github.steveice10.packetlib.io.NetOutput;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.xml.crypto.Data;
import jdk.nashorn.internal.ir.Block;
import org.mcphoton.Photon;
import org.mcphoton.block.BlockType;
import org.mcphoton.impl.entity.AbstractEntity;
import org.mcphoton.utils.Location;
import org.mcphoton.world.BiomeType;
import org.mcphoton.world.ChunkColumn;
import org.mcphoton.world.ChunkSection;
import org.mcphoton.world.World;
import org.mcphoton.world.areas.Area;

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
	/**
	 * The player zones that contain this chunk column. Every modification is notified to the
	 * players.
	 */
	private final Set<PlayerZone> playerZones = ConcurrentHashMap.newKeySet();// thread-safe

	public ChunkColumnImpl(World world, Data data) {
		this.world = world;
		this.data = data;
	}

	public Data getData() {
		return data;
	}

	public Set<AbstractEntity> getEntities() {
		return data.entities;
	}

	public Set<PlayerZone> getPlayerZones() {
		return playerZones;
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
		return Photon.getGameRegistry().getBiome(typeId);//TODO
	}

	@Override
	public void setBiomeType(int x, int z, BiomeType biomeType) {
		int typeId = ((AbstractBiomeType)biomeType).getId();
		synchronized (data.biomes) {
			data.biomes[z << 4 | x] = (byte)typeId;
		}
	}

	@Override
	public BlockType getBlockType(int x, int y, int z) {
		ChunkSection section = data.sections[y / 16];
		if (section == null) {
			return null;//TODO return StandardBlocks.AIR
		}
		return section.getBlockType(x, y & 15, z);

	}

	@Override
	public void setBlockType(int x, int y, int z, BlockType type) {
		ChunkSection section = data.sections[y / 16];
		if (section == null) {
			section = new ChunkSectionImpl()
		}
		section.setBlockType(x, y & 15, z, type);
	}

	@Override
	public Iterator<Location> iterator() {
		return null;//TODO
	}

	public static final class Data implements ChunkColumnData {
		private final Set<AbstractEntity> entities = ConcurrentHashMap.newKeySet();
		private volatile ChunkSectionImpl[] sections;
		private final byte[] biomes;
		private final int x, z;
		private final boolean skylight;
		private volatile boolean biomesChanged;

		public Data(int x, int z, ChunkSectionImpl[] sections, byte[] biomes, boolean skylight) {
			this.x = x;
			this.z = z;
			this.sections = sections;
			this.biomes = biomes;
			this.skylight = skylight;
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
			//TODO
		}

		@Override
		public boolean hasSkylight() {
			return skylight;
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
			int bitmask;
			for (int i = 0; i < sections.length; i++) {
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
		}

		public static Data read(NetInput in) throws IOException {
			boolean skylight = in.readBoolean();
			ChunkSectionImpl[] sections = new ChunkSectionImpl[16];

		}
	}
}