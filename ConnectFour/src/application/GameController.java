package application;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

// TODO: I think I just finished the basic board drawing, will need to test dropping
// TODO: should probably lock the window size during animations, or weirdness might occur

public class GameController {

	@FXML
	StackPane boardStackPane;
	@FXML
	GridPane boardLayer;
	@FXML
	Pane piecesLayer;
	
	private boolean containsPlayer1(int x, int y) {
		return true;
	}
	private boolean containsPlayer2(int x, int y) {
		return false;
	}

	public void initialize() {
		setupBoard();
		// TODO: ask about lambdas? here V
		boardLayer.widthProperty().addListener((obs, oldVal, newVal) -> setupBoard());
		boardLayer.heightProperty().addListener((obs, oldVal, newVal) -> setupBoard());
		// TODO: might also listen for color changes

		// This is where you will add and animate pieces
		// Add test piece
		Circle testPiece = new Circle(20, Color.RED);
		testPiece.setCenterX(100); // Starting X position
		testPiece.setCenterY(-30); // Starting Y position (above the board)
		piecesLayer.getChildren().add(testPiece);

		// Test animation
		TranslateTransition animation = new TranslateTransition(Duration.seconds(1), testPiece);
		animation.setToY(200); // Final Y position (fall into the board)
		animation.setCycleCount(1);
		animation.play();
	}

	private void setupBoard() {
		boardLayer.getChildren().clear(); // Clear existing content

		double cellWidth = boardLayer.getWidth() / 7;
		double cellHeight = boardLayer.getHeight() / 6;

		for (int row = 0; row < 6; row++) {
			for (int col = 0; col < 7; col++) {
				Shape boardCell = createBoardCell(cellWidth, cellHeight);
				boardLayer.add(boardCell, col, row);
				// TODO: check if the cell contains player 1 or 2 piece and add corresponding piece
			}
		}
	}

	private Shape createBoardCell(double width, double height) {
		Rectangle board = new Rectangle(width + 1, height + 1); // slightly larger to cover gaps

		Circle hole = new Circle(width / 2, height / 2, Math.min(width, height) / 2.5);
		hole.setFill(Color.TRANSPARENT);
	    
		Shape boardCell = Shape.subtract(board, hole);
		boardCell.setFill(Color.BROWN);

		return boardCell;
	}
}
