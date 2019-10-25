package application.controllers;

import java.io.File;

import java.net.URL;

import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import application.app.SaveVideo;

import javafx.application.Platform;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import javafx.stage.Stage;

import javafx.util.Duration;

/**
 * Controller that handles PreviewSave.fxml
 *
 * The goal of this window is to Preview the user-created video and 
 * give the user the option to save said video.
 * 
 * This is the last step of the Creation Cycle.
 */

public class PreviewSave extends Controller implements Initializable {
	
	@FXML private AnchorPane _anchor;

	@FXML private Button _back;
	@FXML private Button _home;
	@FXML private Button _anchorHelp;
	@FXML private Button _help;
	@FXML private Button _save;
	
	@FXML private Label _warning;
	
	@FXML private Slider _timeSlider;
	
	@FXML private TextField _field;
	
	@FXML private ProgressBar _progress;
	
	//Media controls
	@FXML private Button _play;
	@FXML private Label _time;
	private ImageView _playImage;
	private ImageView _pauseImage;
	@FXML private Slider _volume;
	
	//Media view
	@FXML private MediaView _view;
	private MediaPlayer _player;
	private final String _VIDPATH = "tmp/video/finalVideo.mp4";
	
	private boolean _videoHasBeenMade = false;
	
	private final String _ORANGE = "#eb7900";
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		File vid = new File(_VIDPATH);
		_player= new MediaPlayer(new Media(vid.toURI().toString()));
		
		_view.setMediaPlayer(_player);
		_player.setCycleCount(MediaPlayer.INDEFINITE);
		_player.setOnEndOfMedia(() -> {
			//Return to start of video when video ends, and stop
			_player.stop();
			_play.setGraphic(_playImage);
		});
		
		_playImage = new ImageView("/play.png");
		_playImage.setFitHeight(26);
		_playImage.setFitWidth(26);
		
		_pauseImage= new ImageView("/pause.png");
		_pauseImage.setFitHeight(26);
		_pauseImage.setFitWidth(26);
		
		_play.setGraphic(_pauseImage);
		_player.play();
		
		/**
		 * Setting up listeners for _timeSlider and _volume.
		 * 
		 * @author: Oracle
		 * Original: https://docs.oracle.com/javase/8/javafx/media-tutorial/playercontrol.htm
		 * Modified by: dongmeilim
		 */
		_timeSlider.valueProperty().addListener(new InvalidationListener() {
			@Override
			public void invalidated(Observable ov) {
				if (_timeSlider.isValueChanging()) {
					//Multiply duration by percentage calculated by slider position
					_player.seek(_player.getMedia().getDuration().multiply(_timeSlider.getValue() / 100.0));
				}
				updateValues();
			}
		});
		
		_player.currentTimeProperty().addListener(new ChangeListener<Duration>() {

			@Override
			public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
				updateValues();
			}
		});

		_volume.valueProperty().addListener(new InvalidationListener() {
			public void invalidated(Observable ov) {
				if (_volume.isValueChanging()) {
					_player.setVolume(_volume.getValue() / 100.0);
				}
			}
		});	
	}
	
	@FXML 
	private void handleBack() {
		_player.stop();
		_player.dispose(); //Release the video
		switchTo(_back.getScene(), getClass().getResource(_PATH+"ImageFetch.fxml"));
		}
	
	@FXML 
	public void handleHome() {
		if (_videoHasBeenMade == true) {
			_player.stop();
			_player.dispose(); //Release the video
			toMenu(_home.getScene());
		} else {
			boolean decision = displayAlert("Are you leaving?", "Progress will not be saved if you quit to home");
			if (decision == true) {
				_player.stop();
				_player.dispose(); //Release the video
				toMenu(_home.getScene());
			}
		}
	}
	
	@FXML 
	private void handleHelp() {
		if (_anchor.isVisible()==false) { //AnchorPane is invisible on startup	
			_home.setDisable(true);
			_back.setDisable(true);
			_save.setDisable(true);
			_play.setDisable(true);
			_player.pause();
			_play.setGraphic(_playImage);
			
			_anchor.setVisible(true);
		} else {
			_home.setDisable(false);
			_back.setDisable(false);
			_save.setDisable(false);
			_play.setDisable(false);
			
			_anchor.setVisible(false);
		}
	}
	
	@FXML 
	private void handleSave() {
		_save.setDisable(true);
		_field.setDisable(true);
		String fileName = _field.getText().trim();
		boolean canSave = checkTextIsCorrect(fileName);
		if (canSave == true) {
			saveVideo(fileName);
		} else {
			_save.setDisable(false);
			_field.setDisable(false);
		}
	}
	
	/**
	 * Error-correction method when saving name for a video.
	 * @param fileName
	 * @return
	 */
	private boolean checkTextIsCorrect(String fileName) {
		List<File> creationList = listDirectory("creations");
		boolean sameFile = false;
		boolean canOverwrite;
		
		if ( fileName.isEmpty() == false) {
			if (Pattern.matches("^[a-zA-Z0-9_-]+$", fileName)) {
				
				for (File comparedFile: creationList) {
					//Get filename without .mp4 extension
					String comparedFileName = comparedFile.getName().substring(0,comparedFile.getName().length()-4);
					if (fileName.equals(comparedFileName) == true) {
						sameFile = true;
						break;
					}
				}
				
				if (sameFile == true) {
					canOverwrite = displayAlert("File already exists!","Are you sure you want to overwrite?");
					if (canOverwrite == true) {
						_warning.setText("");
						return true; //Overwriting file
					} else {
						_field.clear();
						_warning.setText("");
						return false; //Not overwriting file
					}
				} else {
					_warning.setText("");
					return true; //Save valid file
				}
			} else {
				_field.clear();
				_warning.setText("Video contains invalid characters");
				return false;
			}
		} else {
			_field.clear();
			_warning.setText("Please enter a name");
			return false;
		}
	}
	
	/**
	 * Tell SaveVideo to save the video.
	 * @param fileName
	 */
	private void saveVideo(String fileName) {
		SaveVideo saveVideo = new SaveVideo(fileName);
		Thread thread = new Thread(saveVideo);
		thread.start();
		_progress.progressProperty().bind(saveVideo.progressProperty()); //Listen to the thread
		_progress.setStyle("-fx-accent: " + _ORANGE);
		
		saveVideo.setOnSucceeded(e-> {
			_videoHasBeenMade = true;
			_progress.progressProperty().unbind();
			_warning.setText("Video created! You can now go Home or go back and make another version!");
			_home.requestFocus(); //Tell users to go home, job done
		});
	}
	
	/**
	 * Update the duration values.
	 */
	private void updateValues() {
		if (_time != null && _timeSlider != null && _volume != null) {
			Platform.runLater(new Runnable() {
				public void run() {
					Duration currentTime = _player.getCurrentTime();
					Duration duration =_player.getMedia().getDuration();

					_time.setText(formatTime(currentTime, duration));
					_timeSlider.setDisable(duration.isUnknown());
					if (!_timeSlider.isDisabled() 
							&& duration.greaterThan(Duration.ZERO) 
							&& !_timeSlider.isValueChanging()) {
						_timeSlider.setValue(currentTime.divide(duration.toMillis()).toMillis()* 100.0);
					}
					if (!_volume.isValueChanging()) {
						_volume.setValue((int)Math.round(_player.getVolume()* 100));
					}
				}
			});
		}
	}

	@FXML
	private void playPause() {
		if(_player.getStatus()== MediaPlayer.Status.PLAYING) {
			_player.pause();
			_play.setGraphic(_playImage);
		}else {
			_player.play();
			_play.setGraphic(_pauseImage);
		}
	}
	
	/**
	 * Set up the duration display.
	 * 
	 * Author: Oracle
	 * Original: https://docs.oracle.com/javase/8/javafx/media-tutorial/playercontrol.htm
	 * */
	private static String formatTime(Duration elapsed, Duration duration) {
		int intElapsed = (int)Math.floor(elapsed.toSeconds());
		int elapsedHours = intElapsed / (60 * 60);
		if (elapsedHours > 0) {
			intElapsed -= elapsedHours * 60 * 60;
		}
		int elapsedMinutes = intElapsed / 60;
		int elapsedSeconds = intElapsed - elapsedHours * 60 * 60 - elapsedMinutes * 60;

		if (duration.greaterThan(Duration.ZERO)) {
			int intDuration = (int)Math.floor(duration.toSeconds());
			int durationHours = intDuration / (60 * 60);
			if (durationHours > 0) {
				intDuration -= durationHours * 60 * 60;
			}
			int durationMinutes = intDuration / 60;
			int durationSeconds = intDuration - durationHours * 60 * 60 - durationMinutes * 60;
			if (durationHours > 0) {
				return String.format("%d:%02d:%02d/%d:%02d:%02d", 
						elapsedHours, elapsedMinutes, elapsedSeconds, durationHours, durationMinutes, durationSeconds);
			} else {
				return String.format("%02d:%02d/%02d:%02d",
						elapsedMinutes, elapsedSeconds,durationMinutes, durationSeconds);
			}
		} else {
			if (elapsedHours > 0) {
				return String.format("%d:%02d:%02d", elapsedHours, elapsedMinutes, elapsedSeconds);
			} else {
				return String.format("%02d:%02d",elapsedMinutes, elapsedSeconds);
			}
		}
	}
	
	/**
	 * Public setter called in the Controller superclass.
	 * Called by method loadPreviewSave.
	 * 
	 * Releases the video so that the tmp file may be deleted.
	 */
	public void setDisposableMedia() {
		Stage stage = getStage(_back.getScene());
		stage.setOnHiding(e-> {
			if (_player != null) {
				_player.stop();
				_player.dispose();
			}
		});
	}
	
}
