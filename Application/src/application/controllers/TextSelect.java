package application.controllers;

import application.app.TextToAudio;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.net.URL;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import javafx.application.Platform;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;

import javafx.util.Duration;

/**
 * Controller that handles the TextSelect.fxml view.
 * User steps:
 * 	1. User selects text (words between 1 and 40)
 * 	2a. User previews then saves.
 * 	2b. Or user just saves.
 * 
 * After these steps are completed successfully, the
 * application.app.TextToAudio Task is called.
 */

public class TextSelect extends Controller implements Initializable{

	@FXML private Button _menu;
	@FXML private Button _back;
	@FXML private Button _next;
	@FXML private Button _preview;
	@FXML private Button _save;

	@FXML private VBox _radioButtons;
	@FXML private VBox _rightVBox;
	@FXML private HBox _prevAndAudio;
	@FXML private TextArea _text;
	@FXML private ProgressBar _pb;

	private Button _stop;
	private Slider _audioSlider;
	private Button _play;
	private Media _sound; 
	private MediaPlayer _mediaPlayer; 
	private Label _errorLabel = new Label("Sorry, this text contains a word that can't be pronounced.");
	private Label _overflowLabel = new Label();

	private ToggleGroup _rbGroup;
	private File[] _voices;
	private ArrayList<RadioButton> _rbList;

	private String _dir;
	private String _blueBar = "-fx-accent: #315F83";
	private String _purpleBar = "-fx-accent: #896A89";

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		_dir = System.getProperty("user.dir");
		_errorLabel.setWrapText(true);
		_overflowLabel.setWrapText(true);
		//set up radio buttons for voices
		_rbList = new ArrayList<RadioButton>();
		_rbGroup = new ToggleGroup();
		_voices = new File("/usr/share/festival/voices/english").listFiles();

		//make a button for every voice
		for(File voice:_voices) {
			String name = voice.getName();
			if ( name.equals("kal_diphone")) {
				name = "default man";
			}else if(name.equals("akl_nz_jdt_diphone")) {
				name = "NZ man";
			}
			RadioButton rb = new RadioButton(name);
			rb.setToggleGroup(_rbGroup);
			_rbList.add(rb);
		}
		_radioButtons.getChildren().addAll(_rbList);	
		_rbList.get(0).setSelected(true);

		//set up text area with text for wikit
		String text = "";
		//read the text file
		try (BufferedReader reader = new BufferedReader(new FileReader(new File(_dir+"/tmp/text/wikitext.txt")))) {

			String line;
			while ((line = reader.readLine()) != null) {
				text = text+line+"\n";
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//add the text to the text area
		_text.setWrapText(true);
		_text.setText(text);

		//prevent users from going to next view if there are no saved files
		if (filesAreValid() == true) {
			_next.setDisable(false); //You may progress
		}

		//set up the preview / audio controls area (initialize to just preview button)

		_prevAndAudio.getChildren().clear();
		_prevAndAudio.getChildren().add(_preview);

		_stop = new Button("Stop");
		_play = new Button ("Play");
		_play.setPrefWidth(50);
		_audioSlider = new Slider();
		_audioSlider.setPrefWidth(180);

		_stop.setOnAction(new EventHandler<ActionEvent>() {
			@Override 
			public void handle(ActionEvent e) {
				_mediaPlayer.stop();
				//switch back to just preview button
				_prevAndAudio.getChildren().clear();
				_prevAndAudio.getChildren().add(_preview);
			}
		});

		_play.setOnAction(new EventHandler<ActionEvent>() {
			@Override 
			public void handle(ActionEvent e) {

				if(_mediaPlayer.getStatus()==MediaPlayer.Status.PLAYING) {
					_mediaPlayer.pause();
					_play.setText("play");
				}else {
					
					_mediaPlayer.play();
					_play.setText("||");

				}
			}
		});

	}

	@FXML
	private void handleNext() {
		//stop the sound from playing
		if(_mediaPlayer != null) {
			_mediaPlayer.stop();
			_mediaPlayer.dispose();
		}
		switchTo(_back.getScene(), getClass().getResource(_PATH+"ImageFetch.fxml"));
	}

	@FXML
	private void handleHome() {
		//stop the sound from playing
		if(_mediaPlayer != null) {
			_mediaPlayer.stop();
		}

		boolean decision = displayAlert("Are you leaving?", "Progress will not be saved if you quit to home");
		if (decision == true) {
			if (_mediaPlayer != null) {
				_mediaPlayer.dispose();
			}
			switchTo(_back.getScene(), getClass().getResource(_PATH+"Menu.fxml"));
		} else {
			;
		}
	}

	@FXML
	private void handleBack() {
		//stop the sound from playing
		if(_mediaPlayer != null) {
			_mediaPlayer.stop();
		}

		boolean decision = displayAlert("Are you sure you want to leave?", "Progress will not be saved if you leave");
		if (decision == true) {
			if (_mediaPlayer != null) {
				_mediaPlayer.dispose();
			}
			switchTo(_back.getScene(), getClass().getResource(_PATH+"Search.fxml"));
		} else {
			;
		}
	}

	@FXML
	private void preview() {

		_rightVBox.getChildren().remove(_errorLabel);
		_rightVBox.getChildren().remove(_overflowLabel);
		String text = _text.getSelectedText();
		if (text.trim().length() > 0) {
			//get the user's desired inputs
			
			RadioButton rb = (RadioButton)_rbGroup.getSelectedToggle();
			String voice = getVoiceName(rb.getText());

			// Check that the text is within the word limit
			boolean hasunderOrOverflow = underOverflow(text.trim());
			if (hasunderOrOverflow == true) {
				return;
			}

			//create preview.wav in bg thread
			TextToAudio previewBG = new TextToAudio(text,voice,"preview");
			Thread thread = new Thread(previewBG);
			thread.start();

			previewBG.setOnRunning(running -> {
				_prevAndAudio.getChildren().clear();
				_pb.progressProperty().bind(previewBG.progressProperty());
				_pb.setStyle(_blueBar);
			});
			previewBG.setOnSucceeded(succeed -> {
				_pb.progressProperty().unbind();
				//replace preview button with audio controls
				_play.setText("Play");
				try {
					_prevAndAudio.getChildren().addAll(_stop, _play, _audioSlider);
					_sound = new Media(new File(_dir+"/tmp/audio/preview/preview.wav").toURI().toString());
					_mediaPlayer = new MediaPlayer(_sound);
					_mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
					_mediaPlayer.setOnEndOfMedia(() -> {
						_mediaPlayer.stop();
						_play.setText("Play");
					});

					//set up slider
					//The following code is adapted from https://docs.oracle.com/javase/8/javafx/media-tutorial/playercontrol.htm
					_audioSlider.valueProperty().addListener(new InvalidationListener() {
						@Override
						public void invalidated(Observable ov) {
							if (_audioSlider.isValueChanging()) {
								// multiply duration by percentage calculated by slider position
								_mediaPlayer.seek(_mediaPlayer.getMedia().getDuration().multiply(_audioSlider.getValue() / 100.0));
							}
							updateValues();
						}
					});
					_mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
						@Override
						public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
							updateValues();
						}
					});

				}catch(MediaException e) {
					//switch back to preview button
					_prevAndAudio.getChildren().clear();
					_prevAndAudio.getChildren().addAll(_preview);
					_rightVBox.getChildren().add(_errorLabel);
				}

			});



		} else {
			_overflowLabel.setText("Please select a chunk of text.");
			_rightVBox.getChildren().add(_overflowLabel);
		}

	}
	private String getVoiceName(String voice) {
		if ( voice.equals("default man")) {
			voice = "kal_diphone";
		}else if(voice.equals("NZ man")) {
			voice = "akl_nz_jdt_diphone";
		}
		return voice;
	}
	private boolean underOverflow(String text) {
		String[] words = text.split("\\s+"); //Split on whitespace
		boolean hasAWord=false;
		
		if (text.isEmpty() == true) {
			_overflowLabel.setText("Please select a chunk of text.");
			_rightVBox.getChildren().add(_overflowLabel);
			return true;
		}
		
		
		for (String word: words) {
			boolean isAllLetters = word.chars().allMatch(Character::isLetter);
			if (isAllLetters == true) {
				hasAWord=true;
				break; //Only one word needs to be there
			}
		}
		
		if (hasAWord == true) {
			if (words.length > 40) {
				_overflowLabel.setText("Chunk is too large. Please select a smaller chunk.");
				_rightVBox.getChildren().add(_overflowLabel);
				return true;
			} else if (words.length <= 0) {
				_overflowLabel.setText("Please select a chunk of text.");
				_rightVBox.getChildren().add(_overflowLabel);
				return true;
			} else {
				return false;
			}
		} else {
			_overflowLabel.setText("Selected text contained no words.");
			_rightVBox.getChildren().add(_overflowLabel);
			return true;
		}
	}

	/**
	 * Author: Oracle
	 * Original: https://docs.oracle.com/javase/8/javafx/media-tutorial/playercontrol.htm
	 * Modified by: dongmeilim
	 * */
	protected void updateValues() {

		if (_audioSlider != null) {
			Platform.runLater(new Runnable() {
				public void run() {
					// update the slider to the media player time
					Duration currentTime = _mediaPlayer.getCurrentTime();
					Duration duration =_mediaPlayer.getMedia().getDuration();

					_audioSlider.setDisable(duration.isUnknown());
					if (!_audioSlider.isDisabled() && duration.greaterThan(Duration.ZERO) 
							&& !_audioSlider.isValueChanging()) {
						_audioSlider.setValue(currentTime.divide(duration.toMillis()).toMillis()* 100.0);
					}
				}
			});
		}
	}

	@FXML
	private void saveAudio() {
		_rightVBox.getChildren().remove(_errorLabel);
		_rightVBox.getChildren().remove(_overflowLabel);
		boolean hasunderOrOverflow = underOverflow(_text.getSelectedText().trim());
		if (hasunderOrOverflow == true) {
			return;
		}
		saveDialog(false);
	}

	/**
	 * create a save dialog
	 * @param invalidInput true if the user has input an empty string
	 * */
	private void saveDialog (boolean invalidInput) {
		//stop sound from playing
		if(_mediaPlayer != null) {
			_mediaPlayer.stop();
		}

		//create save pop-up
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Save");
		dialog.setHeaderText("Save file");

		//check if the last input was invalid
		if(!invalidInput) {
			dialog.setContentText("Please enter a new name for your file");
		}else {
			dialog.setContentText("Please enter a valid name for your file");
		}

		//change "ok" button to "save"
		Button okBtn = (Button)dialog.getDialogPane().lookupButton(ButtonType.OK);
		okBtn.setText("Save");

		Optional<String> result = dialog.showAndWait();

		if (result.isPresent()){
			String name = result.get();
			File file = new File(_dir+"/tmp/audio/"+name+".wav");

			if (name.isEmpty()|| name.contains(".")) {
				//show another dialog if empty input
				saveDialog(true);
			} else if ((Pattern.matches("^[a-zA-Z0-9_-]+$", name) == false)) {
				//show another dialog if there are invalid characters
				saveDialog(true);
			}else if(file.exists()){
				//show overwrite dialogue
				overwriteDialog(file, name);
			}else {
				//save the file
				saveInBG(name);
				//next button is undisabled onSuccess
			}
		}

	}
	/**
	 * create a dialog for overwriting audio files
	 * */
	private void overwriteDialog(File file, String name) {
		boolean decision = displayAlert("This file already exists", "Would you like to overwrite it?");
		if (decision == true) {
			file.delete();
			saveInBG(name);
		}
	}

	/**
	 * Save the selected text as an audio file
	 * */
	private void saveInBG( String name ) {
		
		String text = _text.getSelectedText();
		if (text.trim().length() > 0) {
			//get the user's desired inputs
			RadioButton rb = (RadioButton)_rbGroup.getSelectedToggle();
			String voice = getVoiceName(rb.getText());

			//create preview.wav in bg thread
			TextToAudio saveBG = new TextToAudio(text,voice, name);
			Thread thread = new Thread(saveBG);
			thread.start();

			saveBG.setOnRunning(running -> {
				_save.setDisable(true);
				// bind the progress bar the the preview task
				_pb.progressProperty().bind(saveBG.progressProperty());	
				_pb.setStyle(_purpleBar);
			});

			saveBG.setOnSucceeded(succeed -> {
				_save.setDisable(false);
				_pb.progressProperty().unbind();

				// Check that the audio was created properly
				File file = new File(_dir+"/tmp/audio/"+name+".wav");
				try {
				/* 	Notes: certain Festival voices will create empty wav files if certain words are used.
					The bash command will still finish without error, but the wav file cannot be used.
					To check if this has happened, a Media object has been made with the said wav file.
					If the wav file has no errors, the Media object is created, otherwise it will throw a
					MediaException.
					The Media object itself is not important as we only want to know if it throws an exception 
					or not, hence it is never used. */

					@SuppressWarnings("unused")
					Media testForError = new Media(file.toURI().toString());
				}catch (MediaException e) {
					// display an error message and remove the erroneous wav file.
					_rightVBox.getChildren().add(_errorLabel);
					file.delete();
					if (!filesAreValid()) {
						_next.setDisable(true);
					}
				}

				List<File> audioClips = listDirectory("tmp/audio");
				int numberOfAudioClips = audioClips.size();
				if (_next.isDisabled() && numberOfAudioClips >= 1) {

					if (filesAreValid()) {
						_next.setDisable(false);
					}
				}
			});	
		}

	}

	/**
	 * Returns true if there is at least one valid wav file in tmp/audio, false otherwise
	 * */
	private boolean filesAreValid() {
		String[] existingFiles = new File (_dir+"/tmp/audio").list();

		// Check for invalid files
		int phantomCount = 0;
		for (String filename: existingFiles) {
			if (filename.contains(".__")) {
				phantomCount++;
			}
		}
		if( existingFiles.length - phantomCount <= 1) {
			return false;
		}
		return true;
	}

}
