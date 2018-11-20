package amyInterface;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import amyGraphics.Texture;

public class Label extends Component {
	public static final int LEFTALIGN = 0;
	public static final int CENTREALIGN = 1;
	public static final int RIGHTALIGN = 2;

	public static final Color base = Color.BLACK;

	private String text;

	private FontTexture font;

	private int alignment;

	private int fontSize;

	private Color fontColor;

	private Set<Component> letters = new LinkedHashSet<Component>();

	private Layout textLayout = new TextLayout();

	public Label() {
		super();

		setFontColour(base);

		setTextAlignment(LEFTALIGN);

		setFontSize(16);
	}

	public Label(String text) {
		this();
		setText(text);
	}

	public void setText(String text) {
		if (text == null) {
			text = "";
		}

		this.text = text;

		rebuildLetters();
	}

	public String getText() {
		return text;
	}

	public void setFont(FontTexture font) {
		this.font = font;
	}

	public FontTexture getFont() {
		return font;
	}

	public void setFontSize(int size) {
		this.fontSize = size;
	}

	public int getFontSize() {
		return fontSize;
	}

	public void setFontColour(Color color) {
		this.fontColor = color;
	}

	public Color getFontColour() {
		return fontColor;
	}

	public void setTextAlignment(int alignment) {
		if (alignment < LEFTALIGN || alignment > RIGHTALIGN) {
			alignment = LEFTALIGN;
		}

		this.alignment = alignment;

		layoutLetters();
	}

	public int getTextAlignment() {
		return alignment;
	}

	public List<Component> getLetters() {
		List<Component> letters = new ArrayList<Component>();

		letters.addAll(this.letters);

		return Collections.unmodifiableList(letters);
	}

	private void rebuildLetters() {
		letters.clear();

		char[] chars = getText().toCharArray();

		for (char c : chars) {
			if (c == '\n' || c == '\r') {
				continue;
			}

			Component letter = new Component();
			Texture letterTexture = font.getRenderTarget(c);

			letter.addTexture(letterTexture);
			letter.setActiveTexture(letterTexture);

			letters.add(letter);
		}

		layoutLetters();
	}

	private void layoutLetters() {
		textLayout.layoutComponents(this, letters);
	}

	@Override
	protected void setX(int x) {
		super.setX(x);

		layoutLetters();
	}

	@Override
	protected void setY(int y) {
		super.setY(y);

		layoutLetters();
	}

	@Override
	protected void setWidth(int width) {
		super.setWidth(width);

		layoutLetters();
	}

	@Override
	protected void setHeight(int height) {
		super.setHeight(height);

		layoutLetters();
	}

	@Override
	protected void callUpdate() {
		super.callUpdate();

		layoutLetters();
	}

	private class TextLayout extends Layout {

		@Override
		protected void layoutComponents(Component parent, Set<Component> children) {
			if (!(parent instanceof Label)) {
				return;
			}

			if (children == null || children.size() == 0) {
				return;
			}

			Label label = (Label) parent;

			int alignment = label.getTextAlignment();

			List<Component> components = new ArrayList<Component>();
			components.addAll(children);

			if (alignment == LEFTALIGN) {
				leftAlign(label, components);
			} else if (alignment == CENTREALIGN) {
				centreAlign(label, components);
			} else if (alignment == RIGHTALIGN) {
				rightAlign(label, components);
			}
		}

		private void leftAlign(Label parent, List<Component> children) {
			int lines = 1;

			int maxWidth = 0;

			int lineHeight = parent.getFontSize();
			double widthFactor = (double) lineHeight / parent.getFont().getHeight();

			int x = 0;
			int y = 0;

			int i = 0;
			char[] chars = parent.getText().toCharArray();

			for (char c : chars) {
				if (c == '\n') {
					maxWidth = Math.max(maxWidth, x);

					x = 0;
					y += lineHeight;

					lines++;
					continue;
				}

				Component child = children.get(i);
				i++;

				int width = (int) (child.getActiveTexture().getWidth() * widthFactor);

				/*if ((x + width) > maxWidth false) {
					x = 0;
					y += lineHeight;

					lines++;
				} else {
					x += width;
				}*/

				child.setX(parent.getX() + x);
				child.setY(parent.getY() + y);
				child.setWidth(width);
				child.setHeight(lineHeight);

				x += width;
			}

			maxWidth = Math.max(maxWidth, x);
			int maxHeight = lineHeight * lines;

			if (parent.getPreferredWidth() != maxWidth || parent.getPreferredHeight() != maxHeight) {
				parent.setPreferredSize(maxWidth, maxHeight);
			}
		}

		private void centreAlign(Label parent, List<Component> children) {
			//TODO
		}

		private void rightAlign(Label parent, List<Component> children) {
			//TODO
		}

	}
}
