package application;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.PasswordField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

public class PasswordDialog extends Dialog<String> {
	private PasswordField passwordField;
	private Stage stage;

	public PasswordDialog() {
		setTitle("Contraseña");
		setHeaderText("Introduce tu contraseña");

		ButtonType passwordButtonType = new ButtonType("Aceptar", ButtonData.OK_DONE);
		getDialogPane().getButtonTypes().addAll(passwordButtonType, ButtonType.CANCEL);

		passwordField = new PasswordField();
		passwordField.setPromptText("Contraseña");

		HBox hBox = new HBox();
		hBox.getChildren().add(passwordField);
		hBox.setPadding(new Insets(20));

		stage = (Stage) this.getDialogPane().getScene().getWindow();
		
		getDialogPane().setPrefWidth(300);
		
		HBox.setHgrow(passwordField, Priority.ALWAYS);

		getDialogPane().setContent(hBox);

		Platform.runLater(() -> passwordField.requestFocus());

		setResultConverter(dialogButton -> {
			if (dialogButton == passwordButtonType) {
				return passwordField.getText();
			}
			return null;
		});
	}
	
	public void setIcon(Image img){
		stage.getIcons().add(img);
	}

	public PasswordField getPasswordField() {
		return passwordField;
	}
}