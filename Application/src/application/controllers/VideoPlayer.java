package application.controllers;

import java.io.File;

import java.net.URL;

import java.util.ResourceBundle;

import javafx.application.Platform;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import javafx.util.Duration;

/**
 * Controller that handles the VideoPlayer.fxml view.
 * Plays the video with JavaFX MediaPlayer while also
 * providing some media controls.
 */

public class VideoPlayer extends Controller implements Initializable{
	@FXML private MediaView _player;
	@FXML private Button _back;
	@FXML private Button _play;
	@FXML private Slider _timeSlider;
	@FXML private Slider _volume;
	@FXML private Label _time;
	@FXML private Label _title;

	private MediaPlayer _mediaPlayer;
	private ImageView _playImage;
	private ImageView _pauseImage;
	private String _vidTitle;
	
	/**
	 * VideoPlayer must be initialised before being set as a controller to an fxml file
	 * so that the video to be played can be passed in from another controller
	 * @param vid the video to play
	 */
	public VideoPlayer(File vid) {
		_mediaPlayer = new MediaPlayer(new Media(vid.toURI().toString()));
		_vidTitle = vid.getName();
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		_title.setText(_vidTitle);
		
		//set up the play icon
		_playImage = new ImageView("/play.png");
		_playImage.setFitHeight(26);
		_playImage.setFitWidth(26);
		
		// set up the pause icon
		_pauseImage= new ImageView("/pause.png");
		_pauseImage.setFitHeight(26);
		_pauseImage.setFitWidth(26);
		
		// play button starts with pause icon
		_play.setGraphic(_pauseImage);
		
		//set up the media player
		_player.setMediaPlayer(_mediaPlayer);
		_mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
		_mediaPlayer.setOnEndOfMedia(() -> {
			// Return to start of video when video ends, and stop
			_mediaPlayer.stop();
			_play.setGraphic(_playImage);
		});
		
		_mediaPlayer.play();

		/**
		 * The following code is adapted from Oracle:
		 * Author: Oracle
		 * Original: https://docs.oracle.com/javase/8/javafx/media-tutorial/playercontrol.htm
		 * Modified by: dongmeilim
		 */
		_timeSlider.valueProperty().addListener(new InvalidationListener() {
			@Override
			public void invalidated(Observable ov) {
				if (_timeSlider.isValueChanging()) {
					// multiply duration by percentage calculated by slider position
					_mediaPlayer.seek(_mediaPlayer.getMedia().getDuration().multiply(_timeSlider.getValue() / 100.0));
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

		_volume.valueProperty().addListener(new InvalidationListener() {
			public void invalidated(Observable ov) {
				if (_volume.isValueChanging()) {
					_mediaPlayer.setVolume(_volume.getValue() / 100.0);
				}
			}
		});
	}

	@FXML
	private void handleBack(){
		_mediaPlayer.stop();
		_mediaPlayer.dispose(); // release the video
		switchTo(_back.getScene(), getClass().getResource(_PATH+"VideoList.fxml"));
	}
	
	/**
	 * Author: Oracle
	 * Original: https://docs.oracle.com/javase/8/javafx/media-tutorial/playercontrol.htm
	 * Modified by: dongmeilim
	 * */
	protected void updateValues() {
		if (_time != null && _timeSlider != null && _volume != null) {
			Platform.runLater(new Runnable() {
				public void run() {
					Duration currentTime = _mediaPlayer.getCurrentTime();
					Duration duration =_mediaPlayer.getMedia().getDuration();

					_time.setText(formatTime(currentTime, duration));
					_timeSlider.setDisable(duration.isUnknown());
					if (!_timeSlider.isDisabled() 
							&& duration.greaterThan(Duration.ZERO) 
							&& !_timeSlider.isValueChanging()) {
						_timeSlider.setValue(currentTime.divide(duration.toMillis()).toMillis()* 100.0);
					}
					if (!_volume.isValueChanging()) {
						_volume.setValue((int)Math.round(_mediaPlayer.getVolume()* 100));
					}
				}
			});
		}
	}

	@FXML
	private void playPause() {
		
		if(_mediaPlayer.getStatus()== MediaPlayer.Status.PLAYING) {
			_mediaPlayer.pause();
			_play.setGraphic(_playImage);
		}else {
			_mediaPlayer.play();
			_play.setGraphic(_pauseImage);
		}
	}
	
	/**
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
}