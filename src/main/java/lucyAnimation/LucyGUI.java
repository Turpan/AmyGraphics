package lucyAnimation;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import amyGraphics.Animation;
import amyGraphics.Animation.MalformedAnimationException;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class LucyGUI extends HBox{

	static final Color clearColor = Color.BLACK;

	private AnimRenderer renderer;
	private FramePanel panel;
	private OrderManager orderManager;
	private List<WorkingFrame> frameList;
	private int selected = -1;

	public LucyGUI() {
		super();
		createComponents();
		setWidth(1);
	}

	private void createComponents() {
		orderManager = new OrderManager();

		renderer = new AnimRenderer();
		VBox imBox = new VBox();
		imBox.getChildren().add(renderer);
		getChildren().add(imBox);

		panel = new FramePanel();
		getChildren().add(panel);

		VBox.setVgrow(renderer, Priority.ALWAYS);
		VBox.setVgrow(panel, Priority.ALWAYS);

		HBox.setHgrow(imBox, Priority.ALWAYS);
		HBox.setHgrow(panel, Priority.NEVER);

		frameList = new ArrayList<WorkingFrame>();

		setEventHandlers();
	}

	public void editOrder() {
		List<WorkingOrder> backup = new ArrayList<WorkingOrder>();
		backup.addAll(orderManager.getOrder());

		Alert orderBox = new Alert(AlertType.CONFIRMATION);
		orderBox.setTitle("Order Manager");
		orderBox.setHeaderText("Configure Order: ");
		orderBox.getDialogPane().setContent(orderManager);

		Optional<ButtonType> result = orderBox.showAndWait();
		if (result.get() != ButtonType.OK) {
			orderManager.setOrder(backup);
			return;
		}

		LucyMain.setUnsaved(true);
	}

	public void saveAnimation(String file) {
		if (!canCompile()) {
			Alert error = new Alert(AlertType.WARNING);
			error.setTitle("Compile Failed");
			error.setHeaderText(null);
			error.setContentText("Either there are zero frames or zero entries in the order list."
					+ System.lineSeparator() + "Can not save until this is fixed.");
			error.showAndWait();
			return;
		}

		Animation anim = compileAnimation();

		if (anim == null) {
			Alert error = new Alert(AlertType.ERROR);
			error.setTitle("Compile Failed");
			error.setHeaderText(null);
			error.setContentText("Something caused the animation file not to compile."
					+ System.lineSeparator() + "Please send the developer a screenshot of your workspace,"
					+ " and any action you took preceding this.");
			error.showAndWait();
			return;
		}

		try {
			LucyIO.writeLucyFile(anim, file);
		} catch (IOException e) {
			Alert error = new Alert(AlertType.ERROR);
			error.setTitle("Save Failed");
			error.setHeaderText(null);
			error.setContentText("Something caused the animation file not to save."
					+ System.lineSeparator() + "Please check that you have access privelages to the file directory,"
					+ "and that the place you are saving to exists.");
			error.showAndWait();
			return;
		}
		LucyMain.setUnsaved(false);
		LucyMain.setCurrentFile(file);
	}

	public void newAnimation() {
		frameList.clear();
		renderer.setWidth(1);
		orderManager.setOrder(new ArrayList<WorkingOrder>());
		update();
		LucyMain.resizeCall();
		LucyMain.setCurrentFile("");
	}

	public void loadAnimation(String file) {
		Animation anim = LucyIO.readLucyFile(file);
		if (anim == null) {
			Alert error = new Alert(AlertType.ERROR);
			error.setTitle("Load Failed");
			error.setHeaderText(null);
			error.setContentText("The file could not be loaded."
					+ System.lineSeparator() + "Please check that this is a .LCY file."
					+ System.lineSeparator() + "If not then the file may have been corrupted.");
			error.showAndWait();
			return;
		}

		frameList.clear();

		int width = anim.getSprite().getWidth() / anim.getFrameWidth();
		int height = anim.getSprite().getHeight() / anim.getFrameHeight();
		int total = width * height;

		int frameHeight = anim.getFrameHeight();
		int frameWidth = anim.getFrameWidth();

		int x = 0;
		int y = 0;

		Image[] frames = new Image[total];

		for (int i=0; i<height; i++) {
			for (int j=0; j<width; j++) {
				frames[(i*width) + j] = SwingFXUtils.toFXImage(anim.getSprite().getSubimage(x, y, frameWidth, frameHeight), null);
				x += frameWidth;
			}
			x = 0;
			y += frameHeight;
		}

		for (Image image : frames) {
			WorkingFrame frame = new WorkingFrame(image, frameList.size());
			frameList.add(frame);
		}

		List<WorkingOrder> orderList = new ArrayList<WorkingOrder>();
		for (int i : anim.getFrameOrder()) {
			WorkingOrder order = new WorkingOrder(i, orderList.size());
			orderList.add(order);
		}

		update();
		renderer.setWidth(width);
		orderManager.setOrder(orderList);
		LucyMain.resizeCall();
		LucyMain.setUnsaved(false);
		LucyMain.setCurrentFile(file);
	}

	public void update() {
		List<Image> images = new ArrayList<Image>();
		for (WorkingFrame workingframe : frameList) {
			images.add(workingframe.getImage());
		}
		renderer.setFrames(images);
		panel.updateList(frameList);
		orderManager.setFrames(frameList);
	}

	public boolean addFrame(File file) {
		Image image;
		try {
			image = new Image(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			return false;
		}

		if (frameList.size() > 0) {
			Image im = frameList.get(0).getImage();
			if (im.getWidth() != image.getWidth()) {
				return false;
			}
		}

		WorkingFrame frame = new WorkingFrame(image, frameList.size());
		frameList.add(frame);

		LucyMain.setUnsaved(true);

		update();

		return true;
	}

	public void removeFrame(int i) {
		int index = frameList.get(i).getIndex();
		frameList.remove(i);

		for (WorkingFrame frame : frameList) {
			if (frame.getIndex() > index) {
				frame.setIndex(frame.getIndex() - 1);
			}
		}

		LucyMain.setUnsaved(true);

		update();
		LucyMain.resizeCall();
	}

	public void moveFrameUp(int i) {
		frameList.get(i).setIndex(i-1);
		frameList.get(i-1).setIndex(i);

		LucyMain.setUnsaved(true);

		sortList();
		update();
	}

	public void moveFrameDown(int i) {
		frameList.get(i).setIndex(i+1);
		frameList.get(i+1).setIndex(i);

		LucyMain.setUnsaved(true);

		sortList();
		update();
	}

	private void sortList() {
		Collections.sort(frameList, (WorkingFrame f1, WorkingFrame f2) -> {
			return f1.getIndex() - f2.getIndex();
		});
	}

	public void setSelected(int index) {
		this.selected = index;
		renderer.setSelected(selected);
	}

	public void setWidth(int width) {
		renderer.setWidth(width);

		LucyMain.setUnsaved(true);
	}

	private boolean canCompile() {
		return (frameList.size() > 0 && orderManager.getOrder().size() > 0);
	}

	private Animation compileAnimation() {
		BufferedImage animImage = compileImage();

		int frameWidth = (int) frameList.get(0).getImage().getWidth();
		int frameHeight = (int) frameList.get(0).getImage().getHeight();

		int[] order = compileOrder();

		try {
			return new Animation(animImage, frameWidth, frameHeight, order);
		} catch (MalformedAnimationException e) {
			return null;
		}
	}

	private BufferedImage compileImage() {
		int frameWidth = renderer.getFrameWidth();

		int width = (int) renderer.getWidth();
		int height = (int) renderer.getHeight();

		int imageWidth = (int) frameList.get(0).getImage().getWidth();
		int imageHeight = (int) frameList.get(0).getImage().getHeight();

		int x = 0;
		int y = 0;
		int count = 0;

		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		Graphics gc = image.getGraphics();
		gc.setColor(clearColor);

		while (count < frameList.size()) {
			BufferedImage frame = SwingFXUtils.fromFXImage(frameList.get(count).getImage(), null);

			gc.drawImage(frame, x, y, null);
			x += imageWidth;
			if ((count+1) % frameWidth == 0) {
				x = 0;
				y += imageHeight;
			}
			count++;
		}

		for (int i=0; i<frameWidth - ((count) % frameWidth); i++) {
			gc.fillRect(x, y, imageWidth, imageHeight);
			x += imageWidth;
		}

		return image;
	}

	private int[] compileOrder() {
		List<WorkingOrder> orderList = orderManager.getOrder();
		int[] order = new int[orderList.size()];

		for (int i=0; i<order.length; i++) {
			order[i] = orderList.get(i).getFrame();
		}

		return order;
	}

	private void setEventHandlers() {
		panel.setUpHandler((ActionEvent e) -> {
			if (selected > 0 && frameList.size() > 1) {
				moveFrameUp(selected);
			}
		});

		panel.setDownHandler((ActionEvent e) -> {
			if (selected < frameList.size()-1 && selected >= 0 && frameList.size() > 1) {
				moveFrameDown(selected);
			}
		});

		panel.setRemoveHandler((ActionEvent e) -> {
			if (selected >= 0) {
				removeFrame(selected);
			}
		});

		panel.addSelectionListener((ObservableValue<? extends Number> obs, Number old, Number selected) -> {
			setSelected(selected.intValue());
		});
	}
}
