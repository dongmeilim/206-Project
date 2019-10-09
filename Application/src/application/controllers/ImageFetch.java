package application.controllers;

import application.app.CreatePreview;
import application.app.DownloadImages;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.fxml.FXML;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Slider;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Controller that handles the ImageFetch.fxml view.
 * After user selects amount of images with slider,
 * the application.app.DownloadImages Task is called.
 */

public class ImageFetch extends Controller {
	
	private final double _WINDOWWIDTH = 750;
	private final int _SPACING = 10;
	private List<CheckBox> _checkBoxes = new ArrayList<CheckBox>();
	
	@FXML private Button _back;
	@FXML private Button _home;
	@FXML private Button _help;
	@FXML private Button _next;
	@FXML private Button _fetch;
	@FXML private Label _imageAmountDisplay;
	@FXML private Label _warning;
	@FXML private ProgressBar _imageProgress;
	@FXML private ProgressIndicator _videoProgress;
	@FXML private VBox _imageContent;
	@FXML private Slider _slider;
	
	@FXML private void handleBack() {switchTo(_back.getScene(), getClass().getResource(_PATH+"TextSelect.fxml"));}
	@FXML 
	public void handleHome() {
		boolean decision = displayAlert("Are you leaving?", "Progress will not be saved if you quit to home");
		if (decision == true) {
			toMenu(_home.getScene());
		}
	}
	
	@FXML private void handleHelp() {
		System.out.println("Under construction");
	}
	
	//TODO add loading icon (circular) and label "Loading Video. . ." before moving on to PreviewSave.fxml
	@FXML private void handleNext() {
		_warning.setText("");
		_next.setDisable(true);
		_videoProgress.setVisible(true);
		createPreview();
		//switchTo(_next.getScene(), getClass().getResource(_PATH+"PreviewSave.fxml"));
	}
	
	@FXML
	private void handleFetch() {
		double number = _slider.getValue();
		String enteredText = Double.toString(number);
		//Trim off the .0
		enteredText = enteredText.substring(0,enteredText.length()-2);
		int enteredNumber = Integer.parseInt(enteredText);
		_fetch.setDisable(true);
		_slider.setDisable(true);
		downloadImages(enteredNumber);
	}
	
	@FXML
	private void handleValueChange() {
		double value = _slider.getValue();
		String stringValue = Double.toString(value);
		//Trim off the .0
		stringValue = stringValue.substring(0,stringValue.length()-2);
		if (value == 1) {
			_imageAmountDisplay.setText(stringValue + " image");  // 1 image
		} else {
			_imageAmountDisplay.setText(stringValue + " images"); // N images
		}
	}
	
	private void downloadImages(int imageAmount) {
		
		clearDirectory("tmp/images/");
		DownloadImages downloadImages = new DownloadImages(imageAmount);
		Thread thread = new Thread(downloadImages);
		thread.start();
		
		_imageProgress.progressProperty().bind(downloadImages.progressProperty());
		_imageProgress.setStyle("-fx-accent: #315F83;");
		
		downloadImages.setOnSucceeded(e-> {
			_imageProgress.progressProperty().unbind();
			loadImages(imageAmount);
			_warning.setText("Please select at least one image.");
		});
	}
	
	private void loadImages(int imageAmount) {		
		HBox firstRow = new HBox(_SPACING);
		firstRow.setPrefWidth(_WINDOWWIDTH);
		HBox secondRow = new HBox(_SPACING);
		secondRow.setPrefWidth(_WINDOWWIDTH);
		HBox thirdRow = new HBox(_SPACING);
		thirdRow.setPrefWidth(_WINDOWWIDTH);
		
		//Height is not final as it is dependent on Row
		final double IMAGEWIDTH = (_WINDOWWIDTH/4) - 40; // -40 because space taken up by padding and literal checkBoxes
		List<File> imageFiles = listDirectory("tmp/images/");
		_checkBoxes.clear();
		
		for (int i=0; i<= imageAmount-1; i++) {
			try {
				File imageFile = imageFiles.get(i);
				FileInputStream inputstream = new FileInputStream(imageFile);
				Image image = new Image(inputstream); 
				ImageView imageView = new ImageView(image);
	        	imageView.setFitWidth(IMAGEWIDTH);
	        	imageView.setPreserveRatio(true);
				inputstream.close();
				
				CheckBox checkBox = new CheckBox();
				checkBox.setText(imageFile.getName());
				checkBox.setGraphic(imageView);
				checkBox.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
				checkBox.setAlignment(Pos.CENTER);
				checkBox.setOnMouseClicked(e-> checkClickedImages());
				_checkBoxes.add(checkBox);
				
				if (i > 7) { //Third Row
					imageView.setFitHeight(thirdRow.getHeight());
					thirdRow.getChildren().add(checkBox);
				} else if (i > 3) {//Second Row
					imageView.setFitHeight(secondRow.getHeight());
					secondRow.getChildren().add(checkBox);
				} else {//First Row
					imageView.setFitHeight(firstRow.getHeight());
					firstRow.getChildren().add(checkBox);
				}
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		_imageContent.getChildren().addAll(firstRow,secondRow,thirdRow);
	}
	
	private void checkClickedImages() {
		List<String> savedImageNames = new ArrayList<String>();
		boolean canProgress = false;
		
		for (CheckBox individualBox: _checkBoxes) {
			if (individualBox.isSelected() ==  true) {
				canProgress = true;
				savedImageNames.add(individualBox.getText());
			}
		}
		if (canProgress == true) { //at least one image is selected
			storeFileType("image", savedImageNames);
			_warning.setText("");
			_next.setDisable(false);
			_next.requestFocus(); //Let the user know it is ok to move forward
		} else {
			_warning.setText("Please select at least one image.");
			_next.setDisable(true);
		}
	}
	
	private void createPreview() {
		CreatePreview createPreview = new CreatePreview();
		Thread thread = new Thread(createPreview);
		thread.start();
		
		_videoProgress.progressProperty().bind(createPreview.progressProperty());
		_videoProgress.setStyle("-fx-accent: #315F83;");
		
		createPreview.setOnSucceeded(e-> {
			_imageProgress.progressProperty().unbind();
			switchTo(_next.getScene(), getClass().getResource(_PATH+"PreviewSave.fxml"));
			
		});	
	}
}
