package org.mcphoton.messaging;

/**
 * A Color.
 * @author TheElectronWill
 */
public enum Color {
	/**
	 * dark black.
	 */
	BLACK("\u001B[2m\u001B[30m", "§0"), /**
	 * light blue.
	 */
	BLUE("\u001B[34m", "§9"), /**
	 * aqua, i.e. light cyan.
	 */
	AQUA("\u001B[36m", "§b"), /**
	 * dark blue.
	 */
	DARK_BLUE("\u001B[2m\u001B[34m", "§1"), /**
	 * dark aqua, i.e dark cyan
	 */
	DARK_AQUA("\u001B[2m\u001B[36m", "§3"), /**
	 * dark green.
	 */
	DARK_GREEN("\u001B[2m\u001B[32m", "§2"), /**
	 * Dark grey, i.e. light black.
	 */
	DARK_GREY("\u001B[30m", "§8"), /**
	 * dark purple, i.e. dark magenta.
	 */
	DARK_PURPLE("\u001B[2m\u001B[35m", "§5"), /**
	 * dark red.
	 */
	DARK_RED("\u001B[2m\u001B[31m", "§4"), /**
	 * Dark yellow, also called "gold" or "orange".
	 */
	GOLD("\u001B[2m\u001B[33m", "§6"), /**
	 * light green
	 */
	GREEN("\u001B[32m", "§a"), /**
	 * Grey, i.e. dark white.
	 */
	GREY("\u001B[37m", "§7"), /**
	 * purple, i.e. light magenta.
	 */
	LIGHT_PURPLE("\u001B[35m", "§d"), /**
	 * light red.
	 */
	RED("\u001B[31m", "§c"), /**
	 * light white.
	 */
	WHITE("\u001B[37m", "§f"), /**
	 * light yellow.
	 */
	YELLOW("\u001B[33m", "§e");

	/**
	 * The ansi code for printing this color in a terminal (works well on Mac, Linux and Solaris).
	 */
	public final String ansi;

	/**
	 * The legacy code of this color.
	 */
	public final String legacy;

	private Color(String ansi, String legacy) {
		this.ansi = ansi;
		this.legacy = legacy;
	}

	@Override
	public String toString() {
		return name().toLowerCase();
	}

	public static Color parseCode(String legacyColorCode) {
		switch (legacyColorCode) {
			case "§0":
				return BLACK;
			case "§1":
				return DARK_BLUE;
			case "§2":
				return DARK_GREEN;
			case "§3":
				return DARK_AQUA;
			case "§4":
				return DARK_RED;
			case "§5":
				return DARK_PURPLE;
			case "§6":
				return GOLD;
			case "§7":
				return GREY;
			case "§8":
				return DARK_GREY;
			case "§9":
				return BLUE;
			case "§a":
				return GREEN;
			case "§b":
				return AQUA;
			case "§c":
				return RED;
			case "§d":
				return LIGHT_PURPLE;
			case "§e":
				return YELLOW;
			case "§f":
				return WHITE;
			default:
				throw new IllegalArgumentException("Invalid color code : " + legacyColorCode);
		}
	}
}