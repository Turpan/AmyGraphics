package lucyAnimation;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

public class LucyMenu extends MenuBar{

	private Menu fileMenu;
	private Menu editMenu;
	private Menu viewMenu;

	private MenuItem newAnim;
	private MenuItem save;
	private MenuItem saveAs;
	private MenuItem load;
	private MenuItem exit;

	private MenuItem add;
	private MenuItem width;
	private MenuItem order;
	
	private MenuItem animRend;

	public LucyMenu() {
		super();
		createComponents();
	}

	private void createComponents() {
		fileMenu = new Menu("File");
		editMenu = new Menu("Edit");
		viewMenu = new Menu("View");

		newAnim = new MenuItem("New");
		save = new MenuItem("Save");
		saveAs = new MenuItem("Save As...");
		load = new MenuItem("Load");
		exit = new MenuItem("Exit");

		add = new MenuItem("Add Frame");
		width = new MenuItem("Set Width");
		order = new MenuItem("Configure Frame Order");
		
		animRend = new MenuItem("Animation");

		fileMenu.getItems().addAll(newAnim, save, saveAs, load, exit);
		editMenu.getItems().addAll(add, width, order);
		viewMenu.getItems().addAll(animRend);

		getMenus().add(fileMenu);
		getMenus().add(editMenu);
		getMenus().add(viewMenu);
	}

	public void setNewHandler(EventHandler<ActionEvent> handler) {
		newAnim.setOnAction(handler);
	}

	public void setSaveHandler(EventHandler<ActionEvent> handler) {
		save.setOnAction(handler);
	}

	public void setSaveAsHandler(EventHandler<ActionEvent> handler) {
		saveAs.setOnAction(handler);
	}

	public void setLoadHandler(EventHandler<ActionEvent> handler) {
		load.setOnAction(handler);
	}

	public void setExitHandler(EventHandler<ActionEvent> handler) {
		exit.setOnAction(handler);
	}

	public void setAddHandler(EventHandler<ActionEvent> handler) {
		add.setOnAction(handler);
	}

	public void setWidthHandler(EventHandler<ActionEvent> handler) {
		width.setOnAction(handler);
	}

	public void setOrderHandler(EventHandler<ActionEvent> handler) {
		order.setOnAction(handler);
	}
	
	public void setAnimationHandler(EventHandler<ActionEvent> handler) {
		animRend.setOnAction(handler);
	}

}
