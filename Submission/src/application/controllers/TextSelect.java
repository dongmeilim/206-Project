package application.controllers;

import application.app.TextToAudio;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.net.URL;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.util.Callback;

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

	@FXML private AnchorPane _anchor;
	
	@FXML private Button _menu;
	@FXML private Button _back;
	@FXML private Button _next;
	@FXML private Button _help;
	@FXML private Button _preview;
	@FXML private Button _save;
	@FXML private Button _reset;

	@FXML private VBox _radioButtons;
	@FXML private VBox _rightVBox;
	@FXML private HBox _prevAndAudio;
	@FXML private TextArea _text;
	@FXML private ProgressBar _pb;

	@FXML private TableView<File> _table;
	@FXML private TableColumn<File, String> _audioCol;
	@FXML private TableColumn<File, Void> _playCol;
	@FXML private TableColumn<File, Void> _deleteCol;
	private final ObservableList<File> _fileList= FXCollections.observableArrayList();;

	private Button _stop;
	private Button _play;
	private Media _sound; 
	private MediaPlayer _mediaPlayer;
	private MediaPlayer _savedPlayer; 
	private ArrayList<Button> _playBtns = new ArrayList<Button>();
	private ArrayList<Button> _delBtns = new ArrayList<Button>();

	private boolean _nextButtonIsEnabled = false;
	
	private Label _errorLabel = new Label("Sorry, this text contains a word that can't be pronounced.");
	private Label _overflowLabel = new Label();

	private ToggleGroup _rbGroup;
	private File[] _voices;
	private ArrayList<RadioButton> _rbList;

	private String _dir;
	private String _blueBar = "-fx-accent: #5c91b0";
	private String _saveBar = "-fx-accent: #eb7900";
	private String _pausedText = "";
	
	private final double _MAXDURATION = 300;

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
				name = "Man";
			}else if(name.equals("akl_nz_jdt_diphone")) {
				name = "NZ Man";
			}
			RadioButton rb = new RadioButton(name);
			rb.setToggleGroup(_rbGroup);
			_rbList.add(rb);
		}
		_radioButtons.getChildren().addAll(_rbList);	
		_rbList.get(0).setSelected(true);

		//set up text area with text for wikit
		resetText();

		//prevent users from going to next view if there are no saved files
		if (filesAreValid() == true) {
			_next.setDisable(false); //You may progress
		}

		//set up the preview / audio controls area (initialize to just preview button)

		_prevAndAudio.getChildren().clear();
		_prevAndAudio.getChildren().add(_preview);

		_stop = new Button("Stop");
		_stop.setPrefWidth(80);
		_stop.setPrefHeight(40);
		_play = new Button ("Play");
		_play.setPrefWidth(80);
		_play.setPrefHeight(40);

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
		updateFileList();
		_table.setSelectionModel(null);
		_table.setItems(_fileList);
		_audioCol.setCellValueFactory(new PropertyValueFactory<>("name"));
		addPlayButtonToTable();
		addDeleteButtonToTable();

	}

	/**
	 * Refreshes the list of files
	 */
	private void updateFileList() {
		List<String> fileNamesToStore = new ArrayList<String>();
		List<File> files = listDirectory("tmp/audio");
		// Record all the files that are invalid in another list then remove them all at once.
		// This is to avoid the ConcurrentModificationException when using the for-loop.
		List<File> toRemove = new ArrayList<File>();		
		for (File file : files) {
			if (file.getName().contains(".__")) {
				toRemove.add(file);
			}
		}
		files.removeAll(toRemove);

		File[] arrayOfFiles = new File[files.size()];
		files.toArray(arrayOfFiles);

		for (int i = 0; i < arrayOfFiles.length; i++) {
			fileNamesToStore.add(arrayOfFiles[i].getName()); //Storing in file for ffmpeg video
		}
		if (fileNamesToStore.isEmpty() == false) {
			storeFileType("audio",fileNamesToStore); //Null check
		}
		_fileList.clear();
		_fileList.addAll(arrayOfFiles);
	}

	@FXML
	private void handleNext() {
		//stop the sound from playing
		if(_mediaPlayer != null) {
			_mediaPlayer.stop();
			_mediaPlayer.dispose();
		}

		boolean isAudioTooLong = isAudioTooLong();
		
		if (isAudioTooLong == true) {
			_errorLabel.setText("Combined audio is too long, please delete some audio clips");
			if (_rightVBox.getChildren().contains(_errorLabel) == false) {
				_rightVBox.getChildren().add(_errorLabel);
			}
		} else {
			if (_rightVBox.getChildren().contains(_errorLabel) == true) {
				_rightVBox.getChildren().remove(_errorLabel);
			}
			switchTo(_next.getScene(), getClass().getResource(_PATH+"ImageFetch.fxml"));
		}
	}
	
	private boolean isAudioTooLong() {
		List<File> files = listDirectory("tmp/audio");
		float durationInSeconds = 0;
		for (File file: files) {
			try {
				AudioInputStream audioInputStream;
				audioInputStream = AudioSystem.getAudioInputStream(file);
				AudioFormat format = audioInputStream.getFormat();
				long audioFileLength = file.length();
				int frameSize = format.getFrameSize();
				float frameRate = format.getFrameRate();
				durationInSeconds = durationInSeconds + (audioFileLength / (frameSize * frameRate));
			} catch (UnsupportedAudioFileException e) {
				e.printStackTrace();
			} catch (EOFException e) {
				// TODO fix EOFException
			} catch (IOException e) {
				//TODO fix RIFFInvalidDataException
			}
		}
		if (durationInSeconds > _MAXDURATION) { //Duration of the background music
			return true;
		} else {
			return false;
		}
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
	private void handleHelp() {
		if (_anchor.isVisible()==false) { //AnchorPane is invisible on startup
			_pausedText = _text.getText();
			_text.clear();
			
			_menu.setDisable(true);
			_back.setDisable(true);
			_prevAndAudio.setDisable(true);
			_reset.setDisable(true);
			_save.setDisable(true);
			if (_nextButtonIsEnabled == true) {
				_next.setDisable(true);
			}
			_radioButtons.setDisable(true);
			_text.setDisable(true);
			_table.setDisable(true);
			
			_anchor.setVisible(true);
		} else {
			_text.setText(_pausedText);
			
			_menu.setDisable(false);
			_back.setDisable(false);
			_prevAndAudio.setDisable(false);
			_reset.setDisable(false);
			_save.setDisable(false);
			if (_nextButtonIsEnabled == true) {
				_next.setDisable(false);
			}
			_radioButtons.setDisable(false);
			_text.setDisable(false);
			_table.setDisable(false);
			
			_anchor.setVisible(false);
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
					_prevAndAudio.getChildren().addAll(_stop, _play);
					_sound = new Media(new File(_dir+"/tmp/audio/preview/preview.wav").toURI().toString());
					_mediaPlayer = new MediaPlayer(_sound);
					_mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
					_mediaPlayer.setOnEndOfMedia(() -> {
						_mediaPlayer.stop();
						_play.setText("Play");
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

	@FXML
	private void resetText() {
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
	}

	private String getVoiceName(String voice) {
		if ( voice.equals("Man")) {
			voice = "kal_diphone";
		}else if(voice.equals("NZ Man")) {
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

	@FXML
	private void saveAudio() throws IOException {
		_rightVBox.getChildren().remove(_errorLabel);
		_rightVBox.getChildren().remove(_overflowLabel);
		boolean hasunderOrOverflow = underOverflow(_text.getSelectedText().trim());
		if (hasunderOrOverflow == true) {
			return;
		}

		File file = new File(System.getProperty("user.dir")+"/tmp/text/audioCount"); 
		BufferedReader br = new BufferedReader(new FileReader(file)); 
		int fileNum = Integer.parseInt(br.readLine()) + 1 ;
		br.close();

		// update the number of audio files that have been created

		BufferedWriter writer = new BufferedWriter(new FileWriter(_dir + "/tmp/text/audioCount"));
		writer.write(fileNum+"");	
		writer.close();

		file = new File(System.getProperty("user.dir")+"/tmp/text/query"); 
		br = new BufferedReader(new FileReader(file));
		char[] whiteSpaceCheck = br.readLine().toCharArray();
		for (int i = 0; i < whiteSpaceCheck.length; i++) {
			if (whiteSpaceCheck[i] == ' ') {
				whiteSpaceCheck[i] = '_';
			}
		}
		
		String name = new String(whiteSpaceCheck);
		name = name+fileNum; 
		
		br.close();

		saveInBG(name);

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
				_next.setDisable(true);
				// bind the progress bar the the preview task
				_pb.progressProperty().bind(saveBG.progressProperty());	
				_pb.setStyle(_saveBar);
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
					new File(_dir+"/tmp/audio/censored/"+name+".wav").delete(); //delete censored audio
					
					if (!filesAreValid()) {
						_next.setDisable(true);
					}
				}
				updateFileList();
				List<File> audioClips = listDirectory("tmp/audio");
				int numberOfAudioClips = audioClips.size();
				if (_next.isDisabled() && numberOfAudioClips >= 1) {

					if (filesAreValid()) {
						_nextButtonIsEnabled = true;
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
		if( existingFiles.length - phantomCount <= 2) {
			return false;
		}
		return true;
	}
	/**
	 * Author: Rip Tutorial
	 * Original: https://riptutorial.com/javafx/example/27946/add-button-to-tableview
	 * Modified: dongmeilim
	 * Comments: dongmeilim
	 */
	private void addPlayButtonToTable() {

		Callback<TableColumn<File, Void>, TableCell<File, Void>> cellFactory = new Callback<TableColumn<File, Void>, TableCell<File, Void>>() {
			@Override
			public TableCell<File, Void> call(final TableColumn<File, Void> param) {
				final TableCell<File, Void> cell = new TableCell<File, Void>() {
					// set up the play buttons
					private final Button playBtn = new Button("Play");


					{
						_playBtns.add(playBtn);
						playBtn.setOnAction((ActionEvent event) -> {
							// Get the Audio in the same row as the button
							File audio = getTableView().getItems().get(getIndex());
							Media media = new Media(audio.toURI().toString());
							//Play or top the selected audio
							if(_savedPlayer == null || !(_savedPlayer.getStatus()== MediaPlayer.Status.PLAYING)) {

								//set up a new media player
								_savedPlayer = new MediaPlayer(media);
								_savedPlayer.setOnEndOfMedia(() -> {
									_savedPlayer.stop();
									_savedPlayer.dispose();
									playBtn.setText("Play");

									//enable other buttons
									for (Button button : _playBtns) {
										button.setDisable(false);
									}
									for (Button button: _delBtns) {
										button.setDisable(false);
									}
								});
								_savedPlayer.play();
								playBtn.setText("Stop");

								//diasble other buttons
								for (Button button : _playBtns) {
									if(button != playBtn) {
										button.setDisable(true);
									}
								}
								for (Button button: _delBtns) {
									button.setDisable(true);
								}

							}else {
								//stop playing
								_savedPlayer.stop();
								_savedPlayer.dispose();
								playBtn.setText("Play");

								//enable other buttons
								for (Button button : _playBtns) {
									button.setDisable(false);
								}
								for (Button button: _delBtns) {
									button.setDisable(false);
								}
							}
						});
					}

					@Override
					public void updateItem(Void item, boolean empty) {
						// insert the play buttons into the table column
						super.updateItem(item, empty);
						if (empty) {
							setGraphic(null);
						} else {
							setGraphic(playBtn);
						}
					}
				};
				return cell;
			}
		};

		_playCol.setCellFactory(cellFactory);

	}

	/**
	 * Author: Rip Tutorial
	 * Original: https://riptutorial.com/javafx/example/27946/add-button-to-tableview
	 * Modified: dongmeilim
	 * Comments: dongmeilim
	 */
	private void addDeleteButtonToTable() {
		Callback<TableColumn<File, Void>, TableCell<File, Void>> cellFactory = new Callback<TableColumn<File, Void>, TableCell<File, Void>>() {
			@Override
			public TableCell<File, Void> call(final TableColumn<File, Void> param) {
				final TableCell<File, Void> cell = new TableCell<File, Void>() {
					// set up the delete button
					private final Button delBtn = new Button("Delete");

					{
						_delBtns.add(delBtn);
						delBtn.setOnAction((ActionEvent event) -> {
							// get the file in the same row as the button
							File file = getTableView().getItems().get(getIndex());

							// Confirm the user wants to delete the file
							Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
							alert.setHeaderText("Are you sure you want to delete "+ file.getName()+"?");
							Optional<ButtonType> result = alert.showAndWait();
							if (result.get() == ButtonType.OK){
								if(file.exists()) {
									//delete the video
									file.delete();
									updateFileList();
								}
								//delete the censored audio file
								new File("tmp/audio/censored/"+file.getName()).delete();

							}

						});
					}

					@Override
					public void updateItem(Void item, boolean empty) {
						// insert delete buttons into the table column
						super.updateItem(item, empty);
						if (empty) {
							setGraphic(null);
						} else {
							setGraphic(delBtn);
						}
					}
				};
				return cell;
			}
		};

		_deleteCol.setCellFactory(cellFactory);

	}

}
