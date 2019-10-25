package application.controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * Controller that handles Score.fxml
 * 
 * This window displays what the user got right and which ones
 * they made mistakes on via Labels and ScrollPanes.
 * 
 * As the third and final step of the Quiz Cycle, the user has the option to
 * make a new game or go home.
 */

public class Score extends Controller implements Initializable {

	@FXML private Button _home;
	@FXML private Button _help;
	@FXML private Button _anchorHelp;
	@FXML private Button _newGame;

	@FXML private AnchorPane _anchor;
	
	@FXML private VBox _correctText;
	@FXML private VBox _wrongText;

	private ArrayList<String> _correct;
	private ArrayList<String> _wrong;

	private final String _FONTSIZE = "20";
	
	public Score(ArrayList<String> correct, ArrayList<String> wrong) {
		_correct=correct;
		_wrong=wrong;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		//Set up correct creations Text
		String correct = "";
		for(String creation: _correct) {
			correct = creation;
			Label label = new Label(correct);
			label.styleProperty().set("-fx-font-size: " + _FONTSIZE + ";");
			_correctText.getChildren().add(label);
		}

		//Set up incorrect creations Text
		String wrong = "";
		for(String creation: _wrong) {
			wrong = creation;
			Label label = new Label(wrong);
			label.styleProperty().set("-fx-font-size: " + _FONTSIZE + ";");
			_wrongText.getChildren().add(label);
		}
	}

	@FXML private void handleHome() { switchTo(_home.getScene(), getClass().getResource(_PATH+"Menu.fxml"));}
	@FXML private void handleNewGame() { switchTo(_home.getScene(), getClass().getResource(_PATH+"QuizSettings.fxml"));}

	@FXML 
	private void handleHelp() {
		if (_anchor.isVisible() == false) { //AnchorPane is invisible on startup
			_anchor.setVisible(true);
			
			_newGame.setDisable(true);
			_correctText.setDisable(true);
			_wrongText.setDisable(true);
			_home.setDisable(true);
		} else {			
			_anchor.setVisible(false);

			_newGame.setDisable(false);
			_correctText.setDisable(false);
			_wrongText.setDisable(false);
			_home.setDisable(false);
		}
	}
}
