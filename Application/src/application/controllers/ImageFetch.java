package application.controllers;

import application.app.DownloadImages;

import javafx.fxml.FXML;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;

/**
 * Controller that handles the ImageFetch.fxml view.
 * After user selects amount of images with slider,
 * the application.app.DownloadImages Task is called.
 */

public class ImageFetch extends Controller {
	
	@FXML private Button _back;
	@FXML private Button _home;
	@FXML private Button _help;
	@FXML private Button _next;
	@FXML private Button _search;
	@FXML private Label _imageAmountDisplay;
	@FXML private Label _warning;
	@FXML private ProgressBar _progress;
	@FXML private Slider _slider;
	
	@FXML private void handleBack() {handleHome();};
	@FXML 
	public void handleHome() {
		boolean decision = displayAlert("Are you leaving?", "Progress will not be saved if you quit to home");
		if (decision == true) {
			toMenu(_home.getScene());
		} else {
			;
		}

	}
	
	@FXML private void handleHelp() {
		System.out.println("Under construction");
	}
	
	@FXML private void handleNext() {System.out.println("Go next");};
	
	@FXML
	private void handleSearch() {
		/*double number = _slider.getValue();
		String enteredText = Double.toString(number);
		//Trim off the .0
		enteredText = enteredText.substring(0,enteredText.length()-2);
		int enteredNumber = Integer.parseInt(enteredText);
		_search.setDisable(true);
		_slider.setDisable(true);
		_warning.setText("");
		downloadImages(enteredNumber);*/
		System.out.println("Under construction");
	}

	@FXML
	private void handleValueChange() {
		double value = _slider.getValue();
		String stringValue = Double.toString(value);
		//Trim off the .0
		stringValue = stringValue.substring(0,stringValue.length()-2);
		if (value == 1) {
			_imageAmountDisplay.setText(stringValue + " image");
		} else {
			_imageAmountDisplay.setText(stringValue + " images");
		}
	}
	
	private void downloadImages(int imageAmount) {
		
		clearDirectory("tmp/images/");
		DownloadImages downloadImages = new DownloadImages(imageAmount);
		Thread thread = new Thread(downloadImages);
		thread.start();
		
		_progress.progressProperty().bind(downloadImages.progressProperty());
		_progress.setStyle("-fx-accent: #315F83;");
		
		downloadImages.setOnSucceeded(e-> {
			_progress.progressProperty().unbind();
			_next.setDisable(false);
			_next.requestFocus(); //Tell users to keep moving forward
		});
	}
	
}
