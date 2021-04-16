import javafx.application.*;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class ClientDriver extends Application {

	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage loginStage) throws Exception {
		// Setup constants
		final String WINDOW_TITLE = "LANOMS";
		final double MAIN_MIN_WIDTH = 900.0, MAIN_MIN_HEIGHT = 500;
		final double LOGIN_WIDTH = 350.0, LOGIN_HEIGHT = 450.0;
		
		// Setup main scene		
		GlobalPane globalPane = new GlobalPane();
		Scene mainScene = new Scene(globalPane);
		mainScene.getStylesheets().add("style.css");
		
		// Setup main stage
		Stage mainStage = new Stage();
		mainStage.setTitle(WINDOW_TITLE);
		mainStage.setMinWidth(MAIN_MIN_WIDTH);
		mainStage.setMinHeight(MAIN_MIN_HEIGHT);
		mainStage.setScene(mainScene);
		mainStage.setOnShowing(e -> {
			loginStage.close();
			globalPane.initialize();
		});
		
		mainStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
		    @Override
		    public void handle(WindowEvent t) {
		        Platform.exit();
		        System.exit(0);
		    }
		});
		
		// Setup loginStage
		LoginPane loginPane = new LoginPane(mainStage);
		Scene loginScene = new Scene(loginPane);
		
		loginScene.getStylesheets().add("login.css");
		loginStage.setResizable(false);
		loginStage.setTitle(WINDOW_TITLE);
		loginStage.setWidth(LOGIN_WIDTH);
		loginStage.setHeight(LOGIN_HEIGHT);
		loginStage.setScene(loginScene);
		loginStage.show();
	}

}