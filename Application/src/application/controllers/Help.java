package application.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * Controller that handles the Help.fxml view.
 */

public class Help extends Controller {
	@FXML private Button _back;

	@FXML private void goBack(){switchNonSkippableWindow(_back.getScene(), getClass().getResource(_PATH+"Menu.fxml"));}

}
