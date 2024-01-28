package application;
	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;



public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader titleLoader = new FXMLLoader(getClass().getResource("Title.fxml"));
		    Parent titleRoot = titleLoader.load();
			Scene titleScene = new Scene(titleRoot);
			
			FXMLLoader difficultySelectionLoader = new FXMLLoader(getClass().getResource("DifficultySelection.fxml"));
			Parent difficultySelectionRoot = difficultySelectionLoader.load();
			Scene difficultySelectionScene = new Scene(difficultySelectionRoot);
			
			titleScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			difficultySelectionScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			
			
			TitleController titleController = titleLoader.getController();
		    titleController.setDifficultySelectionScene(difficultySelectionScene);
		    		    
			primaryStage.setMinWidth(800);
			primaryStage.setMinHeight(600);
			primaryStage.setTitle("Connect Four");
			primaryStage.setScene(titleScene);
			
			//primaryStage.setFullScreen(true);
			primaryStage.setMaximized(true);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
