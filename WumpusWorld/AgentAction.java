
public class AgentAction {
	
	public static final AgentAction moveLeft = new AgentAction(true);
	public static final AgentAction moveRight = new AgentAction(true);
	public static final AgentAction moveUp = new AgentAction(true);
	public static final AgentAction moveDown = new AgentAction(true);
	
	public static final AgentAction pickupSomething = new AgentAction(true);
	public static final AgentAction shootArrowNorth = new AgentAction(true);
	public static final AgentAction shootArrowSouth = new AgentAction(true);
	public static final AgentAction shootArrowEast = new AgentAction(true);
	public static final AgentAction shootArrowWest = new AgentAction(true);
	public static final AgentAction declareVictory = new AgentAction(false);
	public static final AgentAction quit = new AgentAction(false);
	
	public static final AgentAction doNothing = new AgentAction(false);
	
	
	private boolean isAnAction;
	
	public AgentAction(boolean isAnAction) {
		this.isAnAction = isAnAction;
	}
	
	public boolean isAnAction() {
		return isAnAction;
	}
	
	@Override
	public String toString() {
		if(this == moveLeft) {
			return "Move Left";
		}
		else if(this == moveRight) {
			return "Move Right";
		}
		else if(this == moveUp) {
			return "Move Up";
		}
		else if(this == moveDown) {
			return "Move Down";
		}
		else if(this == shootArrowNorth) {
			return "Shoot Arrow North";
		}
		else if(this == shootArrowSouth) {
			return "Shoot Arrow South";
		}
		else if(this == shootArrowEast) {
			return "Shoot Arrow East";
		}
		else if(this == shootArrowWest) {
			return "Shoot Arrow West";
		}
		else if(this == pickupSomething) {
			return "Pickup Something";
		}
		else if(this == declareVictory) {
			return "Declare Victory";
		}
		else if(this == doNothing) {
			return "Do Nothing";
		}
		return null;
	}
	
	public static AgentAction randomAction() {
		AgentAction[] moves = {moveLeft, moveRight, moveUp, moveDown, pickupSomething};
		int i = (int)(Math.random()*moves.length);
		return moves[i];

	}
}
