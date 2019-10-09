package application.app;

import java.io.File;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

/**
 * SoftEng 206 Assignment 3: VARpedia
 * Authors: Mirlington and Dong-Mei Lim
 */
public class Main extends Application {

	@Override	
	public void start(Stage primaryStage) {
		String dir = System.getProperty("user.dir");
		
		// initialize the directories needed for making creations
		new File(dir+"/creations").mkdirs();
		new File(dir+"/tmp/text").mkdirs();
		new File(dir+"/tmp/audio").mkdirs();
		new File(dir+"/tmp/images").mkdirs();
		new File(dir+"/tmp/video").mkdirs();
		// TODO proper file-hierarchy
		new File(dir+"/quiz").mkdirs();
		
		try {
			primaryStage.setTitle("VARpedia");
			
			//load the menu on startup
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/application/view/Menu.fxml"));
			BorderPane root = (BorderPane)loader.load();
			
			Scene scene = new Scene(root,750,550);
			scene.getStylesheets().add("/application/app/application.css");
			primaryStage.setResizable(false);
			primaryStage.setScene(scene);
			primaryStage.show();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}

