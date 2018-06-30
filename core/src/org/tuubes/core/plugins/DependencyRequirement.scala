package org.tuubes.core.plugins

import java.util.{List => JList}

import com.electronwill.utils.StringUtils

/**
 * @author TheElectronWill
 */
final class DependencyRequirement(val name: String,
                                  val compatibilityChecker: String => Boolean,
                                  val optional: Boolean) {
  def accepts(nameAndVersion: String): Boolean = {
    val splitted = StringUtils.split(nameAndVersion, ':')
    val name = splitted.get(0)
    val version = splitted.get(1)
    accepts(name, version)
  }
  def accepts(name: String, version: String): Boolean = {
    name == this.name && compatibilityChecker(version)
  }
  def accepts(infos: PluginInfos): Boolean = accepts(infos.name, infos.version)
}
object DependencyRequirement {
  def apply(nameAndVersion: String, optional: Boolean): DependencyRequirement = {
    val splitted = StringUtils.split(nameAndVersion, ':')
    val name = splitted.get(0)
    val version = splitted.get(1)
    // Parses the version specification:
    val specSplit = StringUtils.split(version, '-')
    val specMain = specSplit.get(0)
    val specLabel: String = if (specSplit.size > 1) specSplit.get(1) else ""
    val labelChecker: String => Boolean = specLabel == _
    val mainChecker: String => Boolean = checkMain(specMain, _)
    val checker: String => Boolean = { s =>
      val splitted = StringUtils.split(s, '-')
      if (splitted.size < 2) {
        mainChecker(s)
      } else {
        labelChecker(splitted.get(1)) && mainChecker(splitted.get(0))
      }
    }
    new DependencyRequirement(name, checker, optional)
  }

  private def checkMain(spec: String, provided: String): Boolean = {
    val specParts = StringUtils.split(spec, '.')
    val providedParts = StringUtils.split(provided, '.')
    padWithZeroes(specParts, providedParts)

    for (i <- 0 until specParts.size) {
      val spec = specParts.get(i)
      val provided = providedParts.get(i)
      if (spec.endsWith("+")) {
        /* A + means that the provided version must be greater than or equal to the one
				specified up to this part. That is, "1.2.9+" accepts "1.2.9", "1.2.10", ... and
				"1.2+.3" accepts "1.2.3" and "1.3.0" but not "1.2.4" */
        val specNumber = spec.substring(0, spec.length - 1).toInt
        val providedNumber = provided.toInt
        if (providedNumber < specNumber) return false // provided too low
        if (providedNumber > specNumber) return true // the provided number is strictly
        // greater than the specified one, therefore we stop here, so that the
        // requirement "1.2+.3+" is fulfilled by, for instance, "1.2.4" and "1.3.0"
      } else if (spec != provided) {
        /* No + means that the provided number must exactly match the specified one */
        return false
      }
    }
    true
  }

  private def padWithZeroes(spec: JList[String], provided: JList[String]): Unit = {
    val diff = spec.size - provided.size
    if (diff < 0) {
      val toAdd = if (spec.get(spec.size - 1).endsWith("+")) "0+" else "0"
      for (i <- 0 until Math.abs(diff)) {
        spec.add(toAdd)
      }
    } else if (diff > 0) {
      for (i <- 0 until diff) {
        provided.add("0")
      }
    }
  }
}
