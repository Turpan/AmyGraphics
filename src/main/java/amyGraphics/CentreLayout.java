package amyGraphics;

import java.util.Iterator;
import java.util.Set;

public class CentreLayout extends Layout {

	@Override
	protected void layoutComponents(Component parent, Set<Component> children) {
		if (children.size() == 0) {
			return;
		}
		
		Iterator<Component> iter = children.iterator();
		
		Component child = iter.next();
		
		child.setWidth(child.getPreferredWidth());
		child.setHeight(child.getPreferredHeight());
		
		if (child.getWidth() < parent.getWidth() && child.getHeight() < parent.getHeight()) {
			int x = (parent.getWidth() - child.getWidth()) / 2;
			int y = (parent.getHeight() - child.getHeight()) / 2;
			
			child.setX(x + parent.getX());
			child.setY(y + parent.getY());
		} else {
			child.setX(parent.getX());
			child.setY(parent.getY());
			
			parent.setPreferredWidth(child.getWidth());
			parent.setPreferredHeight(child.getHeight());
			
			child.setWidth(parent.getWidth());
			child.setHeight(parent.getHeight());
		}
		
		while(iter.hasNext()) {
			Component other = iter.next();
			
			other.setVisible(false);
		}
	}

}
