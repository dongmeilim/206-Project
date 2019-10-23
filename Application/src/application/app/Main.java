package application.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Parent;

import javafx.scene.text.Font;

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
		new File(_DIR+"/creations/query").mkdirs();
		new File(_DIR+"/quiz").mkdirs();
		new File(_DIR+"/tmp/text").mkdirs();
		new File(_DIR+"/tmp/audio").mkdirs();
		new File(_DIR+"/tmp/music").mkdirs();
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
		
		try {
			exportMP3();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		//Delete files upon application exit
		primaryStage.setOnCloseRequest(e-> {
			e.consume(); //Eats up the Close event, so that the program can deal with it
			primaryStage.close();
			File tmpDirectory = new File(_DIR+"/tmp/");
			File scriptDirectory = new File(_DIR+"/scripts/");
			deleteDirectoryRecursively(tmpDirectory);
			deleteDirectoryRecursively(scriptDirectory);
			Platform.exit();
		});

		
		try {
			primaryStage.setTitle("VARpedia");
			//load the menu on startup
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/application/view/Menu.fxml"));
			Parent root = loader.load();

			Font.loadFont(getClass().getResource("/Chewy.ttf").toExternalForm(), 10); //Magic number 10 is overriden by FXML font-sizes
			
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
				fw.write(s+"\n"); //Newline character there to format lines properly
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
	
	/**
	 * Author: Ordiel
	 * Original: https://stackoverflow.com/questions/10308221/how-to-copy-file-inside-jar-to-outside-the-jar
	 * Modified by: Mirlington
	 * 
	 * 
     * Export a resource embedded into a Jar file to the local file path.
     *
     * @param resourceName ie.: "/SmartLibrary.dll"
     * @return The path to the exported resource
     * @throws Exception
     */
    private void exportMP3() throws Exception {
        InputStream stream = null;
        OutputStream resStreamOut = null;
        final String mp3 = "/jlbrock44_-_Staying_Positive.mp3";
        final String tmpPath = "/tmp/music";
        String jarFolder;
        try {
            stream = getClass().getResourceAsStream(mp3);//note that each / is a directory down in the "jar tree" been the jar the root of the tree
            if(stream == null) {
                throw new Exception("Cannot get resource \"" + mp3 + "\" from Jar file.");
            }
            int readBytes;
            byte[] buffer = new byte[4096];
            jarFolder = new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParentFile().getPath().replace('\\', '/');
            resStreamOut = new FileOutputStream(jarFolder + tmpPath + mp3);
            while ((readBytes = stream.read(buffer)) > 0) {
                resStreamOut.write(buffer, 0, readBytes);
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            stream.close();
            resStreamOut.close();
        }
    }
}

