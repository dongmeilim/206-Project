package application.controllers;

import javafx.fxml.FXML;

import javafx.scene.control.Button;

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

public class Menu extends Controller {
	
	@FXML private Button _help;
	@FXML private Button _create;
	@FXML private Button _videos;
	@FXML private Button _quiz;
	
	@FXML private void openHelp() {System.out.println("Deprecated: To be replaced by Quiz");}
	@FXML private void openSearch() {switchTo(_help.getScene(), getClass().getResource(_PATH+"Search.fxml"));}
	@FXML private void videoList() {switchTo(_help.getScene(), getClass().getResource(_PATH+"VideoList.fxml"));}
	
	@FXML
	private void openQuiz() {
		// TODO implement openQuiz
	}
	
}
