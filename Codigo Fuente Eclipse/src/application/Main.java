package application;
	
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;


public class Main extends Application {
	private Stage stage;
	private double xOffset = 0;
    private double yOffset = 0;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Main.fxml"));
			Parent root = fxmlLoader.load();
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			stage = primaryStage;
			stage.initStyle(StageStyle.UNDECORATED);
			stage.setResizable(false);
			stage.getIcons().add(
				    new Image(this.getClass().getResource("/res/icon.png").toString()));
			MainControl control = fxmlLoader.getController();
			control.setMainApp(stage);
			primaryStage.setScene(scene);
			primaryStage.show();
			
			root.setOnMousePressed(new EventHandler<MouseEvent>() {
	            @Override
	            public void handle(MouseEvent event) {
	                xOffset = event.getSceneX();
	                yOffset = event.getSceneY();
	            }
	        });
	        root.setOnMouseDragged(new EventHandler<MouseEvent>() {
	            @Override
	            public void handle(MouseEvent event) {
	                primaryStage.setX(event.getScreenX() - xOffset);
	                primaryStage.setY(event.getScreenY() - yOffset);
	            }
	        });
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
