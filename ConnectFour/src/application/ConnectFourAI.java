package application;

import java.util.ArrayList;
import java.util.Random;

// stores functions that return random, easy, hard, and master level AI moves

// TODO: remove this bit
// the lowest level ai functions make playerdatas, test drops, then check wins. The return values would not
// be used in these functions, you would just know the column that resulted in the win or the valid move.
// This is returned to the main gameplay class, which would then use the column to drop in the real playerData,
// and this return value would be used in the GUI.
// The focus of the AI is the results of columns, not the returned final locations

// or

// have your own drop functions? but it is really no that different, and it would be better to have stuff
// consistent and abstracted

public class ConnectFourAI {

	// returns column of easy AI's move, or -1 if there are no moves available
	// easy AI makes and defends immediate wins, but is clueless otherwise
	// TODO: not yet implemented
	public long easyMove(PlayerData playerData, int player) {
		long validMoves = 0;
		long winningMoves = 0;
		long nonBlunderingMoves = 0;
		long blockingMoves = 0;
		for(long i = 0; i < playerData.getNumColumns(); i++) {
			// check validity, winning move, blundering move, blocking move
			if(isValidMove(playerData, i, player)) {
				validMoves |= 1L << i;
			}
			else {
				continue;
			}
			if(isWinningMove(playerData, i, player)) {
				winningMoves |= 1L << i;
			}
			if(!isBlunderingMove(playerData, i, player)) {
				nonBlunderingMoves |= 1L << i;
			}
			if(isBlockingMove(playerData, i, player)) {
				blockingMoves |= 1L << i;
			}
		}
		// return -1 if no moves available
		if(validMoves == 0) {
			return -1;
		}
		// return random winning move
		if(winningMoves != 0) {
			return selectRandomMove(winningMoves);
		}
		// return random blocking and non-blundering move
		if((blockingMoves & nonBlunderingMoves) != 0) {
			return selectRandomMove(blockingMoves & nonBlunderingMoves);
		}
		// return random blocking move
		if(blockingMoves != 0) {
			return selectRandomMove(blockingMoves);
		}
		// return random non-blundering move
		if(nonBlunderingMoves != 0) {
			return selectRandomMove(nonBlunderingMoves);
		}
		// return random valid move
		return selectRandomMove(validMoves);
	}
	
	// returns the column of a random move given a long mask of possible moves
	// or -1 if no moves are given
	private long selectRandomMove(long winningMoves) {
		// TODO testing
		if(winningMoves == 0) {
			return -1;
		}
		ArrayList<Long> moveList= new ArrayList<>();
		for(long i = 0; i < Long.SIZE; i++) {
			if((1L & (winningMoves >> i)) == 1L) {
				moveList.add(i);
			}
		}
		Random random = new Random();
		return moveList.get(random.nextInt(moveList.size()));
	}
	
	// returns true if the player and move given will block an immediate win
	// by an opponent or false if no block occurs or the move is invalid
	private boolean isBlockingMove(PlayerData playerData, long i, int player) {
		// TODO testing
		PlayerData testData = new PlayerData(playerData);
		// if player 1, drop player 2 in that column and check if they win
		if(player == 1) {
			if(testData.drop(i, 2) == 0) {
				return false;
			}
			if(testData.checkWinner() == 2) {
				return true;
			}
		}
		// if player 2, drop player 1 in that column and check if they win
		if(player == 2) {
			if(testData.drop(i, 1) == 0) {
				return false;
			}
			if(testData.checkWinner() == 1) {
				return true;
			}
		}
		return false;
	}

	private boolean isBlunderingMove(PlayerData playerData, long i, int player) {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean isWinningMove(PlayerData playerData, long i, int player) {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean isValidMove(PlayerData playerData, long i, int player) {
		// TODO Auto-generated method stub
		return false;
	}

	// returns column of hard AI's move, or -1 if there are no moves available
	// hard AI makes and defends immediate wins and aggressively sets up wins
	// TODO: not yet implemented
	public long hardMove(long player1Data, long player2Data, int player) {
		return -1;
	}

	// returns column of master AI's move, or -1 if there are no moves available
	// master AI makes optimal moves according to the solved game strategy
	// TODO: not yet implemented
	public long masterMove(long player1Data, long player2Data, int player) {
		return -1;
	}
}
