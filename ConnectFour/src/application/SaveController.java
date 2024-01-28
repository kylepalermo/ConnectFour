package application;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SaveController {
	@FXML
	BorderPane savePane;
	@FXML
	VBox vBox;
	@FXML
	Scene previousScene;
	@FXML
	Button backButton;
	@FXML
	TextField textField;
	
	private SaveData saveData;
	
	public void initialize(){
		savePane.getStyleClass().add("orangeBluePane");
		textField.setOnAction(saveHandler);
		backButton.setOnAction(backHandler);
	}
	
	public void setPreviousScene(Scene previousScene) {
		this.previousScene = previousScene;
	}
	
	public void setSaveData(SaveData saveData) {
		this.saveData = saveData;
	}
	
	EventHandler<ActionEvent> saveHandler = new EventHandler<>() {
		@Override
		public void handle(ActionEvent event) {
			String saveName = ((TextField) event.getSource()).getText();
			saveGame(saveName);
			
			Stage stage = (Stage) savePane.getScene().getWindow();
			stage.setScene(previousScene);
		}
	};
	
	private void saveGame(String saveName) {
        saveData.setSaveName(saveName);
        saveData.save();
        saveData.setSaveName("Autosave");
    }
	
	// go back to the title or the game without saving
	EventHandler<ActionEvent> backHandler = new EventHandler<>() {
		@Override
		public void handle(ActionEvent event) {
			Stage stage = (Stage) savePane.getScene().getWindow();
			stage.setScene(previousScene);
		}
	};
}
