package lucyAnimation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

public class FramePanel extends VBox{
	
	ListView<String> frameList;
	ObservableList<String> items;
	ButtonPanel buttons;
	static Map<String, Image> images;
	
	public FramePanel() {
		super();
		createComponents();
	}
	
	public void createComponents() {
		items = FXCollections.observableArrayList();
		images = new HashMap<String, Image>();
		frameList = new ListView<String>(items);
		buttons = new ButtonPanel();
		
		frameList.setCellFactory(new Callback<ListView<String>,
				ListCell<String>>() {
            @Override 
            public ListCell<String> call(ListView<String> list) {
                return new Thumb();
            }
        });
		
		VBox.setVgrow(frameList, Priority.ALWAYS);
		VBox.setVgrow(buttons, Priority.NEVER);
		
		getChildren().add(frameList);
		getChildren().add(buttons);
	}
	
	public void updateList(List<WorkingFrame> frames) {
		items.clear();
		images.clear();
		
		int i = 0;
		for (WorkingFrame frame : frames) {
			String id = "Frame: " + i;
			
			items.add(id);
			images.put(id, frame.getImage());
			
			i++;
		}
	}
	
	public void setRemoveHandler(EventHandler<ActionEvent> handler) {
		buttons.setRemoveHandler(handler);
	}
	
	public void setUpHandler(EventHandler<ActionEvent> handler) {
		buttons.setUpHandler(handler);
	}
	
	public void setDownHandler(EventHandler<ActionEvent> handler) {
		buttons.setDownHandler(handler);
	}
	
	public void addSelectionListener(ChangeListener<Number> handler) {
		frameList.getSelectionModel().selectedIndexProperty().addListener(handler);
	}
	
	static class Thumb extends ListCell<String> {
		private ImageView imageView = new ImageView();
		
		@Override
		public void updateItem(String item, boolean empty) {
			super.updateItem(item, empty);
			if (empty || item == null) {
				imageView.setImage(null);
				
				setGraphic(null);
				setText(null);
			} else {
				imageView.setImage(images.get(item));
				imageView.setFitWidth(20);
				imageView.setFitHeight(20);
				
				setGraphic(imageView);
				setText(item);
			}
		}
	}
}
