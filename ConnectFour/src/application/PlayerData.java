package application;

// Stores piece location data for Connect Four, allowing pieces to be dropped and a check for if either player has won

public class PlayerData {
	private long player1Data = 0;
	private long player2Data = 0;
	private long NUM_COLUMNS;
	private long NUM_ROWS;
	
	public PlayerData() {
		NUM_COLUMNS = 7;
		NUM_ROWS = 6;
	}
	
	public PlayerData(long rows, long columns) {
		NUM_COLUMNS = Math.min(columns, 8);
		NUM_ROWS = Math.min(rows, 8);
	}
	
	// Adds a piece of the player's color to the lowest cell in the column
	// returns the destination cell if successful or 0 if the column is full
	// TODO: function is very incomplete
	public long drop(long column, int player){
		if(((column & player1Data) | (column & player2Data)) != 0) {
			return 0;
		}
		for(long i = 0; i < NUM_ROWS; i++) {
			
		}
		
		return 0;
	}
	
	// returns 0 if no winner, 1 for player 1's win, or 2 for player 2's win
	// returns -1 if there are multiple winners
	// TODO: not yet implemented
	public int checkWinner() {
		return 0;
	}
	
	public String toString() {
		// " |0123456"
		StringBuilder dataString = new StringBuilder(" |");
		for(long i = 0; i < NUM_COLUMNS; i++) {
			dataString.append(i);
		}
		// "-+-------"
		dataString.append("\n-+" + "-".repeat((int) NUM_COLUMNS)  + "\n");
		// add numbers
		long pos;
		for(long i = 0; i < NUM_ROWS; i++) {
			dataString.append(i + "|");
			for(long j = 0; j < NUM_COLUMNS; j++) {
				pos = i * NUM_COLUMNS + j;
				if((player1Data & (1L << pos)) != 0) {
					dataString.append("1");
				}
				else if((player2Data & (1L << pos)) != 0) {
					dataString.append("2");
				}
				else {
					dataString.append("*");
				}
			}
			dataString.append("\n");
		}
		return dataString.toString();
	}
}
