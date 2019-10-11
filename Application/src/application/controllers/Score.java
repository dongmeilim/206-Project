package application.controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
public class Score extends Controller implements Initializable {

	@FXML private Button _home;
	@FXML private Button _help;
	@FXML private Button _newGame;
	
	@FXML private Label _correctText;
	@FXML private Label _wrongText;
	
	private ArrayList<String> _correct;
	private ArrayList<String> _wrong;
	
	public Score(ArrayList<String> correct, ArrayList<String> wrong) {
		_correct=correct;
		_wrong=wrong;
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// set up correct creations Text
		String correct = "";
		for(String creation: _correct) {
			correct = correct + creation +"\n";
		}
		_correctText.setText(correct);
		
		// set up incorrect creations Text
		String wrong = "";
		for(String creation: _wrong) {
			wrong = wrong + creation +"\n";
		}
		_wrongText.setText(wrong);
		
	}
	
	@FXML private void handleHome() { switchTo(_home.getScene(), getClass().getResource(_PATH+"Menu.fxml"));}
	@FXML private void handleNewGame() { switchTo(_home.getScene(), getClass().getResource(_PATH+"QuizSettings.fxml"));}
	
	@FXML private void handleHelp() {
		// TODO implement help for Score?
	}
	

}
