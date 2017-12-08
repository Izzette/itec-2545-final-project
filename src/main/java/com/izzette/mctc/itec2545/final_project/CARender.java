package com.izzette.mctc.itec2545.final_project;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.Graphics;

/** Render cellular automata rows to an image. */
class CARender {
	/** The size of the cellular automata. */
	final int size;
	/** The number of rows that will be drawn. */
	final int numberOfRows;
	/** The color mapping to use for each cell color. */
	final Color[] colors;

	private final Image caImage;

	/** Create a new CA renderer.
	 * @param size The size of the cellular automata.
	 * @param numberOfRows The number of rows that will be drawn.
	 * @param colors The color mapping to use for each cell color.
	 */
	CARender(int size, int numberOfRows, Color[] colors) {
		this.size = size;
		this.numberOfRows = numberOfRows;
		this.colors = colors;

		this.caImage = new BufferedImage(
				size, numberOfRows, BufferedImage.TYPE_INT_RGB);
	}

	/** Draw a row of the cellular automata.
	 * @param row The row to draw.
	 * @param rowNumber The row number.
	 * @throws IllegalArgumentException If row.length does not equal this.size, or
	 *                                  if row contains color not defined in
	 *                                  this.colors, or if rowNumber is greater
	 *                                  than this.numberOfRows.
	 */
	void drawRow(int[] row, int rowNumber) {
		if (size != row.length || numberOfRows <= rowNumber)
			throw new IllegalArgumentException();

		Graphics graphics = caImage.getGraphics();

		for (int i = 0; row.length > i; ++i) {
			int cell = row[i];

			if (0 > cell || colors.length <= cell)
				throw new IllegalArgumentException();

			graphics.setColor(colors[cell]);
			graphics.drawRect(i, rowNumber, 1, 1);
		}
	}

	/** Draw image onto graphics.
	 * @param graphics Graphics to draw onto.
	 * @param x The x coordinate to draw onto.
	 * @param y The y coordinate to drawn onto.
	 * @return false if the image pixels are still changing; true otherwise.
	 */
	boolean drawOnto(Graphics graphics, int x, int y) {
		return graphics.drawImage(caImage, x, y, null);
	}
}

// vim: set ts=4 sw=4 noet syn=java:
