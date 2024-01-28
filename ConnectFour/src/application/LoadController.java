package application;

import java.io.IOException;
import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoadController {
	@FXML
	BorderPane loadPane;
	@FXML
	VBox savesVBox;
	@FXML
	Scene previousScene;
	
	Scene difficultySelectionScene;
	
	public void initialize(){
		loadPane.getStyleClass().add("orangeBluePane");
		
		Button newButton;
		ArrayList<String> saveNames = SaveData.getSaveNames();
		for(String name : saveNames) {
			newButton = new Button(name);
			savesVBox.getChildren().add(newButton);
			newButton.setOnAction(loadHandler);
		}
		newButton = new Button("Back");
		savesVBox.getChildren().add(newButton);
		newButton.setOnAction(backHandler);
	}
	
	public void setPreviousScene(Scene previousScene) {
		this.previousScene = previousScene;
	}
	
	public void setDifficultySelectionScene(Scene scene) {
	    difficultySelectionScene = scene;
	}
	
	EventHandler<ActionEvent> loadHandler = new EventHandler<>() {
		@Override
		public void handle(ActionEvent event) {
			String saveName = ((Button) event.getSource()).getText();
			loadGame(saveName);
		}
	};
	
	private void loadGame(String saveName) {
        try {
        	Scene scene = loadPane.getScene();
        	Stage stage = (Stage) scene.getWindow();
        	
        	double currentWidth = stage.getWidth();
	        double currentHeight = stage.getHeight();
        	
        	FXMLLoader gameLoader = new FXMLLoader(getClass().getResource("Game.fxml"));
            Parent gameRoot = gameLoader.load();
            GameController gameController = gameLoader.getController();
            
            gameController.setDifficultySelectionScene(difficultySelectionScene);
            SaveData saveData = new SaveData(saveName);
            gameController.setData(saveData);

            Scene gameScene = new Scene(gameRoot);
			gameScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

            stage.setScene(gameScene);
            stage.setWidth(currentWidth);
			stage.setHeight(currentHeight);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	// go back to the title or the game without loading
	EventHandler<ActionEvent> backHandler = new EventHandler<>() {
		@Override
		public void handle(ActionEvent event) {
			Stage stage = (Stage) loadPane.getScene().getWindow();
			stage.setScene(previousScene);
		}
	};
}
