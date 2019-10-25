package application.app;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.photos.Photo;
import com.flickr4java.flickr.photos.PhotoList;
import com.flickr4java.flickr.photos.PhotosInterface;
import com.flickr4java.flickr.photos.SearchParameters;
import com.flickr4java.flickr.photos.Size;

import java.awt.image.BufferedImage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.imageio.ImageIO;

import javafx.concurrent.Task;

/**
 * Task that downloads images from Flickr
 * in the background.
 */

public class DownloadImages extends Task<Void> {

	private int _imageAmount;
	private float _increment;
	private float _counter = 0;
	
	public DownloadImages(int imageAmount) {
		_imageAmount = imageAmount;
		_increment = 100/_imageAmount; //Using float so that progressBar reaches 100 on unclean divisions
	}
	
	@Override
	protected Void call() throws Exception {
		try {
			updateProgress(_counter,100); //ProgressBar listens to this line
			_counter = _counter+_increment;
			File file = new File("tmp/text/query");
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String query = reader.readLine();
			reader.close();
			FlickrAPI flickr = new FlickrAPI();
			flickr.downloadImages("tmp/images/", _imageAmount, query);	
		} catch (FileNotFoundException e) {
			e.printStackTrace();	
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Private inner-class that fetches images from Flickr
	 * and also updates the DownloadImages Task.
	 */
	
	private class FlickrAPI {
		
		/**
		 * Main function for fetching images
		 * @param tmp
		 * @param imageAmount
		 * @param query
		 */
		private void downloadImages(String tmp, int imageAmount, String query) {
			try {
				String apiKey = getAPIKey("apiKey");
				String sharedSecret = getAPIKey("sharedSecret");

				Flickr flickr = new Flickr(apiKey, sharedSecret, new REST());
				
				int resultsPerPage = imageAmount;
				int page = 0;
				
		        PhotosInterface photos = flickr.getPhotosInterface();
		        SearchParameters params = new SearchParameters();
		        params.setSort(SearchParameters.RELEVANCE);
		        params.setMedia("photos"); 
		        params.setText(query);
		        
		        PhotoList<Photo> results = photos.search(params, resultsPerPage, page);
		        
		        for (Photo photo: results) {
		        	try {
		        		BufferedImage image = photos.getImage(photo,Size.LARGE);
			        	String filename = query.trim().replace(' ', '-')+"-"+System.currentTimeMillis()+"-"+photo.getId()+".jpg";
			        	File outputfile = new File(tmp,filename);
			        	ImageIO.write(image, "jpg", outputfile);
			        	updateProgress(_counter,100); //ProgressBar listens to this line
			        	_counter = _counter + _increment;
		        	} catch (FlickrException fe) {
		        		fe.printStackTrace();
					}
		        }
		        updateProgress(100,100); //ProgressBar listens to this line
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * Helper function for downloadImages
		 * @param key
		 */
		private String getAPIKey(String key) throws Exception {
			String config = "/flickr-api-keys.txt"; //Retrieve API
			InputStream inputstream = getClass().getResourceAsStream(config); 
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputstream)); 
			
			String line;
			while ( (line = bufferedReader.readLine()) != null ) {
				if (line.trim().startsWith(key)) {
					bufferedReader.close();
					return line.substring(line.indexOf("=")+1).trim();
				}
			}
			bufferedReader.close();
			throw new RuntimeException("Couldn't find " + key +" in config file "+config);
		}
	}
}
