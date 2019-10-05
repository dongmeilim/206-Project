package application.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.net.URL;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Controller that handles the ImageSelect.fxml view.
 * Displays checkboxes with ImageViews for user to select.
 */

public class ImageSelect extends Controller implements Initializable {

	@FXML private VBox _content;
	@FXML private Button _next;
	@FXML private Label _warning;
	
	private double _WIDTH = 540;
	private double _HEIGHT = 700;
	private List<String> _savedImageNames;
	private List<CheckBox> _checkBoxes = new ArrayList<CheckBox>();
	
	@FXML private void handleBack() {switchNonSkippableWindow(_content.getScene(), getClass().getResource(_PATH+"ImageFetch.fxml"));};
	
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
	public void handleNext() {
		_savedImageNames = new ArrayList<String>();
		boolean canProgress = false;
		
		for (CheckBox individualBox: _checkBoxes) {
			if (individualBox.isSelected() ==  true) {
				canProgress = true;
				_savedImageNames.add(individualBox.getText());
			}
		}
		if (canProgress == true) { //at least one image is selected
			storeFileType("image", _savedImageNames);
			switchSkippableWindow(_next, "image", getClass().getResource(_PATH));
		} else {
			_warning.setText("Error: No image(s) selected");
		}
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		_checkBoxes.clear();
		Label message = new Label("Please select which images you'd like to keep\n\n");
		_content.getChildren().add(message);
		List<File> imageFiles = listDirectory("tmp/images/");
		for (File imageFile: imageFiles) {
			try {
				
				FileInputStream inputstream = new FileInputStream(imageFile);
				Image image = new Image(inputstream); 
				ImageView imageView = new ImageView(image);
				imageView.setFitHeight(_HEIGHT);
	        	imageView.setFitWidth(_WIDTH);
	        	imageView.setPreserveRatio(true);
				inputstream.close();
				
				CheckBox checkBox = new CheckBox();
				checkBox.setText(imageFile.getName());
				checkBox.setGraphic(imageView);
				checkBox.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
				checkBox.setAlignment(Pos.CENTER);
				
				_content.getChildren().add(checkBox);
				_checkBoxes.add(checkBox);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}	
	}
}
