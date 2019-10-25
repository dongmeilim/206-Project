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

/**
 * Controller that handles the Search.fxml view.
 * 
 * After user types in an appropriate query,
 * the application.app.SearchWiki Task is called.
 * 
 * This is the first step of the Creation Cycle so no loss of progress
 * is warned when returning home.
 */

public class Search extends Controller implements Initializable{
	
	@FXML private AnchorPane _anchor;
	
	@FXML private Button _back;
	@FXML private Button _home;
	@FXML private Button _search;

	@FXML private Label _lastSearchedTerm;
	@FXML private Label _term;
	@FXML private Label _errorMessage;
	
	@FXML private TextField _searchbar;

	private String _dir = System.getProperty("user.dir");
	private SearchWiki _wikitBG;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		//Clean directories using helper function
		clearDir("/tmp/audio");
		clearDir("/tmp/audio/preview");
		clearDir("/tmp/text/censored");
		clearDir("/tmp/audio/censored");
		
		//Create a file to keep track of the number of audio files created by the user
		try {	
			BufferedWriter writer = new BufferedWriter(new FileWriter(_dir + "/tmp/text/audioCount"));
			writer.write("0");	
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		readQueryText();
	}
	
	/**
	 * As the tmp file is deleted upon exit, this file stores the last-searched term.
	 */
	private void readQueryText() {
		
		String query = "";

		try (BufferedReader reader = new BufferedReader(new FileReader(new File(_dir+"/creations/query/query")))) {
			query = reader.readLine();
			reader.close();
		}catch(FileNotFoundException e) {
			try {
				new File(_dir+"/creations/query/query").createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}catch (IOException e) {
			e.printStackTrace();
		}

		_term.setText(query);
	}
	
	/**
	 * This is bound to both the back and home button.
	 */
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
			_home.setDisable(true);
			_back.setDisable(true);
			_searchbar.setDisable(true);
			_search.setDisable(true);
			
			_lastSearchedTerm.setText("");
			_term.setText("");
			
			_anchor.setVisible(true);
		} else {
			_home.setDisable(false);
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

		//Display message if empty input
		if (input.equals("")) {
			_errorMessage.setText("Please enter something to search");
		}else {
			//Prevent user from spamming
			_search.setDisable(true);
			_searchbar.setDisable(true);

			BufferedWriter queriedTerm = new BufferedWriter(new FileWriter(_dir + "/tmp/text/query"));
			queriedTerm.write(input);
			queriedTerm.close();
			
			BufferedWriter lastTermSearched = new BufferedWriter(new FileWriter(_dir + "/creations/query/query"));
			lastTermSearched.write(input);
			lastTermSearched.close();

			//Search the term in the background thread
			_wikitBG = new SearchWiki(input);
			Thread thread = new Thread(_wikitBG);
			thread.start();

			//Display wait message to reader
			_wikitBG.setOnRunning(running -> {
				_errorMessage.setText("Please wait...");
			});

			//Update the GUI once the process is finished
			_wikitBG.setOnSucceeded(succeed -> {
				if (_wikitBG.getValue()) {
					switchTo(_search.getScene(), getClass().getResource(_PATH+"TextSelect.fxml"));
				} else {
					//Let the user enter another term if there is no entry for the previous one on wikit
					_errorMessage.setText("Sorry, there is no entry for this term. Please enter a new term");
					_search.setDisable(false);
					_searchbar.setDisable(false);
					_home.setDisable(false);
					_back.setDisable(false);
					_searchbar.clear();
				}
			});
		}
	}
	
	/**
	 * Helper function that clears directories.
	 * Directories are created if they don't already exist.
	 * @param dirName
	 */
	private void clearDir(String dirName) {
		//Create folder if it does not exist
		File dir = new File (_dir+dirName);
		dir.mkdir();

		//Clear the folder
		File[] files = dir.listFiles();
		if(files!=null) { 
			for(File file: files) {
				file.delete();
			}
		}
	}
}