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
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import com.electronwill.json.Json;
import java.io.InputStreamReader;

public class JsonConfiguration extends BaseConfiguration {

	public JsonConfiguration() {
		super();
	}

	public JsonConfiguration(Map<String, Object> map) {
		super(map);
	}

	@Override
	public synchronized void readFrom(InputStream in) throws IOException {
		map = Json.readObject(new InputStreamReader(in, StandardCharsets.UTF_8));
	}

	@Override
	public synchronized void writeTo(OutputStream out) throws IOException {
		Json.write(map, new OutputStreamWriter(out, StandardCharsets.UTF_8), true);
	}

	public synchronized String writeToString() throws IOException {
		return Json.writeToString(map, false);
	}

}
