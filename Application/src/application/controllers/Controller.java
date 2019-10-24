package application.controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.net.URL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.fxml.FXMLLoader;

import javafx.scene.Scene;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;

import javafx.stage.Stage;

/**
 * Abstract class from which all other controllers extend.
 * Stores general methods that all other controllers use.
 */

abstract class Controller { //Package-private

private boolean _decision;

protected final String _PATH = "/application/view/";
	
	/**
	 * Displays a confirmation window and sets decision to true or false depending on which button clicked
	 * @param title
	 * @param message
	 * @param decision
	 */
	
	protected boolean displayAlert(String header, String body) {//Pop-up box
		_decision = false; //Remains false if user presses Cancel
		
		Alert alert = new Alert(AlertType.CONFIRMATION, body); //Custom-made Alert
		alert.setHeaderText(header);
		alert.showAndWait().ifPresent(response -> {
			if (response == ButtonType.OK) {
				_decision=true;
			}
		});
		
		return _decision;
		
	}
	
	/**
	 * Lists all files (not directories) using the file.listFiles() method
	 * @param directory
	 */
	
	protected List<File> listDirectory(String directory) {
		
		String applicationPath;
		List<File> files = new ArrayList<File>();
		
		// Retrieves absolute path to Application directory
		applicationPath = System.getProperty("user.dir");
		File[] fixedFileArray = new File(applicationPath+"/"+directory).listFiles();
		for (File file: fixedFileArray) {
			if (file.isDirectory() != true) {
				files.add(file); //Add non-directories
			}
		}
		return files;
	}
	
	/**
	 * Navigates windows
	 * @param button
	 * @param nextWindow
	 */
	
	protected void switchTo(Scene scene, URL nextWindowURL) {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(nextWindowURL);
			scene.setRoot(loader.load());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**Shortcut method to the Menu**/
	protected void toMenu(Scene scene) {switchTo(scene, getClass().getResource(_PATH+"Menu.fxml"));};

	/**Special method for PreviewSave.fxml so that media is dispoed upon file exit**/
	protected void loadPreviewSave(Scene scene, URL nextWindowURL) {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(nextWindowURL);
			scene.setRoot(loader.load());
			PreviewSave previewSave = loader.getController(); //Safe cast as this method is specific to PreviewSave.fxml
			previewSave.setDisposableMedia();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**Super class gives access to stage and scene**/
	protected Stage getStage(Scene scene) {
		return (Stage) scene.getWindow(); //Safe cast as we know the window is always Stage
	}
	
	/**
	 * Stores file order
	 * @param type, savedItems
	 */
	
	protected void storeFileType(String type, List<String> savedItems) {
		//File location
		File storedOrder = new File("tmp/text/"+type+"FilesForVideo");
		//Store order as string;
		String typeOrderString = "";
		for (String item: savedItems) {
			typeOrderString = typeOrderString + item + " ";
		}
		//Trim last whitespace
		typeOrderString = typeOrderString.substring(0,typeOrderString.length()-1);
		
		try {
			FileWriter writer = new FileWriter(storedOrder,false);
			writer.write(typeOrderString);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Retrieves file of stored order (remembers the order of files and remembers when to skip)
	 * @param type
	 */
	
	protected List<String> retrieveFileType(String type) {
		try {
			File file = new File("tmp/text/"+type+"Order");
			FileReader reader = new FileReader(file);
			String output = "";
			int i;
			while ((i=reader.read()) != -1) {
				output = output+(char)i;
			}
			reader.close();
			List<String> savedItems = Arrays.asList(output.split(" "));
			return savedItems;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Deletes directories
	 * @param directory
	 */
	
	protected void deleteDirectoryRecursively(File directory) {
	    File[] allContents = directory.listFiles();
	    if (allContents != null) { //Keep going if there are files
	        for (File file : allContents) {
	            deleteDirectoryRecursively(file);
	        }
	    }
	    String name = directory.getAbsolutePath();
	    boolean output = directory.delete();
	    if (output == false) {
	    	System.out.println(name);
	    }
	}
}
