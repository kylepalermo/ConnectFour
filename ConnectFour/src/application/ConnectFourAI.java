package application;

import java.util.ArrayList;
import java.util.Random;

// stores functions that return random, easy, and hard level AI moves

public class ConnectFourAI {

	// public methods

	// returns column of the AI move from the corresponding difficulty
	// or -1 if there is no move available or the difficulty is invalid
	public static long AImove(PlayerData playerData, int player, int difficulty) {
		switch (difficulty) {
		case 0:
			return randomMove(playerData, player);
		case 1:
			return easyMove(playerData, player);
		case 2:
			return hardMove(playerData, player);
		}
		return -1;
	}

	// returns column of random AI's move, or -1 if there are no moves available
	// random AI makes random available moves
	public static long randomMove(PlayerData playerData, int player) {
		long validMoves = 0;
		for (long i = 0; i < playerData.getNumColumns(); i++) {
			if (isValidMove(playerData, i, player)) {
				validMoves |= 1L << i;
			}
		}

		if (validMoves == 0) {
			return -1;
		}
		return selectRandomMove(validMoves);
	}

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

	// returns column of hard AI's move, or -1 if there are no moves available
	// hard AI makes and defends immediate wins and looks ahead 8 moves to make
	// plays
	public static long hardMove(PlayerData playerData, int player) {
		return minimax(playerData, player, 8, Integer.MIN_VALUE, Integer.MAX_VALUE, true).column;
	}

	// private helper methods

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

	// returns true if the move is valid or false otherwise
	private static boolean isValidMove(PlayerData playerData, long i, int player) {
		PlayerData testData = new PlayerData(playerData);
		return testData.drop(i, player) != -1;
	}

	// returns true if the player and move given will cause the player to win
	// or false if no win is made or the move is invalid
	private static boolean isWinningMove(PlayerData playerData, long i, int player) {
		PlayerData testData = new PlayerData(playerData);
		if (testData.drop(i, player) == -1) {
			return false;
		}
		return testData.checkWinner() == player;
	}

	// returns true if the player and move given will create an opportunity for
	// the opponent's win or false if no blunder is made or the move is invalid
	private static boolean isBlunderingMove(PlayerData playerData, long i, int player) {
		PlayerData testData = new PlayerData(playerData);
		// if player 1, drop player 1, then player 2 in that column and check for win
		if (player == 1) {
			if (testData.drop(i, 1) == -1) {
				return false;
			}
			if (testData.drop(i, 2) == -1) {
				return false;
			}
			return testData.checkWinner() == 2;
		}
		// if player 2, drop player 2, then player 1 in that column and check for win
		if (player == 2) {
			if (testData.drop(i, 2) == -1) {
				return false;
			}
			if (testData.drop(i, 1) == -1) {
				return false;
			}
			return testData.checkWinner() == 1;
		}
		return false;
	}

	// returns true if the player and move given will block an immediate win
	// by an opponent or false if no block occurs or the move is invalid
	private static boolean isBlockingMove(PlayerData playerData, long i, int player) {
		PlayerData testData = new PlayerData(playerData);
		// if player 1, drop player 2 in that column and check if they win
		if (player == 1) {
			if (testData.drop(i, 2) == -1) {
				return false;
			}
			return testData.checkWinner() == 2;
		}
		// if player 2, drop player 1 in that column and check if they win
		if (player == 2) {
			if (testData.drop(i, 1) == -1) {
				return false;
			}
			return testData.checkWinner() == 1;
		}
		return false;
	}

	// Returns the count of moves that will lead to a win for the given player
	private static int countWinningMoves(PlayerData playerData, int player) {
		int winningMoveCount = 0;
		for (long i = 0; i < playerData.getNumColumns(); i++) {
			if (isWinningMove(playerData, i, player)) {
				winningMoveCount++;
			}
		}
		return winningMoveCount;
	}

	// Returns the count of moves that would cause the player to create an
	// opportunity for the opponent's win
	private static int countBlunderingMoves(PlayerData playerData, int player) {
		int blunderingMoveCount = 0;
		for (long i = 0; i < playerData.getNumColumns(); i++) {
			if (isBlunderingMove(playerData, i, player)) {
				blunderingMoveCount++;
			}
		}
		return blunderingMoveCount;
	}

	// Returns the count of moves that will block an immediate win by an opponent
	private static int countBlockingMoves(PlayerData playerData, int player) {
		int blockingMoveCount = 0;
		for (long i = 0; i < playerData.getNumColumns(); i++) {
			if (isBlockingMove(playerData, i, player)) {
				blockingMoveCount++;
			}
		}
		return blockingMoveCount;
	}

	// Return a list of valid moves
	private static ArrayList<Long> getValidMoves(PlayerData board) {
		ArrayList<Long> validMoves = new ArrayList<>();
		for (long i = 0; i < board.getNumColumns(); i++) {
			if (isValidMove(board, i, 2)) {
				validMoves.add(i);
			}
		}
		return validMoves;
	}

	private static int evaluateBoard(PlayerData board, int player) {
		int score = 0;
		// Add points for winning moves
		score += countWinningMoves(board, player) * 100;
		// Subtract points for blundering moves
		score -= countBlunderingMoves(board, player) * 20;
		// Add points for 1 blocking move, Subtract points for blocking moves > 1
		if (countBlockingMoves(board, player) == 1) {
			score += 50;
		} else {
			score -= countBlockingMoves(board, player) * 30;
		}
		return score;
	}

	private static Move minimax(PlayerData board, int player, int depth, int alpha, int beta,
			boolean maximizingPlayer) {
		if (depth == 0 || board.isFull() || board.checkWinner() != 0) {
			return new Move(null, evaluateBoard(board, player));
		}

		// Maximizing player (AI)
		if (maximizingPlayer) {
			Move bestMove = new Move(null, Integer.MIN_VALUE);
			for (Long column : getValidMoves(board)) {
				PlayerData newBoard = new PlayerData(board);
				newBoard.drop(column, player);
				Move move = minimax(newBoard, player, depth - 1, alpha, beta, false);
				move.column = column;
				if (move.score > bestMove.score) {
					bestMove = move;
				}
				alpha = Math.max(alpha, bestMove.score);
				if (beta <= alpha) {
					break; // Alpha-beta pruning
				}
			}
			return bestMove;
		} else {
			// Minimizing player (opponent)
			Move bestMove = new Move(null, Integer.MAX_VALUE);
			int opponent = player == 1 ? 2 : 1;
			for (Long column : getValidMoves(board)) {
				PlayerData newBoard = new PlayerData(board);
				newBoard.drop(column, opponent);
				Move move = minimax(newBoard, player, depth - 1, alpha, beta, true);
				move.column = column;
				if (move.score < bestMove.score) {
					bestMove = move;
				}
				beta = Math.min(beta, bestMove.score);
				if (beta <= alpha) {
					break; // Alpha-beta pruning
				}
			}
			return bestMove;
		}
	}

	// Class for representing moves in minimax

	private static class Move {
		public Long column;
		public int score;

		public Move(Long column, int score) {
			this.column = column;
			this.score = score;
		}
	}

}
