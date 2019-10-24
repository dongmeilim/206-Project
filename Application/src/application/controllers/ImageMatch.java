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

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;


/**
 * Controller that handles the ImageFetch.fxml view.
 * 
 * The user must select one element from both ListViews
 * before progressing.
 * 
 * This, along with the other *Match windows is the second
 * step of the Quiz Cycle.
 */

public class ImageMatch extends Controller implements Initializable {
	
	@FXML private BorderPane _border;
	@FXML private Button _back;
	@FXML private Button _home;
	@FXML private Button _help;
	@FXML private Button _anchorHelp;
	@FXML private Button _match;
	@FXML private Label _errorLabel;
	@FXML private ListView<String> _thumbnails;
	@FXML private ListView<String> _terms;
	
	@FXML private AnchorPane _anchor;
	
	private int _questionAmount;
	
	private final int _IMAGEWIDTH=100;
	private final int _IMAGEHEIGHT=100;
	
	private ObservableList<String> _observableImageNames = FXCollections.observableArrayList(); 
	private ObservableList<String> _observableTerms = FXCollections.observableArrayList(); 
	
	private ArrayList<String> _guessedImage = new ArrayList<String>();
	private int[] _imageGuesses; //stores the number of failed attempts per question. The corresponding terms are in _imageFileRecord
	private ArrayList<String> _imageFileRecord = new ArrayList<String>(); // index of creation names correspond to _termsRecords 
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		try {
			File questionFile = new File(System.getProperty("user.dir")+"/tmp/text/numQuestions.txt");
	    	BufferedReader bufferedReaderQuestion = new BufferedReader(new FileReader(questionFile)); 
			String questionAmountString = bufferedReaderQuestion.readLine();
			_questionAmount = Integer.parseInt(questionAmountString);
			bufferedReaderQuestion.close();
		} catch (IOException eQuestion) {
			eQuestion.printStackTrace();
		}
		
		//Initialize scoring array with zeroes
		_imageGuesses = new int[_questionAmount];
		
		generateRandomCreations();
		
		_thumbnails.setItems(_observableImageNames);
		_thumbnails.setCellFactory(param -> new Thumbnail());
		Collections.shuffle(_observableTerms);
		_terms.setItems(_observableTerms);
	}
	
	/**
	 * Randomly generate images for the questions
	 */
	private void generateRandomCreations() {
		List<File> creations = listDirectory("creations");
		Random rand = new Random();
		for (int i = 0; i < _questionAmount; i++) {
			try {
				//get a random creation
				int index = rand.nextInt(creations.size());
				String creationName = creations.get(index).getName();
				creations.remove(index);
				
				// get the name of the creation
				String creationNameNoExtension = creationName.substring(0,creationName.length()-4);
				_observableImageNames.add(creationNameNoExtension);
				_imageFileRecord.add(creationNameNoExtension);
				
				//get the term for the creation
				File queryFile = new File("quiz/"+creationNameNoExtension+"/query");
	        	BufferedReader bufferedReaderQuery = new BufferedReader(new FileReader(queryFile)); 
	    		String query = bufferedReaderQuery.readLine();
	    		bufferedReaderQuery.close();
	    		_observableTerms.add(query);
				
			} catch (FileNotFoundException eQuery) {
				eQuery.printStackTrace();
			} catch (IOException eQuery) {
				eQuery.printStackTrace();
			}
		}
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
		if (_anchor.isVisible() == false) { //AnchorPane is invisible on startup

			_anchor.setVisible(true);
			
			_thumbnails.setDisable(true);
			_terms.setDisable(true);
			_back.setDisable(true);
			_home.setDisable(true);
			_match.setDisable(true);
		} else {			
			_anchor.setVisible(false);

			_thumbnails.setDisable(false);
			_terms.setDisable(false);
			_back.setDisable(false);
			_home.setDisable(false);
			_match.setDisable(false);

		}
	}
	
	@FXML private void handleMatch() throws IOException {
		//get user's selected items
		String selCreationName = _thumbnails.getSelectionModel().getSelectedItem();
		String selQuery = _terms.getSelectionModel().getSelectedItem();
		
		if(selCreationName!=null && selQuery != null) {
	
			File queryFile = new File("quiz/"+selCreationName+"/query");
			BufferedReader bufferedReaderQuery = new BufferedReader(new FileReader(queryFile)); 
    		String query = bufferedReaderQuery.readLine();
    		bufferedReaderQuery.close();
			
    		// check if the match is correct
			if(query.equalsIgnoreCase(selQuery)) {
				_guessedImage.add(selCreationName);

				_observableImageNames.remove(selCreationName);
				_observableTerms.remove(selQuery);
				_errorLabel.setText("Good Job!!!");
			} else {
				_errorLabel.setText("Uh-oh! Try again!");
				
				//record failed attempt
				int index = _imageFileRecord.indexOf(selCreationName);
				_imageGuesses[index] ++;
			}

			_thumbnails.getSelectionModel().clearSelection();
			_terms.getSelectionModel().clearSelection();
		}
		


		if (_observableImageNames.isEmpty() == true && _observableTerms.isEmpty() == true) {
			// get the lists of right and wrong creations
			ArrayList<String> wrongGuesses = new ArrayList<String>();
			ArrayList<String> rightGuesses = new ArrayList<String>();

			for (int i = 0; i<_imageGuesses.length;i++) {
				String creationName = _imageFileRecord.get(i);
				
				if(_imageGuesses[i]>0) {
					wrongGuesses.add(creationName);
				}else {
					rightGuesses.add(creationName);
				}
			}
			
			// pass the lists into the score scene and change scene
			Scene scene = _match.getScene();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/view/Score.fxml"));
			Score controller = new Score(rightGuesses,wrongGuesses);
			loader.setController(controller);
			scene.setRoot(loader.load());

		}
		
	}
	
	//Accessed by both the query ListView and the Thumbnail ListView
	@FXML private void clearError() {
		_errorLabel.setText("");
	}

	
	/**
	 * Private inner class to render images from strings in the ListView
	 */
	
	private class Thumbnail extends ListCell<String> {
			
		private final ImageView _imageView = new ImageView();
		
		public Thumbnail() {
			setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
			setAlignment(Pos.CENTER);
			setOnMouseClicked(e->clearError());
		}
		
		@Override
	    protected void updateItem(String item, boolean empty) { //changes the order
	        super.updateItem(item, empty);
	        
	        if (empty || item == null) {
	        	setGraphic(null);
	        	
	        } else {
	        	String name = _observableImageNames.get(getListView().getItems().indexOf(item));
	        	try {
	        		// get the query term
					File queryFile = new File("quiz/"+name+"/query");
		        	BufferedReader bufferedReaderQuery = new BufferedReader(new FileReader(queryFile)); 
		    		String query = bufferedReaderQuery.readLine();
		    		bufferedReaderQuery.close();
		    		
		    		//get the image
		    		File updatedImageFile = new File("quiz/"+name+"/"+query+".jpg");
		    		FileInputStream fileInputStream = new FileInputStream(updatedImageFile);
		    		Image updatedImage = new Image(fileInputStream);
		    		_imageView.setImage(updatedImage);
	    		} catch (IOException e) {
					e.printStackTrace();
				}
	 
	        	_imageView.setFitHeight(_IMAGEHEIGHT);
	        	_imageView.setFitWidth(_IMAGEWIDTH);
	        	_imageView.setPreserveRatio(true);
	            setGraphic(_imageView); 
	        }
	    }
	}
}
