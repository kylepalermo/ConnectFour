package application;

import java.util.Map;

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
import javafx.util.Pair;

// TODO: I think I just finished the basic board drawing, will need to test dropping
// TODO: should probably lock the window size during animations, or weirdness might occur
// TODO: make things more generalized

public class GameController {

	@FXML
	StackPane boardStackPane;
	@FXML
	GridPane boardLayer;
	@FXML
	Pane piecesLayer;
	
	// For testing 
	private final long numColumns = 7;
	private final long numRows = 6;
	private final long player1Data = 1;
	private final long player2Data = 2;
	private final Color player1Color = Color.ORANGE;
	private final Color player2Color = Color.BLUE;
	// Testing constructor is in use
	private PlayerData playerData = new PlayerData(numColumns, numRows, player1Data, player2Data);

	public void initialize() {
		setupBoard();
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
		piecesLayer.getChildren().clear();
		
		Map<Pair<Integer, Integer>, Integer> boardState = playerData.getBoardState();
		
		double cellWidth = boardLayer.getWidth() / playerData.getNumColumns();
		double cellHeight = boardLayer.getHeight() / playerData.getNumRows();
		
		for (int row = 0; row < playerData.getNumRows(); row++) {
			for (int col = 0; col < playerData.getNumColumns(); col++) {
				// add board cells
				Shape boardCell = createBoardCell(cellWidth, cellHeight);
				boardLayer.add(boardCell, col, row);
				// add pieces
				Pair<Integer, Integer> cellPair = new Pair<>(row, col);
				Integer player = boardState.get(cellPair);
				if(player != null) {
					Circle piece  = new Circle(Math.min(cellWidth, cellHeight) / 2 - 1,
							player == 1 ? player1Color : player2Color);
					
					double xPosition = (col + .5) * cellWidth;
					double yPosition = (row + .5) * cellHeight;
					piece.setLayoutX(xPosition);
					piece.setLayoutY(yPosition);
					piecesLayer.getChildren().add(piece);
				}
			}
		}
		System.out.println(playerData);
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
