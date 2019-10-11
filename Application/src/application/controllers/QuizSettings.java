package application.controllers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;

/**
 * Controller that handles the ImageFetch.fxml view.
 * After user selects amount of images with slider,
 * the application.app.DownloadImages Task is called.
 */

public class QuizSettings extends Controller implements Initializable {
	
	@FXML private Button _back;
	@FXML private Button _home;
	@FXML private Button _help;
	@FXML private Button _next;
	@FXML private Label _questionAmountDisplay;
	@FXML private Label _warning;
	@FXML private ProgressBar _progress;
	@FXML private RadioButton _audioRadio;
	@FXML private RadioButton _imageRadio;
	@FXML private RadioButton _bothRadio;
	@FXML private Slider _slider;
	@FXML private ToggleGroup _mode;
	
	@FXML private void handleBack() {handleHome();}
	@FXML public void handleHome() {toMenu(_home.getScene());}
	
	@FXML private void handleHelp() {
		System.out.println("Under construction");
	}
	//TODO bind progress bar to something
	@FXML private void handleNext() throws IOException {
		
		//get the number of questions
		int numQuestions = (int)_slider.getValue();
		String dir = System.getProperty("user.dir");
		BufferedWriter writer = new BufferedWriter(new FileWriter(dir + "/tmp/text/numQuestions.txt"));
		writer.write(numQuestions + "");	
		writer.close();
		
		Toggle selectedMode = _mode.getSelectedToggle();
		String game; //Will never end up being undefined
		if (selectedMode.equals(_audioRadio)) {
			game="AudioMatch.fxml";
		} else if (selectedMode.equals(_imageRadio)) {
			game="ImageMatch.fxml";
		} else {
			System.out.println("Video quiz");
			game="VideoMatch.fxml";
		}
		switchTo(_next.getScene(),getClass().getResource(_PATH+game));
		
		
		
	}
	
	@FXML
	private void handleValueChange() {
		double value = _slider.getValue();
		String stringValue = Double.toString(value);
		//Trim off the .0
		stringValue = stringValue.substring(0,stringValue.length()-2);
		if (value == 1) {
			_questionAmountDisplay.setText(stringValue + " question");  // 1 question
		} else {
			_questionAmountDisplay.setText(stringValue + " questions"); // N questions
		}
	}
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		List<File> creations = listDirectory("creations");
		int creationAmount = creations.size();
		if (creationAmount == 1) {
			_slider.setDisable(true); //Only one question is present
		} else {
			_slider.setDisable(false);
			_slider.setMax(creationAmount); //Max value is max amount of questions
		}
	}
	
	
	
	
}
