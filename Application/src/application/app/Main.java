package application.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Parent;

/**
 * SoftEng 206 Assignment 3: VARpedia
 * Authors: Mirlington and Dong-Mei Lim
 */
public class Main extends Application {

	private final String _PREVIEWSCRIPT = "createffmpegpreview.sh";
	private final String _PREVIEWTEXT = "/createffmpegpreview.txt";
	private final String _SAVESCRIPT = "saveCreation.sh";
	private final String _SAVETEXT = "/saveCreation.txt";
	private final String _DIR = System.getProperty("user.dir");
	
	@Override	
	public void start(Stage primaryStage) {
		
		// initialize the directories needed for making creations
		new File(_DIR+"/creations").mkdirs();
		new File(_DIR+"/quiz").mkdirs();
		new File(_DIR+"/tmp/text").mkdirs();
		new File(_DIR+"/tmp/audio").mkdirs();
		new File(_DIR+"/tmp/images").mkdirs();
		new File(_DIR+"/tmp/video").mkdirs();
		
		new File(_DIR+"/scripts/").mkdirs();
		File previewScript = new File(_DIR+"/scripts/"+_PREVIEWSCRIPT);
		File saveScript = new File(_DIR+"/scripts/"+_SAVESCRIPT);
		try {
			generateScript(previewScript,_PREVIEWTEXT);
			generateScript(saveScript,_SAVETEXT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		primaryStage.setOnCloseRequest(e-> {
			//e.consume(); //Eats up the Close event, so that the program can deal with it
			File tmpDirectory = new File(_DIR+"/tmp/");
			File scriptDirectory = new File(_DIR+"/scripts/");
			deleteDirectoryRecursively(tmpDirectory);
			deleteDirectoryRecursively(scriptDirectory);
			primaryStage.close();
		});

		
		try {
			primaryStage.setTitle("VARpedia");
			//load the menu on startup
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/application/view/Menu.fxml"));
			Parent root = loader.load();

			Scene scene = new Scene(root,750,550);
			scene.getStylesheets().add("/application/app/application.css");
			primaryStage.setResizable(false);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	private void generateScript(File script, String contentFile) throws IOException {
		if (!script.exists()) { //test for non-existence
			BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(contentFile)));
			FileWriter fw = new FileWriter(script, true);
			String s;
			while ((s = br.readLine()) != null) { // read a line
				fw.write(s+"\n");
				fw.flush();
			}
			br.close();
			fw.close();
		}
	}
	
	private void deleteDirectoryRecursively(File directory) {
	    File[] allContents = directory.listFiles();
	    if (allContents != null) { //Keep going if there are files
	        for (File file : allContents) {
	            deleteDirectoryRecursively(file);
	        }
	    }
	    directory.delete();
	}
}

