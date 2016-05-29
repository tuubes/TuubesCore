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
package org.mcphoton.utils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

import javax.imageio.ImageIO;

/**
*
* @author DJmaxZPLAY
*/
public class PhotonFavicon {

	private String encodedFavicon;
	
	public PhotonFavicon() {
		setDefault();
	}	

	public void encode(BufferedImage image) throws IllegalArgumentException, Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ImageIO.write(image, "png", bos);
		byte[] imageBytes = bos.toByteArray();
		encodedFavicon = "data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes);
	}

	public String getEncodedFavicon() {
		return encodedFavicon;
	}

	public void setDefault() {
		encodedFavicon = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAEAAAABACAIAAAAlC+aJAAAAAXNSR0IArs4c"
				+ "6QAAAARnQU1BAACxjwv8YQUAAAAZdEVYdFNvZnR3YXJlAEFkb2JlIEltYWdlUmVhZHlxyWU8AAADJmlUWHRYTUw6Y"
				+ "29tLmFkb2JlLnhtcAAAAAAAPD94cGFja2V0IGJlZ2luPSLvu78iIGlkPSJXNU0wTXBDZWhpSHpyZVN6TlRjemtjOW"
				+ "QiPz4gPHg6eG1wbWV0YSB4bWxuczp4PSJhZG9iZTpuczptZXRhLyIgeDp4bXB0az0iQWRvYmUgWE1QIENvcmUgNS4"
				+ "2LWMwNjcgNzkuMTU3NzQ3LCAyMDE1LzAzLzMwLTIzOjQwOjQyICAgICAgICAiPiA8cmRmOlJERiB4bWxuczpyZGY9"
				+ "Imh0dHA6Ly93d3cudzMub3JnLzE5OTkvMDIvMjItcmRmLXN5bnRheC1ucyMiPiA8cmRmOkRlc2NyaXB0aW9uIHJkZ"
				+ "jphYm91dD0iIiB4bWxuczp4bXA9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC8iIHhtbG5zOnhtcE1NPSJodH"
				+ "RwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvbW0vIiB4bWxuczpzdFJlZj0iaHR0cDovL25zLmFkb2JlLmNvbS94YXA"
				+ "vMS4wL3NUeXBlL1Jlc291cmNlUmVmIyIgeG1wOkNyZWF0b3JUb29sPSJBZG9iZSBQaG90b3Nob3AgQ0MgMjAxNSAo"
				+ "V2luZG93cykiIHhtcE1NOkluc3RhbmNlSUQ9InhtcC5paWQ6M0UxNzI3RkMyNDE5MTFFNkJENUNBM0EzODRGRkFEO"
				+ "TUiIHhtcE1NOkRvY3VtZW50SUQ9InhtcC5kaWQ6M0UxNzI3RkQyNDE5MTFFNkJENUNBM0EzODRGRkFEOTUiPiA8eG"
				+ "1wTU06RGVyaXZlZEZyb20gc3RSZWY6aW5zdGFuY2VJRD0ieG1wLmlpZDozRTE3MjdGQTI0MTkxMUU2QkQ1Q0EzQTM"
				+ "4NEZGQUQ5NSIgc3RSZWY6ZG9jdW1lbnRJRD0ieG1wLmRpZDozRTE3MjdGQjI0MTkxMUU2QkQ1Q0EzQTM4NEZGQUQ5"
				+ "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";
	}
}
