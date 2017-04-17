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
package org.mcphoton.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.temporal.TemporalAccessor;
import java.util.Map;

/**
 * A TOML configuration.
 *
 * @see https://github.com/toml-lang/toml
 * @author TheElectronWill
 */
public class TomlConfiguration extends BaseConfiguration {

	public TomlConfiguration() {
		// defined by the photon's implementation
	}

	public TomlConfiguration(Map<String, Object> map) {
		// defined by the photon's implementation
	}

	public TomlConfiguration(File file) throws IOException {
		// defined by the photon's implementation
	}

	public TomlConfiguration(InputStream in) throws IOException {
		// defined by the photon's implementation
	}

	/**
	 * Checks if the key exists and refers to a {@link TemporalAccessor} (basically Date or DateTime) value.
	 *
	 * @param key the key key the key (may be compound)
	 * @return true if the key refers to a Date or DateTime
	 */
	public boolean containsTemporal(String key) {
		return get(key) instanceof TemporalAccessor;
	}

	/**
	 * Gets the {@link TemporalAccessor} value mapped to this key.
	 *
	 * @param key the key (may be compound)
	 * @return the mapped value, converted to a TemporalAccessor.
	 */
	public TemporalAccessor getTemporal(String key) {
		return (TemporalAccessor) get(key);
	}

	@Override
	public void readFrom(InputStream in) throws IOException {
		// defined by the photon's implementation
	}

	@Override
	public void writeTo(OutputStream out) throws IOException {
		// defined by the photon's implementation
	}

}
