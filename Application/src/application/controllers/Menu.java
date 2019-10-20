package application.controllers;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

/**
 * Controller that handles the Menu.fxml view.
 * The first window that is seen by the user.
 */

public class Menu extends Controller implements Initializable {

	@FXML private Button _create;
	@FXML private Button _videos;
	@FXML private Button _quiz;
	@FXML private Label _warning;

	@FXML private AnchorPane _anchor;
	@FXML private BorderPane _border;

	private boolean _notEnoughCreations;

	@FXML private void openHelp() {
		if (_anchor.isVisible() == false) { //AnchorPane is invisible on startup
			_create.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
			_videos.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
			_quiz.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

			_create.setDisable(true);
			_videos.setDisable(true);
			_quiz.setDisable(true);

			_anchor.setVisible(true);
		} else {
			_create.setContentDisplay(ContentDisplay.LEFT);
			_videos.setContentDisplay(ContentDisplay.LEFT);
			_quiz.setContentDisplay(ContentDisplay.LEFT);

			_create.setDisable(false);
			_videos.setDisable(false);
			if (!_notEnoughCreations) { //Don't re-enable the quiz
				_quiz.setDisable(false);				
			}

			_anchor.setVisible(false);
		}
	}
	@FXML private void openSearch() {switchTo(_create.getScene(), getClass().getResource(_PATH+"Search.fxml"));}
	@FXML private void openVideos() {switchTo(_videos.getScene(), getClass().getResource(_PATH+"VideoList.fxml"));}
	@FXML private void openQuiz() {switchTo(_quiz.getScene(), getClass().getResource(_PATH+"QuizSettings.fxml"));}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		List<File> creations = listDirectory("creations");
		int creationAmount = creations.size();
		if (creationAmount < 2) {
			_quiz.setDisable(true);
			_notEnoughCreations = true;
			_warning.setText("Quiz disabled: You need at least two videos");
		} else {
			_quiz.setDisable(false);
			_notEnoughCreations = false;
			_warning.setText("");
		}
	}
}
