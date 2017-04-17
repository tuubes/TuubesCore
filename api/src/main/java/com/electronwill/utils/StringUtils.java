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
package com.electronwill.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for manipulating Strings. It provides faster methods than the standard Java library.
 *
 * @author TheElectronWill
 *
 */
public final class StringUtils {

	/**
	 * Removes a character from a String.
	 *
	 * @return a new String contaning the given String without the character to remove
	 */
	public static String remove(String seq, char toRemove) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < seq.length(); i++) {
			final char ch = seq.charAt(i);
			if (ch != toRemove) {
				sb.append(ch);
			}
		}
		return sb.toString();
	}

	/**
	 * Removes several character from a String.
	 *
	 * @return a new String contaning the given String without the characters to remove
	 */
	public static String remove(String seq, char... toRemove) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < seq.length(); i++) {
			final char ch = seq.charAt(i);
			boolean append = true;
			for (int j = 0; j < toRemove.length; j++) {
				final char c = toRemove[j];
				if (ch == c) {
					append = false;
					break;
				}
			}
			if (append) {
				sb.append(ch);
			}
		}
		return sb.toString();
	}

	/**
	 * Splits a String around occurences of a character. The result is similar to
	 * {@link String#split(String)}.
	 */
	public static List<String> split(String cs, char sep) {
		List<String> list = new ArrayList<>(4);
		split(cs, sep, list);
		return list;
	}

	/**
	 * Splits a String around occurences of a character, and put the result in a List. The result is similar
	 * to {@link String#split(String)}.
	 */
	public static void split(String str, char sep, List<String> list) {
		int pos0 = 0;
		for (int i = 0; i < str.length(); i++) {
			char ch = str.charAt(i);
			if (ch == sep) {
				list.add(str.substring(pos0, i));
				pos0 = i + 1;
			}
		}
		if (pos0 < str.length()) {
			list.add(str.substring(pos0, str.length()));
		}
	}

	public static List<String> splitArguments(String str) {
		List<String> list = new ArrayList<>();
		splitArguments(str, list);
		return list;
	}

	public static void splitArguments(String str, List<String> list) {
		StringBuilder sb = new StringBuilder();
		int pos = 0;
		boolean escape = false, inQuotes = false;
		while (pos < str.length()) {
			char ch = str.charAt(pos++);
			if (escape) {
				escape = false;
				sb.append(ch);
			} else if (ch == '\\') {
				escape = true;
			} else if (ch == '"' || ch == '\'') {
				if (inQuotes) {
					inQuotes = false;
					list.add(sb.toString());
					sb.setLength(0);
				} else {
					inQuotes = true;
				}
			} else if (ch == ' ' && !inQuotes) {
				if (sb.length() > 0) {
					list.add(sb.toString());
					sb.setLength(0);
				}
			} else {
				sb.append(ch);
			}
		}
		if (sb.length() > 0) {
			list.add(sb.toString());
		}
	}

	private StringUtils() {
	}

}
