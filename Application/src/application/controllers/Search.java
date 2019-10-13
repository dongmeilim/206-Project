package application.controllers;

import application.app.SearchWiki;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.net.URL;

import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

/**
 * Controller that handles the Search.fxml view.
 * After user types in an appropriate query,
 * the application.app.SearchWiki Task is called.
 */
//TODO implement safe search
public class Search extends Controller implements Initializable{
	@FXML private Button _menu;
	@FXML private Button _back;
	@FXML private Button _search;

	@FXML private Label _lastSearchedTerm;
	@FXML private Label _term;
	@FXML private Label _errorMessage;
	@FXML private TextField _searchbar;

	private String _dir = System.getProperty("user.dir");
	private SearchWiki _wikitBG;
	
	@FXML private BorderPane _border;
	@FXML private AnchorPane _anchor;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//clean the directory to prepare for a new creation

		//delete all files in tmp/audio
		clearDir("/tmp/audio");
		
		//create and clear preview folder
		clearDir("/tmp/audio/preview");
		
		//create and clear text/censored
		clearDir("/tmp/text/censored");
		
		//create and clear audio/censored
		clearDir("/tmp/audio/censored");
		
		// create a file to keep track of the number of audio files created by the user
		try {	
			BufferedWriter writer = new BufferedWriter(new FileWriter(_dir + "/tmp/text/audioCount"));
			writer.write("0");	
			writer.close();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		readQueryText();
	}
	
	private void readQueryText() {
		//read the text file
		String query = "";

		try (BufferedReader reader = new BufferedReader(new FileReader(new File(_dir+"/tmp/text/query")))) {
			query = reader.readLine();
			reader.close();
		}catch(FileNotFoundException e) {
			try {
				new File(_dir+"/tmp/text/query").createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}catch (IOException e) {
			e.printStackTrace();
		}

		_term.setText(query);
	}
	
	
	/**
	 * This method is bound to both the back and menu buttons
	 * */
	@FXML
	private void handleBack() {
		if (_wikitBG !=null &&_wikitBG.isRunning()) {
			_wikitBG.cancel();
		}
		switchTo(_back.getScene(), getClass().getResource(_PATH+"Menu.fxml"));
	}
	
	@FXML
	private void handleHelp() { //AnchorPane is invisible on startup
		if (_anchor.isVisible()==false) {
			_menu.setDisable(true);
			_back.setDisable(true);
			_searchbar.setDisable(true);
			_search.setDisable(true);
			
			_lastSearchedTerm.setText("");
			_term.setText("");
			
			_anchor.setVisible(true);
		} else {
			_menu.setDisable(false);
			_back.setDisable(false);
			_searchbar.setDisable(false);
			_search.setDisable(false);
			
			_lastSearchedTerm.setText("Last Searched Term:");
			readQueryText();
			
			_anchor.setVisible(false);
		}
	}

	@FXML
	private void search() throws IOException {
		String input = _searchbar.getText();
		_term.setText(input);

		//display message if empty input
		if (input.equals("")) {
			_errorMessage.setText("Please enter something to search");
		}else {
			//prevent user from spamming
			_search.setDisable(true);
			_searchbar.setDisable(true);

			BufferedWriter writer = new BufferedWriter(new FileWriter(_dir + "/tmp/text/query"));
			writer.write(input);
			writer.close();

			//search the term in bg thread
			_wikitBG = new SearchWiki(input);
			Thread thread = new Thread(_wikitBG);
			thread.start();

			//display wait message to reader
			_wikitBG.setOnRunning(running -> {
				_errorMessage.setText("Please wait...");
			});

			//update the GUI once the process is finished
			_wikitBG.setOnSucceeded(succeed -> {
				if (_wikitBG.getValue()) {
					//move on to the next step

					switchTo(_search.getScene(), getClass().getResource(_PATH+"TextSelect.fxml"));

				} else {
					//let the user enter another term if there is no entry for the previous one on wikit
					_errorMessage.setText("Sorry, there is no entry for this term. Please enter a new term");
					_search.setDisable(false);
					_searchbar.setDisable(false);
					_menu.setDisable(false);
					_back.setDisable(false);
					_searchbar.clear();
				}
			});
		}
	}
	
	private void clearDir(String dirName) {
		//create folder if it does not exist
		File dir = new File (_dir+dirName);
		dir.mkdir();

		//clear the folder
		File[] files = dir.listFiles();
		if(files!=null) { 
			for(File file: files) {
				file.delete();
			}
		}
	}
}