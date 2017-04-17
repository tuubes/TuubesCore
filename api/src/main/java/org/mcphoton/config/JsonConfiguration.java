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
import java.util.Map;

/**
 * A JSON configuration.
 *
 * @see http://json.org/
 * @author TheElectronWill
 */
public class JsonConfiguration extends BaseConfiguration {

	public JsonConfiguration() {
		// defined by the photon's implementation
	}

	public JsonConfiguration(Map<String, Object> data) {
		// defined by the photon's implementation
	}

	public JsonConfiguration(File file) throws IOException {
		// defined by the photon's implementation
	}

	public JsonConfiguration(InputStream in) throws IOException {
		// defined by the photon's implementation
	}

	@Override
	public void readFrom(InputStream in) throws IOException {
		// defined by the photon's implementation
	}

	@Override
	public void writeTo(OutputStream out) throws IOException {
		// defined by the photon's implementation
	}

	public String writeToString() throws IOException {
		return null;
		// defined by the photon's implementation
	}

}
