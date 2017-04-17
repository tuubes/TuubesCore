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

import java.util.HashMap;
import java.util.Map;

public class ScoreChatMessage extends ChatMessage {

	public ScoreChatMessage(Map<String, Object> map) {
		super(map);
	}

	public ScoreChatMessage(String playerName, String objectiveName) {
		HashMap<String, String> map = new HashMap<>();
		map.put("name", playerName);
		map.put("objective", objectiveName);
		this.map.put("score", map);
	}

	public String getObjectiveName() {
		Map<String, String> m = (Map) map.get("score");
		return m.get("objective");
	}

	public String getPlayerName() {
		Map<String, String> m = (Map) map.get("score");
		return m.get("name");
	}

	public void setObjectiveName(String name) {
		Map<String, String> m = (Map) map.get("score");
		m.put("objective", name);
	}

	public void setPlayerName(String name) {
		Map<String, String> m = (Map) map.get("score");
		m.put("name", name);
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
