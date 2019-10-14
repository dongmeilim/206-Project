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
import javafx.scene.layout.AnchorPane;
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
	private String _currentAmount = "";
	
	private boolean _fetchButtonIsEnabled = true;
	private boolean _nextButtonIsEnabled = false;
	
	@FXML private AnchorPane _anchor;
	@FXML private AnchorPane _cursorAnchor;
	@FXML private Button _back;
	@FXML private Button _home;
	@FXML private Button _help;
	@FXML private Button _next;
	@FXML private Button _fetch;
	@FXML private CheckBox _backgroundMusic;
	@FXML private Label _imageAmountDisplay;
	@FXML private Label _warning;
	@FXML private ProgressBar _imageProgress;
	@FXML private ProgressIndicator _videoProgress;
	@FXML private VBox _imageContent;
	@FXML private Slider _slider;
	// TODO make it so user can always use arrow keys for navigating slider
	@FXML private void handleBack() {switchTo(_back.getScene(), getClass().getResource(_PATH+"TextSelect.fxml"));}
	@FXML 
	public void handleHome() {
		boolean decision = displayAlert("Are you leaving?", "Progress will not be saved if you quit to home");
		if (decision == true) {
			toMenu(_home.getScene());
		}
	}
	
	@FXML private void handleHelp() {
		
		if (_anchor.isVisible()==false) { //AnchorPane is invisible on startup
			_currentAmount = _imageAmountDisplay.getText();
			_imageAmountDisplay.setText("");
			
			_home.setDisable(true);
			_back.setDisable(true);
			_slider.setDisable(true);
			_backgroundMusic.setDisable(true);
			if (_fetchButtonIsEnabled == true) {
				_fetch.setDisable(true);
			} else if (_imageProgress.getProgress() >= 1) {
				_cursorAnchor.setVisible(true);
			}
			if (_nextButtonIsEnabled == true) {
				_next.setDisable(true);
			}
			
			for (CheckBox checkBox: _checkBoxes) {
				checkBox.setDisable(true);
			}
			
			_anchor.setVisible(true);
		} else {
			_imageAmountDisplay.setText(_currentAmount);
			
			_home.setDisable(false);
			_back.setDisable(false);
			_slider.setDisable(false);
			_backgroundMusic.setDisable(false);
			if (_fetchButtonIsEnabled == true) {
				_fetch.setDisable(false);
			} else if (_imageProgress.getProgress() >= 1) {
				_cursorAnchor.setVisible(false);
			}
			if (_nextButtonIsEnabled == true) {
				_next.setDisable(false);
			}
			
			for (CheckBox checkBox: _checkBoxes) {
				checkBox.setDisable(false);
			}
			
			_anchor.setVisible(false);
		}
	}
	
	@FXML private void handleNext() {
		_warning.setText("");
		_next.setDisable(true);
		_videoProgress.setVisible(true);
		createPreview();
	}
	
	@FXML
	private void handleFetch() {
		double number = _slider.getValue();
		String enteredText = Double.toString(number);
		//Trim off the .0
		enteredText = enteredText.substring(0,enteredText.length()-2);
		int enteredNumber = Integer.parseInt(enteredText);
		_fetch.setDisable(true);
		_fetchButtonIsEnabled = false;
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
		_imageProgress.setStyle("-fx-accent: #5c91b0;");
		
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
			_nextButtonIsEnabled = true;
			_next.requestFocus(); //Let the user know it is ok to move forward
		} else {
			_warning.setText("Please select at least one image.");
			_nextButtonIsEnabled = false;
			_next.setDisable(true);
		}
	}
	
	private void createPreview() {
		boolean haveMusicInVideo;
		if (_backgroundMusic.isSelected() == true) {
			haveMusicInVideo = true;
		} else {
			haveMusicInVideo = false;
		}
		
		CreatePreview createPreview = new CreatePreview(haveMusicInVideo);
		Thread thread = new Thread(createPreview);
		thread.start();
		
		_videoProgress.progressProperty().bind(createPreview.progressProperty());
		_videoProgress.setStyle("-fx-accent: #5c91b0;");
		
		createPreview.setOnRunning(running -> {
			_imageContent.setDisable(true);
		});
		
		createPreview.setOnSucceeded(e-> {
			_imageProgress.progressProperty().unbind();
			switchTo(_next.getScene(), getClass().getResource(_PATH+"PreviewSave.fxml"));
			
		});	
	}
}
