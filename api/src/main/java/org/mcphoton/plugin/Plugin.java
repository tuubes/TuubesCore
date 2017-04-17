/*
 * Copyright (c) 2016 MCPhoton <http://mcphoton.org> and contributors.
 *
 * This file is part of the Photon API <https://github.com/mcphoton/Photon-API>.
 *
 * The Photon API is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Photon API is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.mcphoton.plugin;

import java.io.File;
import org.slf4j.Logger;

/**
 * A plugin that may be loaded and unloaded.
 * <h1>Dependency format</h1>
 * Each string defines a dependency like this: <code>dependency:versionRequirement</code><br />
 * The version requirement describes the version of the dependency that is needed by this plugin. It has two
 * parts: a condition, and a version number.
 * <h2>Conditions</h2>
 * The following conditions may be used:
 * <ul>
 * <li><code>==</code> strictly equal</li>
 * <li><code>!=</code> not equal</li>
 * <li><code>~=</code> compatible</li>
 * <li>{@code >=} greater than or equal to</li>
 * <li>{@code > } strictly greater than</li>
 * <li>{@code <=} less than or equal to</li>
 * <li>{@code < } strictly less than</li>
 * </ul>
 * <h2>Version number</h2>
 * This system is based on <a href="http://semver.org/">Semantic Versioning</a>. A version number consists of
 * 3 integers (major, minor and patch number), separated by a dot, like for example "1.3.15". You can use less
 * than 3 numbers, in which case any missing number will be replaced by a zero. For example, "1.3" is the same
 * as "1.3.0".<br />
 * You may also add a supplementary char sequence to the end of the version number, prefixed by an hyphen
 * (minus sign). For example: "1.2.1-alpha"
 * <h2>Wildcard requirements with '*'</h2>
 * The character '*' replaces an integer. It allows for any version at its position.<br />
 * For example, "== 1.2.*" allows any version that starts with "1.2", like "1.2.0", "1.2.21", etc. And "==
 * 1.*.3" allows for any version that has a major version of 1 and a patch version of 3, like "1.0.3",
 * "1.17.3", etc.<br />
 * <b>The wildcard may only be used with a "strictly equal" or "non equal" condition.</b>
 * <h2>Minimum requirements with '+'</h2>
 * The character '+' goes to the end of an integer. It allows for any version that is greater or equal to the
 * specified one.<br />
 * For example, "== 1.2.3+" allow any version that has a major of 1, a minor of 2 and a
 * patch greater or equal to 3, like "1.2.3" and "1.2.14". And "== 1.2+.3" allows for any version that has a
 * major of 1, a minor greater or equal to 2 and a patch of 3, like "1.2.3" and "1.25.3".<br />
 * <b>The + may only be used with a "strictly equal" or "non equal" condition.</b>
 * <h2>Compatible condition</h2>
 * The "compatible" condition allows for any version that is compatible to the specified one according to the
 * semantic versioning. There are two cases:
 * <ul>
 * <li>The major version is 0: in that case, a version is compatible with the requirement if and only if:
 * <ul>
 * <li>they have the same supplementary char sequence</li>
 * <li>AND they have the same minor version number</li>
 * <li>AND the patch version number is greater than or equal to the required one</li>
 * </ul>
 * </li>
 * <li>The major version isn't 0: in that case, a version is compatible with the requirement if and only if:
 * <ul>
 * <li>they have the same supplementary char sequence</li>
 * <li>AND they have the same major version number</li>
 * <li>AND the minor version is greater than or equal to the required one</li>
 * <li>AND, if the minor version is equal to the required one, the patch version is greater than or equal to
 * the required one</li>
 * </ul>
 * </li>
 * </ul>
 */
public interface Plugin {

	/**
	 * @return the plugin's name.
	 */
	String getName();

	/**
	 * @return the plugin's author(s).
	 */
	String getAuthor();

	/**
	 * @return the plugin's version. The version should follow the principles of
	 * <a href="http://semver.org/">Semantic versioning</a>.
	 */
	String getVersion();

	/**
	 * @return the plugin's required dependencies. If there is no dependancy it doesn't return null but an empty
	 * array instead.
	 * @see Plugin the dependency format
	 */
	String[] getRequiredDependencies();

	/**
	 * @return the plugin's optional dependencies. If there is no dependancy it doesn't return null but an
	 * empty array instead.
	 * @see Plugin the dependency format
	 */
	String[] getOptionalDependencies();

	/**
	 * @return the directory that this plugin may use to store files.
	 */
	File getDirectory();

	/**
	 * @return the plugin's main config file.
	 */
	File getConfigFile();

	/**
	 * @return the plugin's logger.
	 */
	Logger getLogger();

	/**
	 * Called when the plugin is loaded.
	 */
	void onLoad();

	/**
	 * Called when the plugin is unloaded.
	 */
	void onUnload();

}
