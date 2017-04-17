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

import java.util.List;
import java.util.Map;
import org.mcphoton.Photon;

/**
 * A textual chat message. This class also provides a way to parse a message that uses '§'-codes, and to
 * create such a message.
 *
 * @author TheElectronWill
 */
public class TextChatMessage extends ChatMessage {

	/**
	 * Parses a "legacy string" that contains color and style codes. Each code consists of 2 characters: the
	 * '§' character and another character. The second character definds the color/style to apply.
	 */
	public static TextChatMessage parse(CharSequence csq) {
		TextChatMessage main = null;// the main part of the msg
		TextChatMessage current = null;// the part we're currently working on
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < csq.length(); i++) {
			char c = csq.charAt(i);
			if (c == '§' && i + 1 < csq.length()) {

				if (main == null) {
					main = new TextChatMessage(sb.toString());
					current = main;
				} else {
					current.setText(sb.toString());
					if (current != main) {
						main.addExtra(current);
					}
					current = new TextChatMessage();
				}
				sb = new StringBuilder();

				char c2 = csq.charAt(++i);
				switch (c2) {
					case '0':
						current.setColor(Color.BLACK);
						break;
					case '1':
						current.setColor(Color.DARK_BLUE);
						break;
					case '2':
						current.setColor(Color.DARK_GREEN);
						break;
					case '3':
						current.setColor(Color.DARK_AQUA);
						break;
					case '4':
						current.setColor(Color.DARK_RED);
						break;
					case '5':
						current.setColor(Color.DARK_PURPLE);
						break;
					case '6':
						current.setColor(Color.GOLD);
						break;
					case '7':
						current.setColor(Color.GREY);
						break;
					case '8':
						current.setColor(Color.DARK_GREY);
						break;
					case '9':
						current.setColor(Color.BLUE);
						break;
					case 'a':
						current.setColor(Color.GREEN);
						break;
					case 'b':
						current.setColor(Color.AQUA);
						break;
					case 'c':
						current.setColor(Color.RED);
						break;
					case 'd':
						current.setColor(Color.LIGHT_PURPLE);
						break;
					case 'e':
						current.setColor(Color.YELLOW);
						break;
					case 'f':
						current.setColor(Color.WHITE);
						break;
					case 'k':
						current.setObfuscated(true);
						break;
					case 'l':
						current.setBold(true);
						break;
					case 'm':
						current.setStrikethrough(true);
						break;
					case 'n':
						current.setUnderlined(true);
						break;
					case 'o':
						current.setItalic(true);
						break;
					case 'r':
						current.setColor(Color.WHITE);
						current.setObfuscated(false);
						current.setBold(false);
						current.setStrikethrough(false);
						current.setUnderlined(false);
						current.setItalic(false);
						break;
					default:// unknown code
						sb.append(c).append(c2);
				}
			} else {
				sb.append(c);
			}
		}
		if (main == null) {
			main = new TextChatMessage(sb.toString());
		} else {
			current.setText(sb.toString());
			if (current != main) {
				main.addExtra(current);
			}
		}
		return main;
	}

	public TextChatMessage() {
	}

	public TextChatMessage(Map<String, Object> map) {
		super(map);
	}

	public TextChatMessage(String text) {
		map.put("text", text);
	}

	public String getText() {
		return (String) map.get("text");
	}

	public void setText(String text) {
		map.put("text", text);
	}

	/**
	 * Returns the "legacy string" which represents this TextChatMessage with color and style codes. Each code
	 * consists of 2 characters: the '§' character and another character. The second character definds the
	 * color/style to apply.
	 */
	@Override
	public String toLegacyString() {
		StringBuilder sb = new StringBuilder();
		if (isBold()) {
			sb.append("§l");
		}
		if (isObfuscated()) {
			sb.append("§k");
		}
		if (isStrikethrough()) {
			sb.append("§m");
		}
		if (isUnderlined()) {
			sb.append("§n");
		}
		if (isItalic()) {
			sb.append("§o");
		}
		Color color = getColor();
		if (color != null) {
			sb.append(color.legacy);
		}
		sb.append(getText());

		List<Object> extras = getExtras();
		if (extras != null) {
			for (Object extra : extras) {
				if (extra instanceof TextChatMessage) {
					TextChatMessage extraMessage = (TextChatMessage) extra;
					if ((extraMessage.isBoldSet() && !extraMessage.isBold() && this.isBold())
							|| (extraMessage.isObfuscatedSet() && !extraMessage.isObfuscated() && this.isObfuscated())
							|| (extraMessage.isStrikethroughSet() && !extraMessage.isStrikethrough() && this.isStrikethrough())
							|| (extraMessage.isUnderlinedSet() && !extraMessage.isUnderlined() && this.isUnderlined())
							|| (extraMessage.isItalicSet() && !extraMessage.isItalic() && this.isItalic())) {
						sb.append("§r");
					}
					sb.append(extraMessage.toLegacyString());
				} else {
					sb.append(extra.toString());
				}
			}
		}
		return sb.toString();
	}

	/**
	 * Returns a string which represents this TextChatMessage with console codes, to use it in the Terminal
	 * (console). Each code consists of a special character sequence. When such a sequence is read by the
	 * Terminal (console), it creates color/style.
	 */
	@Override
	public String toConsoleString() {
		return Photon.isConsoleAdvanced() ? toColorfoulConsoleString() : toBasicConsoleString();
	}

	private String toColorfoulConsoleString() {
		StringBuilder sb = new StringBuilder();
		if (isBold()) {
			sb.append("\u001B[1m");
		}
		// obfuscated does not exist
		if (isStrikethrough()) {
			sb.append("\u001B[9m");
		}
		if (isUnderlined()) {
			sb.append("\u001B[4m");
		}
		if (isItalic()) {
			sb.append("\u001B[3m");
		}

		sb.append(getColor().ansi);
		sb.append(getText());

		List<Object> extras = getExtras();
		if (extras != null) {
			for (Object extra : extras) {
				if (extra instanceof TextChatMessage) {
					TextChatMessage extraMessage = (TextChatMessage) extra;
					if ((extraMessage.isBoldSet() && !extraMessage.isBold() && this.isBold())
							|| (extraMessage.isObfuscatedSet() && !extraMessage.isObfuscated() && this.isObfuscated())
							|| (extraMessage.isStrikethroughSet() && !extraMessage.isStrikethrough() && this.isStrikethrough())
							|| (extraMessage.isUnderlinedSet() && !extraMessage.isUnderlined() && this.isUnderlined())
							|| (extraMessage.isItalicSet() && !extraMessage.isItalic() && this.isItalic())) {
						sb.append("\u001B[0m");
					}
					sb.append(extraMessage.toColorfoulConsoleString());
				} else {
					sb.append(extra.toString());
				}
			}
		}
		sb.append("\u001B[0m");
		return sb.toString();
	}

	private String toBasicConsoleString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getText());

		List<Object> extras = getExtras();
		if (extras != null) {
			for (Object extra : extras) {
				if (extra instanceof TextChatMessage) {
					TextChatMessage extraMessage = (TextChatMessage) extra;
					sb.append(extraMessage.toBasicConsoleString());
				} else {
					sb.append(extra.toString());
				}
			}
		}
		return sb.toString();
	}

}
