package application.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
		File text= new File(_dir+"/tmp/text/temp.txt");
		text.createNewFile();
		PrintWriter printWriter = new PrintWriter(_dir+"/tmp/text/temp.txt", "UTF-8");
		printWriter.println(_text);
		printWriter.close();

		//call bash festival to create wav file from text
		String s;
		if (_name.equals("preview")) {
			s = "text2wave -o "+_dir+"/tmp/audio/preview/preview.wav "+_dir+"/tmp/text/temp.txt temp.txt -eval \"(voice_"+_voice+")\"";
		}else {

			// save the selected text to a transcript file
			text = new File(_dir+"/tmp/text/censored/"+ _name + ".txt");
			text.createNewFile();
			printWriter = new PrintWriter(_dir+"/tmp/text/censored/"+ _name + ".txt", "UTF-8");
			printWriter.println(_text);
			printWriter.close();

			//read the query file
			String query = "";

			BufferedReader reader = new BufferedReader(new FileReader(new File(_dir+"/tmp/text/query")));
			query = reader.readLine();
			reader.close();
			
			// create the censored text for the quiz
			String censorText = "sed -i 's/"+query+"/----/Ig' "+_dir+"/tmp/text/censored/"+ _name + ".txt";
			ProcessBuilder censor = new ProcessBuilder("bash", "-c", censorText);
			Process process = censor.start();
			process.waitFor();
			
			//create the censored audio
			censorText = "text2wave -o "+_dir+"/tmp/audio/censored/"+_name+".wav "+_dir+"/tmp/text/censored/"+_name+".txt -eval \"(voice_"+_voice+")\"";
			censor = new ProcessBuilder("bash", "-c", censorText);
			process = censor.start();
			process.waitFor();

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
