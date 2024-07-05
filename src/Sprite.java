import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import java.io.File;
import java.io.IOException;

public class Sprite {
	
	// CONDITION : chaque frame du spriteSheet doit etre du 32x32
	public static final int TILE_SIZE = 32;
	private static BufferedImage spriteSheet; 

	public static BufferedImage loadSprite(String file) {
		BufferedImage sprite = null;
		try {
			sprite = ImageIO.read(new File(file));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sprite;
	}

	// pour récupérer une partie d'un spriteSheet
	public static BufferedImage getSprite(String file, int xGrid, int yGrid) {
		spriteSheet = loadSprite(file);
		return spriteSheet.getSubimage(xGrid * TILE_SIZE, yGrid * TILE_SIZE, TILE_SIZE, TILE_SIZE);
	}

}
