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
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;


/**
 * Controller that handles the QuizSettings.fxml view.
 * 
 * The user may select a game mode via RadioButtons and
 * the amount of questions using the slider.
 * 
 * This is the first step of the Quiz Cycle.
 */

public class QuizSettings extends Controller implements Initializable {
	
	@FXML private Button _back;
	@FXML private Button _home;
	@FXML private Button _help;
	@FXML private Button _anchorHelp;
	@FXML private Button _next;
	@FXML private Label _questionAmountDisplay;
	@FXML private Label _warning;
	@FXML private RadioButton _audioRadio;
	@FXML private RadioButton _imageRadio;
	@FXML private RadioButton _bothRadio;
	@FXML private Slider _slider;
	@FXML private ToggleGroup _mode;
	
	@FXML private AnchorPane _anchor;
	
	@FXML private void handleBack() {handleHome();}
	@FXML public void handleHome() {toMenu(_home.getScene());}
	
	@FXML private void handleHelp() {
		if (_anchor.isVisible() == false) { //AnchorPane is invisible on startup
			
			_anchor.setVisible(true);
			_audioRadio.setDisable(true);
			_imageRadio.setDisable(true);
			_bothRadio.setDisable(true);
			_slider.setDisable(true);
			_back.setDisable(true);
			_home.setDisable(true);
			_next.setDisable(true);
		} else {			
			_anchor.setVisible(false);

			_audioRadio.setDisable(false);
			_imageRadio.setDisable(false);
			_bothRadio.setDisable(false);
			_slider.setDisable(false);
			_back.setDisable(false);
			_home.setDisable(false);
			_next.setDisable(false);
			
		}
	}

	@FXML private void handleNext() throws IOException {
		
		//get the number of questions
		int numQuestions = (int)_slider.getValue();
		String dir = System.getProperty("user.dir");
		BufferedWriter writer = new BufferedWriter(new FileWriter(dir + "/tmp/text/numQuestions.txt"));
		writer.write(numQuestions + "");	
		writer.close();
		
		Toggle selectedMode = _mode.getSelectedToggle();
		String game = _PATH; //Will never end up being undefined
		if (selectedMode.equals(_audioRadio)) {
			game=game+"AudioMatch.fxml";
		} else if (selectedMode.equals(_imageRadio)) {
			game=game+"ImageMatch.fxml";
		} else {
			game=game+"VideoMatch.fxml";
		}
		switchTo(_next.getScene(),getClass().getResource(game));
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
		if (creationAmount == 2) {
			_slider.setDisable(true); //Only one question is present
		} else {
			_slider.setDisable(false);
			_slider.setMax(creationAmount); //Max value is max amount of questions
		}
	}
}
