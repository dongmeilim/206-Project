package application.controllers;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 * Controller that handles the Menu.fxml view.
 * The first window that is seen by the user.
 */

// TODO key bindings
// Add key bindings where possible. This will help reduce time taken to make video etc.
// Enter for save
// Space for play/pause
// ??? for next
// Esc for back

public class Menu extends Controller implements Initializable {
	
	@FXML private Button _help;
	@FXML private Button _create;
	@FXML private Button _videos;
	@FXML private Button _quiz;
	@FXML private Label _warning;

	@FXML private void openHelp() {System.out.println("To be implemented!");}
	@FXML private void openSearch() {switchTo(_help.getScene(), getClass().getResource(_PATH+"Search2.fxml"));}
	@FXML private void openVideos() {switchTo(_help.getScene(), getClass().getResource(_PATH+"VideoList.fxml"));}
	@FXML private void openQuiz() {switchTo(_quiz.getScene(), getClass().getResource(_PATH+"QuizSettings.fxml"));}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		List<File> creations = listDirectory("creations");
		int creationAmount = creations.size();
		if (creationAmount < 2) {
			_quiz.setDisable(true);
			_warning.setText("Quiz disabled: You need at least two videos");
		} else {
			_quiz.setDisable(false);
			_warning.setText("");
		}
	}
}
