package application.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


/**
 * Controller that handles the ImageFetch.fxml view.
 * After user selects amount of images with slider,
 * the application.app.DownloadImages Task is called.
 */

public class ImageMatch extends Controller {
	
	@FXML private Button _back;
	@FXML private Button _home;
	@FXML private Button _help;
	@FXML private Button _match;
	@FXML private ListView _thumbnails;
	
	private ObservableList<Image> _observableImages = FXCollections.observableArrayList(); 
	
	@FXML 
	private void handleBack() {
		boolean decision = displayAlert("Are you leaving?", "Progress will not be saved if you go back");
		if (decision == true) {
			switchTo(_back.getScene(),getClass().getResource(_PATH+"QuizSettings.fxml"));
		}
	}
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
	
	@FXML private void handleMatch() {
		
	}
	/*
	private void setImages(int questionAmount) {
		ListView<St>
		 List<File> creations = listDirectory("creations");
		 for (int i = 0; i < (questionAmount - 1); i++) {
		  		_imageList = new ListView<String>();
		_imageList.getItems().addAll(savedImages);
		try {
			for (String imageName: savedImages) { //load images from files
			
				FileInputStream inputstream;
				inputstream = new FileInputStream("tmp/images/"+imageName);
				Image image = new Image(inputstream);
				_observableImages.add(image);
			}	
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}	
		_imageList.setCellFactory(param -> new DragImageCell());
		_imageList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		_imageList.setPrefHeight(_HEIGHT);
		_content.getChildren().add(_imageList);	
		 * }
		 
		//TODO implement setter
	}*/
	
	private class Thumbnail extends ListCell<String> {
			
			private final ImageView _imageView = new ImageView();
			
			public Thumbnail() {
				setContentDisplay(ContentDisplay.GRAPHIC_ONLY); 
				setAlignment(Pos.CENTER);
	    }
		
		@Override
	    protected void updateItem(String item, boolean empty) { //changes the order
	        super.updateItem(item, empty);
	        
	        if (empty || item == null) {
	
	        	setGraphic(null);
	        	
	        } else {
	        	_imageView.setImage(_observableImages.get(getListView().getItems().indexOf(item)));
	        	_imageView.setFitHeight(50);
	        	_imageView.setFitWidth(50);
	        	_imageView.setPreserveRatio(true);
	        	
	            setGraphic(_imageView); 
	        }
	    }
	}	
}
