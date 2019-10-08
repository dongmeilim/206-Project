package application.controllers;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

public class PreviewSave extends Controller implements Initializable {
	
	@FXML private Button _back;
	@FXML private Button _home;
	@FXML private Button _help;
	@FXML private Label _warning;
	@FXML private Slider _timeSlider;
	
	@FXML private Button _play;
	@FXML private Label _time;
	private ImageView _playImage;
	private ImageView _pauseImage;
	@FXML private Slider _volume;
	
	@FXML private MediaView _view;
	private MediaPlayer _player;

	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		String dir = System.getProperty("user.dir");
		List<File> videos = listDirectory("creations"); // TODO mockdata put proper implementation in
		File vid = videos.get(0);
		_player= new MediaPlayer(new Media(vid.toURI().toString()));
		
		_view.setMediaPlayer(_player);
		_player.setCycleCount(MediaPlayer.INDEFINITE);
		_player.setOnEndOfMedia(() -> {
			// Return to start of video when video ends, and stop
			_player.stop();
			_play.setGraphic(_playImage);
		});
		
		_playImage = new ImageView("file:"+dir+"/assets/play.png");
		_playImage.setFitHeight(26);
		_playImage.setFitWidth(26);
		
		_pauseImage= new ImageView("file:"+dir+"/assets/pause.png");
		_pauseImage.setFitHeight(26);
		_pauseImage.setFitWidth(26);
		
		// play button starts with pause icon
		_play.setGraphic(_playImage);
		
	}
	
	@FXML private void handleBack() {switchTo(_back.getScene(), getClass().getResource(_PATH+"ImageFetch.fxml"));}
	
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
	
	@FXML private void handleSave() {
		System.out.println("Under construction");
	}
	// TODO smooth playback
	// When you pause the video - save the time, when you play the video, load the time
	@FXML private void playPause() {
		System.out.println("Under construction");
	}
}
