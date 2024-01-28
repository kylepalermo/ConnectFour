package application;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PathTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
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
	@FXML
	StackPane boardStackPane;
	@FXML
	Pane lineLayer;
	@FXML
	MenuButton moveLogMenuButton;
	@FXML
	Button newGameButtonToolbar;
	@FXML
	Button saveGameButtonToolbar;
	@FXML
	Button loadGameButtonToolbar;
	@FXML
	MenuItem logMenuItem;

	private Color player1Color = Color.hsb(30, .75, 1);
	private Color player2Color = Color.hsb(240, .75, 1);
	private PlayerData playerData = new PlayerData();
	private int difficulty = 0; // Random
	private boolean isAnimating = false;
	private int winner = 0;
	private Scene difficultySelectionScene;
	private SaveData saveData;

	public void initialize() {
		gamePane.getStyleClass().add("orangeBluePane");
		setupBoard();
		boardLayer.widthProperty().addListener((obs, oldVal, newVal) -> setupBoard());
		boardLayer.heightProperty().addListener((obs, oldVal, newVal) -> setupBoard());
		orangeBlue.setOnAction(colorChange);
		pinkGreen.setOnAction(colorChange);
		yellowPurple.setOnAction(colorChange);
		newGameButtonToolbar.setOnAction(newGameHandler);
		saveGameButtonToolbar.setOnAction(saveGameHandler);
		loadGameButtonToolbar.setOnAction(loadGameHandler);
		boardLayer.setOnMouseClicked(dropHandler);
	}

	public void setDifficulty(int difficulty) {
		this.difficulty = difficulty;
	}

	public void setDifficultySelectionScene(Scene scene) {
		difficultySelectionScene = scene;
	}

	public void setData(SaveData loadData) {
		ArrayList<Integer> moves = loadData.getMoves();
		int loadPlayer = 1;
		for (Integer move : moves) {
			playerData.drop((long) move, loadPlayer);
			loadPlayer = loadPlayer == 1 ? 2 : 1;
		}
		difficulty = loadData.getDifficulty();
		saveData = loadData;
		saveData.setSaveName("autosave");
		refreshMoveLog();
	}

	private void refreshMoveLog() {
		ArrayList<Integer> moves = saveData.getMoves();
		if (moves.size() == 0) {
			logMenuItem.setText("No moves");
			return;
		}

		String logString = "Player | Column\n";
		int logPlayer = 1;
		for (Integer move : moves) {
			logString += logPlayer + "         | " + (move + 1) + "\n";
			logPlayer = logPlayer == 1 ? 2 : 1;
		}
		logMenuItem.setText(logString);
	}

	// draws board with an extra empty first row for the dropping animation
	private void setupBoard() {
		boardLayer.getChildren().clear(); // Clear existing content
		piecesLayer.getChildren().clear();
		lineLayer.getChildren().clear();

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

				Circle piece = new Circle(Math.min(cellWidth, cellHeight) / 2 - 1);
				if (player == null) {
					piece.setFill(Color.WHITE);
				} else {
					piece.setFill(player == 1 ? player1Color : player2Color);
				}
				double xPosition = (col + .5) * cellWidth;
				double yPosition = (row + .5 + 1) * cellHeight;
				piece.setLayoutX(xPosition);
				piece.setLayoutY(yPosition);
				piecesLayer.getChildren().add(piece);
			}
		}

		if (winner != 0 || playerData.isFull()) {
			ArrayList<Pair<Integer, Integer>> winningCells = playerData.winningLine();
			winLine(winningCells.get(0), winningCells.get(3), winner, null);
			winBox(null);
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
		// create and place piece
		double cellWidth = boardLayer.getWidth() / playerData.getNumColumns();
		double cellHeight = boardLayer.getHeight() / (playerData.getNumRows() + 1);
		Circle piece = new Circle(Math.min(cellWidth, cellHeight) / 2 - 1, player == 1 ? player1Color : player2Color);

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
		transition.setOnFinished(e -> postAnimationLogic(player));
		transition.play();
	}

	// draw across (one of) the winning lines and display a congratulatory message
	// with buttons
	private void winAnimation() {
		ArrayList<Pair<Integer, Integer>> winningCells = playerData.winningLine();
		// winning line and prompt animations
		if (winner == 0) {
			// no winLine if there is a stalemate
			TranslateTransition winBoxAnimation = new TranslateTransition();
			winBox(winBoxAnimation);
			winBoxAnimation.play();
		} else {
			Timeline winLineAnimation = new Timeline();
			winLine(winningCells.get(0), winningCells.get(3), winner, winLineAnimation);
			TranslateTransition winBoxAnimation = new TranslateTransition();
			winBox(winBoxAnimation);
			SequentialTransition winningAnimations = new SequentialTransition(winLineAnimation, winBoxAnimation);
			winningAnimations.play();
		}
	}

	// return a line from the first cell to the other cell with the player's color
	// if timeline is not null, timeline will become an animation drawing the line
	private Line winLine(Pair<Integer, Integer> startCell, Pair<Integer, Integer> endCell, int player,
			Timeline timeline) {
		double cellWidth = boardLayer.getWidth() / playerData.getNumColumns();
		double cellHeight = boardLayer.getHeight() / (playerData.getNumRows() + 1);
		double lineWidth = Math.min(cellWidth, cellHeight) / 1.25;
		double startX = (startCell.getValue() + .5) * cellWidth;
		double startY = (startCell.getKey() + 1.5) * cellHeight;
		double endX = (endCell.getValue() + .5) * cellWidth;
		double endY = (endCell.getKey() + 1.5) * cellHeight;

		// Calculate the direction vector and normalize it
		double dx = endX - startX;
		double dy = endY - startY;
		double length = Math.sqrt(dx * dx + dy * dy);
		double dirX = dx / length; // Normalized direction x
		double dirY = dy / length; // Normalized direction y

		// Adjust start and end points. fixes a quirk with how line width is added to
		// its length
		startX += dirX * lineWidth / 2;
		startY += dirY * lineWidth / 2;
		endX -= dirX * lineWidth / 2;
		endY -= dirY * lineWidth / 2;

		Line line = new Line(startX, startY, startX, startY);
		line.setStrokeWidth(lineWidth);
		line.setOpacity(.5);
		line.setStroke(player == 1 ? player1Color : player2Color);

		if (timeline != null) {
			KeyValue kvEndX = new KeyValue(line.endXProperty(), endX);
			KeyValue kvEndY = new KeyValue(line.endYProperty(), endY);
			KeyFrame kf = new KeyFrame(Duration.seconds(1), kvEndX, kvEndY);
			timeline.getKeyFrames().add(kf);
		} else {
			line.setEndX(endX);
			line.setEndY(endY);
		}
		lineLayer.getChildren().add(line);
		return line;
	}

	private VBox winBox(TranslateTransition transition) {
		// create winning message box
		VBox messageBox = new VBox();
		Label messageLabel = new Label();
		switch (winner) {
		case 0:
			messageLabel.setText("It's a Draw");
			break;
		case 1:
			messageLabel.setText("You Win");
			break;
		case 2:
			messageLabel.setText("You Lose");
			break;
		}
		Button newGameButton = new Button("New Game");
		newGameButton.setOnAction(newGameHandler);
		Button loadGameButton = new Button("Load Game");
		loadGameButton.setOnAction(loadGameHandler);
		double cellHeight = boardLayer.getHeight() / (playerData.getNumRows() + 1);
		messageLabel.setPrefHeight(cellHeight / 3);
		newGameButton.setPrefHeight(cellHeight / 3);
		loadGameButton.setPrefHeight(cellHeight / 3);
		messageBox.getChildren().addAll(messageLabel, newGameButton, loadGameButton);
		messageBox.setAlignment(Pos.CENTER);
		messageBox.setPrefWidth(200);
		messageBox.setPrefHeight(cellHeight);
		// add to the scene
		messageBox.setLayoutX((boardLayer.getWidth() / playerData.getNumColumns()) * 3.5 - 100);
		lineLayer.getChildren().add(messageBox);

		if (transition != null) {
			messageBox.setLayoutY(-500);
			// Animate the box
			transition.setDuration(Duration.seconds(1));
			transition.setNode(messageBox);
			transition.setToY(475); // The end position on screen
			transition.setOnFinished(e -> {
				enableInteractions();
				lineLayer.setMouseTransparent(false);
				saveGameButtonToolbar.setDisable(true);
			});
		} else {
			messageBox.setLayoutY(-25);
		}

		return messageBox;
	}

	EventHandler<ActionEvent> newGameHandler = new EventHandler<>() {
		@Override
		public void handle(ActionEvent event) {
			Stage stage = (Stage) piecesLayer.getScene().getWindow();
			stage.setScene(difficultySelectionScene);
		}
	};

	EventHandler<ActionEvent> saveGameHandler = new EventHandler<>() {
		@Override
		public void handle(ActionEvent event) {
			try {
				Scene scene = gamePane.getScene();
				Stage stage = (Stage) scene.getWindow();

				double currentWidth = stage.getWidth();
				double currentHeight = stage.getHeight();

				FXMLLoader saveLoader = new FXMLLoader(getClass().getResource("Save.fxml"));
				Parent saveRoot = saveLoader.load();
				SaveController saveController = saveLoader.getController();

				saveController.setPreviousScene(scene);
				saveController.setSaveData(saveData);

				Scene saveScene = new Scene(saveRoot);
				saveScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

				stage.setScene(saveScene);
				stage.setWidth(currentWidth);
				stage.setHeight(currentHeight);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};

	EventHandler<ActionEvent> loadGameHandler = new EventHandler<>() {
		@Override
		public void handle(ActionEvent event) {
			try {
				Scene scene = gamePane.getScene();
				Stage stage = (Stage) scene.getWindow();

				double currentWidth = stage.getWidth();
				double currentHeight = stage.getHeight();

				FXMLLoader loadLoader = new FXMLLoader(getClass().getResource("Load.fxml"));
				Parent loadRoot = loadLoader.load();
				LoadController loadController = loadLoader.getController();

				loadController.setDifficultySelectionScene(difficultySelectionScene);
				loadController.setPreviousScene(scene);

				Scene loadScene = new Scene(loadRoot);
				loadScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

				stage.setScene(loadScene);
				stage.setWidth(currentWidth);
				stage.setHeight(currentHeight);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};

	EventHandler<ActionEvent> colorChange = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			if (isAnimating) {
				return;
			}
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
			if (isAnimating) {
				return;
			}
			disableInteractions();
			// calculate column
			double xPosition = event.getX();
			double cellWidth = boardLayer.getWidth() / playerData.getNumColumns();
			int droppedColumn = (int) (xPosition / cellWidth);
			// drop piece
			int droppedRow = playerData.drop(droppedColumn, 1);
			if (droppedRow == -1) {
				// the column was full, do nothing
				enableInteractions();
				return;
			}
			saveData.addMove(droppedColumn);
			dropAnimation(droppedColumn, droppedRow, 1);
		}
	};

	private void disableInteractions() {
		isAnimating = true;

		Stage stage = (Stage) piecesLayer.getScene().getWindow();
		stage.setResizable(false);

		orangeBlue.setDisable(true);
		pinkGreen.setDisable(true);
		yellowPurple.setDisable(true);
		loadGameButtonToolbar.setDisable(true);
		saveGameButtonToolbar.setDisable(true);
		newGameButtonToolbar.setDisable(true);

	}

	private void enableInteractions() {
		isAnimating = false;

		Stage stage = (Stage) piecesLayer.getScene().getWindow();
		stage.setResizable(true);

		orangeBlue.setDisable(false);
		pinkGreen.setDisable(false);
		yellowPurple.setDisable(false);
		loadGameButtonToolbar.setDisable(false);
		saveGameButtonToolbar.setDisable(false);
		newGameButtonToolbar.setDisable(false);
	}

	private void postAnimationLogic(int player) {
		refreshMoveLog();
		// check winner
		winner = playerData.checkWinner();
		if (winner != 0 || playerData.isFull()) {
			// winning/losing/stalemate sequence
			winAnimation();
			return;
		}

		if (player == 1) {
			// ai turn
			int droppedColumn = (int) ConnectFourAI.AImove(playerData, 2, difficulty);
			int droppedRow = playerData.drop(droppedColumn, 2);
			saveData.addMove(droppedColumn);
			dropAnimation(droppedColumn, droppedRow, 2);
			return;
		} else if (player == 2) {
			saveData.save();
			enableInteractions();
			return;
		}
	}
}
