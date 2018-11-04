package amyInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class VerticalLayout extends Layout {
	
	public static final int LEFTALIGN = 0;
	public static final int CENTREALIGN = 1;
	public static final int RIGHTALIGN = 2;
	
	public int alignment;
	
	public VerticalLayout() {
		this(LEFTALIGN);
	}
	
	public VerticalLayout(int alignment) {
		setAlignment(alignment);
	}
	
	public void setAlignment(int alignment) {
		if (alignment < LEFTALIGN || alignment > RIGHTALIGN) {
			alignment = CENTREALIGN;
		}
		
		this.alignment = alignment;
	}

	@Override
	protected void layoutComponents(Component parent, Set<Component> children) {
		if (alignment == LEFTALIGN) {
			layoutLeft(parent, children);
		} else if (alignment == CENTREALIGN) {
			layoutCentre(parent, children);
		} else if (alignment == RIGHTALIGN) {
			layoutRight(parent, children);
		}
	}
	
	private void layoutLeft(Component parent, Set<Component> children) {
		List<Component> childrenList = new ArrayList<Component>();
		childrenList.addAll(children);
		
		int maxHeight = parent.getHeight();
		int currentHeight = 0;
		
		int index = 0;
		
		for (Component child : children) {
			child.setX(parent.getX());
			child.setY(parent.getY() + currentHeight);
			child.setWidth(child.getPreferredWidth());
			child.setHeight(child.getPreferredHeight());
			
			currentHeight += child.getHeight();
			
			if (currentHeight > maxHeight) {
				break;
			}
			
			index++;
		}
		
		for (int i=index; i<childrenList.size(); i++) {
			Component child = childrenList.get(i);
			
			child.setX(parent.getX());
			child.setY(parent.getY());
			child.setWidth(0);
			child.setHeight(0);
		}
		
		if (currentHeight < maxHeight) {
			int freeSpace = maxHeight - currentHeight;
			
			List<Component> resizables = new ArrayList<Component>();
			
			for (Component child : children) {
				if (child.isResizable()) {
					resizables.add(child);
				}
			}
			
			if (resizables.size() > 0) {
				freeSpace /= resizables.size();
				
				for (Component child : resizables) {
					int height = child.getHeight() + freeSpace;
					
					child.setHeight(height);
					
					index = childrenList.indexOf(child);
					
					for (int i=index+1; i<childrenList.size(); i++) {
						child = childrenList.get(i);
						child.setY(child.getY() + height);
					}
				}
			}
		}
	}
	
	private void layoutCentre(Component parent, Set<Component> children) {
		List<Component> childrenList = new ArrayList<Component>();
		childrenList.addAll(children);
		
		int maxHeight = parent.getHeight();
		int currentHeight = 0;
		
		int index = 0;
		
		for (Component child : children) {
			int x = 0;
			
			if (child.getPreferredWidth() < parent.getWidth()) {
				x = (parent.getWidth() - child.getPreferredWidth()) / 2;
			}
			
			child.setX(parent.getX() + x);
			child.setY(parent.getY() + currentHeight);
			child.setWidth(child.getPreferredWidth());
			child.setHeight(child.getPreferredHeight());
			
			currentHeight += child.getHeight();
			
			if (currentHeight > maxHeight) {
				break;
			}
			
			index++;
		}
		
		for (int i=index; i<childrenList.size(); i++) {
			Component child = childrenList.get(i);
			
			int x = 0;
			
			if (child.getPreferredWidth() < parent.getWidth()) {
				x = (parent.getWidth() - child.getPreferredWidth()) / 2;
			}
			
			child.setX(parent.getX() + x);
			child.setY(parent.getY());
			child.setWidth(0);
			child.setHeight(0);
		}
		
		if (currentHeight < maxHeight) {
			int freeSpace = maxHeight - currentHeight;
			
			List<Component> resizables = new ArrayList<Component>();
			
			for (Component child : children) {
				if (child.isResizable()) {
					resizables.add(child);
				}
			}
			
			if (resizables.size() > 0) {
				freeSpace /= resizables.size();
				
				for (Component child : resizables) {
					int height = child.getHeight() + freeSpace;
					
					child.setHeight(height);
					
					index = childrenList.indexOf(child);
					
					for (int i=index+1; i<childrenList.size(); i++) {
						child = childrenList.get(i);
						child.setY(child.getY() + height);
					}
				}
			}
		}
	}
	
	private void layoutRight(Component parent, Set<Component> children) {
		List<Component> childrenList = new ArrayList<Component>();
		childrenList.addAll(children);
		
		int maxHeight = parent.getHeight();
		int currentHeight = 0;
		
		int index = 0;
		
		for (Component child : children) {
			child.setX(parent.getRight());
			child.setY(parent.getY() + currentHeight);
			child.setWidth(child.getPreferredWidth());
			child.setHeight(child.getPreferredHeight());
			
			currentHeight += child.getHeight();
			
			if (currentHeight > maxHeight) {
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
		
		if (currentHeight < maxHeight) {
			int freeSpace = maxHeight - currentHeight;
			
			List<Component> resizables = new ArrayList<Component>();
			
			for (Component child : children) {
				if (child.isResizable()) {
					resizables.add(child);
				}
			}
			
			if (resizables.size() > 0) {
				freeSpace /= resizables.size();
				
				for (Component child : resizables) {
					int height = child.getHeight() + freeSpace;
					
					child.setHeight(height);
					
					index = childrenList.indexOf(child);
					
					for (int i=index+1; i<childrenList.size(); i++) {
						child = childrenList.get(i);
						child.setY(child.getY() + height);
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
