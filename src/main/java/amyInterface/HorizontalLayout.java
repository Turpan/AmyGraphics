package amyInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class HorizontalLayout extends Layout {

	public static final int TOPALIGN = 0;
	public static final int CENTREALIGN = 1;
	public static final int BOTTOMALIGN = 2;

	public int alignment;

	public HorizontalLayout() {
		this(TOPALIGN);
	}

	public HorizontalLayout(int alignment) {
		setAlignment(alignment);
	}

	public void setAlignment(int alignment) {
		if (alignment < TOPALIGN || alignment > BOTTOMALIGN) {
			alignment = CENTREALIGN;
		}

		this.alignment = alignment;
	}

	@Override
	protected void layoutComponents(Component parent, Set<Component> children) {
		if (alignment == TOPALIGN) {
			layoutTop(parent, children);
		} else if (alignment == CENTREALIGN) {
			layoutCentre(parent, children);
		} else if (alignment == BOTTOMALIGN) {
			layoutBottom(parent, children);
		}
	}

	private void layoutTop(Component parent, Set<Component> children) {
		List<Component> childrenList = new ArrayList<Component>();
		childrenList.addAll(children);

		int maxWidth = parent.getWidth();
		int currentWidth = 0;

		int index = 0;

		for (Component child : children) {
			child.setX(parent.getX() + currentWidth);
			child.setY(parent.getY());
			child.setWidth(child.getPreferredWidth());
			child.setHeight(child.getPreferredHeight());

			currentWidth += child.getWidth();

			if (currentWidth > maxWidth) {
				break;
			}

			index++;
		}

		for (int i=index; i<childrenList.size(); i++) {
			Component child = childrenList.get(i);

			child.setX(parent.getRight());
			child.setY(parent.getY());
			child.setWidth(0);
			child.setHeight(0);
		}

		if (currentWidth < maxWidth) {
			int freeSpace = maxWidth - currentWidth;

			List<Component> resizables = new ArrayList<Component>();

			for (Component child : children) {
				if (child.isResizable()) {
					resizables.add(child);
				}
			}

			if (resizables.size() > 0) {
				freeSpace /= resizables.size();

				for (Component child : resizables) {
					int width = child.getWidth() + freeSpace;

					child.setWidth(width);

					index = childrenList.indexOf(child);

					for (int i=index+1; i<childrenList.size(); i++) {
						child = childrenList.get(i);
						child.setX(child.getX() + width);
					}
				}
			}
		}
	}

	private void layoutCentre(Component parent, Set<Component> children) {
		List<Component> childrenList = new ArrayList<Component>();
		childrenList.addAll(children);

		int maxWidth = parent.getWidth();
		int currentWidth = 0;

		int index = 0;

		for (Component child : children) {
			int y = 0;

			if (child.getPreferredHeight() < parent.getHeight()) {
				y = (parent.getHeight() - child.getPreferredHeight()) / 2;
			}

			child.setX(parent.getX() + currentWidth);
			child.setY(parent.getY() + y);
			child.setWidth(child.getPreferredWidth());
			child.setHeight(child.getPreferredHeight());

			currentWidth += child.getWidth();

			if (currentWidth > maxWidth) {
				break;
			}

			index++;
		}

		for (int i=index; i<childrenList.size(); i++) {
			Component child = childrenList.get(i);

			int y = 0;

			if (child.getPreferredHeight() < parent.getHeight()) {
				y = (parent.getHeight() - child.getPreferredHeight()) / 2;
			}

			child.setX(parent.getRight());
			child.setY(parent.getY() + y);
			child.setWidth(0);
			child.setHeight(0);
		}

		if (currentWidth < maxWidth) {
			int freeSpace = maxWidth - currentWidth;

			List<Component> resizables = new ArrayList<Component>();

			for (Component child : children) {
				if (child.isResizable()) {
					resizables.add(child);
				}
			}

			if (resizables.size() > 0) {
				freeSpace /= resizables.size();

				for (Component child : resizables) {
					int width = child.getWidth() + freeSpace;

					child.setWidth(width);

					index = childrenList.indexOf(child);

					for (int i=index+1; i<childrenList.size(); i++) {
						child = childrenList.get(i);
						child.setX(child.getX() + width);
					}
				}
			}
		}
	}

	private void layoutBottom(Component parent, Set<Component> children) {
		List<Component> childrenList = new ArrayList<Component>();
		childrenList.addAll(children);

		int maxWidth = parent.getWidth();
		int currentWidth = 0;

		int index = 0;

		for (Component child : children) {
			int y = 0;

			if (child.getPreferredHeight() < parent.getHeight()) {
				y = (parent.getHeight() - child.getPreferredHeight());
			}

			child.setX(parent.getX() + currentWidth);
			child.setY(parent.getY() + y);
			child.setWidth(child.getPreferredWidth());
			child.setHeight(child.getPreferredHeight());

			currentWidth += child.getWidth();

			if (currentWidth > maxWidth) {
				break;
			}

			index++;
		}

		for (int i=index; i<childrenList.size(); i++) {
			Component child = childrenList.get(i);

			int y = 0;

			if (child.getPreferredHeight() < parent.getHeight()) {
				y = (parent.getHeight() - child.getPreferredHeight());
			}

			child.setX(parent.getRight());
			child.setY(parent.getY() + y);
			child.setWidth(0);
			child.setHeight(0);
		}

		if (currentWidth < maxWidth) {
			int freeSpace = maxWidth - currentWidth;

			List<Component> resizables = new ArrayList<Component>();

			for (Component child : children) {
				if (child.isResizable()) {
					resizables.add(child);
				}
			}

			if (resizables.size() > 0) {
				freeSpace /= resizables.size();

				for (Component child : resizables) {
					int width = child.getWidth() + freeSpace;

					child.setWidth(width);

					index = childrenList.indexOf(child);

					for (int i=index+1; i<childrenList.size(); i++) {
						child = childrenList.get(i);
						child.setX(child.getX() + width);
					}
				}
			}
		}
	}

	public static Component createFreeSpace(int width, int height) {
		Component freeSpace = new Component();
		freeSpace.setPreferredSize(width, height);

		return freeSpace;
	}

	public static Component createGlue() {
		Component freeSpace = new Component();
		freeSpace.setResizable(true);

		return freeSpace;
	}
}
