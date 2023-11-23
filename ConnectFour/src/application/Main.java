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
			Parent root = FXMLLoader.load(getClass().getResource("Game.fxml"));
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setMinWidth(root.minWidth(-1));
			primaryStage.setMinHeight(root.minHeight(-1));
			primaryStage.setMaxWidth(root.maxWidth(-1));
			primaryStage.setMaxHeight(root.maxHeight(-1));
			primaryStage.setTitle("Connect Four");
			
			primaryStage.setScene(scene);
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
