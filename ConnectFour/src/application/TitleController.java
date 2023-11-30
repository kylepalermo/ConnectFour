package application;

import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;

public class TitleController {
	@FXML
	BorderPane titlePane;
	public void initialize() {
		titlePane.getStyleClass().add("orangeBluePane");
	}
}
