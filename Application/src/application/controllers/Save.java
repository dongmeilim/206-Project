package application.controllers;

import application.app.CreateVideo;

import java.io.File;

import java.util.List;
import java.util.regex.Pattern;

import javafx.fxml.FXML;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/**
 * Controller that handles the Save.fxml view.
 * After user types in an appropriate filename.
 * The application.app.CreateVideo Task is called.
 */

public class Save extends Controller {

	@FXML private Button _back;
	@FXML private Button _home;
	@FXML private Button _saveButton;
	@FXML private Label _warning;
	@FXML private TextField _saveField;
	@FXML private ProgressBar _progress;
	@FXML private VBox _content;

	private boolean _videoHasBeenMade = false;
	
	@FXML public void handleBack() {switchSkippableWindow(_back,"image", getClass().getResource(_PATH));};
	@FXML 
	public void handleHome() {
		
		if (_videoHasBeenMade == false) {
			
			boolean decision = displayAlert("Are you leaving?", "Progress will not be saved if you quit to home");
			if (decision == true) {
				switchNonSkippableWindow(_content.getScene(), getClass().getResource(_PATH+"Menu.fxml"));
			} else {
				;
			}
		} else {
			switchNonSkippableWindow(_content.getScene(), getClass().getResource(_PATH+"Menu.fxml"));//Display nothing because creation is done
		}

	}
	//No next button in this window, i.e. no handling required
	
	@FXML
	private void handleSave() {
		String fileName = _saveField.getText();
		List<File> creationList = listDirectory("creations");
		boolean decision;
		boolean sameFile = false;
		
		if ( fileName.isEmpty() == false) {
			if (Pattern.matches("^[a-zA-Z0-9_-]+$", fileName)) {
				
				for (File comparedFile: creationList) {
					//Get the filename without .mp4
					String comparedFileName = comparedFile.getName().replace(comparedFile.getName().substring(comparedFile.getName().length()-4), "");
					if (fileName.equals(comparedFileName) == true) {
						sameFile = true;
						break;
					}
				}
				
				if (sameFile == true) {
					decision = displayAlert("File already exists!","Are you sure you want to overwrite?");
					if (decision == true) {
						_saveButton.setDisable(true);
						_saveField.setDisable(true);
						_warning.setText("");
						create(fileName);
					} else {
						_saveField.clear();
						_warning.setText("");
					}
				} else {
					_saveButton.setDisable(true);
					_saveField.setDisable(true);
					_warning.setText("");
					create(fileName);
				}
			} else {
				_warning.setText("Video contains invalid characters");
			}
		} else {
			_warning.setText("Please enter a name");
		}
	}

	private void create(String fileName) {
		CreateVideo createVideo = new CreateVideo(fileName);
		Thread thread = new Thread(createVideo);
		thread.start();
		
		_progress.progressProperty().bind(createVideo.progressProperty()); //Listen to the thread
		_progress.setStyle("-fx-accent: #896A89;");
		
		createVideo.setOnSucceeded(e-> {
			_progress.progressProperty().unbind();
			_back.setDisable(true);
			_videoHasBeenMade = true;
			_home.requestFocus(); //Tell users to go home, job done
		});
	}
	
}
