package org.mcphoton

import java.util

import com.electronwill.collections.{ConcurrentIndexMap, IndexMap}
import com.electronwill.nightconfig.core.{Config, UnmodifiableConfig}
import com.electronwill.nightconfig.core.file.FileConfig
import org.mcphoton.block.BlockType
import org.mcphoton.entity.{MobType, ObjectType}
import org.mcphoton.item.ItemType
import org.mcphoton.server.PhotonServer.DirConfig
import org.mcphoton.world.BiomeType

/**
 * @author TheElectronWill
 */
object GameRegistry {
	// Configs that contains "uniqueName -> id" definitions
	private val blocksConfig = FileConfig.of((DirConfig / "blocks.toml").toJava)
	private val mobsConfig = FileConfig.of((DirConfig / "entities_mobs.toml").toJava)
	private val objectsConfig = FileConfig.of((DirConfig / "entities_objects.toml").toJava)
	private val itemsConfig = FileConfig.of((DirConfig / "items.toml").toJava)
	private val biomesConfig = FileConfig.of((DirConfig / "biomes.toml").toJava)

	// Map that link ids to types
	private val blocksIdMap = new IndexMap[Registration[BlockType]]
	private val mobsIdMap = new IndexMap[MobType]
	private val objectsIdMap = new IndexMap[ObjectType]
	private val itemsIdMap = new IndexMap[Registration[ItemType]]
	private val biomesIdMap = new IndexMap[BiomeType]

	// Map that link names to types
	private val blocksNameMap = new util.HashMap[String, BlockType]
	private val mobsNameMap = new util.HashMap[String, MobType]
	private val objectsNameMap = new util.HashMap[String, ObjectType]
	private val itemsNameMap = new util.HashMap[String, ItemType]
	private val biomesNameMap = new util.HashMap[String, BiomeType]

	// Get ID by name
	private[mcphoton] def blockId(name: String): Int = blocksConfig.get(name)
	private[mcphoton] def entityMobId(name: String): Int = mobsConfig.get(name)
	private[mcphoton] def entityObjectId(name: String): Int = objectsConfig.get(name)
	private[mcphoton] def itemId(name: String): Int = itemsConfig.get[Config](name).get("id")
	private[mcphoton] def biomeId(name: String): Int = biomesConfig.get(name)

	// Get type by ID (and additional data if needed)
	private[mcphoton] def block(id: Int, metadata: Int): BlockType = blocksIdMap.get(id).variant(metadata)
	private[mcphoton] def entityMob(id: Int): MobType = mobsIdMap.get(id)
	private[mcphoton] def entityObject(id: Int): ObjectType = objectsIdMap.get(id)
	private[mcphoton] def item(id: Int, damageData: Int): ItemType = itemsIdMap.get(id).variant(damageData)
	private[mcphoton] def biome(id: Int): BiomeType = biomesIdMap.get(id)

	// Get registration info by ID
	private[mcphoton] def blocks(id: Int): Registration[BlockType] = blocksIdMap.get(id)
	private[mcphoton] def items(id: Int): Registration[ItemType] = itemsIdMap.get(id)
	private[mcphoton] def block(fullId: Int): BlockType = block(fullId >> 4, fullId & 16)

	// Get type by name
	def block(name: String): BlockType = blocksNameMap.get(name)
	def entityMob(name: String): MobType = mobsNameMap.get(name)
	def entityObject(name: String): ObjectType = objectsNameMap.get(name)
	def item(name: String): ItemType = itemsNameMap.get(name)
	def biome(name: String): BiomeType = biomesNameMap.get(name)

	// Registration methods
	def registerBlock(name: String, blockType: BlockType): (Int, Option[Int]) = {
		registerVariant(name, blockType, blocksConfig, blocksIdMap, blocksNameMap)
	}

	def registerEntityMob(name: String, mobType: MobType): Int = {
		registerBasic(name, mobType, mobsConfig, mobsIdMap, mobsNameMap)
	}

	def registerEntityObject(name: String, objectType: ObjectType): Int = {
		registerBasic(name, objectType, objectsConfig, objectsIdMap, objectsNameMap)
	}

	def registerItem(name: String, itemType: ItemType): (Int, Option[Int]) = {
		registerVariant(name, itemType, itemsConfig, itemsIdMap, itemsNameMap)
	}

	def registerBiome(name: String, biomeType: BiomeType): Int = {
		registerBasic(name, biomeType, biomesConfig, biomesIdMap, biomesNameMap)
	}

	private def registerBasic[T <: Type[_]](name: String, t: T, config: FileConfig,
											idMap: IndexMap[T],
											nameMap: util.Map[String, T]): Int = {
		val id: Int = config.get(name)
		idMap.put(id, t)
		nameMap.put(name, t)
		id
	}

	private def registerVariant[T <: Type[_]](name: String, t: T, config: FileConfig,
											  idMap: IndexMap[Registration[T]],
											  nameMap: util.Map[String, T]): (Int, Option[Int]) = {
		val conf: UnmodifiableConfig = config.get(name)
		val id: Int = conf.get("id")
		val data: String = conf.get("additionalData")
		val result =
			if (data == "none") {
				val reg = new SingleRegistration[T](t)
				idMap.put(id, reg)
				(id, None)
			} else {
				val dataValue: Int = data.toInt
				idMap.compute(id, (_, v) => {
					v match {
						case null => new VariantsRegistration[T](t, dataValue)
						case vr: VariantsRegistration[T] =>
							vr.variants.put(dataValue, t)
							vr
						case _ =>
							val tcn = t.getClass.getSimpleName
							throw new IllegalStateException(
								s"""Invalid $tcn registration: cannot register a variant of id $id
								   | because a basic type (without variant) has already been
								   | registered with the same id.""".stripMargin);

					}
				})
				(id, Some(dataValue))
			}
		nameMap.put(name, t)
		result
	}

	/**
	 * Registration infos of a type that may have variants.
	 */
	private[mcphoton] sealed trait Registration[A <: Type[_]] {
		def variant(additionalData: Int): A
	}

	/**
	 * Registration infos of a type that has mutliple variants (like Stone).
	 */
	private[mcphoton] final class VariantsRegistration[A](firstVariantType: A,
														  firstVariantData: Int) extends Registration[A] {
		private[mcphoton] val variants = new ConcurrentIndexMap[A]
		variants.put(firstVariantData, firstVariantType)
		override def variant(damageData: Int): A = variants.get(damageData)
	}

	/**
	 * Registration info of a simple item type that has no variants.
	 */
	private[mcphoton] final class SingleRegistration[A](val `type`: A) extends Registration[A] {
		override def variant(damageData: Int): A = `type`
	}
}