package laneAndCars;

public interface ILane {
	public void enterFromTheLeft(); 		// allowed if no cars from the right
											// are on the lane;

	
	
	public void exitToTheRight(); 			// a car has left the lane to the right;

	
	public void enterFromTheRight(); 		// allowed if no cars from the left
											// are on the lane;

	
	
	public void exitToTheLeft(); // a car has left the lane to the left;
}
