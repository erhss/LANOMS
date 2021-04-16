import java.util.concurrent.Semaphore;

import javafx.application.Platform;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginPane extends VBox {
	public static final int ENTER_ID = 0, INVALID_ID = 1, NO_CONNECTION = 2, SUCCESS = 3;
	private final long SLEEP_TIME = 100, TIMEOUT_TIME = 5000;
	private TextField tf_username;
	private PasswordField pf_password;
	private Label lb_subtitle;
	private Button bt_signIn;
	private final String[] str_messages = { 
			"Please enter your user credentials",
			"Invalid user credentials",
			"Could not establish a connection"
	} ;
	private int currentSubtitle;
	private Stage followUpStage;
	
	private static Semaphore semaphore = new Semaphore(1);
	private static volatile int loginCode;
	
	private class LoginThread implements Runnable {
		private String username;
		private String password;
		
		private LoginThread(String username, String password) {
			try {
				semaphore.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			this.username = username;
			this.password = password;
		}

		@Override
		public void run() {
			loginCode = -1;
			ClientController.login(username, password);
			
			long startTime = System.currentTimeMillis();
			while(loginCode == -1) {
				if (System.currentTimeMillis() - startTime > TIMEOUT_TIME) 
					throw new RuntimeException("LoginThread timedout.");
				try {
					Thread.sleep(SLEEP_TIME);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			System.out.println("Login code: " + loginCode);
			
			switch(loginCode) {
			case INVALID_ID - 1:
				switchSubtitle(INVALID_ID);
				break;
			case SUCCESS - 2:
				UserCache.setCurrentUser(username);
				UserCache.updateUserList();
				
				while (UserCache.isEmpty()) {
					if (System.currentTimeMillis() - startTime > TIMEOUT_TIME) 
						throw new RuntimeException("LoginThread timedout.");
					try {
						Thread.sleep(SLEEP_TIME);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				ConversationCache.updateConversations();
				
				while (!ConversationCache.isInitialized()) {
					if (System.currentTimeMillis() - startTime > TIMEOUT_TIME) 
						throw new RuntimeException("LoginThread timedout.");
					try {
						Thread.sleep(SLEEP_TIME);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				Thread thread = new Thread(new Runnable() {
					@Override
					public void run() {
						followUpStage.show();
					}
				});
				Platform.runLater(thread);
				break;
				
			}
			semaphore.release();
		}
		
	}
	
	public static void setLoginCode(int code) {
		loginCode = code;
	}
	
	public LoginPane(Stage followUpStage) {
		super(10.0);
		this.followUpStage = followUpStage;
		
		tf_username = new TextField();
		tf_username.setMaxWidth(200);
		tf_username.getStyleClass().add("text-field");
		tf_username.setOnAction(e -> logIn());
		
		pf_password = new PasswordField();
		pf_password.setMaxWidth(200);
		pf_password.getStyleClass().add("text-field");
		pf_password.setOnAction(e -> logIn());
		
		Region topSpacer = new Region();
		topSpacer.setPrefHeight(20);
		
		Region bottomSpacer = new Region();
		bottomSpacer.setPrefHeight(20);
		
		bt_signIn = new Button("Sign In");
		bt_signIn.setPrefWidth(100);
		
		ImageView iv_logo = new ImageView("logo.png");
		iv_logo.setPreserveRatio(true);
		iv_logo.setFitHeight(60.0);
		
		Label lb_title = new Label("Welcome to LANOMS");
		lb_title.getStyleClass().add("title");
		
		lb_subtitle = new Label(str_messages[ENTER_ID]);
		currentSubtitle = ENTER_ID;
		lb_subtitle.getStyleClass().add("subtitle");
		lb_subtitle.setPadding(new Insets(0.0, 0.0, 0.0, 0.0));
		
		getStyleClass().add("background");
		setAlignment(Pos.CENTER);
		getChildren().addAll(iv_logo, topSpacer, lb_title, lb_subtitle, tf_username, pf_password, bottomSpacer, bt_signIn);
		
		bt_signIn.setOnAction(e -> logIn());
	}
	
	private void switchSubtitle(int code) {
		//currentSubtitle = (currentSubtitle + 1) % 3;
		currentSubtitle = code;
		lb_subtitle.setText(str_messages[currentSubtitle]);
		lb_subtitle.pseudoClassStateChanged(PseudoClass.getPseudoClass("error"), currentSubtitle != ENTER_ID);
	}
	
	private void logIn() {
		try {
			SocketClient.connect();
		} catch (Exception e) {
			switchSubtitle(NO_CONNECTION);
			e.printStackTrace();
		}
		Thread thread = new Thread(new LoginThread(tf_username.getText(), pf_password.getText()));
		thread.start();
	}
}
