//Brianna Morreale
//12/9/2022
import java.util.LinkedList;
import java.util.Queue;

public class AgentBrain {

	// Don't delete this variable
	private AgentAction nextMove;

	// We reload the brain each time, so this variable needs to be static
	private static int numGamesPlayed = 0;
	private static boolean keyboardPlayOnly = false;
	private boolean bool = false;
	private boolean bool2 = false;

	private int currentNumMoves;

	public AgentBrain() {
		nextMove = null;

		bool2 = false;

		numGamesPlayed++;

		currentNumMoves = 0;
	}

	public void setNextMove(AgentAction m) {
		if (nextMove != null) {
			System.out.println("Trouble adding move, only allowed to add 1 at a time");
		} else {
			nextMove = m;
		}
	}

	// For wumpus world, we do one move at a time
	public AgentAction getNextMove(GameTile[][] visibleMap) {
		// Possible things to add to your moves
		// nextMove = AgentAction.doNothing;
		// nextMove = AgentAction.moveDown;
		// nextMove = AgentAction.moveUp;
		// nextMove = AgentAction.moveUp;
		// nextMove = AgentAction.moveLeft;
		// nextMove = AgentAction.pickupSomething;
		// nextMove = AgentAction.declareVictory;
		//
		// nextMove = AgentAction.shootArrowNorth;
		// nextMove = AgentAction.shootArrowSouth;
		// nextMove = AgentAction.shootArrowEast;
		// nextMove = AgentAction.shootArrowWest;
		// nextMove = AgentAction.quit;

		// Ideally you would remove all this code, but I left it in so the keylistener
		// would work
		if (keyboardPlayOnly) {
			if (nextMove == null) {
				return AgentAction.doNothing;
			} else {
				AgentAction tmp = nextMove;
				nextMove = null;
				return tmp;
			}

		} else {
			// This code plays 5 "games" and then quits
			// Just does random things

			if (numGamesPlayed >= 21) {
				return AgentAction.quit;
			} else {
				currentNumMoves++;
				if (currentNumMoves < 50) {
					// call method
					if (bool2 == false) {
						nextMove = searchDarkness(visibleMap);
					} else {
						nextMove = getOutAlive(visibleMap);
					}

					return nextMove;

				} else {
					return AgentAction.declareVictory;
				}
			}
		}

	}

	private AgentAction getOutAlive(GameTile[][] visibleMap) {
		int playerRow;
		int playerCol;

		GameTile[][] mentalMap = new GameTile[6][6];
		for (int i = 0; i < visibleMap.length; i++) {
			for (int j = 0; j < visibleMap[i].length; j++) {
				if(visibleMap[i][j] != null) {
					mentalMap[i][j] = new GameTile(visibleMap[i][j]);
				}
			}
		}

		for (int i = 0; i < visibleMap.length; i++) {
			for (int j = 0; j < visibleMap[i].length; j++) {
				if (visibleMap[i][j] != null && visibleMap[i][j].hasPlayer()) {
					playerRow = i;
					playerCol = j;
					
					if (playerRow == 4 && playerCol == 1) {
						return AgentAction.declareVictory;
					}

					mentalMap[playerRow][playerCol].setPit(true);

					int a = Integer.MAX_VALUE;
					int b = Integer.MAX_VALUE;
					int c = Integer.MAX_VALUE;
					int d = Integer.MAX_VALUE;

					
					a = traverse(mentalMap, playerRow - 1, playerCol);
					b = traverse(mentalMap, playerRow, playerCol + 1);
					c = traverse(mentalMap, playerRow, playerCol - 1);
					d = traverse(mentalMap, playerRow + 1, playerCol);
					
					System.out.println("Get out alive A "+a );
					System.out.println("Get out alive B "+b );
					System.out.println("Get out alive C "+c);
					System.out.println("Get out alive D "+d );

					if (a <= b && a <= c && a <= d) {
						return AgentAction.moveUp;
					}

					if (b <= a && b <= c && b <= d) {
						return AgentAction.moveRight;
					}

					if (c <= a && c <= b && c <= d) {
						return AgentAction.moveLeft;
					}

					if (d <= a && d <= b && d <= c) {
						return AgentAction.moveDown;
					}

					mentalMap[playerRow][playerCol].setPit(false);
				}
			}
		}

		return null;
	}

	private AgentAction huntWumpus(GameTile[][] visibleMap) {
		int playerRow;
		int playerCol;

		GameTile[][] mentalMap = new GameTile[6][6];
		for (int i = 0; i < visibleMap.length; i++) {
			for (int j = 0; j < visibleMap[i].length; j++) {
				mentalMap[i][j] = new GameTile(visibleMap[i][j]);
			}
		}

		// Hunt Wumpus
		for (int i = 0; i < visibleMap.length; i++) {
			for (int j = 0; j < visibleMap[i].length; j++) {
				if (visibleMap[i][j].hasPlayer()) {
					playerRow = i;
					playerCol = j;

					// Shoot Wumpus
					if (visibleMap[playerRow][playerCol].hasStench() && !visibleMap[playerRow][playerCol].hasPit()
							&& !visibleMap[playerRow][playerCol].isWall()) {
						System.out.println("Ready to Shoot");
						if (mentalMap[playerRow - 1][playerCol].hasWumpus()) {
							bool = true;
							bool2 = true;
							return AgentAction.shootArrowNorth;
						}
						if (mentalMap[playerRow + 1][playerCol].hasWumpus()) {
							bool = true;
							bool2 = true;
							return AgentAction.shootArrowSouth;
						}
						if (mentalMap[playerRow][playerCol - 1].hasWumpus()) {
							bool = true;
							bool2 = true;
							return AgentAction.shootArrowWest;
						}
						if (mentalMap[playerRow][playerCol + 1].hasWumpus()) {
							bool = true;
							bool2 = true;
							return AgentAction.shootArrowEast;
						}

					}
					mentalMap[playerRow][playerCol].setPit(true);

					int a = Integer.MAX_VALUE;
					int b = Integer.MAX_VALUE;
					int c = Integer.MAX_VALUE;
					int d = Integer.MAX_VALUE;

					a = hunt(mentalMap, playerRow - 1, playerCol);
					b = hunt(mentalMap, playerRow, playerCol + 1);
					c = hunt(mentalMap, playerRow, playerCol - 1);
					d = hunt(mentalMap, playerRow + 1, playerCol);

					if (a <= b && a <= c && a <= d) {
						if (bool) {
							bool = false;
							return AgentAction.declareVictory;
						}

						
						return AgentAction.moveUp;
					}

					if (b <= a && b <= c && b <= d) {
						if (bool) {
							bool = false;
							return AgentAction.declareVictory;
						}

						
						return AgentAction.moveRight;
					}

					if (c <= a && c <= b && c <= d) {
						if (bool) {
							bool = false;
							return AgentAction.declareVictory;
						}

						
						return AgentAction.moveLeft;
					}

					if (d <= a && d <= b && d <= c) {
						if (bool) {
							bool = false;
							return AgentAction.declareVictory;
						}

					
						return AgentAction.moveDown;
					}

					mentalMap[playerRow][playerCol].setPit(false);
				}
			}
		}
		return null;
	}

	private AgentAction findGold(GameTile[][] visibleMap) {
		int playerRow;
		int playerCol;

		GameTile[][] mentalMap = new GameTile[6][6];
		for (int i = 0; i < visibleMap.length; i++) {
			for (int j = 0; j < visibleMap[i].length; j++) {
				if(visibleMap[i][j] != null) {
					mentalMap[i][j] = new GameTile(visibleMap[i][j]);
				}
			}
		}

		// Hunt Wumpus
		for (int i = 0; i < visibleMap.length; i++) {
			for (int j = 0; j < visibleMap[i].length; j++) {
				if ( visibleMap[i][j] != null && visibleMap[i][j].hasPlayer()) {
					playerRow = i;
					playerCol = j;

					// Shoot Wumpus
					if (!visibleMap[playerRow][playerCol].hasPit() && !visibleMap[playerRow][playerCol].isWall()) {
						System.out.println("Ready to Shoot");
						if (visibleMap[playerRow - 1][playerCol] != null && mentalMap[playerRow - 1][playerCol].hasWumpus()) {
							return AgentAction.shootArrowNorth;
						}
						if (visibleMap[playerRow + 1][playerCol] != null && mentalMap[playerRow + 1][playerCol].hasWumpus()) {
							return AgentAction.shootArrowSouth;
						}
						if (visibleMap[playerRow][playerCol - 1] != null && mentalMap[playerRow][playerCol - 1].hasWumpus()) {
							return AgentAction.shootArrowWest;
						}
						if (visibleMap[playerRow][playerCol + 1] != null && mentalMap[playerRow][playerCol + 1].hasWumpus()) {

							return AgentAction.shootArrowEast;
						}

					}
					
					if(visibleMap[playerRow][playerCol].hasGlitter()) {
						bool2 = true;
						return AgentAction.pickupSomething;
					}
					
					mentalMap[playerRow][playerCol].setPit(true);

					int a = Integer.MAX_VALUE;
					int b = Integer.MAX_VALUE;
					int c = Integer.MAX_VALUE;
					int d = Integer.MAX_VALUE;

					a = scrounge(mentalMap, playerRow - 1, playerCol);
					b = scrounge(mentalMap, playerRow, playerCol + 1);
					c = scrounge(mentalMap, playerRow, playerCol - 1);
					d = scrounge(mentalMap, playerRow + 1, playerCol);

					if (a <= b && a <= c && a <= d) {
						if (bool) {
							bool = false;
							return AgentAction.declareVictory;
						}

						
						return AgentAction.moveUp;
					}

					if (b <= a && b <= c && b <= d) {
						if (bool) {
							bool = false;
							return AgentAction.declareVictory;
						}

					
						return AgentAction.moveRight;
					}

					if (c <= a && c <= b && c <= d) {
						if (bool) {
							bool = false;
							return AgentAction.declareVictory;
						}

					
						return AgentAction.moveLeft;
					}

					if (d <= a && d <= b && d <= c) {
						if (bool) {
							bool = false;
							return AgentAction.declareVictory;
						}

				
						return AgentAction.moveDown;
					}

					mentalMap[playerRow][playerCol].setPit(false);
				}
			}
		}

		return null;
	}
	
	private AgentAction searchDarkness(GameTile[][] visibleMap) {
		int playerRow;
		int playerCol;

		GameTile[][] mentalMap = new GameTile[6][6];
		for (int i = 0; i < visibleMap.length; i++) {
			for (int j = 0; j < visibleMap[i].length; j++) {
				if(visibleMap[i][j] != null) {
					mentalMap[i][j] = new GameTile(visibleMap[i][j]);
				}
			}
		}

		// Hunt Wumpus
		for (int i = 0; i < visibleMap.length; i++) {
			for (int j = 0; j < visibleMap[i].length; j++) {
				if ( visibleMap[i][j] != null && visibleMap[i][j].hasPlayer()) {
					playerRow = i;
					playerCol = j;
					
					
					if(visibleMap[playerRow][playerCol].hasGlitter()) {
						System.out.println("Found Gold!");
						bool2 = true;
						return AgentAction.pickupSomething;
					}
					
					
					mentalMap[playerRow][playerCol].setPit(true);

					int a = Integer.MAX_VALUE;
					int b = Integer.MAX_VALUE;
					int c = Integer.MAX_VALUE;
					int d = Integer.MAX_VALUE;

					
					if(mentalMap[playerRow - 1][playerCol] != null || !(mentalMap[playerRow][playerCol].hasStench() ||mentalMap[playerRow][playerCol].hasBreeze())) {
						a = brave(mentalMap, playerRow - 1, playerCol);
					}
					if(mentalMap[playerRow ][playerCol + 1] != null || !(mentalMap[playerRow][playerCol].hasStench() ||mentalMap[playerRow][playerCol].hasBreeze())) {
						b = brave(mentalMap, playerRow, playerCol + 1);
					}
					if(mentalMap[playerRow][playerCol - 1] != null || !(mentalMap[playerRow][playerCol].hasStench() ||mentalMap[playerRow][playerCol].hasBreeze())) {
						c = brave(mentalMap, playerRow, playerCol - 1);
					}
					if(mentalMap[playerRow + 1][playerCol] != null || !(mentalMap[playerRow][playerCol].hasStench() ||mentalMap[playerRow][playerCol].hasBreeze())) {
						d = brave(mentalMap, playerRow + 1, playerCol);
					}
					
					if (a == Integer.MAX_VALUE && b == Integer.MAX_VALUE && c == Integer.MAX_VALUE && d == Integer.MAX_VALUE) {
						bool = true;
						bool2 = true;
						return AgentAction.doNothing;
					}

					if (a <= b && a <= c && a <= d) {
						return AgentAction.moveUp;
					}

					if (b <= a && b <= c && b <= d) {
						return AgentAction.moveRight;
					}

					if (c <= a && c <= b && c <= d) {
						return AgentAction.moveLeft;
					}

					if (d <= a && d <= b && d <= c) {
						return AgentAction.moveDown;
					}

					mentalMap[playerRow][playerCol].setPit(false);
				}
			}
		}
		bool = true;
		bool2 = true;
		return null;
	}

	private static int traverse(GameTile[][] traverseMap, int playerRow, int playerCol) {

		GameTile[][] mentalMap = new GameTile[6][6];
		for (int i = 0; i < traverseMap.length; i++) {
			for (int j = 0; j < traverseMap[i].length; j++) {
				if(traverseMap[i][j] != null){
					mentalMap[i][j] = new GameTile(traverseMap[i][j]);
				}
			}
		}
		
		if (playerRow == 4 && playerCol == 1) {
			return 1;
		}
		
		if (playerRow < 1 || playerRow >= mentalMap.length - 1 || playerCol < 1
				|| playerCol >= mentalMap[playerRow].length - 1) {
			return Integer.MAX_VALUE;
		}
		
		int a = Integer.MAX_VALUE;
		int b = Integer.MAX_VALUE;
		int c = Integer.MAX_VALUE;
		int d = Integer.MAX_VALUE;


		if(mentalMap[playerRow][playerCol] == null) {
			return Integer.MAX_VALUE;
		}
		if (mentalMap[playerRow][playerCol] != null && mentalMap[playerRow][playerCol].isWall()) {
			return Integer.MAX_VALUE;
		}
		if (mentalMap[playerRow][playerCol] != null && mentalMap[playerRow][playerCol].hasPit()) {
			return Integer.MAX_VALUE;
		}
		if (mentalMap[playerRow][playerCol] != null && mentalMap[playerRow][playerCol].hasWumpus()) {
			return Integer.MAX_VALUE;
		}
	
		if(mentalMap[playerRow][playerCol] != null) {
			mentalMap[playerRow][playerCol].setPit(true);
		}

		if (mentalMap[playerRow][playerCol - 1] != null && !mentalMap[playerRow][playerCol - 1].isWall() && !mentalMap[playerRow][playerCol - 1].hasPit()) {
			a = traverse(mentalMap, playerRow, playerCol - 1);

		}

		if (mentalMap[playerRow + 1][playerCol] != null && !mentalMap[playerRow + 1][playerCol].isWall() && !mentalMap[playerRow + 1][playerCol].hasPit()) {
			b = traverse(mentalMap, playerRow + 1, playerCol);
		}

		if (mentalMap[playerRow - 1][playerCol] != null && !mentalMap[playerRow - 1][playerCol].isWall() && !mentalMap[playerRow - 1][playerCol].hasPit()) {
			c = traverse(mentalMap, playerRow - 1, playerCol);
		}

		if (mentalMap[playerRow][playerCol + 1] != null && !mentalMap[playerRow][playerCol + 1].isWall() && !mentalMap[playerRow][playerCol + 1].hasPit()) {
			d = traverse(mentalMap, playerRow, playerCol + 1);
		}


		if (a != Integer.MAX_VALUE && a != Integer.MAX_VALUE + 1 && a <= b && a <= c && a <= d) {
			return a + 1;
		}

		if (b != Integer.MAX_VALUE && b != Integer.MAX_VALUE + 1 && b <= a && b <= c && b <= d) {
			return b + 1;
		}

		if (c != Integer.MAX_VALUE && c != Integer.MAX_VALUE + 1 && c <= b && c <= a && c <= d) {
			return c + 1;
		}

		if (d != Integer.MAX_VALUE && d != Integer.MAX_VALUE + 1 && d <= b && d <= c && d <= a) {
			return d + 1;
		}

		return Integer.MAX_VALUE;

	}

	private static int hunt(GameTile[][] traverseMap, int playerRow, int playerCol) {
		
		GameTile[][] mentalMap = new GameTile[6][6];
		for (int i = 0; i < traverseMap.length; i++) {
			for (int j = 0; j < traverseMap[i].length; j++) {
				mentalMap[i][j] = new GameTile(traverseMap[i][j]);
			}
		}
		
		int a = Integer.MAX_VALUE;
		int b = Integer.MAX_VALUE;
		int c = Integer.MAX_VALUE;
		int d = Integer.MAX_VALUE;


		if (mentalMap[playerRow][playerCol].isWall()) {
			return Integer.MAX_VALUE;
		}
		if (mentalMap[playerRow][playerCol].hasPit()) {
			return Integer.MAX_VALUE;
		}
		if (mentalMap[playerRow][playerCol].hasWumpus()) {
			return Integer.MAX_VALUE;
		}
		if (mentalMap[playerRow][playerCol].hasStench()) {
			return 1;
		}

		if (playerRow < 1 || playerRow >= mentalMap.length - 1 || playerCol < 1
				|| playerCol >= mentalMap[playerRow].length - 1) {
			return Integer.MAX_VALUE;
		}

		mentalMap[playerRow][playerCol].setPit(true);

		if (!mentalMap[playerRow][playerCol - 1].isWall() && !mentalMap[playerRow][playerCol - 1].hasPit()) {
			a = hunt(mentalMap, playerRow, playerCol - 1);

		}

		if (!mentalMap[playerRow + 1][playerCol].isWall() && !mentalMap[playerRow + 1][playerCol].hasPit()) {
			b = hunt(mentalMap, playerRow + 1, playerCol);
		}

		if (!mentalMap[playerRow - 1][playerCol].isWall() && !mentalMap[playerRow - 1][playerCol].hasPit()) {
			c = hunt(mentalMap, playerRow - 1, playerCol);
		}

		else if (!mentalMap[playerRow][playerCol + 1].isWall() && !mentalMap[playerRow][playerCol + 1].hasPit()) {
			d = hunt(mentalMap, playerRow, playerCol + 1);
		}


		if (a != Integer.MAX_VALUE && a != Integer.MAX_VALUE - 1 && a <= b && a <= c && a <= d) {
			return a + 1;
		}

		if (b != Integer.MAX_VALUE && a != Integer.MAX_VALUE - 1 && b <= a && b <= c && b <= d) {
			return b + 1;
		}

		if (c != Integer.MAX_VALUE && a != Integer.MAX_VALUE - 1 && c <= b && c <= a && c <= d) {
			return c + 1;
		}

		if (d != Integer.MAX_VALUE && a != Integer.MAX_VALUE - 1 && d <= b && d <= c && d <= a) {
			return d + 1;
		}

		return Integer.MAX_VALUE;
	}

	private static int scrounge(GameTile[][] traverseMap, int playerRow, int playerCol) {
		GameTile[][] mentalMap = new GameTile[6][6];
		for (int i = 0; i < traverseMap.length; i++) {
			for (int j = 0; j < traverseMap[i].length; j++) {
				if(mentalMap[i][j] != null) {
					mentalMap[i][j] = new GameTile(traverseMap[i][j]);
				}
			}
		}
		
		if (mentalMap[playerRow][playerCol] != null && !mentalMap[playerRow][playerCol].hasGlitter()) {
			System.out.println("Found the gold!");
			return 1;
		}
		
		if (playerRow < 1 || playerRow >= mentalMap.length - 1 || playerCol < 1
				|| playerCol >= mentalMap[playerRow].length - 1) {
			return Integer.MAX_VALUE;
		}
		
		int a = Integer.MAX_VALUE;
		int b = Integer.MAX_VALUE;
		int c = Integer.MAX_VALUE;
		int d = Integer.MAX_VALUE;


		if (mentalMap[playerRow][playerCol] != null && mentalMap[playerRow][playerCol].isWall()) {
			return Integer.MAX_VALUE;
		}
		if (mentalMap[playerRow][playerCol] != null && mentalMap[playerRow][playerCol].hasPit()) {
			return Integer.MAX_VALUE;
		}
		if (mentalMap[playerRow][playerCol] != null && mentalMap[playerRow][playerCol].hasWumpus()) {
			return Integer.MAX_VALUE;
		}
	
		if(mentalMap[playerRow][playerCol] != null) {
			mentalMap[playerRow][playerCol].setPit(true);
		}
		

		if (mentalMap[playerRow][playerCol - 1] != null && !mentalMap[playerRow][playerCol - 1].isWall() && !mentalMap[playerRow][playerCol + 1].hasPit()) {
			a = brave(mentalMap, playerRow, playerCol - 1);

		}

		if (mentalMap[playerRow + 1][playerCol] != null && !mentalMap[playerRow + 1][playerCol].isWall() && !mentalMap[playerRow][playerCol + 1].hasPit()) {
			b = brave(mentalMap, playerRow + 1, playerCol);
		}

		if (mentalMap[playerRow - 1][playerCol] != null && !mentalMap[playerRow - 1][playerCol].isWall() && !mentalMap[playerRow][playerCol + 1].hasPit()) {
			c = brave(mentalMap, playerRow - 1, playerCol);
		}

		else if (mentalMap[playerRow][playerCol + 1] != null && !mentalMap[playerRow][playerCol + 1].isWall() && !mentalMap[playerRow][playerCol + 1].hasPit()) {
			d = brave(mentalMap, playerRow, playerCol + 1);
		}


		if (a != Integer.MAX_VALUE && a != Integer.MAX_VALUE + 1 && a <= b && a <= c && a <= d) {
			return a + 1;
		}

		if (b != Integer.MAX_VALUE && a != Integer.MAX_VALUE + 1 && b <= a && b <= c && b <= d) {
			return b + 1;
		}

		if (c != Integer.MAX_VALUE && a != Integer.MAX_VALUE + 1 && c <= b && c <= a && c <= d) {
			return c + 1;
		}

		if (d != Integer.MAX_VALUE && a != Integer.MAX_VALUE + 1 && d <= b && d <= c && d <= a) {
			return d + 1;
		}

		return Integer.MAX_VALUE;
	}
	
	private static int brave(GameTile[][] traverseMap, int playerRow, int playerCol) {
		GameTile[][] mentalMap = new GameTile[6][6];
		for (int i = 0; i < traverseMap.length; i++) {
			for (int j = 0; j < traverseMap[i].length; j++) {
				if(traverseMap[i][j] != null) {
					mentalMap[i][j] = new GameTile(traverseMap[i][j]);
				}
			}
		}
		

		if (playerRow < 1 || playerRow >= mentalMap.length - 1 || playerCol < 1
				|| playerCol >= mentalMap[playerRow].length - 1) {
			return Integer.MAX_VALUE;
		}
		
		
		if (mentalMap[playerRow][playerCol] == null) {
			return 1;
		}
		
		
		int a = Integer.MAX_VALUE;
		int b = Integer.MAX_VALUE;
		int c = Integer.MAX_VALUE;
		int d = Integer.MAX_VALUE;

		

		if (mentalMap[playerRow][playerCol] != null && mentalMap[playerRow][playerCol].isWall()) {
			return Integer.MAX_VALUE;
		}
		if (mentalMap[playerRow][playerCol] != null && mentalMap[playerRow][playerCol].hasPit()) {
			return Integer.MAX_VALUE;
		}
		if (mentalMap[playerRow][playerCol] != null && mentalMap[playerRow][playerCol].hasWumpus()) {
			return Integer.MAX_VALUE;
		}
		if (mentalMap[playerRow][playerCol] != null && mentalMap[playerRow][playerCol].hasBreeze()) {
			return Integer.MAX_VALUE;
		}
		if (mentalMap[playerRow][playerCol] != null && mentalMap[playerRow][playerCol].hasStench()) {
			return Integer.MAX_VALUE;
		}
		
	
		if(mentalMap[playerRow][playerCol] != null) {
			mentalMap[playerRow][playerCol].setPit(true);
		}
		
		if(!(mentalMap[playerRow][playerCol].hasStench() || mentalMap[playerRow][playerCol].hasBreeze())) {
			if (mentalMap[playerRow][playerCol - 1] == null || (!mentalMap[playerRow][playerCol - 1].isWall() && !mentalMap[playerRow][playerCol - 1].hasPit())) {
				a = brave(mentalMap, playerRow, playerCol - 1);
		
			}
		
			if (mentalMap[playerRow + 1][playerCol] == null || (!mentalMap[playerRow + 1][playerCol].isWall() && !mentalMap[playerRow + 1][playerCol].hasPit())) {
				b = brave(mentalMap, playerRow + 1, playerCol);
			}
		
			if (mentalMap[playerRow - 1][playerCol] == null || (!mentalMap[playerRow - 1][playerCol].isWall() && !mentalMap[playerRow - 1][playerCol].hasPit())) {
				c = brave(mentalMap, playerRow - 1, playerCol);
			}
		
			if (mentalMap[playerRow][playerCol + 1] == null || (!mentalMap[playerRow][playerCol + 1].isWall() && !mentalMap[playerRow][playerCol + 1].hasPit())) {
				d = brave(mentalMap, playerRow, playerCol + 1);
			}
		}
		
		
		if (a == Integer.MAX_VALUE && b == Integer.MAX_VALUE && c == Integer.MAX_VALUE && d == Integer.MAX_VALUE) {
			System.out.println("ALL BAD");
		}

		if (a != Integer.MAX_VALUE && a != Integer.MAX_VALUE + 1 && a <= b && a <= c && a <= d) {
			return a + 1;
		}

		if (b != Integer.MAX_VALUE && a != Integer.MAX_VALUE + 1 && b <= a && b <= c && b <= d) {
			return b + 1;
		}

		if (c != Integer.MAX_VALUE && a != Integer.MAX_VALUE + 1 && c <= b && c <= a && c <= d) {
			return c + 1;
		}

		if (d != Integer.MAX_VALUE && a != Integer.MAX_VALUE + 1 && d <= b && d <= c && d <= a) {
			return d + 1;
		}

		return Integer.MAX_VALUE;
	}

}


