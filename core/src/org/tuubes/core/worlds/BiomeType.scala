package org.tuubes.core.worlds

import org.tuubes.core.{Type, TypeRegistry}

/**
 * A type of biome.
 *
 * @author TheElectronWill
 */
abstract class BiomeType(n: String) extends Type[BiomeType](n, BiomeType) {}

/**
 * Companion object and registry of biome types.
 */
object BiomeType extends TypeRegistry[BiomeType] {}
