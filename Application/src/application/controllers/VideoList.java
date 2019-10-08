package application.controllers;

import java.io.File;
import java.io.IOException;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.event.ActionEvent;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

/**
 * Controller that handles the VideoList.fxml view.
 * Displays videos in a TableView for playback or deletion.
 * Videos can also be ordered by date of creation or by alphabetical order.
 */

public class VideoList extends Controller implements Initializable{

	@FXML private Button _back;
	@FXML private Button _sortABC;
	@FXML private Button _sortDate;
	@FXML private TableView<File> _table;
	@FXML private TableColumn<File, Void> _thumbnails;
	@FXML private TableColumn<File, String> _videos;
	@FXML private TableColumn<File, Void> _play;
	@FXML private TableColumn<File, Void> _delete;

	private String _dir = System.getProperty("user.dir");
	private boolean _sortByABC = true;

	private final ObservableList<File> _fileList= FXCollections.observableArrayList();;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		new File(_dir+"/creations").mkdirs();
		updateFileList();
		_table.setSelectionModel(null);
		_table.setItems(_fileList);
		_videos.setCellValueFactory(new PropertyValueFactory<>("name"));
		addPlayButtonToTable();
		addDeleteButtonToTable();

	}

	@FXML private void goBack(){ switchTo(_back.getScene(), getClass().getResource(_PATH+"Menu.fxml")); }

	/**
	 * Refreshes the list of files and sorts them appropriately
	 */
	private void updateFileList() {
		List<File> files = listDirectory("creations");
		File[] arrayOfFiles = new File[files.size()];
		files.toArray(arrayOfFiles);

		if(_sortByABC) { //Not case-sensitive

			Arrays.sort(arrayOfFiles, new Comparator<File>() {
				public int compare(File f1, File f2) {
					return f1.getName().compareToIgnoreCase(f2.getName());
				}
			});

		} else {

			Arrays.sort(arrayOfFiles, new Comparator<File>(){
				public int compare(File f1, File f2) {

					if (Long.compare(f1.lastModified(), f2.lastModified()) == 1) {
						return -1;
					}

					return Long.compare(f1.lastModified(), f2.lastModified());
				}
			});
		}
		_fileList.clear();
		_fileList.addAll(arrayOfFiles);
	}

	/**
	 * Author: Rip Tutorial
	 * Original: https://riptutorial.com/javafx/example/27946/add-button-to-tableview
	 * Modified: dongmeilim
	 * Comments: dongmeilim
	 */
	private void addPlayButtonToTable() {

		Callback<TableColumn<File, Void>, TableCell<File, Void>> cellFactory = new Callback<TableColumn<File, Void>, TableCell<File, Void>>() {
			@Override
			public TableCell<File, Void> call(final TableColumn<File, Void> param) {
				final TableCell<File, Void> cell = new TableCell<File, Void>() {
					// set up the play buttons
					private final Button playBtn = new Button("Play");

					{
						playBtn.setOnAction((ActionEvent event) -> {
							// Get the video in the same row as the button
							File video = getTableView().getItems().get(getIndex());

							// Open the video player with the video
							Scene scene = playBtn.getScene();
							FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/view/VideoPlayer.fxml"));
							VideoPlayer controller = new VideoPlayer(video);
							loader.setController(controller);

							try {
								scene.setRoot(loader.load());
							} catch (IOException e) {
								e.printStackTrace();
							}


						});
					}

					@Override
					public void updateItem(Void item, boolean empty) {
						// insert the play buttons into the table column
						super.updateItem(item, empty);
						if (empty) {
							setGraphic(null);
						} else {
							setGraphic(playBtn);
						}
					}
				};
				return cell;
			}
		};

		_play.setCellFactory(cellFactory);

	}

	/**
	 * Author: Rip Tutorial
	 * Original: https://riptutorial.com/javafx/example/27946/add-button-to-tableview
	 * Modified: dongmeilim
	 * Comments: dongmeilim
	 */
	private void addDeleteButtonToTable() {
		Callback<TableColumn<File, Void>, TableCell<File, Void>> cellFactory = new Callback<TableColumn<File, Void>, TableCell<File, Void>>() {
			@Override
			public TableCell<File, Void> call(final TableColumn<File, Void> param) {
				final TableCell<File, Void> cell = new TableCell<File, Void>() {
					// set up the delete button
					private final Button delBtn = new Button("Delete");

					{
						delBtn.setOnAction((ActionEvent event) -> {
							// get the video in the same row as the button
							File file = getTableView().getItems().get(getIndex());

							// Confirm the user wants to delete the video
							Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
							alert.setHeaderText("Are you sure you want to delete "+ file.getName()+"?");
							Optional<ButtonType> result = alert.showAndWait();
							if (result.get() == ButtonType.OK){
								if(file.exists()) {
									//delete the video
									file.delete();
									updateFileList();
								}
							}

						});
					}

					@Override
					public void updateItem(Void item, boolean empty) {
						// insert delete buttons into the table column
						super.updateItem(item, empty);
						if (empty) {
							setGraphic(null);
						} else {
							setGraphic(delBtn);
						}
					}
				};
				return cell;
			}
		};

		_delete.setCellFactory(cellFactory);

	}

	/**
	 * Author: Rip Tutorial
	 * Original: https://riptutorial.com/javafx/example/27946/add-button-to-tableview
	 * Modified: dongmeilim
	 * Comments: dongmeilim
	 */
	//TODO add thumbnails to table
	/*
	private void addThumbnail() {
		Callback<TableColumn<File, Void>, TableCell<File, Void>> cellFactory = new Callback<TableColumn<File, Void>, TableCell<File, Void>>() {
			@Override
			public TableCell<File, Void> call(final TableColumn<File, Void> param) {
				final TableCell<File, Void> cell = new TableCell<File, Void>() {
					// set up the thumbnail
					
					private final ImageView thumbnail = new ImageView();
					{
						File file = getTableView().getItems().get(getIndex());
						thumbnail.setImage(new Image(file.toURI().toString()));
						thumbnail.setFitHeight(50);
					}

					@Override
					public void updateItem(Void item, boolean empty) {
						// insert delete buttons into the table column
						super.updateItem(item, empty);
						if (empty) {
							setGraphic(null);
						} else {
							setGraphic(thumbnail);
						}
					}
				};
				return cell;
			}
		};

		_thumbnails.setCellFactory(cellFactory);

	}
	*/

	@FXML
	private void sortABC() {
		_sortByABC=true;
		updateFileList();
	}

	@FXML
	private void sortDate() {
		_sortByABC=false;
		updateFileList();
	}
}