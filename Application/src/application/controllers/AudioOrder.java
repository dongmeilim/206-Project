package application.controllers;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.ObservableList;

import javafx.fxml.FXML;

import javafx.geometry.Pos;

import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;

/**
 * Controller that handles the AudioOrder.fxml view.
 * Uses a drag-n-drop ListView to organise the audio.
 */

public class AudioOrder extends Controller {
	
	@FXML private VBox _content;
	
	private ListView<String> _audioList;
	private final int _HEIGHT = 550;
	
	@FXML private void handleBack() {switchNonSkippableWindow(_content.getScene(), getClass().getResource(_PATH+"AudioSelect.fxml"));};
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
		storeFileType("audio", _audioList.getItems());
		switchNonSkippableWindow(_content.getScene(), getClass().getResource(_PATH+"ImageFetch.fxml"));
	}
	
	public void setSavedAudio(List<String> savedAudio) {
		_audioList = new ListView<String>();
		_audioList.getItems().addAll(savedAudio);
		_audioList.setCellFactory(param -> new DragStringCell());
		_audioList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		_audioList.setPrefHeight(_HEIGHT);
		_content.getChildren().add(_audioList);	
	}
	
	/**
	 *Author: Jewelsea
	 *Original: https://gist.github.com/jewelsea/7821196
	 *Modified by: Mirlington
	 *Comments by: Mirlington
	 */
	
	private class DragStringCell extends ListCell<String> {
		
		public DragStringCell() {
			ListCell<String> cell = this;
			
			setContentDisplay(ContentDisplay.LEFT);
			setAlignment(Pos.CENTER);
			setOnDragDetected(e-> {
				if (getItem() == null) {
					return;
				}

				
				Dragboard dragboard = startDragAndDrop(TransferMode.MOVE); //Draggable clipboard
				ClipboardContent content = new ClipboardContent();
				content.putString(getItem()); //Cell string is in content
				
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
	        	setText(null);
	        	
	        } else {
	        	setText(item);
	        }
	    }
		
	}
	

}
