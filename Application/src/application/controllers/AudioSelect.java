package application.controllers;

import java.io.File;

import java.net.URL;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Controller that handles the AudioSelect.fxml view.
 * Displays checkboxes with names of audio files for user to select.
 */

public class AudioSelect extends Controller implements Initializable {

	private List<CheckBox> _checkBoxes = new ArrayList<CheckBox>();
	private List<String> _savedAudio;

	@FXML private VBox _content;
	@FXML private Label _warning;
	@FXML private Button _next;

	@FXML private Button _back;
	@FXML private void handleBack() {switchNonSkippableWindow(_content.getScene(),getClass().getResource(_PATH+"TextSelect.fxml"));}; 
	@FXML 
	public void handleHome() {
		boolean decision = displayAlert("Are you leaving?", "Progress will not be saved if you quit to home");
		if (decision == true) {
			switchNonSkippableWindow(_content.getScene(), getClass().getResource(_PATH+"Menu.fxml"));
		} else {
			;
		}

	}

	@FXML 
	private void handleNext() {
		_savedAudio = new ArrayList<String>();
		boolean canProgress = false;

		for (CheckBox individualBox: _checkBoxes) {
			if (individualBox.isSelected() ==  true) {
				canProgress = true;
				_savedAudio.add(individualBox.getText());
			}
		}
		if (canProgress == true) { //at least one audio clip is selected
			storeFileType("audio", _savedAudio);
			switchSkippableWindow(_next, "audio", getClass().getResource(_PATH));
		} else {
			_warning.setText("Error: No audio clip(s) selected");
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		_checkBoxes.clear();
		Label message = new Label("Please select which audio clips you'd like to keep\n\n");
		_content.getChildren().add(message);

		List<File> audioFiles = listDirectory("tmp/audio/"); //Should be non-null as last window should check for files
		for (File audioFile: audioFiles) {
			//Phantom files are not shown
			if(!audioFile.getName().contains(".__")) {
				CheckBox checkBox = new CheckBox();
				checkBox.setText(audioFile.getName());
				_content.getChildren().add(checkBox);
				_checkBoxes.add(checkBox);
			}
		}
	}

}
