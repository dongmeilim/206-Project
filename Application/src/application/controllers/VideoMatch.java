package application.controllers;

import java.io.BufferedReader;
import java.io.File;
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
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

/**
 * Controller that handles the VideoMatch.fxml view.
 * 
 * The user must select one element from both ListViews
 * before progressing.
 * 
 * This, along with the other *Match windows is the second
 * step of the Quiz Cycle.
 */

public class VideoMatch extends Controller implements Initializable {

	@FXML private Button _back;
	@FXML private Button _home;
	@FXML private Button _help;
	@FXML private Button _anchorHelp;
	@FXML private Button _match;

	@FXML private AnchorPane _anchor;

	@FXML private ListView<String> _videos;
	@FXML private ListView<String> _terms;
	@FXML private Label _errorLabel;

	private MediaPlayer _mediaPlayer;

	private final int _MEDIAWIDTH=200;
	private final int _MEDIAHEIGHT=200;

	private int _numQuestions;
	private ArrayList<Button> _playBtns = new ArrayList<Button>();

	private ObservableList<String> _observableVideoNames = FXCollections.observableArrayList(); 
	private ObservableList<String> _observableTerms = FXCollections.observableArrayList(); 

	private ArrayList<String> _guessedVideo = new ArrayList<String>();
	private int[] _videoGuesses; //stores the number of failed attempts per question. The corresponding terms are in _videoFileRecord
	private ArrayList<String> _videoFileRecord = new ArrayList<String>(); // index of creation names correspond to _termsRecords 

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		try {
			File questionFile = new File(System.getProperty("user.dir")+"/tmp/text/numQuestions.txt");
			BufferedReader bufferedReaderQuestion = new BufferedReader(new FileReader(questionFile)); 
			String questionAmountString = bufferedReaderQuestion.readLine();
			_numQuestions = Integer.parseInt(questionAmountString);
			bufferedReaderQuestion.close();
		} catch (IOException eQuestion) {
			eQuestion.printStackTrace();
		}

		// generate random videos for the quiz
		List<File> creations = listDirectory("creations");
		Random rand = new Random();
		for (int i = 0; i < _numQuestions; i++) {
			try {
				int index = rand.nextInt(creations.size());

				String creationName = creations.get(index).getName();
				creations.remove(index);
				String creationNameNoExtension = creationName.substring(0,creationName.length()-4);
				_observableVideoNames.add(creationNameNoExtension);
				_videoFileRecord.add(creationNameNoExtension);

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
		_videos.setItems(_observableVideoNames);
		_videos.setCellFactory(param -> new VideoSnippet());
		Collections.shuffle(_observableTerms);
		_terms.setItems(_observableTerms);

		_videoGuesses = new int[_numQuestions];

	}

	@FXML 
	private void handleBack() {
		boolean decision = displayAlert("Are you leaving?", "Progress will not be saved if you go back");
		if (decision == true) {
			if (_mediaPlayer!=null) {
				_mediaPlayer.stop();
				_mediaPlayer.dispose(); // release the video
			}
			switchTo(_back.getScene(),getClass().getResource(_PATH+"QuizSettings.fxml"));
		}
	}
	@FXML
	public void handleHome() {
		boolean decision = displayAlert("Are you leaving?", "Progress will not be saved if you quit to home");
		if (decision == true) {
			if (_mediaPlayer!=null) {
				_mediaPlayer.stop();
				_mediaPlayer.dispose(); // release the video
			}
			toMenu(_home.getScene());
		}
	}

	@FXML private void handleHelp() {
		if (_anchor.isVisible() == false) { //AnchorPane is invisible on startup

			_anchor.setVisible(true);

			_videos.setDisable(true);
			_terms.setDisable(true);
			_back.setDisable(true);
			_home.setDisable(true);
			_match.setDisable(true);
		} else {			
			_anchor.setVisible(false);

			_videos.setDisable(false);
			_terms.setDisable(false);
			_back.setDisable(false);
			_home.setDisable(false);
			_match.setDisable(false);

		}
	}

	@FXML private void handleMatch() throws IOException {
		//get users selected items
		String selCreationName = _videos.getSelectionModel().getSelectedItem();
		String selQuery = _terms.getSelectionModel().getSelectedItem();

		if(selCreationName!=null && selQuery != null) {
			
			File queryFile = new File("quiz/"+selCreationName+"/query");
			BufferedReader bufferedReaderQuery = new BufferedReader(new FileReader(queryFile)); 
			String query = bufferedReaderQuery.readLine();
			bufferedReaderQuery.close();

			if(query.equalsIgnoreCase(selQuery)) {
				_guessedVideo.add(selCreationName);

				_observableVideoNames.remove(selCreationName);
				_observableTerms.remove(selQuery);
				_errorLabel.setText("Good Job!!!");
			} else {
				_errorLabel.setText("Uh-oh! Try again!");

				//record failed attempt
				int index = _videoFileRecord.indexOf(selCreationName);
				_videoGuesses[index] ++;
			}

			_videos.getSelectionModel().clearSelection();
			_terms.getSelectionModel().clearSelection();

			if (_observableVideoNames.size()==0) {
				// get the lists of right and wrong creations
				ArrayList<String> wrongGuesses = new ArrayList<String>();
				ArrayList<String> rightGuesses = new ArrayList<String>();

				for (int i = 0; i<_videoGuesses.length;i++) {
					String filename = _videoFileRecord.get(i).toString();


					if(_videoGuesses[i]>0) {
						wrongGuesses.add(filename);
					}else {
						rightGuesses.add(filename);
					}
				}

				Scene scene = _match.getScene();
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/view/Score.fxml"));
				Score controller = new Score(rightGuesses,wrongGuesses);
				loader.setController(controller);
				scene.setRoot(loader.load());
			}
		}
	}

	@FXML private void clearError() {
		_errorLabel.setText("");
	}

	/**
	 * Private inner class that renders a video in MediaView and a play/stop button as controls.
	 */
	private class VideoSnippet extends ListCell<String> {

		private final MediaView _mediaView = new MediaView();
		private HBox _hbox = new HBox();
		private Pane _pane = new Pane();
		private Button _playBtn = new Button("Play");
		private Media _lastItem;

		public VideoSnippet() {
			super();
			_hbox.getChildren().addAll(_mediaView, _pane, _playBtn);
			HBox.setHgrow(_pane, Priority.ALWAYS);
			_hbox.setAlignment(Pos.CENTER);
			_playBtns.add(_playBtn);
			//setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
			setAlignment(Pos.CENTER);

			_playBtn.setOnAction(e -> {

				Media mediaPlayed = _lastItem;
				if(_mediaPlayer == null || !(_mediaPlayer.getStatus()== MediaPlayer.Status.PLAYING)) {

					//set up a new media player
					_mediaPlayer = new MediaPlayer(mediaPlayed);
					_mediaView.setMediaPlayer(_mediaPlayer);
					_mediaPlayer.setOnEndOfMedia(() -> {
						_mediaPlayer.stop();
						//_mediaPlayer.dispose();
						_playBtn.setText("Play");

						//enable other buttons
						disableOtherBtns(false, _playBtn);


					});
					_mediaPlayer.play();
					_playBtn.setText("Stop");

					//Disable other buttons
					disableOtherBtns(true, _playBtn);

				} else {
					//stop playing
					_mediaPlayer.stop();
					//_mediaPlayer.dispose();
					_playBtn.setText("Play");

					//enable other buttons
					disableOtherBtns(false, _playBtn);
				}

			});

			setOnMouseClicked(e->clearError());
		}
		
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


		@Override
		protected void updateItem(String item, boolean empty) { //changes the order
			super.updateItem(item, empty);

			if (empty || item == null) {
				setGraphic(null);
				_lastItem = null;
			} else {
				String name = _observableVideoNames.get(getListView().getItems().indexOf(item));
				try {

					File queryFile = new File("quiz/"+name+"/query");
					BufferedReader bufferedReaderQuery = new BufferedReader(new FileReader(queryFile)); 
					String query = bufferedReaderQuery.readLine();
					bufferedReaderQuery.close();

					File updatedVideoFile = new File("quiz/"+name+"/"+query+".mp4");
					Media updatedVideo = new Media(updatedVideoFile.toURI().toString());
					_lastItem = updatedVideo;
					MediaPlayer mediaPlayer = new MediaPlayer(updatedVideo);
					_mediaView.setMediaPlayer(mediaPlayer);
				} catch (IOException e) {
					e.printStackTrace();
				}

				_mediaView.setFitHeight(_MEDIAHEIGHT);
				_mediaView.setFitWidth(_MEDIAWIDTH);
				_mediaView.setPreserveRatio(true);
				setGraphic(_hbox);
			}
		}
	}

}
