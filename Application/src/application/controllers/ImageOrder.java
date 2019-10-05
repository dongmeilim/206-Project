package application.controllers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;

import javafx.geometry.Pos;

import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;

/**
 * Controller that handles the ImageOrder.fxml view.
 * Uses a drag-n-drop ListView (displaying image thumbnails)
 * to organise the images.
 */

public class ImageOrder extends Controller {

	@FXML private VBox _content;
	
	
	private ObservableList<Image> _observableImages = FXCollections.observableArrayList(); //method used to instantiate
	private ListView<String> _imageList;
	private final int _HEIGHT = 550;
	@FXML private void handleBack() {switchNonSkippableWindow(_content.getScene(), getClass().getResource(_PATH+"ImageSelect.fxml"));};	
	@FXML 
	public void handleHome() {
		boolean decision = displayAlert("Are you leaving?", "Progress will not be saved if you quit to home");
		if (decision == true) {
			switchNonSkippableWindow(_content.getScene(), getClass().getResource(_PATH+"Menu.fxml"));
		} else {
			;
		}

	}
	@FXML private void handleNext() {
		storeFileType("image", _imageList.getItems());
		switchNonSkippableWindow(_content.getScene(),getClass().getResource(_PATH+"Save.fxml"));	
	}
	
	public void setSavedImages(List<String> savedImages) {
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
	}
	
	/**
	 * Similar to DragStringCell
	 * This was made an inner class so that the DragImageCell class has access to _observableImages
	 * This makes it so that each cell sees one objective observable image list.
	 *
	 *Author: Jewelsea
	 *Original: https://gist.github.com/jewelsea/7821196
	 *Modified by: Mirlington
	 *Comments by: Mirlington
	 *
	 */
	
	private class DragImageCell extends ListCell<String> {
		
		private final ImageView _imageView = new ImageView();
		
		public DragImageCell() {
			ListCell<String> cell = this;
			setContentDisplay(ContentDisplay.GRAPHIC_ONLY); 
			setAlignment(Pos.CENTER);
			setOnDragDetected(e-> {
				if (getItem() == null) {
					return;
				}

				
				Dragboard dragboard = startDragAndDrop(TransferMode.MOVE); //Draggable clipboard
				ClipboardContent content = new ClipboardContent();
				content.putString(getItem()); //Cell string is in content
				
				ObservableList<String> items = getListView().getItems();
				dragboard.setDragView(_observableImages.get(items.indexOf(getItem())));
				
				dragboard.setContent(content);
				e.consume();
			});
			
			setOnDragOver(e-> {
				if (e.getGestureSource() != cell && e.getDragboard().hasString()) {
					e.acceptTransferModes(TransferMode.MOVE); //upon dragging - moves the cell underneath
	            }
			});
			
	        setOnDragDropped(e -> {
	            if (getItem() == null) {
	                return;
	            }

	            Dragboard db = e.getDragboard();
	            boolean success = false;

	            if (db.hasString()) {
	            	ObservableList<String> items = getListView().getItems();
	                int draggedIndex = items.indexOf(db.getString()); //Cell being dragged (perpetrator)
	                int thisIndex = items.indexOf(getItem()); //Cell being dropped on (victim)
	                
	                
	                //Updates under the hood (so that order is saved post-scene-refresh)
	                items.set(draggedIndex, getItem());
	                items.set(thisIndex, db.getString());
	                
                	//This tidbit swaps image a with image b (what the user sees)
                    Image temp = _observableImages.get(draggedIndex);
                    _observableImages.set(draggedIndex, _observableImages.get(thisIndex));
                    _observableImages.set(thisIndex, temp);
	                
	                

	                List<String> itemscopy = new ArrayList<>(getListView().getItems());
	              	getListView().getItems().setAll(itemscopy);

	                success = true;
	            }
	            e.setDropCompleted(success);
	            e.consume();
	        });
	        setOnDragDone(DragEvent::consume);
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
