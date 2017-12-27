package application;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Scanner;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MainControl implements Initializable{
	@FXML
	private ChoiceBox<String> selectDicc;
	@FXML
	private TextArea txtLog;
	@FXML
	private ProgressBar progressLoad;
	@FXML
	private Button btnDetener;
	@FXML
	private ToggleButton btnShowProgress;
	@FXML
	private TextField inLimitProgress; 
	@FXML
	private ToggleButton btnSavePasswd;
	@FXML
	private TextField inSavePath;

	private String password = "";
	private StringBuilder string;
	private String dicc = "";
	private FileInputStream fileDicc;
	private long start;
	private boolean running = false;
	private long probadas, contador;
	private boolean savePasswd = false;
	private File savePasswdPath;
	private boolean cont = true;
	private int limitCont = 0;
	private Stage mainStage;

	public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
		txtLog.appendText("[SMR2] [Ataque de fuerza bruta]\n");
		selectDicc.setItems(FXCollections.observableArrayList(
				"Alfanumérico minúsculas", "Alfanumérico con mayúsculas", "Numérico", "Alfabético minusculas", "Alfabético con mayúsculas", "Seleccionar archivo como diccionario")
				);
		selectDicc.getSelectionModel().selectFirst();
	}

	public void setMainApp(Stage stage){
		mainStage = stage;
		txtLog.appendText("[SISTEMA] OK\n");
	}

	public void loop(int index) {
		for(int i = 0; i < dicc.length(); i++) {
			string.setCharAt(index, dicc.charAt(i));
			if(index < string.length() - 1)
				loop(index + 1);

			if(!running) break;
			
			probadas++;
			
			if(cont){
				contador++;
				if(contador >= limitCont){
					Platform.runLater(new Runnable(){

						@Override
						public void run() {
							txtLog.appendText("Combinaciones probadas: " + String.valueOf(probadas) + "\n");
						}

					});

					contador = 0;
				}
			}

			if(string.toString().equals(password)) {
				final StringBuilder stringAux = string;
				running = false;
				final long current = System.currentTimeMillis();
				final long probadasAux = probadas;
				
				if(savePasswd){
					try(FileWriter fw = new FileWriter(savePasswdPath, true);
							BufferedWriter bw = new BufferedWriter(fw);
							PrintWriter out = new PrintWriter(bw))
					{
						out.println("Contraseña: " + stringAux + "\n" +
								"Tiempo transcurrido: " + convertmillis(current - start) + "\n" + 
								"Contraseñas intentadas: " + String.valueOf(probadasAux) + "\n" +
								"<------------------------------------------------------>\n");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				Platform.runLater(new Runnable(){

					@Override
					public void run() {
						txtLog.appendText("Contraseña encontrada: " + stringAux + "\n");
						txtLog.appendText("Tiempo transcurrido: " + convertmillis(current - start) + "\n");
						txtLog.appendText("Contraseñas intentadas: " + String.valueOf(probadasAux) + "\n");
					}

				});

				if(savePasswd){

				}

				return;
			}
		}
	}

	@FXML
	public void detener(){
		if(running){
			running = false;
			progressLoad.setProgress(0);
		}else{
			txtLog.appendText("[FUERZA BRUTA] No hay procesos en curso\n");
			progressLoad.setProgress(0);
		}
	}

	@FXML
	public void limpiar(){
		txtLog.setText("[SMR2] [Ataque de fuerza bruta]\n");
	}

	@FXML
	public void salir(){
		System.exit(0);
	}

	@FXML
	public void confSavePath(){
		FileChooser fc = new FileChooser();
		fc.setTitle("Archivo para guardar");
		File f = fc.showSaveDialog(mainStage);

		inSavePath.setText(f.getAbsolutePath());
	}

	@FXML
	public void iniciar() {
		if(running){
			txtLog.appendText("[FUERZA BRUTA] Hay un proceso en curso, no puedes iniciar otro\n");

			return;
		}else{
			txtLog.appendText("[FUERZA BRUTA] Iniciado\n");
		}

		while(password.equals("")) {
			PasswordDialog passwDlg = new PasswordDialog();

			passwDlg.setIcon(new Image(this.getClass().getResource("/res/icon.png").toString()));

			Optional<String> op = passwDlg.showAndWait();

			if (op.isPresent()) {
				password = new String(passwDlg.getPasswordField().getText());
			}else{
				return;
			}

		}


		String diccIn = String.valueOf(selectDicc.getSelectionModel().getSelectedIndex());

		switch(diccIn){
		case "0":
			txtLog.appendText("Iniciando crackeo [Seleccionado diccionario alfanumerico minusc]\n");
			dicc = "abcdefghijklmnopqrstuvwxyz0123456789";

			break;
		case "1":
			txtLog.appendText("Iniciando crackeo [Seleccionado diccionario alfanumerico mayusc]\n");
			dicc = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

			break;
		case "2":
			txtLog.appendText("Iniciando crackeo [Seleccionado diccionario numérico]\n");
			dicc = "0123456789";

			break;
		case "3":
			txtLog.appendText("Iniciando crackeo [Seleccionado diccionario alfabetico sin mayusc]\n");
			dicc = "abcdefghijklmnopqrstuvwxyz";

			break;
		case "4":
			txtLog.appendText("Iniciando crackeo [Seleccionado diccionario alfabetico con mayusc]\n");
			dicc = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

			break;
		case "5":
			FileChooser fc = new FileChooser();
			fc.setTitle("Seleccionar diccionario");
			File f = fc.showOpenDialog(mainStage);

			fileDicc = null;

			try {
				fileDicc = new FileInputStream(f.getAbsolutePath());
				txtLog.appendText("Iniciando crackeo [Seleccionado diccionario de archivo]\nArchivo " + f.getAbsolutePath() + " cargado, tamaño: " + String.valueOf(f.length()) + "\n");
			} catch(IOException e){
				e.printStackTrace();
			}

			break;
		default:
			txtLog.appendText("[FUERZA BRUTA] Error al seleccionar diccionario, proceso finalizado\n");

			return;
		}


		cont = btnShowProgress.isSelected();
		if(cont){
			try{
				limitCont = Integer.parseInt(inLimitProgress.getText());
			}catch(NumberFormatException e){
				txtLog.appendText("[SISTEMA] Error, el valor limite progreso debe ser numerico\n");
				running = false;
				return;
			}
		}

		savePasswd = btnSavePasswd.isSelected();
		if(savePasswd){
			savePasswdPath = new File(inSavePath.getText());

			if(savePasswdPath.exists()){
				try {
					txtLog.appendText("[SISTEMA] Archivo " + savePasswdPath.getAbsolutePath() + " ya existe, escribiendo desde el final\n");
				} catch (Exception e) {
					txtLog.appendText("[SISTEMA] Archivo " + savePasswdPath.getAbsolutePath() + " no se puede abrir\n");
					return;
				}
			}else{
				try {
					savePasswdPath.createNewFile();
					txtLog.appendText("[SISTEMA] Archivo " + savePasswdPath.getAbsolutePath() + " se ha creado\n");
				} catch (IOException e) {
					txtLog.appendText("[SISTEMA] Archivo " + savePasswdPath.getAbsolutePath() + " no se puede crear\n");
					return;
				}
			}
		}

		progressLoad.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);

		new Thread(new Runnable() {

			@Override
			public void run() {
				start = System.currentTimeMillis();
				running = true;
				probadas = 0;
				contador = 0;

				if(!diccIn.equals("5")){
					string = new StringBuilder("");
					
					while(running) {
						string.append(dicc.charAt(0));
						for(int i = 0; i < string.length() - 1 && running; i++) {
							for(int j = 0; j  < dicc.length() && running; j++) {
								string.setCharAt(i, dicc.charAt(j));
								loop(i + 1);
							}
						}
					}
				}else{
					string = new StringBuilder("");
					boolean encontrada = false;
					String word;
					Scanner sc = new Scanner(fileDicc);
					contador = 0;
					probadas = 0;

					while(running) {
						if(sc.hasNextLine()){
							word = sc.nextLine();

							probadas++;

							if(cont){
								contador++;
								if(contador >= limitCont){
									Platform.runLater(new Runnable(){

										@Override
										public void run() {
											txtLog.appendText("Combinaciones probadas: " + String.valueOf(probadas) + "\n");
										}

									});

									contador = 0;
								}
							}

							if(word.equals(password)){
								final String stringAux = word;
								running = false;
								encontrada = true;
								final long current = System.currentTimeMillis();
								final long probadasAux = probadas;

								if(savePasswd){
									try(FileWriter fw = new FileWriter(savePasswdPath, true);
											BufferedWriter bw = new BufferedWriter(fw);
											PrintWriter out = new PrintWriter(bw))
									{
										out.println("Contraseña: " + stringAux + "\n" +
												"Tiempo transcurrido: " + convertmillis(current - start) + "\n" + 
												"Contraseñas intentadas: " + String.valueOf(probadasAux) + "\n" +
												"<------------------------------------------------------>\n");
									} catch (IOException e) {
										e.printStackTrace();
									}
								}

								Platform.runLater(new Runnable(){

									@Override
									public void run() {
										txtLog.appendText("Contraseña encontrada: " + stringAux + "\n");
										txtLog.appendText("Tiempo transcurrido: " + convertmillis(current - start) + "\n");
										txtLog.appendText("Contraseñas intentadas: " + String.valueOf(probadasAux) + "\n");
									}

								});
								break;
							}
						}else{
							running = false;
						}
					}

					sc.close();
					try {
						fileDicc.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					if(!encontrada){
						Platform.runLater(new Runnable(){

							@Override
							public void run() {
								txtLog.appendText("La contraseña no se encuentra en el diccionario\n");
							}

						});
					}
				}

				password = "";
				savePasswd = false;
				savePasswdPath = null;

				Platform.runLater(new Runnable(){

					@Override
					public void run() {
						txtLog.appendText("[FUERZA BRUTA] Proceso finalizado\n<------------------------------------->\n");
						progressLoad.setProgress(0);
					}

				});

				System.gc();
			}

		}).start();
	}

	public String convertmillis(long input) {
		int days = 0, hours = 0, minutes = 0, seconds = 0, millis = 0;

		int day = 86400000;
		int hour = 3600000;
		int minute = 60000;
		int second = 1000;


		if(input >= day) {
			days = (int) (input / day);
			millis = (int) (input % day);
		} else 
			millis = (int) input;

		if(millis >= hour) {
			hours = millis / hour;
			millis = millis% hour;
		}

		if(millis >= minute) {
			minutes = millis / minute;
			millis = millis % minute;
		}

		if(millis >= second) {
			seconds = millis / second;
			millis = millis % second;
		}

		return (days  + " dia(s), " + hours + "h, " + minutes + "min, " + seconds + "s y " + millis + "ms");
	}
}
