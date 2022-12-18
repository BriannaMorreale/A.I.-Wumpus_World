import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;


public class GameTile {
	
	public static boolean CHEATMODE_ON = false;

	public static BufferedImage pit;
	public static BufferedImage wumpus;
	public static BufferedImage glitter;
	public static BufferedImage breeze;
	public static BufferedImage stench;
	public static BufferedImage wumpusScream;
	public static BufferedImage wall;
	public static BufferedImage ground;
	public static BufferedImage unknown;
	public static BufferedImage playerImage;

	public static final int IS_GROUND = 0;
	public static final int IS_WALL = 1;
	public static final int IS_UNKNOWN = 2;

	static {
		setupPictures();
	}

	private boolean hasPit;
	private boolean hasWumpus;
	private boolean hasGlitter;
	private boolean hasBreeze;
	private boolean hasStench;
	private boolean hasPlayer;
	private boolean heardScream;
	private int tileType;

	private boolean discovered;

	public GameTile(){
		tileType = IS_UNKNOWN;
		discovered = false;
		hasPit = false;
		hasWumpus = false;
		hasGlitter = false;
		hasBreeze = false;
		hasStench = false;
		hasPlayer = false;
		heardScream = false;
		
	}

	public GameTile(int tileType, boolean discovered){
		this.discovered = discovered;
		this.tileType = tileType;

		hasPit = false;
		hasWumpus = false;
		hasGlitter = false;
		hasBreeze = false;
		hasStench = false;
		hasPlayer = false;
	}
	
	public GameTile(GameTile t) {
		discovered = t.discovered;
		tileType = t.tileType;

		hasPit = t.hasPit;
		hasWumpus = t.hasWumpus;
		hasGlitter = t.hasGlitter;
		hasBreeze = t.hasBreeze;
		hasStench = t.hasStench;
		hasPlayer = t.hasPlayer;
		heardScream = t.heardScream;
	}
	

	public boolean heardScream() {
		return heardScream;
	}

	public void setHeardScream(boolean heardScream) {
		this.heardScream = heardScream;
	}

	public boolean isGround() {
		return tileType == IS_GROUND;
	}

	public boolean isWall() {
		return tileType == IS_WALL;
	}

	public int getTileType() {
		return tileType;
	}

	public void setTileType(int tileType) {
		this.tileType = tileType;
	}

	public boolean hasPlayer() {
		return hasPlayer;
	}

	public void setPlayer(boolean hasPlayer) {
		this.hasPlayer = hasPlayer;
	}

	public boolean hasPit() {
		return hasPit;
	}

	public void setPit(boolean hasPit) {
		this.hasPit = hasPit;
	}

	public boolean hasWumpus() {
		return hasWumpus;
	}

	public void setWumpus(boolean hasWumpus) {
		this.hasWumpus = hasWumpus;
	}

	public boolean hasStench() {
		return hasStench;
	}

	public void setStench(boolean hasStench) {
		this.hasStench = hasStench;
	}

	public boolean hasBreeze() {
		return hasBreeze;
	}

	public void setBreeze(boolean hasBreeze) {
		this.hasBreeze = hasBreeze;
	}

	public boolean hasGlitter() {
		return hasGlitter;
	}

	public void setGlitter(boolean hasGlitter) {
		this.hasGlitter = hasGlitter;
	}

	public boolean hasBeenDiscovered() {
		return discovered;
	}

	public void setDiscovered(boolean discovered) {
		this.discovered = discovered;
	}



	public void drawTheImage(Graphics g, int colOnGraphics, int rowOnGraphics, int imageWidth, int imageHeight){

		if(!hasBeenDiscovered() && CHEATMODE_ON == false) {
			g.drawImage(unknown, (int)colOnGraphics,(int)rowOnGraphics, imageWidth, imageHeight, null);
		}
		else {

			if(tileType == IS_GROUND){
				g.drawImage(ground, (int)colOnGraphics,(int)rowOnGraphics, imageWidth, imageHeight, null);
			}
			else if(tileType == IS_WALL){
				g.drawImage(wall, (int)colOnGraphics,(int)rowOnGraphics, imageWidth, imageHeight, null);
			}
			else if(tileType == IS_UNKNOWN){
				g.drawImage(unknown, (int)colOnGraphics,(int)rowOnGraphics, imageWidth, imageHeight, null);
			}
			else {
				System.err.println("Unknown tile type " + tileType);
			}

			if(hasPlayer) {
				g.drawImage(playerImage, (int)colOnGraphics,(int)rowOnGraphics, imageWidth, imageHeight, null);
			}
			if(hasStench) {
				g.drawImage(stench, (int)colOnGraphics+imageWidth/2,(int)rowOnGraphics, imageWidth/2, imageHeight/2, null);
			}
			if(hasBreeze) {
				g.drawImage(breeze, (int)colOnGraphics,(int)rowOnGraphics, imageWidth/2, imageHeight/2, null);
			}
			if(hasGlitter) {
				g.drawImage(glitter, (int)colOnGraphics,(int)rowOnGraphics, imageWidth, imageHeight, null);
			}
			if(hasWumpus) {
				g.drawImage(wumpus, (int)colOnGraphics,(int)rowOnGraphics, imageWidth, imageHeight, null);
			}
			if(hasPit) {
				g.drawImage(pit, (int)colOnGraphics,(int)rowOnGraphics, imageWidth, imageHeight, null);
			}
			if(heardScream) {
				g.drawImage(wumpusScream, (int)colOnGraphics,(int)rowOnGraphics+imageHeight/2, imageWidth/2, imageHeight/2, null);
			}
			
		}

	}

	@Override
	public String toString() {
		String s = "";
		if(tileType == IS_GROUND){
			s += " ";
		}
		else if(tileType == IS_WALL){
			s += "#";
		}
		else if(tileType == IS_UNKNOWN){
			s += "U";
		}
		else {
			System.err.println("Unknown tile type " + tileType);
		}

		if(hasPlayer) {
			s += "P";
		}
		if(hasStench) {
			s += "S";
		}
		if(hasBreeze) {
			s += "B";
		}
		if(hasGlitter) {
			s += "G";
		}
		if(hasWumpus) {
			s += "U";
		}
		if(hasPit) {
			s += "H";
		}
		if(heardScream) {
			s += "D";
		}
		return s + " ";
	}

	public static void setupPictures() {
		pit = Screen.findRandomImage("images\\Dungeon Crawl Stone Soup Full\\dungeon\\traps");
		wumpus = Screen.findRandomImage("images\\Dungeon Crawl Stone Soup Full\\monster");
		glitter = Screen.findRandomImage("images\\Dungeon Crawl Stone Soup Full\\item\\gold");
		breeze = Screen.findRandomImage("images\\Dungeon Crawl Stone Soup Full\\dungeon\\floor\\breeze");
		stench = Screen.findRandomImage("images\\Dungeon Crawl Stone Soup Full\\dungeon\\floor\\stench");
		wumpusScream = Screen.findRandomImage("images\\Dungeon Crawl Stone Soup Full\\dungeon\\floor\\wumpusScream");
		wall = Screen.findRandomImage("images\\Dungeon Crawl Stone Soup Full\\dungeon\\wall");
		ground = Screen.findRandomImage("images\\Dungeon Crawl Stone Soup Full\\dungeon\\floor"); 
		unknown = Screen.findRandomImage("images\\Dungeon Crawl Stone Soup Full\\dungeon\\floor\\unknown");
		playerImage = Screen.loadPlayerImage();
	}
	
}
