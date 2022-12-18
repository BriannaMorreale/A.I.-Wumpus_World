import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.JPanel;


public class Screen extends JPanel implements KeyListener{
	private static final long serialVersionUID = 1L;
	public static final int tileSize = 128;

	public static final int SLEEP_TIME = 150;

	private GameTile [][] fullMap;
	private GameTile [][] visableMap;

	private int numActions; 
	private boolean playerDeclaresVictory;
	private boolean quit;
	private boolean pickedUpGold;
	private boolean metDeath;

	private int playerX;
	private int playerY;

	private AgentBrain brain;

	private int points = 0;
	private boolean hasArrow;

	public Screen(int rows, int cols){

		rows+=2; //Pad it with walls around the outside
		cols+=2; //Pad it with walls around the outside
		fullMap = new GameTile[rows][cols];
		visableMap = new GameTile[rows][cols];


		System.out.println("Key Listener");
		System.out.println("\tawsd or arrows to move");
		System.out.println("\tijkl to shoot arrow");
		System.out.println("\tspacebar to pickup gold");
		System.out.println("\tv to declare victory");
		System.out.println("\tc to cheat");
		System.out.println("\tq to quit");


		//Just in case things don't work so well with the map
		this.setSize(fullMap.length*tileSize,fullMap[0].length*tileSize);
		this.setPreferredSize(getSize());
		this.setMinimumSize(getSize());

		this.setFocusable(true);

		this.addKeyListener(this);

		setDoubleBuffered(true);

		this.setSize(cols*tileSize,rows*tileSize);
		this.setPreferredSize(getSize());
		this.setMinimumSize(getSize());

		setupInitialVariables();

	}

	//This method adds a pit to the map, and the associated breeze's
	//Doesn't add a pit if it is on a wall
	public static void addPit(int x, int y, GameTile[][] map) {
		if(x < map.length-1 && y < map[0].length-1 && x > 0 && y > 0) {
			if(!map[x][y].isWall()) {
				map[x][y].setPit(true);;
				addBreeze(x+1,y, map);
				addBreeze(x-1,y, map);
				addBreeze(x,y+1, map);
				addBreeze(x,y-1, map);
			}
		}
	}
	//This method adds the wumpus to the map, and the associated stench
	//Doesn't add a wumpus if it is on a wall, but allow wumpus to be in a pit - as if he fell down it
	public static void addWumpus(int x, int y, GameTile[][] map) {
		if(x < map.length-1 && y < map[0].length-1 && x > 0 && y > 0) {
			if(!map[x][y].isWall()) {
				map[x][y].setWumpus(true);
				addStench(x+1,y, map);
				addStench(x-1,y, map);
				addStench(x,y+1, map);
				addStench(x,y-1, map);
			}
		}
	}

	public static BufferedImage findRandomImage(String foldername) {
		BufferedImage image = null;
		try {
			File folder = new File(foldername);
			String [] files = folder.list();
			File f = new File(folder.getCanonicalPath() + "\\" + files[(int)((Math.random()*files.length))]);
			while(f.isDirectory()) { //2 folders in that array
				f = new File(folder.getCanonicalPath()  + "\\" + files[(int)((Math.random()*files.length))]);
			}
			image = ImageIO.read(f);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return image;
	}

	public static BufferedImage loadPlayerImage() {
		BufferedImage playerImage = findRandomImage("images\\Dungeon Crawl Stone Soup Full\\player\\base");
		playerImage = findRandomImage("images\\Dungeon Crawl Stone Soup Full\\player\\cloak",playerImage);
		playerImage = findRandomImage("images\\Dungeon Crawl Stone Soup Full\\player\\boots",playerImage);
		playerImage = findRandomImage("images\\Dungeon Crawl Stone Soup Full\\player\\gloves",playerImage);
		playerImage = findRandomImage("images\\Dungeon Crawl Stone Soup Full\\player\\draconic_head",playerImage);
		//playerImage = findRandomImage("images\\Dungeon Crawl Stone Soup Full\\player\\draconic_wing",playerImage);
		return playerImage;
	}
	
	private static void addBreeze(int x, int y, GameTile[][] map) {
		if(x < map.length-1 && y < map[0].length-1 && x > 0 && y > 0) {
			if(!map[x][y].isWall()) {
				map[x][y].setBreeze(true);;
			}
		}
	}

	private static void addStench(int x, int y, GameTile[][] map) {
		if(x < map.length-1 && y < map[0].length-1 && x > 0 && y > 0) {
			if(!map[x][y].isWall()) {
				map[x][y].setStench(true);
			}
		}
	}

	private void setupInitialVariables() {
		GameTile.setupPictures();
		int rows = fullMap.length;
		int cols = fullMap[0].length;

		hasArrow = true;
		pickedUpGold = false;
		metDeath = false;

		for(int i = 0; i < rows; i++) {
			for(int j = 0; j<cols; j++) {
				fullMap[i][j] = new GameTile(GameTile.IS_GROUND, false);
				visableMap[i][j] = null;
			}
		}

		//Add the walls to the outside of the map
		for (int i = 0; i < rows; i++){
			fullMap[i][0] = new GameTile(GameTile.IS_WALL, true);
			fullMap[i][cols-1] =  new GameTile(GameTile.IS_WALL, true);
			visableMap[i][0] = new GameTile(GameTile.IS_WALL, true);
			visableMap[i][cols-1] = new GameTile(GameTile.IS_WALL, true);
		}
		for (int i = 0; i < cols; i++){
			fullMap[0][i] = new GameTile(GameTile.IS_WALL, true);
			fullMap[rows-1][i] = new GameTile(GameTile.IS_WALL, true);
			visableMap[0][i] = new GameTile(GameTile.IS_WALL, true);
			visableMap[rows-1][i] = new GameTile(GameTile.IS_WALL, true);
		}

		//Add pits, and the breeze around them
		int numPits = (int)(Math.random()*3+1); //1 through 3
		for(int i = 0; i < numPits; i++) {
			boolean found = false;
			while(!found) {
				int x = (int)(Math.random()*fullMap.length-2)+1;
				int y = (int)(Math.random()*fullMap[0].length-2)+1;
				if(x == fullMap.length-2 && y == 1) {
					//Don't add to where player starts
				}
				else if(fullMap[x][y].isGround()) {
					//System.out.println("Adding pit to " + x + " " + y);
					addPit(x,y,fullMap);
					found = true;
				}
			}
		}
		//Add wumpus and stench
		
		
		{
			boolean found = false;
			while(!found) {
				int x = (int)(Math.random()*fullMap.length-2)+1;
				int y = (int)(Math.random()*fullMap[0].length-2)+1;
				if(x == fullMap.length-2 && y == 1) {
					//Don't add to where player starts
				}
				else if(fullMap[x][y].isGround()) {
					//Don't need to remember breeze, because -1000
					addWumpus(x,y,fullMap);
					found = true;
				}
				else if(fullMap[x][y].hasPit() || fullMap[x][y].isWall()) {
					//Don't add it
				}
				else {
					System.err.println("Unhandled case when adding wumpus: " + fullMap[x][y].getTileType());
				}
			}
		}


		//Add Walls to inside of map
		int numWalls = (int)(Math.random()*(rows-3)); //0 through rows-3
		for(int i = 0; i < numWalls; i++) {
			boolean found = false;
			while(!found) {
				int x = (int)(Math.random()*fullMap.length-2)+1;
				int y = (int)(Math.random()*fullMap[0].length-2)+1;
				if(x == fullMap.length-2 && y == 1) {
					//Don't add to where player starts
				}
				else if(fullMap[x][y].isGround() && !fullMap[x][y].hasGlitter() && !fullMap[x][y].hasWumpus()) {
					fullMap[x][y] = new GameTile(GameTile.IS_WALL, false);
					found = true;
				}
			}
		}
		//Add gold
		{
			boolean found = false;
			while(!found) {
				int x = (int)(Math.random()*fullMap.length-2)+1;
				int y = (int)(Math.random()*fullMap[0].length-2)+1;
				if(x == fullMap.length-2 && y == 1) {
					//Don't add to where player starts
				}
				else if(fullMap[x][y].isWall() || fullMap[x][y].hasPit()) {
					//Don't add it
				}
				else if(fullMap[x][y].isGround()) {
					fullMap[x][y].setGlitter(true);

					found = true;
				}
				else {
					System.err.println("Unhandled case when adding stench " + fullMap[x][y].getTileType());
				}
			}
		}


		makeThingsVisableAtThisLocation(fullMap.length-2,1);
		playerX = fullMap.length-2;
		playerY = 1;

		//Cheater code
		//266 - 270 comment out
		//playerX = (int)(Math.random()*(fullMap.length-2))+1;
		//playerY = (int)(Math.random()*(fullMap.length-2))+1;
//		for(int i = 0; i < fullMap.length; i++) {
//			for(int j = 0; j < fullMap[i].length; j++) {
//				makeThingsVisableAtThisLocation(i,j);
//			}
//		}

		fullMap[playerX][playerY].setPlayer(true);

		numActions = 0;
		playerDeclaresVictory = false;

		brain = new AgentBrain();

	}

	private void setNextMove(KeyEvent k, Screen s) {
		int keyEventCode = k.getKeyCode();
		//		System.out.println("Key Event " + keyEventCode);

		if(keyEventCode == KeyEvent.VK_RIGHT || keyEventCode == KeyEvent.VK_D) {
			brain.setNextMove(AgentAction.moveRight);
		}
		else if(keyEventCode == KeyEvent.VK_LEFT || keyEventCode == KeyEvent.VK_A) {
			brain.setNextMove(AgentAction.moveLeft);
		}
		else if(keyEventCode == KeyEvent.VK_UP || keyEventCode == KeyEvent.VK_W) {
			brain.setNextMove(AgentAction.moveUp);
		}
		else if(keyEventCode == KeyEvent.VK_DOWN || keyEventCode == KeyEvent.VK_S) {
			brain.setNextMove(AgentAction.moveDown);
		}
		else if (keyEventCode == KeyEvent.VK_V) {//Player Declares Victory
			brain.setNextMove(AgentAction.declareVictory);
		}
		else if (keyEventCode == KeyEvent.VK_Q) {//Player quits
			brain.setNextMove(AgentAction.quit);
		}
		else if(keyEventCode == KeyEvent.VK_SPACE) {
			//pickup gold/elixer
			brain.setNextMove(AgentAction.pickupSomething);
		}
		else if(keyEventCode == KeyEvent.VK_I) {
			brain.setNextMove(AgentAction.shootArrowNorth);
		}
		else if(keyEventCode == KeyEvent.VK_J) {
			brain.setNextMove(AgentAction.shootArrowWest);
		}
		else if(keyEventCode == KeyEvent.VK_K) {
			brain.setNextMove(AgentAction.shootArrowSouth);
		}
		else if(keyEventCode == KeyEvent.VK_L) {
			brain.setNextMove(AgentAction.shootArrowEast);
		}
		else if(keyEventCode == KeyEvent.VK_C) {
			//Cheatmode on
			GameTile.CHEATMODE_ON = !GameTile.CHEATMODE_ON;

		}
		else {
			System.out.println("Unknown key event " + keyEventCode);
		}

	}

	private void makeThingsVisableAtThisLocation(int x, int y){
		//draw the gold
		visableMap[x][y] = fullMap[x][y];
		visableMap[x][y].setDiscovered(true);
		//System.out.println("making visable: " + fullMap[x][y]);
	}

	private boolean isValidMove(int newRow, int newCol) {
		//System.out.println("Looking at spot " + newX + " " + newY);
		if( 0 <= newRow && 0 <= newCol && newRow < fullMap.length && newCol < fullMap[0].length) {
			//System.out.println("In bounds");
			makeThingsVisableAtThisLocation(newRow, newCol);
			if(fullMap[newRow][newCol].isGround()) {
				return true;
			}
			else {
				return false;
			}
		}
		else {
			System.out.println("Out of bounds");
		}

		return false;
	}

	private void move() {
		AgentAction action = brain.getNextMove(visableMap);
		if(action == null) {
			return;
		}

		int row = playerX;
		int col = playerY;

		if(action == AgentAction.declareVictory) {
			playerDeclaresVictory = true;
		}
		else if(action == AgentAction.quit) {
			playerDeclaresVictory = true;
			quit = true;
		}
		else if(action == AgentAction.pickupSomething) {
			if(fullMap[row][col].hasGlitter()) {
				pickedUpGold = true;
				fullMap[row][col].setGlitter(false);
			}
		}
		else if(action == AgentAction.moveRight) {
			makeThingsVisableAtThisLocation(row,col+1);
			if(isValidMove(row,col+1)) {
				fullMap[row][col].setPlayer(false);
				fullMap[row][col+1].setPlayer(true);
				visableMap[row][col] = fullMap[row][col];
				visableMap[row][col+1] = fullMap[row][col+1];
				playerY++;
			}
		}
		else if(action == AgentAction.moveLeft) {
			makeThingsVisableAtThisLocation(row,col-1);
			if(isValidMove(row,col-1)) {
				fullMap[row][col].setPlayer(false);
				fullMap[row][col-1].setPlayer(true);
				visableMap[row][col] = fullMap[row][col];
				visableMap[row][col-1] = fullMap[row][col-1];
				playerY--;
			}
		}
		else if(action == AgentAction.moveUp) {
			makeThingsVisableAtThisLocation(row-1,col);
			if(isValidMove(row-1,col)) {
				fullMap[row][col].setPlayer(false);
				fullMap[row-1][col].setPlayer(true);
				visableMap[row][col] = fullMap[row][col];
				visableMap[row-1][col] = fullMap[row-1][col];
				playerX--;
			}
		}
		else if(action == AgentAction.moveDown) {
			makeThingsVisableAtThisLocation(row+1,col);
			if(isValidMove(row+1,col)) {
				fullMap[row][col].setPlayer(false);
				fullMap[row+1][col].setPlayer(true);
				visableMap[row][col] = fullMap[row][col];
				visableMap[row+1][col] = fullMap[row+1][col];
				playerX++;
			}
		}
		else if(action == AgentAction.shootArrowNorth) {
			shootArrow(AgentAction.shootArrowNorth);
		}
		else if(action == AgentAction.shootArrowSouth) {
			shootArrow(AgentAction.shootArrowSouth);
		}
		else if(action == AgentAction.shootArrowEast) {
			shootArrow(AgentAction.shootArrowEast);
		}
		else if(action == AgentAction.shootArrowWest) {
			shootArrow(AgentAction.shootArrowWest);
		}
		else if(action == AgentAction.doNothing) {

		}
		else {
			System.out.println("Unhandled action " + action);
		}

		if(action.isAnAction()) {
			numActions++;
			if(fullMap[playerX][playerY].hasPit()) {
				System.out.println("Player met an untimely death by pit");
				metDeath = true;
				playerDeclaresVictory = true;
			}
			if(fullMap[playerX][playerY].hasWumpus()) {
				System.out.println("Player met an untimely death by wumpus");
				metDeath = true;
				playerDeclaresVictory = true;
			}
		}

		//repaint();
	}

	private void shootArrow(AgentAction a) {
		//Only shoot if there is an arrow
		if(!hasArrow) {
			System.out.println("\tNo more arrows");
			return;
		}
		hasArrow = false;

		int x = 0;
		int y = 0;
		boolean foundPlayer = false;
		for(int i = 0; i < fullMap.length && !foundPlayer; i++) {
			for(int j = 0; j < fullMap[i].length && !foundPlayer; j++) {
				if(fullMap[i][j].hasPlayer()) {
					foundPlayer = true;
					x = i;
					y = j;
					//					System.out.println("Found player at " + x + " " + y);
				}
			}

		}
		if(foundPlayer) {
			//arrow stops at wumpus or wall
			boolean foundWumpus = false;
			if(a == AgentAction.shootArrowNorth) {
				for(int i = x; i >= 0 && !foundWumpus; i--) {
					if(fullMap[i][y].hasWumpus()) {
						//						System.out.println("Found wumpus at " + i + " " + y);
						foundWumpus = true;
						fullMap[i][y].setWumpus(false);
						echoTheScream();
					}
					else if (fullMap[i][y].isWall()) {
						return;
					}
				}
			}
			else if(a == AgentAction.shootArrowSouth) {
				for(int i = x; i < fullMap.length && !foundWumpus; i++) {
					if(fullMap[i][y].hasWumpus()) {
						//						System.out.println("Found wumpus at " + i + " " + y);
						foundWumpus = true;
						fullMap[i][y].setWumpus(false);
						echoTheScream();
					}
					else if (fullMap[i][y].isWall()) {
						return;
					}
				}
			}
			else if(a == AgentAction.shootArrowWest) {
				for(int i = y; i >= 0 && !foundWumpus; i--) {
					if(fullMap[x][i].hasWumpus()) {
						//						System.out.println("Found wumpus at " + x + " " + i);
						foundWumpus = true;
						fullMap[x][i].setWumpus(false);
						echoTheScream();
					}
					else if(fullMap[x][i].isWall()) {
						return;
					}
				}
			}
			else if(a == AgentAction.shootArrowEast) {
				for(int i = y; i < fullMap[x].length && !foundWumpus; i++) {
					if(fullMap[x][i].hasWumpus()) {
						//						System.out.println("Found wumpus at " + x + " " + i);
						foundWumpus = true;
						fullMap[x][i].setWumpus(false);
						echoTheScream();
					}
					else if(fullMap[x][i].isWall()) {
						return;
					}
				}
			}
		}
	}

	private void echoTheScream() {
		System.out.println("\tWumpus's woeful death scream echoes through the cave");
		for(int i = 0; i < fullMap.length; i++) {
			for(int j = 0; j < fullMap[i].length; j++) {
				fullMap[i][j].setHeardScream(true);
				if(visableMap[i][j] != null) {
					visableMap[i][j].setHeardScream(true);
				}
			}
		}
	}

	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);

		if(playerDeclaresVictory) {
			System.out.println("This round: ");
			points -= numActions;
			System.out.println("\tTotal Actions: " + numActions);
			if(playerX == fullMap.length-2 && playerY == 1) {
				if(pickedUpGold) {
					points += 1000;
					System.out.println("\tFound gold +1000");
				}
				else {
					System.out.println("\tMade it out alive! But without the gold :(");
				}
			}
			else {
				points -= 1000;
				if(metDeath) {
					System.out.println("\tDied from pit or wumpus -1000");
				}
				else{
					System.out.println("\tGot lost in dark -1000");
				}
			}
			if(!hasArrow) {
				points -= 10;
				System.out.println("\tShot arrow -10");
			}
			System.out.println(points + " Current point total ");
			if(quit) {
				System.exit(0);
			}
			setupInitialVariables();
		}
		else {
			move();
		}

		if(fullMap != null){
			for (int i = 0; i < fullMap.length; i++){
				for (int j = 0; j < fullMap[i].length; j++){
					fullMap[i][j].drawTheImage(g, j*tileSize, i*tileSize, tileSize, tileSize); //Drawing is by col, then row
				}
			}
		}



		try {
			Thread.sleep(SLEEP_TIME);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		repaint();
	}


	private static BufferedImage copyImage(BufferedImage source){
		BufferedImage b = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
		Graphics g = b.getGraphics();
		g.drawImage(source, 0, 0, null);
		g.dispose();
		return b;
	}

	private static BufferedImage findRandomImage(String foldername, BufferedImage bottomStartingImage) {
		BufferedImage image = copyImage(bottomStartingImage);
		BufferedImage tmp = findRandomImage(foldername);
		image.getGraphics().drawImage(tmp,0,0,null);
		return image;
	}


	@Override
	public void keyPressed(KeyEvent e) {
		//System.out.println("Key pressed");
		setNextMove(e,this);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		//System.out.println("Key released");
	}

	@Override
	public void keyTyped(KeyEvent e) {
		//System.out.println("Key typed");
	}

}
