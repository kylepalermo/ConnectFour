package application;

import java.util.Map;

import javafx.animation.PathTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;

public class GameController {

	@FXML
	BorderPane gamePane;
	@FXML
	GridPane boardLayer;
	@FXML
	Pane piecesLayer;
	@FXML
	MenuItem orangeBlue;
	@FXML
	MenuItem pinkGreen;
	@FXML
	MenuItem yellowPurple;
	
	
	private Color player1Color = Color.hsb(30, .75, 1);
	private Color player2Color = Color.hsb(240, .75, 1);
	private PlayerData playerData = new PlayerData();
	private int playerTurn = 1;
	private int difficulty = 0; // Random

	public void initialize() {
		gamePane.getStyleClass().add("orangeBluePane");
		setupBoard();
		boardLayer.widthProperty().addListener((obs, oldVal, newVal) -> setupBoard());
		boardLayer.heightProperty().addListener((obs, oldVal, newVal) -> setupBoard());
		orangeBlue.setOnAction(colorChange);
		pinkGreen.setOnAction(colorChange);
		yellowPurple.setOnAction(colorChange);
		boardLayer.setOnMouseClicked(dropHandler);
	}
	
	// draws board with an extra empty first row for the dropping animation
	private void setupBoard() {
		boardLayer.getChildren().clear(); // Clear existing content
		piecesLayer.getChildren().clear();
		
		Map<Pair<Integer, Integer>, Integer> boardState = playerData.getBoardState();
		
		double cellWidth = boardLayer.getWidth() / playerData.getNumColumns();
		double cellHeight = boardLayer.getHeight() / (playerData.getNumRows() + 1);
		
		for (int row = 0; row < playerData.getNumRows(); row++) {
			for (int col = 0; col < playerData.getNumColumns(); col++) {
				// add board cells
				Shape boardCell = createBoardCell(cellWidth, cellHeight);
				boardLayer.add(boardCell, col, row + 1);
				// add pieces
				Pair<Integer, Integer> cellPair = new Pair<>(row, col);
				Integer player = boardState.get(cellPair);
				
				Circle piece  = new Circle(Math.min(cellWidth, cellHeight) / 2 - 1);
				if(player == null) {
					piece.setFill(Color.WHITE);
				}
				else {
					piece.setFill(player == 1 ? player1Color : player2Color);
				}
				double xPosition = (col + .5) * cellWidth;
				double yPosition = (row + .5 + 1) * cellHeight;
				piece.setLayoutX(xPosition);
				piece.setLayoutY(yPosition);
				piecesLayer.getChildren().add(piece);
				
			}
		}
	}

	private Shape createBoardCell(double width, double height) {
		Rectangle board = new Rectangle(width + 1, height + 1); // slightly larger to cover gaps

		Circle hole = new Circle(width / 2, height / 2, Math.min(width, height) / 2.5);
		hole.setFill(Color.TRANSPARENT);
	    
		Shape boardCell = Shape.subtract(board, hole);
		boardCell.setFill(Color.rgb(205, 133, 63));
		return boardCell;
	}
	
	
	// drop a piece belonging to player to the position pieceColumn, pieceRow
	private void dropAnimation(long pieceColumn, long pieceRow, int player) {
		// lock resizing until animation is finished
		Stage stage = (Stage) piecesLayer.getScene().getWindow();
		stage.setResizable(false);
		// create and place piece
		double cellWidth = boardLayer.getWidth() / playerData.getNumColumns();
		double cellHeight = boardLayer.getHeight() / (playerData.getNumRows() + 1);
		Circle piece  = new Circle(Math.min(cellWidth, cellHeight) / 2 - 1,
				player == 1 ? player1Color : player2Color);
		
		double xPosition = (pieceColumn + .5) * cellWidth;
		double yStart = .5 * cellHeight;
		// position values in a transition are relative to the starting position
		double yEnd = (pieceRow + 1) * cellHeight;
		double yBounce = yEnd - .75 * cellHeight;
		double duration = (yEnd + (yEnd - yBounce) * 2);
		
		piece.setLayoutX(xPosition);
		piece.setLayoutY(yStart);
		piecesLayer.getChildren().add(piece);
		// move piece
		Path path = new Path();
		path.getElements().add(new MoveTo(0, 0));
		path.getElements().add(new LineTo(0, yEnd));
		path.getElements().add(new LineTo(0, yBounce));
		path.getElements().add(new LineTo(0, yEnd));
		PathTransition transition = new PathTransition(Duration.millis(duration), path, piece);
		transition.setOnFinished(e -> stage.setResizable(true));
		transition.play();
		// TODO: logically, certain stuff should happen when finished, and certain stuff should not happen while animation
	}
	
	EventHandler<ActionEvent> colorChange = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			MenuItem source = (MenuItem) event.getSource();
			switch (source.getId()) {
			case "orangeBlue":
				player1Color = Color.hsb(30, .75, 1);
				player2Color = Color.hsb(240, .75, 1);
				gamePane.getStyleClass().add("orangeBluePane");
				gamePane.getStyleClass().remove("pinkGreenPane");
				gamePane.getStyleClass().remove("yellowPurplePane");
				setupBoard();
				break;
			case "pinkGreen":
				player1Color = Color.hsb(330, .75, 1);
				player2Color = Color.hsb(120, .75, 1);
				gamePane.getStyleClass().add("pinkGreenPane");
				gamePane.getStyleClass().remove("orangeBluePane");
				gamePane.getStyleClass().remove("yellowPurplePane");
				setupBoard();
				break;
			case "yellowPurple":
				player1Color = Color.hsb(60, .75, 1);
				player2Color = Color.hsb(270, .75, 1);
				gamePane.getStyleClass().add("yellowPurplePane");
				gamePane.getStyleClass().remove("orangeBluePane");
				gamePane.getStyleClass().remove("pinkGreenPane");
				setupBoard();
				break;
			}
		}
	};
	
	EventHandler<MouseEvent> dropHandler = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent event) {
			double xPosition = event.getX();
			double cellWidth = boardLayer.getWidth() / playerData.getNumColumns();
			int columnClicked = (int) (xPosition / cellWidth);
			
			int droppedRow = playerData.drop(columnClicked, playerTurn);
			if(droppedRow == -1) {
				// the column was full, do nothing
				return;
			}
			dropAnimation(columnClicked, droppedRow, playerTurn);
			
			int winner = playerData.checkWinner();
			if(winner == playerTurn) {
				// winning sequence
			}
		}
	};
	
	private void AIMove() {
		if(difficulty == 0) {
			
		}
	}
}
