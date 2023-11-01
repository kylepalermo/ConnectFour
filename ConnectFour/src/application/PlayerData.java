package application;

// Stores piece location data for Connect Four, allowing pieces to be dropped and a check for if either player has won
// getPlayerData is used for AI to make moves, and toString is used for debugging

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
	
	// returns the piece location data of the player specified
	// or 0 if the player does not exist
	public long getPlayerData(int player) {
		if(player == 1) {
			return player1Data;
		}
		if(player == 2) {
			return player2Data;
		}
		return 0;
	}
	
	// Adds a piece of the player's color to the lowest cell in the column
	// returns the destination cell if successful or 0 if the column is full
	public long drop(long column, int player){
		// check if column out of bounds or if the column is full
		if(column >= NUM_COLUMNS || ((column & player1Data) | (column & player2Data)) != 0) {
			return 0;
		}
		// loop through positions in column starting at the bottom
		long position = 0;
		for(long i = NUM_ROWS - 1; i >= 0; i--) {
			position = (1L << (i * NUM_COLUMNS + column));
			// check if neither player has a piece at the position
			if(((player1Data & position) == 0) && ((player2Data & position) == 0)) {
				// update playerData and return destination cell
				if(player == 1) {
					player1Data = player1Data | position;
				}
				if(player == 2) {
					player2Data = player2Data | position;
				}
				return position;
			}
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
		long position;
		for(long i = 0; i < NUM_ROWS; i++) {
			dataString.append(i + "|");
			for(long j = 0; j < NUM_COLUMNS; j++) {
				position = i * NUM_COLUMNS + j;
				if((player1Data & (1L << position)) != 0) {
					dataString.append("1");
				}
				else if((player2Data & (1L << position)) != 0) {
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
