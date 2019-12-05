package amyInterface;

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

		if (child.getWidth() < parent.getWidth()) {
			int x = (parent.getWidth() - child.getWidth()) / 2;

			child.setX(x + parent.getX());
		} else {
			child.setX(parent.getX());
			
			if (parent.getPreferredWidth() < child.getWidth()) parent.setPreferredWidth(child.getWidth());
			
			child.setWidth(parent.getWidth());
		}
		
		if (child.getHeight() < parent.getHeight()) {
			int y = (parent.getHeight() - child.getHeight()) / 2;
			
			child.setY(y + parent.getY());
		} else {
			child.setY(parent.getY());
			
			if (parent.getPreferredHeight() < child.getHeight()) parent.setPreferredHeight(child.getHeight());
			
			child.setHeight(parent.getHeight());
		}

		while(iter.hasNext()) {
			Component other = iter.next();

			other.setVisible(false);
		}
	}

}
