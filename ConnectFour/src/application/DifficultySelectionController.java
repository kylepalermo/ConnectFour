package application;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class DifficultySelectionController {
	@FXML
	BorderPane selectionPane;
	@FXML
	Button randomButton;
	@FXML
	Button easyButton;
	@FXML
	Button hardButton;

	public void initialize() {
		selectionPane.getStyleClass().add("orangeBluePane");
		randomButton.setOnAction(newGameHandler);
		easyButton.setOnAction(newGameHandler);
		hardButton.setOnAction(newGameHandler);
	}

	private void startNewGame(int difficulty) {
		try {
			Scene scene = selectionPane.getScene();
			Stage stage = (Stage) scene.getWindow();

			double currentWidth = stage.getWidth();
			double currentHeight = stage.getHeight();

			FXMLLoader gameLoader = new FXMLLoader(getClass().getResource("Game.fxml"));
			Parent gameRoot = gameLoader.load();
			GameController gameController = gameLoader.getController();

			gameController.setDifficulty(difficulty);
			gameController.setDifficultySelectionScene(scene);
			gameController.setData(new SaveData("autosave", difficulty));

			Scene gameScene = new Scene(gameRoot);
			gameScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

			stage.setScene(gameScene);
			stage.setWidth(currentWidth);
			stage.setHeight(currentHeight);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	EventHandler<ActionEvent> newGameHandler = new EventHandler<>() {
		@Override
		public void handle(ActionEvent event) {
			Button difficultyButton = (Button) event.getSource();
			int difficulty = 0;
			switch (difficultyButton.getText()) {
			case "Random":
				difficulty = 0;
				break;
			case "Easy":
				difficulty = 1;
				break;
			case "Hard":
				difficulty = 2;
				break;
			}
			startNewGame(difficulty);
		}
	};
}
