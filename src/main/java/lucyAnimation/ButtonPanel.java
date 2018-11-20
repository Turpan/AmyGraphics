package lucyAnimation;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class ButtonPanel extends HBox{

	protected Button remove;
	protected Button up;
	protected Button down;

	protected GridPane grid;

	public ButtonPanel() {
		super();
		createComponents();
	}

	public void createComponents() {
		HBox whiteSpace = new HBox();

		grid = new GridPane();

		remove = new Button("Remove");
		up = new Button("Up");
		down = new Button("Down");

		grid.add(remove, 2, 1);
		grid.add(up, 0, 0);
		grid.add(down, 2, 0);

		getChildren().add(whiteSpace);
		getChildren().add(grid);

		HBox.setHgrow(whiteSpace, Priority.ALWAYS);
		HBox.setHgrow(grid, Priority.NEVER);
	}

	public void setRemoveHandler(EventHandler<ActionEvent> handler) {
		remove.setOnAction(handler);
	}

	public void setUpHandler(EventHandler<ActionEvent> handler) {
		up.setOnAction(handler);
	}

	public void setDownHandler(EventHandler<ActionEvent> handler) {
		down.setOnAction(handler);
	}
}
