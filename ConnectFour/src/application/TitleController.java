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

public class TitleController {
	@FXML
	BorderPane titlePane;
	@FXML
	Button newGameButton;
	@FXML
	Button loadGameButton;
	
	private Scene difficultySelectionScene;
	
	public void initialize() {
		titlePane.getStyleClass().add("orangeBluePane");
		newGameButton.setOnAction(newGameHandler);
		loadGameButton.setOnAction(loadGameHandler);
	}

	public void setDifficultySelectionScene(Scene scene) {
	    difficultySelectionScene = scene;
	}
	
	EventHandler<ActionEvent> newGameHandler = new EventHandler<>() {
		@Override
		public void handle(ActionEvent event) {
			Stage stage = (Stage) titlePane.getScene().getWindow();
			double currentWidth = stage.getWidth();
	        double currentHeight = stage.getHeight();
	        
			stage.setScene(difficultySelectionScene);
			stage.setWidth(currentWidth);
			stage.setHeight(currentHeight);
		}
	};
	
	EventHandler<ActionEvent> loadGameHandler = new EventHandler<>() {
		@Override
		public void handle(ActionEvent event) {
			try {
	        	Scene scene = titlePane.getScene();
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
	            // Handle the exception
	        }
		}
	};
}
