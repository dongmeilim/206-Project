package application.app;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.collections.ObservableList;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

import javafx.concurrent.Task;
import javafx.geometry.Pos;

/**
 * Task that downloads images from Flickr
 * in the background.
 */

public class ReadyImages extends Task<Void> {

	private List<File> _creations;
	
	private ListView<String> _thumbnails;
	private ObservableList<Image> _observableImages;
	
	private ListView<String> _terms;
	private ObservableList<String> _observableTerms;
	
	private int _questionAmount;
	// TODO probably delete this, looks like multithreading won't actually work or is necessary.
	
	public ReadyImages(List<File> creations, ListView<String> thumbnails, ObservableList<Image> observableImages,
			ListView<String> terms, ObservableList<String> observableTerms, int questionAmount) {
		_creations = creations;		
		_thumbnails = thumbnails;
		_observableImages = observableImages;
		_terms = terms;
		_observableTerms = observableTerms;
		_questionAmount = questionAmount;
	}
	
	@Override
	protected Void call() throws Exception {
		Random rand = new Random();
		for (int i = 0; i <= (_questionAmount-1); i++) {
			try {
				int index; //Won't ever be undefined

				if ((_questionAmount-1) == 0 || (_creations.size()-1) == 0) {
					index=0;
				} else if ( (_creations.size()-1) <= (_questionAmount-1)) {
					index = rand.nextInt(_creations.size()-1);
				} else {
					index = rand.nextInt(_questionAmount-1);
				}
				
				String creationName = _creations.get(index).getName();
				_creations.remove(index);
				_thumbnails.getItems().add(creationName);
				String creationNameNoExtension = creationName.substring(0,creationName.length()-4);
				
				File queryFile = new File("quiz/"+creationNameNoExtension+"/query");
	        	BufferedReader bufferedReader = new BufferedReader(new FileReader(queryFile)); 
	    		String query = bufferedReader.readLine();
	    		bufferedReader.close();
	    		_observableTerms.add(query);
				
				FileInputStream inputStream = new FileInputStream("quiz/"+creationNameNoExtension+"/"+query+".jpg");
				Image image = new Image(inputStream);
				_observableImages.add(image);
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		_thumbnails.setCellFactory(param -> new Thumbnail());
		Collections.shuffle(_observableTerms);
		_terms.setItems(_observableTerms);
		return null;

	}
	
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
