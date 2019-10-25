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
		_dir=System.getProperty("user.dir"); //Current directory
	}
	@Override
	protected Void call() throws Exception {
		updateProgress(0, 3); //ProgressBar listens to this line
		
		//Save the selected text to a text file
		File text= new File(_dir+"/tmp/text/temp.txt");
		text.createNewFile();
		PrintWriter printWriter = new PrintWriter(_dir+"/tmp/text/temp.txt", "UTF-8");
		printWriter.println(_text);
		printWriter.close();

		//Call bash festival to create a wav file from the text
		String command;
		if (_name.equals("preview")) {
			command = "text2wave -o "+_dir+"/tmp/audio/preview/preview.wav "+_dir+"/tmp/text/temp.txt temp.txt -eval \"(voice_"+_voice+")\"";
		}else {

			//Save the selected text to a transcript file
			text = new File(_dir+"/tmp/text/censored/"+ _name + ".txt");
			text.createNewFile();
			printWriter = new PrintWriter(_dir+"/tmp/text/censored/"+ _name + ".txt", "UTF-8");
			printWriter.println(_text);
			printWriter.close();

			//Read the query file
			String query = "";
			BufferedReader reader = new BufferedReader(new FileReader(new File(_dir+"/tmp/text/query")));
			query = reader.readLine();
			reader.close();
			
			//Create the censored text for the quiz
			String censorText = "sed -i 's/"+query+"/. SOMETHING. /Ig' "+_dir+"/tmp/text/censored/"+ _name + ".txt";
			ProcessBuilder censor = new ProcessBuilder("bash", "-c", censorText);
			Process process = censor.start();
			process.waitFor();
			
			//Create the censored audio
			censorText = "text2wave -o "+_dir+"/tmp/audio/censored/"+_name+".wav "+_dir+"/tmp/text/censored/"+_name+".txt -eval \"(voice_"+_voice+")\"";
			censor = new ProcessBuilder("bash", "-c", censorText);
			process = censor.start();
			process.waitFor();

			command = "text2wave -o "+_dir+"/tmp/audio/"+_name+".wav "+_dir+"/tmp/text/temp.txt temp.txt -eval \"(voice_"+_voice+")\"";
		}
		
		updateProgress(1, 3); //ProgressBar listens to this line

		ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", command);

		try {
			Process process = processBuilder.start();
			updateProgress(2, 3); //ProgressBar listens to this line		
			process.waitFor();
			updateProgress(3, 3); //ProgressBar listens to this line
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}
}
