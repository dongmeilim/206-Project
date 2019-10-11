package application.controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Callback;


/**
 * Controller that handles the AudioMatch.fxml view
 */

public class AudioMatch extends Controller implements Initializable{
	
	@FXML private Button _back;
	@FXML private Button _home;
	@FXML private Button _help;
	@FXML private Button _match;
	@FXML private Label _errorLabel;

	@FXML private ListView<File> _audio;
	@FXML private ListView <String> _terms;
	
	private int _numQuestions;
	private  MediaPlayer _mp;
	private ArrayList<Button> _playBtns = new ArrayList<Button>();
	
	private ArrayList<File> _allFiles;
	private ObservableList<File> _audioList = FXCollections.observableArrayList();
	private ObservableList<String> _queryList = FXCollections.observableArrayList();
	
	private ArrayList<File> _guessedAudio = new ArrayList<File>();
	private ArrayList<String> _guessedQuery = new ArrayList<String>();
	private int[] _audioScores;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// Get the number of questions from a file
		File file = new File(System.getProperty("user.dir")+"/tmp/text/numQuestions.txt"); 
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(file));
			_numQuestions = Integer.parseInt(br.readLine()) ;
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		_audioScores = new int[_numQuestions];
		for (int i = 0; i<_audioScores.length;i++) {
		System.out.println(_audioScores[i]);
		
			
		}
		//populate the audio list
		File[] files = new File("quiz").listFiles(File::isDirectory);
		_allFiles = new ArrayList<File>(Arrays.asList(files));
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
		
		//add buttons to the audio list
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
		File selAudio = _audio.getSelectionModel().getSelectedItem();
		String selQuery = _terms.getSelectionModel().getSelectedItem();
		
		if(selAudio!=null && selQuery != null) {
			String audioName = selAudio.getName();
			audioName = audioName.substring(0, audioName.length()-4);
			
			if(audioName.equalsIgnoreCase(selQuery)) {
				_guessedAudio.add(selAudio);
				_guessedQuery.add(selQuery);

				_audioList.remove(selAudio);
				_queryList.remove(selQuery);
				_errorLabel.setText("Good Job!!!");
			}else {
				_errorLabel.setText("Uh-oh! Try again!");
			}

			
			_audio.getSelectionModel().clearSelection();
			_terms.getSelectionModel().clearSelection();
		}
		

		
		if (_audioList.size()==0) {
			//TODO move to scoring window and calculate scores
		}
	}
	
	private void generateRandomAudioFiles() throws NumberFormatException, IOException {
		Random rand = new Random();
		File audio;
		File queryFile;
		String query;

		for (int i = 0; i < _numQuestions; i++) { 
			//generate random file from list of files
            int index = rand.nextInt(_allFiles.size()); 
            queryFile = new File("quiz/"+_allFiles.get(index).getName()+"/query");
            
            //get the term for that file
        	BufferedReader br = new BufferedReader(new FileReader(queryFile)); 
    		query = br.readLine();
    		br.close();
            //get the audio for that file
            audio = new File("quiz/"+_allFiles.get(index).getName()+"/"+query+".wav");
          
            //store audio and query in list
            _audioList.add(audio); 
            _queryList.add(query);
            
            _allFiles.remove(index); 
        } 
		
	}
	
	/**
	 * This class contains adapted code.
	 * Source: https://stackoverflow.com/questions/15661500/javafx-listview-item-with-an-image-button
	 * @author Rainer Schwarze
	 * Modified : dongmeilim
	 */
    public class ButtonCell extends ListCell<File> {
        HBox hbox = new HBox();
        Label label = new Label("(empty)");
        Pane pane = new Pane();
        Button playBtn = new Button("Play");
        File lastItem;

        public ButtonCell() {
            super();
            hbox.getChildren().addAll(label, pane, playBtn);
            HBox.setHgrow(pane, Priority.ALWAYS);
            _playBtns.add(playBtn);
            
            playBtn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    
					// Get the Audio in the same row as the button
					File audio = lastItem;
					Media media = new Media(audio.toURI().toString());
					//Play or top the selected audio
					if(_mp == null || !(_mp.getStatus()== MediaPlayer.Status.PLAYING)) {

						//set up a new media player
						_mp = new MediaPlayer(media);
						_mp.setOnEndOfMedia(() -> {
							_mp.stop();
							_mp.dispose();
							playBtn.setText("Play");

							//enable other buttons
							for (Button button : _playBtns) {
								button.setDisable(false);
							}
						});
						_mp.play();
						playBtn.setText("Stop");

						//Disable other buttons
						for (Button button : _playBtns) {
							if(button != playBtn) {
								button.setDisable(true);
							}
						}

					}else {
						//stop playing
						_mp.stop();
						_mp.dispose();
						playBtn.setText("Play");

						//enable other buttons
						for (Button button : _playBtns) {
							button.setDisable(false);
						}
					}
                }
            });
        }

        @Override
        protected void updateItem(File item, boolean empty) {
            super.updateItem(item, empty);
            setText(null);  // No text in label of super class
            if (empty) {
                lastItem = null;
                setGraphic(null);
            } else {
                lastItem = item;
                label.setText("Question "+ (getIndex()+1));
                label.setStyle("-fx-text-fill: black; -fx-effect: null;");
                setGraphic(hbox);
            }
        }
    }


	
	//private void setAudio() {
		//TODO implement setter
	//}
	
	
	
}
