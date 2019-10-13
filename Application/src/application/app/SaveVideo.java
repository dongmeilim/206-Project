package application.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javafx.concurrent.Task;

/**
 * Task that creates video in the background.
 * ffmpeg is used to concatenate audio and image sequence
 * into a slideshow.
 */

public class SaveVideo extends Task<Void> {

	private final File _NULL = new File("/dev/null");
	private String _fileName;
	
	public SaveVideo(String fileName) {
		_fileName = fileName;
	}
	
	@Override
	protected Void call() throws Exception {
		try {
			updateProgress(0,100); //ProgressBar listens to this line
			ProcessBuilder pb = new ProcessBuilder();
			pb.redirectError(_NULL);
			pb.directory(new File("scripts/"));
			pb.command("bash","saveCreation.sh", _fileName);
			pb.start().waitFor();
			Process process = pb.start();
	        InputStream stdout = process.getInputStream();
	        BufferedReader stdoutBuffered = new BufferedReader(new InputStreamReader(stdout));
	        String line = null;
	        double percentage;
	        while ((line = stdoutBuffered.readLine()) != null ) {
	        	percentage = Double.parseDouble(line);
	        	updateProgress(percentage,100); //ProgressBar listens to this line
	        }
	        stdoutBuffered.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
