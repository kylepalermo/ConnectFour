package application;

import java.util.ArrayList;
import java.util.HashMap;

import javafx.util.Pair;

// Stores piece location data for Connect Four, allowing pieces to be dropped and a check for if either player has won
// toString is used for debugging

public class PlayerData {
	private long player1Data = 0;
	private long player2Data = 0;
	private long numColumns;
	private long numRows;

	public PlayerData() {
		numColumns = 7;
		numRows = 6;
	}

	// Initialize game state for testing and debugging
	public PlayerData(long columns, long rows, long p1Data, long p2Data) {
		numColumns = columns;
		numRows = rows;
		player1Data = p1Data;
		player2Data = p2Data;
	}

	public PlayerData(long columns, long rows) {
		if (columns * rows > 64 || columns < 1 || rows < 1) {
			throw new IllegalArgumentException("Board must have between 1 and 64 spaces");
		}
		numColumns = columns;
		numRows = rows;
	}

	public PlayerData(PlayerData other) {
		this.player1Data = other.player1Data;
		this.player2Data = other.player2Data;
		this.numColumns = other.numColumns;
		this.numRows = other.numRows;
	}

	// returns the number of columns in the board
	public long getNumColumns() {
		return numColumns;
	}

	// returns the number of rows in the board
	public long getNumRows() {
		return numRows;
	}

	// Adds a piece of the player's color to the lowest cell in the column
	// returns the destination row if successful or -1 if the column is full
	public int drop(long column, int player) {
		// check if column out of bounds
		if (column < 0 || column >= numColumns) {
			throw new IllegalArgumentException("Column index out of bounds: " + column);
		}
		// check if the column is full
		if (((1L << column) & (player1Data | player2Data)) != 0) {
			return -1;
		}
		// loop through positions in column starting at the bottom
		long position = 0;
		for (long i = numRows - 1; i >= 0; i--) {
			position = (1L << (i * numColumns + column));
			// check if neither player has a piece at the position
			if (((player1Data & position) == 0) && ((player2Data & position) == 0)) {
				// update playerData and return destination cell
				if (player == 1) {
					player1Data = player1Data | position;
				}
				if (player == 2) {
					player2Data = player2Data | position;
				}
				return (int) i;
			}
		}

		return 0;
	}

	// returns 0 if no winner, or 1 or 2 if player 1 or 2 is in a winning state
	public int checkWinner() {
		long winComparison = 0;
		// vertical check
		if (numRows >= 4) {
			winComparison = 1L | (1L << numColumns) | (1L << (numColumns * 2)) | (1L << (numColumns * 3));
			long numChecks = (numRows - 3) * numColumns;
			for (long i = 0; i < numChecks; i++) {
				if ((player1Data & winComparison) == winComparison) {
					return 1;
				}
				if ((player2Data & winComparison) == winComparison) {
					return 2;
				}
				winComparison = winComparison << 1;
			}
		}
		// horizontal check
		if (numColumns >= 4) {
			for (long i = 0; i < numRows; i++) {
				winComparison = (1L << (i * numColumns)) | (1L << (i * numColumns + 1)) | (1L << (i * numColumns + 2))
						| (1L << (i * numColumns + 3));
				for (long j = 0; j < numColumns - 3; j++) {
					if ((player1Data & winComparison) == winComparison) {
						return 1;
					}
					if ((player2Data & winComparison) == winComparison) {
						return 2;
					}
					winComparison = winComparison << 1;
				}
			}
		}
		// forward diagonal ( \ ) check
		if (numColumns >= 4 && numRows >= 4) {
			for (long i = 0; i < numRows - 3; i++) {
				winComparison = (1L << (i * numColumns)) | (1L << ((i + 1) * numColumns + 1))
						| (1L << ((i + 2) * numColumns + 2)) | (1L << ((i + 3) * numColumns + 3));
				for (long j = 0; j < numColumns - 3; j++) {
					if ((player1Data & winComparison) == winComparison) {
						return 1;
					}
					if ((player2Data & winComparison) == winComparison) {
						return 2;
					}
					winComparison = winComparison << 1;
				}
			}
		}
		// backward diagonal ( / ) check
		if (numColumns >= 4 && numRows >= 4) {
			for (long i = 0; i < numRows - 3; i++) {
				winComparison = (1L << (i * numColumns) + 3) | (1L << ((i + 1) * numColumns + 2))
						| (1L << ((i + 2) * numColumns + 1)) | (1L << ((i + 3) * numColumns));
				for (long j = 0; j < numColumns - 3; j++) {
					if ((player1Data & winComparison) == winComparison) {
						return 1;
					}
					if ((player2Data & winComparison) == winComparison) {
						return 2;
					}
					winComparison = winComparison << 1;
				}
			}
		}
		return 0;
	}

	// returns an arraylist of pairs with the coordinates of the cells in (one of)
	// the winning lines
	// will be empty if there is no winner
	public ArrayList<Pair<Integer, Integer>> winningLine() {
		ArrayList<Pair<Integer, Integer>> cellArray = new ArrayList<>();
		long winComparison = 0;
		long winningLine = 0;
		// vertical check
		if (numRows >= 4) {
			winComparison = 1L | (1L << numColumns) | (1L << (numColumns * 2)) | (1L << (numColumns * 3));
			long numChecks = (numRows - 3) * numColumns;
			for (long i = 0; i < numChecks; i++) {
				if ((player1Data & winComparison) == winComparison || (player2Data & winComparison) == winComparison) {
					winningLine = winComparison;
					break;
				}
				winComparison = winComparison << 1;
			}
		}
		// horizontal check
		if (winningLine == 0 && numColumns >= 4) {
			for (long i = 0; i < numRows; i++) {
				winComparison = (1L << (i * numColumns)) | (1L << (i * numColumns + 1)) | (1L << (i * numColumns + 2))
						| (1L << (i * numColumns + 3));
				for (long j = 0; j < numColumns - 3; j++) {
					if ((player1Data & winComparison) == winComparison
							|| (player2Data & winComparison) == winComparison) {
						winningLine = winComparison;
						break;
					}
					winComparison = winComparison << 1;
				}
			}
		}
		// forward diagonal ( \ ) check
		if (winningLine == 0 && numColumns >= 4 && numRows >= 4) {
			for (long i = 0; i < numRows - 3; i++) {
				winComparison = (1L << (i * numColumns)) | (1L << ((i + 1) * numColumns + 1))
						| (1L << ((i + 2) * numColumns + 2)) | (1L << ((i + 3) * numColumns + 3));
				for (long j = 0; j < numColumns - 3; j++) {
					if ((player1Data & winComparison) == winComparison
							|| (player2Data & winComparison) == winComparison) {
						winningLine = winComparison;
						break;
					}
					winComparison = winComparison << 1;
				}
			}
		}
		// backward diagonal ( / ) check
		if (winningLine == 0 && numColumns >= 4 && numRows >= 4) {
			for (long i = 0; i < numRows - 3; i++) {
				winComparison = (1L << (i * numColumns) + 3) | (1L << ((i + 1) * numColumns + 2))
						| (1L << ((i + 2) * numColumns + 1)) | (1L << ((i + 3) * numColumns));
				for (long j = 0; j < numColumns - 3; j++) {
					if ((player1Data & winComparison) == winComparison
							|| (player2Data & winComparison) == winComparison) {
						winningLine = winComparison;
						break;
					}
					winComparison = winComparison << 1;
				}
			}
		}
		// convert winning line into cellArray
		if (winningLine != 0) {
			long position = 1L;
			for (long i = 0; i < numRows; i++) {
				for (long j = 0; j < numColumns; j++) {
					if ((winningLine & position) != 0) {
						cellArray.add(new Pair<Integer, Integer>((int) i, (int) j));
					}
					position <<= 1;
				}
			}
		}
		return cellArray;
	}

	// displays board, used for testing and debugging game logic
	public String toString() {
		// " |0123456"
		StringBuilder dataString = new StringBuilder(" |");
		for (long i = 0; i < numColumns; i++) {
			dataString.append(i);
		}
		// "-+-------"
		dataString.append("\n-+" + "-".repeat((int) numColumns) + "\n");
		// add numbers
		long position;
		for (long i = 0; i < numRows; i++) {
			dataString.append(i + "|");
			for (long j = 0; j < numColumns; j++) {
				position = i * numColumns + j;
				if ((player1Data & (1L << position)) != 0) {
					dataString.append("1");
				} else if ((player2Data & (1L << position)) != 0) {
					dataString.append("2");
				} else {
					dataString.append("*");
				}
			}
			dataString.append("\n");
		}
		return dataString.toString();
	}

	// returns a map of row, column pairs to whether player 1 or 2 has a piece at
	// that location
	public HashMap<Pair<Integer, Integer>, Integer> getBoardState() {
		HashMap<Pair<Integer, Integer>, Integer> boardState = new HashMap<>();
		long position = 1L;
		for (long i = 0; i < numRows; i++) {
			for (long j = 0; j < numColumns; j++) {
				if ((player1Data & position) != 0) {
					boardState.put(new Pair<Integer, Integer>((int) i, (int) j), 1);
				} else if ((player2Data & position) != 0) {
					boardState.put(new Pair<Integer, Integer>((int) i, (int) j), 2);
				}
				position <<= 1;
			}
		}
		return boardState;
	}

	// returns true if each cell on the board has been filled
	public boolean isFull() {
		long fullComparison = (1L << (numRows * numColumns)) - 1;
		return fullComparison == (player1Data | player2Data);
	}
}
