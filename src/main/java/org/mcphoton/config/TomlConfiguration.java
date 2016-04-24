/* 
 * Copyright (c) 2016 MCPhoton <http://mcphoton.org> and contributors.
 * 
 * This file is part of the Photon Server Implementation <https://github.com/mcphoton/Photon-Server>.
 * 
 * The Photon Server Implementation is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * The Photon Server Implementation is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.mcphoton.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.temporal.TemporalAccessor;
import java.util.Map;
import com.electronwill.toml.Toml;
import com.electronwill.toml.TomlException;

public class TomlConfiguration extends BaseConfiguration {
	
	public TomlConfiguration() {
		super();
	}
	
	public TomlConfiguration(Map<String, Object> map) {
		super(map);
	}
	
	/**
	 * Checks if the key exists and refers to a {@link TemporalAccessor} (basically Date or
	 * DateTime) value.
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
	public synchronized void readFrom(InputStream in) throws IOException, TomlException {
		map = Toml.read(in);
	}
	
	@Override
	public synchronized void writeTo(OutputStream out) throws IOException {
		Toml.write(map, out);
	}
	
}
