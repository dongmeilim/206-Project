package application.controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

import application.app.DownloadImages;
import application.app.ReadyImages;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


/**
 * Controller that handles the ImageFetch.fxml view.
 * After user selects amount of images with slider,
 * the application.app.DownloadImages Task is called.
 */

public class ImageMatch extends Controller implements Initializable {
	
	@FXML private Button _back;
	@FXML private Button _home;
	@FXML private Button _help;
	@FXML private Button _match;
	@FXML private Label _errorLabel;
	@FXML private ListView<String> _thumbnails;
	@FXML private ListView<String> _terms;
	
	private String _questionAmountString;
	
	private ObservableList<Image> _observableImages = FXCollections.observableArrayList(); 
	private ObservableList<String> _observableTerms = FXCollections.observableArrayList(); 
	
	private ArrayList<String> _guessedImage = new ArrayList<String>();
	private ArrayList<String> _guessedQuery = new ArrayList<String>(); //TODO discuss: is this needed?
	
	private List<File> _creations = listDirectory("creations");
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		try {
			File questionFile = new File("quiz/questionAmount");
	    	BufferedReader bufferedReaderQuestion = new BufferedReader(new FileReader(questionFile)); 
			_questionAmountString = bufferedReaderQuestion.readLine();
			bufferedReaderQuestion.close();
		} catch (IOException eQuestion) {

			eQuestion.printStackTrace();
		}
		
		
		int questionAmount = Integer.parseInt(_questionAmountString);
		
		List<File> creations = listDirectory("creations");
		Random rand = new Random();
		for (int i = 0; i <= (questionAmount-1); i++) {
			try {
				int index; //Won't ever be undefined

				if ((questionAmount-1) == 0 || (creations.size()-1) == 0) {
					index=0;
				} else if ( (creations.size()-1) <= (questionAmount-1)) {
					index = rand.nextInt(creations.size()-1);
				} else {
					index = rand.nextInt(questionAmount-1);
				}
				
				String creationName = creations.get(index).getName();
				creations.remove(index);
				_thumbnails.getItems().add(creationName);
				String creationNameNoExtension = creationName.substring(0,creationName.length()-4);
				
				File queryFile = new File("quiz/"+creationNameNoExtension+"/query");
	        	BufferedReader bufferedReaderQuery = new BufferedReader(new FileReader(queryFile)); 
	    		String query = bufferedReaderQuery.readLine();
	    		bufferedReaderQuery.close();
	    		_observableTerms.add(query);
				
				FileInputStream inputStream = new FileInputStream("quiz/"+creationNameNoExtension+"/"+query+".jpg");
				Image image = new Image(inputStream);
				_observableImages.add(image);
				
			} catch (FileNotFoundException eQuery) {
				eQuery.printStackTrace();
			} catch (IOException eQuery) {
				eQuery.printStackTrace();
			}
		}
		_thumbnails.setCellFactory(param -> new Thumbnail());
		Collections.shuffle(_observableTerms);
		_terms.setItems(_observableTerms);
	
	}
	
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
	
		//get users selected items
		String selCreationName = _thumbnails.getSelectionModel().getSelectedItem();
		String selQuery = _terms.getSelectionModel().getSelectedItem();
		
		if(selCreationName!=null && selQuery != null) {
	
			String creationNameNoExtension =  selCreationName.substring(0,selCreationName.length()-4);
			File imageLocation = new File("quiz/"+creationNameNoExtension+"/"+selQuery+".jpg");
			
			if(imageLocation.exists()) {
				_guessedImage.add(selCreationName);
				_guessedQuery.add(selQuery);

				_thumbnails.getItems().remove(selCreationName);
				_observableTerms.remove(selQuery);
				_errorLabel.setText("Good Job!!!");
			} else {
				_errorLabel.setText("Uh-oh! Try again!");
			}

			_thumbnails.getSelectionModel().clearSelection();
			_terms.getSelectionModel().clearSelection();
		}
		

		
		if (_observableImages.size()==0) {
			//TODO move to scoring window and calculate scores
		}
		
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
