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
package org.mcphoton.messaging;

import java.util.Map;

public class SelectorChatMessage extends ChatMessage {

	public SelectorChatMessage(Map<String, Object> map) {
		super(map);
	}

	public SelectorChatMessage(String selector) {
		map.put("selector", selector);
	}

	public String getSelector() {
		return (String) map.get("selector");
	}

	public void setSelector(String selector) {
		map.put("selector", selector);
	}

	@Override
	public String toConsoleString() {
		return toString();
	}

	@Override
	public String toLegacyString() {
		return toString();
	}

}
