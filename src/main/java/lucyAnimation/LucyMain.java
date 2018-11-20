package lucyAnimation;

import java.io.File;
import java.util.List;
import java.util.Optional;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class LucyMain extends Application{

	//TODO only for test
	private static String[] args;
	private static boolean unsaved;
	private static String currentFile = "";

	private LucyGUI gui;
	private LucyMenu menu;

	private static Stage stage;

	public static void main(String[] args) {
		launch();
	}

	@Override
	public void start(Stage stage) throws Exception {
		LucyMain.stage = stage;
		gui = new LucyGUI();
		menu = new LucyMenu();
		stage.setTitle("LCY File Packer");

		VBox main = new VBox();
		main.getChildren().addAll(menu, gui);
		VBox.setVgrow(gui, Priority.ALWAYS);
		Scene scene = new Scene(main);
		stage.setScene(scene);
		stage.show();
		createMenuListeners();

		setUnsaved(false);
	}

	private void newDialogue() {
		if (unsaved) {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Unsaved Changes");
			alert.setContentText("You have made unsaved changes to this file. Creating a new animation will discard these changes."
					+ " Proceed with creating new file?");
			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.CANCEL) {
				return;
			}
		}

		gui.newAnimation();
	}

	private void saveDialogue() {
		File file = new File(currentFile);
		if (file.exists()) {
			gui.saveAnimation(currentFile);
		} else {
			saveAsDialogue();
		}
	}

	private void saveAsDialogue() {
		FileChooser fc  = new FileChooser();
		fc.setTitle("Save Animation");
		File file = fc.showSaveDialog(stage);
		if (file != null) {
			gui.saveAnimation(file.getAbsolutePath());
		}
	}

	private void loadDialogue() {
		if (unsaved) {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Unsaved Changes");
			alert.setContentText("You have made unsaved changes to this file. Loading this animation will discard these changes."
					+ " Proceed with loading file?");
			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.CANCEL) {
				return;
			}
		}

		FileChooser fc = new FileChooser();
		fc.setTitle("Load Animation");
		fc.getExtensionFilters().add(
				new FileChooser.ExtensionFilter("LUCY Files", "*.lcy"));
		File file = fc.showOpenDialog(stage);
		if (file != null) {
			gui.loadAnimation(file.getAbsolutePath());
		}
	}

	private void createMenuListeners() {
		menu.setNewHandler((ActionEvent e) -> {
			newDialogue();
		});

		menu.setSaveHandler((ActionEvent e) -> {
			saveDialogue();
		});

		menu.setSaveAsHandler((ActionEvent e) -> {
			saveAsDialogue();
		});

		menu.setLoadHandler((ActionEvent e) -> {
			loadDialogue();
		});

		menu.setExitHandler((ActionEvent e) -> {
			exitCall();
		});

		menu.setAddHandler((ActionEvent e) -> {
			FileChooser fc = new FileChooser();
			fc.setTitle("Select Image To Add");
			List<File> files = fc.showOpenMultipleDialog(stage);
			if (files != null) {
				boolean[] succ = new boolean[files.size()];
				int i = 0;
				for (File file : files) {
					succ[i] = gui.addFrame(file);
					if (succ[i]) {
						resizeCall();
					}
					i++;
				}
				displayFailure(succ);
			}
		});

		menu.setWidthHandler((ActionEvent e) -> {
			TextInputDialog input = new TextInputDialog();

			input.setTitle("Edit Width");
			input.setHeaderText("Enter New Width");
			input.setContentText("Width: ");

			Optional<String> result = input.showAndWait();
			if (result.isPresent()) {
				int width;
				try {
					width = Integer.parseInt(result.get());
				} catch (NumberFormatException ex) {
					return;
				}
				if (width > 0) {
					gui.setWidth(width);
					resizeCall();
				}
			}
		});

		menu.setOrderHandler((ActionEvent e) -> {
			gui.editOrder();
		});
	}

	private void displayFailure(boolean[] succ) {
		int count = 0;
		for (boolean bool : succ) {
			if (!bool) {
				count += 1;
			}
		}

		if (count > 0) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Add Failed");
			alert.setHeaderText(count + " failed operations.");
			alert.setContentText("One or more images failed to add, as they were not the same size as the images already present.");
			alert.showAndWait();
		}
	}

	private void exitCall() {
		if (unsaved) {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Unsaved Changes");
			alert.setContentText("You have made unsaved changes to this file. Exiting will discard these changes."
					+ " Exit without saving?");
			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.CANCEL) {
				return;
			}
		}

		stage.close();
	}

	public static void resizeCall() {
		stage.sizeToScene();
	}

	public static void setUnsaved(boolean unsaved) {
		LucyMain.unsaved = unsaved;
	}


	public static void createErrorMessage(String title, String message) {
		Alert error = new Alert(AlertType.ERROR);
		error.setTitle(title);
		error.setHeaderText(null);
		error.setContentText(message);
		error.showAndWait();
	}

	public static void setCurrentFile(String file) {
		LucyMain.currentFile = file;
	}

}
