import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;

public class UserSettingsLayout extends VBox {
	private FileChooser fileChooser;
	private ImageView iv_displayPicture;
	private TextArea ta_message;
	
	public UserSettingsLayout(double padding) {
		super(padding);
		fileChooser = new FileChooser();
		ta_message = new TextArea();
		ta_message.setWrapText(true);
		ta_message.setPrefHeight(60.0);
		setMessage();
		
		// Set anchors for blank page
		AnchorPane.setTopAnchor(this, 10.0);
		AnchorPane.setBottomAnchor(this, 10.0);
		AnchorPane.setLeftAnchor(this, 10.0);
		AnchorPane.setRightAnchor(this, 10.0);
				
		// Set title
		Label lb_title = new Label("Ali Cheddadi\n");
		lb_title.setStyle("-fx-font-size: 18px");
		
		// Set image
		iv_displayPicture = new ImageView("user_placeholder.png");
		iv_displayPicture.setPreserveRatio(true);
		iv_displayPicture.setFitHeight(64.0);
		
		// Set button
		Button bt_changeDisplay = new Button("Edit display picture");
		bt_changeDisplay.setOnAction(e -> openFile(e));
		
		// Set status
		ComboBox<String> cb_status = new ComboBox<String>();
		cb_status.getItems().addAll("Online", "Away", "Busy", "Out for lunch");
		cb_status.getSelectionModel().selectFirst();
		HBox hb_status = new HBox(5.0);
		hb_status.setAlignment(Pos.CENTER);
		hb_status.getChildren().addAll(new Label("Online status: "), cb_status);
		
		// Set message
		VBox vb_message = new VBox(5.0);
		vb_message.setMaxWidth(300.0);
		
		Label lb_message = new Label("Short message: ");
		
		Region rg_spacer = new Region();
		rg_spacer.setPrefHeight(10);
		
		Button bt_submitMessage = new Button("Save All");
		bt_submitMessage.setPrefWidth(100);
		bt_submitMessage.setStyle("-fx-font-size: 16;");
		
		
		bt_submitMessage.setOnAction(e -> {
			if (Lock.isBusy()) {
				Thread thread = new Thread(new Runnable() {
					@Override
					public void run() {
						while(Lock.isBusy()) {
							try {
								Thread.sleep(500);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						makeUserInfo(ta_message.getText(), cb_status.getSelectionModel().getSelectedItem());
					}
				});
				Platform.runLater(thread);
			}
			else makeUserInfo(ta_message.getText(), cb_status.getSelectionModel().getSelectedItem());
		});
		
		
		vb_message.getChildren().addAll(lb_message, ta_message);
				
		// Set children
		setStyle("-fx-background-color: white");
		setAlignment(Pos.CENTER);
		setPadding(new Insets(10.0, 10.0, 10.0, 10.0));
		getChildren().addAll(lb_title, iv_displayPicture, bt_changeDisplay, hb_status, vb_message, rg_spacer, bt_submitMessage);
	}
	
	private void openFile(ActionEvent event) {
		Window stage = this.getScene().getWindow();
		fileChooser.setTitle("Select a Picture");
		fileChooser.getExtensionFilters().add(new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));
		
		try {
			File file = fileChooser.showOpenDialog(stage);
			fileChooser.setInitialDirectory(file.getParentFile());
			if (file != null) updatePicture(file.getPath());
		} catch (Exception ex){
			
		}
		
	}
	
	private void updatePicture(String path) {
		File img = new File(path);
		InputStream isImage;
		try {
			isImage = (InputStream) new FileInputStream(img);
			iv_displayPicture.setImage(new Image(isImage));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void setMessage() {
		ta_message.setText(UserCache.getUser(UserCache.getCurrentUser()).getMessage());
	}
	
	private void makeUserInfo(String message, String status) {
		ClientController.makeUserInfo(UserCache.getCurrentUser(), message, status);
		UserCache.getUser(UserCache.getCurrentUser()).setStatus(status);
		UserCache.getUser(UserCache.getCurrentUser()).setMessage(message);
		
		UserCache.updateUserList();
		
		// Reset auto-update timer.
	    GlobalPane.delayUserUpdate();
	}
}
