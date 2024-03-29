package application.controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Callback;


/**
 * Controller that handles the AudioMatch.fxml view.
 * 
 * The user must select one element from both ListViews
 * before progressing.
 * 
 * This, along with the other *Match windows is the second
 * step of the Quiz Cycle.
 */

public class AudioMatch extends Controller implements Initializable{

	@FXML private AnchorPane _anchor;
	
	@FXML private Button _back;
	@FXML private Button _home;
	@FXML private Button _help;
	@FXML private Button _anchorHelp;
	@FXML private Button _match;
	
	@FXML private Label _errorLabel;
	
	@FXML private ListView<File> _audio;
	@FXML private ListView <String> _terms;

	private int _numQuestions;
	private  MediaPlayer _player;
	private ArrayList<Button> _playBtns = new ArrayList<Button>();

	private ArrayList<File> _allFiles;
	private ObservableList<File> _audioList = FXCollections.observableArrayList();
	private ObservableList<String> _queryList = FXCollections.observableArrayList();

	private ArrayList<File> _guessedAudio = new ArrayList<File>();
	private int[] _audioGuesses; //Stores the number of failed attempts per question.
	private ArrayList<File> _audioFileRecord = new ArrayList<File>(); //Names of files that corresponds to _audioGuesses	

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//Get the number of questions from a file
		File file = new File(System.getProperty("user.dir")+"/tmp/text/numQuestions.txt"); 
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(file));
			_numQuestions = Integer.parseInt(reader.readLine()) ;
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		//Initialise scoring array with zeroes
		_audioGuesses = new int[_numQuestions];

		//Populate the audio list
		_allFiles = new ArrayList<File>(listDirectory("creations"));
		try {
			generateRandomAudioFiles();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
		_audio.setItems(_audioList);
		Collections.shuffle(_queryList);
		_terms.setItems(_queryList);

		//Add buttons to the audio list
		_audio.setCellFactory(new Callback<ListView<File>, ListCell<File>>() {
			public ListCell<File> call(ListView<File> param) {
				return new ButtonCell();
			}
		});

	}

	@FXML 
	private void handleBack() {
		boolean decision = displayAlert("Are you leaving?", "Progress will not be saved if you go back");
		if (decision == true) {
			if (_player!=null) {
				_player.stop();
				_player.dispose(); //Release the video
			}
			switchTo(_back.getScene(),getClass().getResource(_PATH+"QuizSettings.fxml"));
		}
	}
	@FXML
	public void handleHome() {
		boolean decision = displayAlert("Are you leaving?", "Progress will not be saved if you quit to home");
		if (decision == true) {
			if (_player!=null) {
				_player.stop();
				_player.dispose(); //Release the video
			}
			toMenu(_home.getScene());
		}
	}

	@FXML 
	private void handleHelp() {
		if (_anchor.isVisible() == false) { //AnchorPane is invisible on startup
			_anchor.setVisible(true);
			
			_audio.setDisable(true);
			_terms.setDisable(true);
			_back.setDisable(true);
			_home.setDisable(true);
			_match.setDisable(true);
		} else {			
			_anchor.setVisible(false);

			_audio.setDisable(false);
			_terms.setDisable(false);
			_back.setDisable(false);
			_home.setDisable(false);
			_match.setDisable(false);
		}
	}

	@FXML 
	private void handleMatch() throws IOException {
		//Get user's selected items
		File selAudio = _audio.getSelectionModel().getSelectedItem();
		String selQuery = _terms.getSelectionModel().getSelectedItem();

		if(selAudio!=null && selQuery != null) {
			String audioName = selAudio.getName();
			audioName = audioName.substring(0, audioName.length()-4);
			
			//Check if match is correct (Ignore Case)
			if(audioName.equalsIgnoreCase(selQuery)) {
				_guessedAudio.add(selAudio);

				_audioList.remove(selAudio);
				_queryList.remove(selQuery);
				_errorLabel.setText("Good Job!!!");
			}else {
				_errorLabel.setText("Uh-oh! Try again!");

				//Record failed attempt
				int index = _audioFileRecord.indexOf(selAudio);
				_audioGuesses[index] ++;

			}

			//Clear selection from listviews
			_audio.getSelectionModel().clearSelection();
			_terms.getSelectionModel().clearSelection();

			if (_audioList.size()==0) {
				//Get the lists of right and wrong creations
				ArrayList<String> wrongGuesses = new ArrayList<String>();
				ArrayList<String> rightGuesses = new ArrayList<String>();

				for (int i = 0; i<_audioGuesses.length;i++) {
					String filename = _audioFileRecord.get(i).toString();
					filename = filename.substring(filename.indexOf("/")+1, filename.lastIndexOf("/"));

					if(_audioGuesses[i]>0) {
						wrongGuesses.add(filename);
					}else {
						rightGuesses.add(filename);
					}
				}
				//Load the score scene with the scores
				Scene scene = _match.getScene();
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/view/Score.fxml"));
				Score controller = new Score(rightGuesses,wrongGuesses);
				loader.setController(controller);
				scene.setRoot(loader.load());
			}
		}
	}

	/**
	 * Randomly generate a list of _numQuestion files for the questions
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	private void generateRandomAudioFiles() throws NumberFormatException, IOException {
		Random rand = new Random();
		File audio;
		File queryFile;
		String query;
		for (int i = 0; i < _numQuestions; i++) { 
			//Generate random file from list of files
			int index = rand.nextInt(_allFiles.size()); 
			String creationName = _allFiles.get(index).getName();
			creationName = creationName.substring(0,creationName.length()-4);

			queryFile = new File("quiz/"+creationName+"/query");

			//Get the term for that file
			BufferedReader reader = new BufferedReader(new FileReader(queryFile)); 
			query = reader.readLine();
			reader.close();

			//Get the audio for that file
			audio = new File("quiz/"+creationName+"/"+query+".wav");

			//Store audio and query in lists
			_audioList.add(audio); 
			_queryList.add(query);
			_audioFileRecord.add(audio);

			_allFiles.remove(index); 
		} 
	}

	/**Method accessed by onClick events from the FXML.*/
	@FXML 
	private void clearError() {
		_errorLabel.setText("");
	}

	/**
	 * This private inner class adds buttons and labels as graphics to the listview.
	 * The fields do not begin with a hyphen to distinguish from outer-class fields.
	 * 
	 * @author Rainer Schwarze
	 * Original: https://stackoverflow.com/questions/15661500/javafx-listview-item-with-an-image-button
	 * Modified by: dongmeilim
	 */
	private class ButtonCell extends ListCell<File> {
		HBox hbox = new HBox();
		Label label = new Label("(empty)");
		Pane pane = new Pane();
		Button playBtn = new Button("Play");
		File lastItem;

		public ButtonCell() {
			super();
			hbox.getChildren().addAll(label, pane, playBtn);
			HBox.setHgrow(pane, Priority.ALWAYS);
			hbox.setAlignment(Pos.CENTER);
			_playBtns.add(playBtn);

			playBtn.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {

					//Get the Audio in the same row as the button
					File audio = lastItem;
					Media media = new Media(audio.toURI().toString());
					//Play or top the selected audio
					if(_player == null || !(_player.getStatus()== MediaPlayer.Status.PLAYING)) {

						//Set up a new media player
						_player = new MediaPlayer(media);
						_player.setOnEndOfMedia(() -> {
							_player.stop();
							_player.dispose();
							playBtn.setText("Play");

							//Enable other buttons
							disableOtherBtns(false, playBtn);
						});
						_player.play();
						playBtn.setText("Stop");

						//Disable other buttons
						disableOtherBtns(true, playBtn);

					}else {
						//Stop playing
						_player.stop();
						playBtn.setText("Play");

						//Enable other buttons
						disableOtherBtns(false, playBtn);
					}
				}
			});
			setOnMouseClicked(e->clearError());
		}

		@Override
		protected void updateItem(File item, boolean empty) {
			super.updateItem(item, empty);
			setText(null); //No text in label of super class
			if (empty) {
				lastItem = null;
				setGraphic(null);
			} else {
				lastItem = item;
				int index = _audioFileRecord.indexOf(item);
				label.setText("Question "+ (index+1));
				label.setStyle("-fx-text-fill: black; -fx-effect: null;");
				setGraphic(hbox);
			}
		}
	}
	
	/**
	 * Disable the other play buttons and the match button
	 * @param disable
	 * @param playBtn
	 */
	private void disableOtherBtns(boolean disable, Button playBtn) {
		//Disable other play buttons
		for (Button button : _playBtns) {
			if(button != playBtn) {
				button.setDisable(disable);
			}
		}
		//Disable Match button
		_match.setDisable(disable);
	}
}
