package org.mcphoton.messaging;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.json.JsonFormat;
import com.electronwill.nightconfig.json.MinimalJsonWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A chat message. In the new chat system it is stored as a JSON object, that's why there's a map in
 * this class. A message consist of a main part plus several optional parts called "extras". These
 * extras can be text, or other ChatMessage.
 *
 * @author TheElectronWill
 */
public abstract class ChatMessage {
	protected final Map<String, Object> map;

	public ChatMessage() {
		this.map = new HashMap<>();
	}

	public ChatMessage(Map<String, Object> map) {
		this.map = map;
	}

	public Color getColor() {
		return map.containsKey("color") ? Color.valueOf(getColorName().toUpperCase()) : null;
	}

	public String getColorName() {
		return (String)map.get("color");
	}

	public Map<String, Object> getMap() {
		return map;
	}

	public List<Object> getExtras() {
		return (List<Object>)map.get("extra");
	}

	public void addExtra(Object extra) {
		if (extra == this) {
			throw new IllegalArgumentException("Cannot add a ChatMessage to it's own extras");
		}
		List<Object> extras = getExtras();
		if (extras == null) {
			extras = new ArrayList<>();
			setExtras(extras);
		}
		if (extra instanceof ChatMessage) {
			ChatMessage extraMsg = (ChatMessage)extra;
			extras.add(extraMsg.getMap());
		} else {
			extras.add(extra);
		}
	}

	public String getInsertion() {
		return (String)map.get("insertion");
	}

	/**
	 * Checks if this message is bold.
	 *
	 * @return true if it's bold, false if it's not bold or if the bold property is unset.
	 */
	public boolean isBold() {
		return (boolean)map.getOrDefault("bold", false);
	}

	public boolean isBoldSet() {
		return map.containsKey("bold");
	}

	/**
	 * Checks if this message is italic.
	 *
	 * @return true if it's italic, false if it's not italic or if the italic property is unset.
	 */
	public boolean isItalic() {
		return (boolean)map.getOrDefault("italic", false);
	}

	public boolean isItalicSet() {
		return map.containsKey("italic");
	}

	/**
	 * Checks if this message is obfuscated.
	 *
	 * @return true if it's obfuscated, false if it's not obfuscated or if the obfuscated property
	 * is unset.
	 */
	public boolean isObfuscated() {
		return (boolean)map.getOrDefault("obfuscated", false);
	}

	public boolean isObfuscatedSet() {
		return map.containsKey("obfuscated");
	}

	/**
	 * Checks if this message is strikethrough.
	 *
	 * @return true if it's strikethrough, false if it's not strikethrough or if the strikethrough
	 * property is
	 * unset.
	 */
	public boolean isStrikethrough() {
		return (boolean)map.getOrDefault("strikethrough", false);
	}

	public boolean isStrikethroughSet() {
		return map.containsKey("strikethrough");
	}

	/**
	 * Checks if this message is underlined.
	 *
	 * @return true if it's underlined, false if it's not underlined or if the underlined property
	 * is unset.
	 */
	public boolean isUnderlined() {
		return (boolean)map.getOrDefault("underlined", false);
	}

	public boolean isUnderlinedSet() {
		return map.containsKey("underlined");
	}

	public void setBold(boolean bold) {
		map.put("bold", bold);
	}

	public void setClickEvent(String action, String value) {
		HashMap<String, String> map = new HashMap<>();
		map.put("action", action);
		map.put("value", value);
		this.map.put("clickEvent", map);
	}

	public void setColor(Color color) {
		map.put("color", color.toString());
	}

	public void setExtras(List<Object> extras) {
		map.put("extra", extras);
	}

	public void setHoverEvent(String action, String value) {
		HashMap<String, String> map = new HashMap<>();
		map.put("action", action);
		map.put("value", value);
		this.map.put("hoverEvent", map);
	}

	public void setInsertion(String insertion) {
		map.put("insertion", insertion);
	}

	public void setItalic(boolean italic) {
		map.put("italic", italic);
	}

	public void setObfuscated(boolean obfuscated) {
		map.put("obfuscated", obfuscated);
	}

	public void setStrikethrough(boolean strikethrough) {
		map.put("strikethrough", strikethrough);
	}

	public void setUnderlined(boolean underlined) {
		map.put("underlined", underlined);
	}

	public void unsetBold() {
		map.remove("bold");
	}

	public void unsetClickEvent() {
		map.remove("clickEvent");
	}

	public void unsetColor() {
		map.remove("color");
	}

	public void unsetExtras() {
		map.remove("extra");
	}

	public void unsetHoverEvent() {
		map.remove("hoverEvent");
	}

	public void unsetInsertion() {
		map.remove("insertion");
	}

	public void unsetItalic() {
		map.remove("italic");
	}

	public void unsetObfuscated() {
		map.remove("obfuscated");
	}

	public void unsetStrikethrough() {
		map.remove("strikethrough");
	}

	public void unsetUndelined() {
		map.remove("underlined");
	}

	/**
	 * Returns a string which represents this TextChatMessage with console codes, to use it in the
	 * Terminal (console). Each code consists of a special character sequence. When such a
	 * sequence is read by the Terminal, it creates color/style.
	 */
	public abstract String toConsoleString();

	/**
	 * Returns the "legacy string" which represents this TextChatMessage with color and style codes.
	 * Each code consists of 2 characters: the '§' character and another character which defines
	 * the color or style to apply.
	 */
	public abstract String toLegacyString();

	/**
	 * Returns a JSON representation of this chat message.
	 */
	@Override
	public String toString() {
		Config conf = Config.wrap(map, JsonFormat.minimalInstance());
		return new MinimalJsonWriter().writeToString(conf);
	}
}