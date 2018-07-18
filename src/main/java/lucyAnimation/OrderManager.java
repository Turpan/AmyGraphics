package lucyAnimation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

public class OrderManager extends VBox {
	
	private List<WorkingOrder> orders = new ArrayList<WorkingOrder>();
	
	private ObservableList<Integer> orderList = FXCollections.observableArrayList();
	private ObservableList<String> frameList = FXCollections.observableArrayList();
	
	private static Map<String, Image> images = new HashMap<String, Image>();
	
	private ListView<String> frameView;
	private ListView<Integer> orderView;
	
	private int selected;
	
	private OrderButtons buttons;
	
	public OrderManager() {
		super();
		createComponents();
		createHandlers();
	}

	private void createComponents() {
		HBox listBox = new HBox();
		
		buttons = new OrderButtons();
		
		frameView = new ListView<String>(frameList);
		orderView = new ListView<Integer>(orderList);
		
		frameView.setCellFactory(new Callback<ListView<String>,
				ListCell<String>>() {
            @Override 
            public ListCell<String> call(ListView<String> list) {
                return new Thumb();
            }
        });
		
		listBox.getChildren().add(frameView);
		listBox.getChildren().add(orderView);
		
		VBox.setVgrow(listBox, Priority.ALWAYS);
		VBox.setVgrow(buttons, Priority.NEVER);
		
		getChildren().add(listBox);
		getChildren().add(buttons);
	}
	
	public List<WorkingOrder> getOrder() {
		return orders;
	}
	
	public void setOrder(List<WorkingOrder> orders) {
		this.orders = orders;
		updateOrder();
	}
	
	public void setFrames(List<WorkingFrame> frames) {
		frameList.clear();
		images.clear();
		
		int i = 0;
		for (WorkingFrame frame : frames) {
			String id = "Frame: " + i;
			
			frameList.add(id);
			images.put(id, frame.getImage());
			
			i++;
		}
		
		checkForErrors();
		updateOrder();
	}
	
	private void checkForErrors() {
		Iterator<WorkingOrder> iter = orders.iterator();
		while(iter.hasNext()) {
			WorkingOrder order = iter.next();
			if (order.getFrame() >= frameList.size()) {
				iter.remove();
			}
		}
	}
	
	private void updateOrder() {
		orderList.clear();
		
		for (WorkingOrder order : orders) {
			orderList.add(order.getFrame());
		}
	}
	
	private void sortList() {
		Collections.sort(orders, (WorkingOrder o1, WorkingOrder o2) -> {
			return o1.getIndex() - o2.getIndex();
		});
	}
	
	public void addOrder(int frame) {
		WorkingOrder order = new WorkingOrder(frame, orders.size());
		orders.add(order);
		
		updateOrder();
	}
	
	public void removeOrder(int i) {
		int index = orders.get(i).getIndex();
		orders.remove(i);
		
		for (WorkingOrder order : orders) {
			if (order.getIndex() > index) {
				order.setIndex(order.getIndex() - 1);
			}
		}
		
		updateOrder();
	}
	
	public void moveOrderUp(int i) {
		orders.get(i).setIndex(i-1);
		orders.get(i-1).setIndex(i);
		
		sortList();
		
		updateOrder();
	}
	
	public void moveOrderDown(int i) {
		orders.get(i).setIndex(i+1);
		orders.get(i+1).setIndex(i);
		
		sortList();
		
		updateOrder();
	}
	
	private void createHandlers() {
		buttons.setUpHandler((ActionEvent e) -> {
			if (selected > 0 && orderList.size() > 1) {
				moveOrderUp(selected);
			}
		});
		
		buttons.setDownHandler((ActionEvent e) -> {
			if (selected < orderList.size()-1 && selected >= 0 && orderList.size() > 1) {
				moveOrderDown(selected);
			}
		});
		
		buttons.setAddHandler((ActionEvent e) -> {
			int selected = frameView.getSelectionModel().getSelectedIndex();
			if (selected >= 0) {
				addOrder(selected);
			}
		});
		
		buttons.setRemoveHandler((ActionEvent e) -> {
			if (selected >= 0) {
				removeOrder(selected);
			}
		});
		
		orderView.getSelectionModel().selectedIndexProperty().addListener((ObservableValue<? extends Number> obs, Number old, Number selected) -> {
			this.selected = selected.intValue();
		});
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
				imageView.setFitWidth(50);
				imageView.setFitHeight(50);
				
				setGraphic(imageView);
				setText(item);
			}
		}
	}
}
