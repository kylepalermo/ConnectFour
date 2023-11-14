package application;

import java.util.ArrayList;
import java.util.Random;

// stores functions that return random, easy, hard, and master level AI moves

public class ConnectFourAI {

	// returns column of easy AI's move, or -1 if there are no moves available
	// easy AI makes and defends immediate wins, and does not make moves that
	// allow enemy wins on top of them
	public static long easyMove(PlayerData playerData, int player) {
		long validMoves = 0;
		long winningMoves = 0;
		long nonBlunderingMoves = 0;
		long blockingMoves = 0;
		for (long i = 0; i < playerData.getNumColumns(); i++) {
			// check validity, winning move, blundering move, blocking move
			if (isValidMove(playerData, i, player)) {
				validMoves |= 1L << i;
			} else {
				continue;
			}
			if (isWinningMove(playerData, i, player)) {
				winningMoves |= 1L << i;
			}
			if (!isBlunderingMove(playerData, i, player)) {
				nonBlunderingMoves |= 1L << i;
			}
			if (isBlockingMove(playerData, i, player)) {
				blockingMoves |= 1L << i;
			}
		}
		/*
		 * System.out.println("Valid Moves: " + Long.toBinaryString(validMoves));
		 * System.out.println("Winning Moves: " + Long.toBinaryString(winningMoves));
		 * System.out.println("Non-Blundering Moves: " +
		 * Long.toBinaryString(nonBlunderingMoves));
		 * System.out.println("Blocking Moves: " + Long.toBinaryString(blockingMoves));
		 */
		// return -1 if no moves available
		if (validMoves == 0) {
			return -1;
		}
		// return random winning move
		if (winningMoves != 0) {
			return selectRandomMove(winningMoves);
		}
		// return random blocking and non-blundering move
		if ((blockingMoves & nonBlunderingMoves) != 0) {
			return selectRandomMove(blockingMoves & nonBlunderingMoves);
		}
		// return random blocking move
		if (blockingMoves != 0) {
			return selectRandomMove(blockingMoves);
		}
		// return random non-blundering move
		if (nonBlunderingMoves != 0) {
			return selectRandomMove(nonBlunderingMoves);
		}
		// return random valid move
		return selectRandomMove(validMoves);
	}

	// returns the column of a random move given a long mask of possible moves
	// or -1 if no moves are given
	private static long selectRandomMove(long winningMoves) {
		if (winningMoves == 0) {
			return -1;
		}
		ArrayList<Long> moveList = new ArrayList<>();
		for (long i = 0; i < Long.SIZE; i++) {
			if ((1L & (winningMoves >> i)) == 1L) {
				moveList.add(i);
			}
		}
		Random random = new Random();
		return moveList.get(random.nextInt(moveList.size()));
	}

	// returns true if the player and move given will block an immediate win
	// by an opponent or false if no block occurs or the move is invalid
	private static boolean isBlockingMove(PlayerData playerData, long i, int player) {
		PlayerData testData = new PlayerData(playerData);
		// if player 1, drop player 2 in that column and check if they win
		if (player == 1) {
			if (testData.drop(i, 2) == 0) {
				return false;
			}
			return testData.checkWinner() == 2;
		}
		// if player 2, drop player 1 in that column and check if they win
		if (player == 2) {
			if (testData.drop(i, 1) == 0) {
				return false;
			}
			return testData.checkWinner() == 1;
		}
		return false;
	}

	// returns true if the player and move given will create an opportunity for
	// the opponent's win or false if no blunder is made or the move is invalid
	private static boolean isBlunderingMove(PlayerData playerData, long i, int player) {
		PlayerData testData = new PlayerData(playerData);
		// if player 1, drop player 1, then player 2 in that column and check for win
		if (player == 1) {
			if (testData.drop(i, 1) == 0) {
				return false;
			}
			if (testData.drop(i, 2) == 0) {
				return false;
			}
			return testData.checkWinner() == 2;
		}
		// if player 2, drop player 2, then player 1 in that column and check for win
		if (player == 2) {
			if (testData.drop(i, 2) == 0) {
				return false;
			}
			if (testData.drop(i, 1) == 0) {
				return false;
			}
			return testData.checkWinner() == 1;
		}
		return false;
	}

	// returns true if the player and move given will cause the player to win
	// or false if no win is made or the move is invalid
	private static boolean isWinningMove(PlayerData playerData, long i, int player) {
		PlayerData testData = new PlayerData(playerData);
		if (testData.drop(i, player) == 0) {
			return false;
		}
		return testData.checkWinner() == player;
	}

	// returns true if the move is valid or false otherwise
	private static boolean isValidMove(PlayerData playerData, long i, int player) {
		PlayerData testData = new PlayerData(playerData);
		return testData.drop(i, player) != 0;
	}

	// returns column of hard AI's move, or -1 if there are no moves available
	// hard AI makes and defends immediate wins and aggressively sets up wins
	// TODO: not yet implemented
	public static long hardMove(PlayerData playerData, int player) {
		return -1;
	}

	// returns column of master AI's move, or -1 if there are no moves available
	// master AI makes optimal moves according to the solved game strategy
	// TODO: not yet implemented
	public static long masterMove(PlayerData playerData, int player) {
		return -1;
	}
}
