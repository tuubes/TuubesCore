package org.mcphoton.plugin;

import java.util.function.Function;

/**
 *
 * @author TheElectronWill
 */
public class DependencyRequirement {

	private final String name, description;
	private final boolean optional;
	private final Function<String, Boolean> validator;

	public DependencyRequirement(String name, String description, boolean optional, Function<String, Boolean> validator) {
		this.name = name;
		this.description = description;
		this.optional = optional;
		this.validator = validator;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public boolean isOptional() {
		return optional;
	}

	@Override
	public String toString() {
		return name + ": " + description;
	}

	/**
	 * Checks if a version number satisfies this requirement.
	 *
	 * @param version the version to check
	 * @return true if is satisfies this requirement, false otherwise
	 */
	public boolean satisfiesRequirement(String version) {
		return validator.apply(version);
	}

	public static DependencyRequirement parse(String requirement, boolean optional) {
		String[] parts = requirement.split(":");
		if (parts.length != 2) {
			throw new IllegalArgumentException();
		}

		String name = parts[0].trim();
		String versionReq = parts[1].trim();
		String version, condition;
		if (versionReq.charAt(1) == '=') {//== or != or ~= or >= or <=
			condition = versionReq.substring(0, 2);
			version = versionReq.substring(2).trim();
		} else {//> or <
			condition = versionReq.substring(0, 1);
			version = versionReq.substring(1).trim();
		}

		parts = version.split("-", 2);
		String[] mainIdentifiers = parts[0].split("\\.");
		String majorStr = mainIdentifiers[0];
		String minorStr = mainIdentifiers.length > 1 ? mainIdentifiers[1] : "0";
		String patchStr;
		if (mainIdentifiers.length > 2) {
			patchStr = mainIdentifiers[2];
		} else if (minorStr.equals("*") || minorStr.endsWith("+")) {
			patchStr = "*";
		} else {
			patchStr = "0";
		}
		String supplementary = parts.length > 1 ? parts[1] : "";

		Function<String, Boolean> validator;
		switch (condition) {
			case "==":
				validator = createEqualityValidator(majorStr, minorStr, patchStr, supplementary);
				break;
			case "!=":
				validator = createInequalityValidator(majorStr, minorStr, patchStr, supplementary);
				break;
			case "~=":
				validator = createCompatibilityValidator(majorStr, minorStr, patchStr, supplementary);
				break;
			case ">=":
				validator = (v) -> compareVersions(v, majorStr, minorStr, patchStr, supplementary) >= 0;
				break;
			case ">":
				validator = (v) -> compareVersions(v, majorStr, minorStr, patchStr, supplementary) > 0;
				break;
			case "<=":
				validator = (v) -> compareVersions(v, majorStr, minorStr, patchStr, supplementary) <= 0;
				break;
			case "<":
				validator = (v) -> compareVersions(v, majorStr, minorStr, patchStr, supplementary) < 0;
				break;
			default:
				throw new IllegalArgumentException("Invalid condition \"" + condition + "\"");
		}
		return new DependencyRequirement(name, versionReq, optional, validator);
	}

	/**
	 * Compares two versions.
	 *
	 * @param v1 the version's identifiers (must be numbers)
	 * @param v2 the version's identifiers (must be numbers)
	 * @return a positive number if v1 is greater than v2, zero if v1 is equal to v2 and a negative number if
	 * v1 is smaller than v2
	 */
	private static int compareVersions(String[] v1, String[] v2) {
		if (v1.length != v2.length) {
			throw new IllegalArgumentException();
		}
		for (int i = 0; i < v1.length; i++) {
			String partV1 = v1[i];
			String partV2 = v2[i];
			Integer i1 = Integer.parseInt(partV1), i2 = Integer.parseInt(partV2);
			int comparison = i1.compareTo(i2);
			if (comparison != 0) {
				return comparison;
			}
		}
		return 0;
	}

	/**
	 * Compares two versions.
	 *
	 * @param v1 the v1 string
	 * @param majorV2 the v2 major identifier
	 * @param minorV2 the v2 minor identifier
	 * @param patchV2 the v2 patch identifier
	 * @param supplementaryV2 the v2 supplementary identifier
	 * @return a positive number if v1 is greater than v2, zero if v1 is equal to v2 and a negative number if
	 * v1 is smaller than v2
	 */
	private static int compareVersions(String v1, String majorV2, String minorV2, String patchV2, String supplementaryV2) {
		String[] v1Parts = v1.split("-", 2);
		String[] v1Identifiers = v1Parts[0].split("\\.");
		String majorV1 = v1Identifiers[0];
		String minorV1 = v1Identifiers.length > 1 ? v1Identifiers[1] : "0";
		String patchV1 = v1Identifiers.length > 2 ? v1Identifiers[2] : "0";
		String supplementaryV1 = v1Parts.length > 1 ? v1Parts[1] : "";

		v1Identifiers = new String[] {majorV1, minorV1, patchV1};
		String[] v2Identifiers = {majorV2, minorV2, patchV2};

		if (supplementaryV1.equals(supplementaryV2)) {
			return compareVersions(v1Identifiers, v2Identifiers);
		}
		if (supplementaryV1.isEmpty()) {//the version without a supplementary identifier is always greater
			return +1;//v1 is greater than v2
		}
		if (supplementaryV2.isEmpty()) {//the version without a supplementary identifier is always greater
			return -1;//v2 is greater than v1
		}
		return supplementaryV1.compareTo(supplementaryV2);
	}

	private static Function<String, Boolean> createCompatibilityValidator(String majorStr, String minorStr, String patchStr, String supplementary) {
		return (v) -> {
			String[] vParts = v.split("-", 2);
			String[] vIdentifiers = vParts[0].split("\\.");
			String vMajor = vIdentifiers[0];
			String vMinor = vIdentifiers.length > 1 ? vIdentifiers[1] : "0";
			String vPatch = vIdentifiers.length > 2 ? vIdentifiers[2] : "0";
			String vSuppl = vParts.length > 1 ? vParts[1] : "";

			String[] v1, v2;
			/* if (majorStr.equals("0")) {
			 * return vSuppl.equals(supplementary) && vMajor.equals("0") && vMinor.equals(minorStr) &&
			 * vPatch.compareTo(patchStr) >= 0;
			 * }
			 * return vSuppl.equals(supplementary) && vMajor.equals(majorStr) && vMinor.compareTo(minorStr) >=
			 * 0 && vPatch.compareTo(patchStr) >= 0;
			 */
			boolean majorCompatible;
			if (majorStr.equals("0") && vMajor.equals("0")) {
				majorCompatible = minorStr.equals(vMinor);
				v1 = new String[] {vPatch};
				v2 = new String[] {patchStr};
			} else {
				majorCompatible = majorStr.equals(vMajor);
				v1 = new String[] {vMinor, vPatch};
				v2 = new String[] {minorStr, patchStr};
			}
			return majorCompatible && vSuppl.equals(supplementary) && compareVersions(v1, v2) >= 0;
		};
	}

	private static Function<String, Boolean> createInequalityValidator(String majorStr, String minorStr, String patchStr, String supplementary) {
		Function<String, Boolean> equalityValidator = createEqualityValidator(majorStr, minorStr, patchStr, supplementary);
		return (v) -> !equalityValidator.apply(v);
	}

	private static Function<String, Boolean> createEqualityValidator(String majorStr, String minorStr, String patchStr, String supplementary) {
		Function<String, Boolean> majorValidator = createEqualityValidator(majorStr);
		Function<String, Boolean> minorValidator = createEqualityValidator(minorStr);
		Function<String, Boolean> patchValidator = createEqualityValidator(patchStr);
		return (v) -> {
			String[] vParts = v.split("-", 2);
			String[] vIdentifiers = vParts[0].split("\\.");
			String vMajor = vIdentifiers[0];
			String vMinor = vIdentifiers.length > 1 ? vIdentifiers[1] : "0";
			String vPatch = vIdentifiers.length > 2 ? vIdentifiers[2] : "0";
			String vSuppl = vParts.length > 1 ? vParts[1] : "";
			return majorValidator.apply(vMajor) && minorValidator.apply(vMinor) && patchValidator.apply(vPatch) && supplementary.equals(vSuppl);
		};
	}

	private static Function<String, Boolean> createEqualityValidator(String identifier) {
		if (identifier.equals("*")) {
			return (s) -> true;
		}
		if (identifier.endsWith("+")) {
			String v = identifier.substring(0, identifier.length() - 1);
			return (s) -> Integer.parseInt(s) >= Integer.parseInt(v);
		}
		return (s) -> s.equals(identifier);
	}

}
