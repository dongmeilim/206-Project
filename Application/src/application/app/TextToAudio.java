package application.app;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javafx.concurrent.Task;

/**
 * Task that creates wav file from selected text
 * in the background using festival.
 */

public class TextToAudio extends Task<Void> {
	
	private String _text;
	private String _voice;
	private String _name;
	private String _dir;
	
	public TextToAudio(String text, String voice, String name) {
		_text=text;
		_voice=voice;
		_name = name;
		_dir=System.getProperty("user.dir");
	}
	@Override
	protected Void call() throws Exception {
		// save the selected text to a text file
		File tempScript= new File(_dir+"/tmp/text/temp.txt");
		tempScript.createNewFile();
		PrintWriter printWriter = new PrintWriter(_dir+"/tmp/text/temp.txt", "UTF-8");
		printWriter.println(_text);
		printWriter.close();
		
		//call bash festival to create wav file from text
		String s;
		if (_name.equals("preview")) {
		s = "text2wave -o "+_dir+"/tmp/audio/preview/preview.wav "+_dir+"/tmp/text/temp.txt temp.txt -eval \"(voice_"+_voice+")\"";
		}else {
			s = "text2wave -o "+_dir+"/tmp/audio/"+_name+".wav "+_dir+"/tmp/text/temp.txt temp.txt -eval \"(voice_"+_voice+")\"";
		}
		updateProgress(1, 3);

		ProcessBuilder builder = new ProcessBuilder("bash", "-c", s);
				
		try {
			Process process = builder.start();
			updateProgress(2, 3);		
			process.waitFor();
			updateProgress(3, 3);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}
}
