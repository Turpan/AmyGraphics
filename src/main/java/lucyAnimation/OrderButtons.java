package lucyAnimation;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

public class OrderButtons extends ButtonPanel{
	private Button add;

	public OrderButtons() {
		super();
	}

	@Override
	public void createComponents() {
		super.createComponents();

		add = new Button("Add");

		grid.add(add, 0, 1);

	}

	public void setAddHandler(EventHandler<ActionEvent> handler) {
		add.setOnAction(handler);
	}

}
