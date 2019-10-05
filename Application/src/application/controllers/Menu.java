package application.controllers;

import javafx.fxml.FXML;

import javafx.scene.control.Button;

/**
 * Controller that handles the Menu.fxml view.
 * The first window that is seen by the user.
 */

public class Menu extends Controller {
	
	@FXML private Button _help;
	@FXML private Button _create;
	@FXML private Button _videos;
	
	
	@FXML
	private void openHelp() {
		switchNonSkippableWindow(_help.getScene(), getClass().getResource(_PATH+"Help.fxml"));
	}
	
	@FXML
	private void openSearch() {
		switchNonSkippableWindow(_help.getScene(), getClass().getResource(_PATH+"Search.fxml"));
	}
	
	@FXML
	private void videoList() {
		switchNonSkippableWindow(_help.getScene(), getClass().getResource(_PATH+"VideoList.fxml"));
	}
	
}
