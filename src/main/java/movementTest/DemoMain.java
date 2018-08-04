package movementTest;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

import movement.Entity.MalformedEntityException;
import movement.Vectors.Vector.MalformedVectorException;

public class DemoMain {
	static JFrame frame;
	static JPanel mainPanel;
	static MovementDemo demo;
	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
					createAndShowGUI();
				} catch (MalformedEntityException | MalformedVectorException e) {
					e.printStackTrace();
				}
            }
        });
	}
	static void createAndShowGUI() throws MalformedEntityException, MalformedVectorException {
		mainPanel = new JPanel();
		mainPanel.setLayout(null);
		mainPanel.setBounds(0, 0, 600, 600);
		//mainPanel.setBackground(Color.BLUE);
		frame = new JFrame("Civilisation Clicker");
		frame.setLayout(null);
        frame.setFocusTraversalKeysEnabled(false);
        frame.requestFocusInWindow();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);
        frame.setIconImage(new ImageIcon("graphics/icons/city_game.png").getImage());
        frame.setUndecorated(true);
        frame.setVisible(true);
        frame.setResizable(false);
        frame.add(mainPanel);
        demo = new MovementDemo();
	}
}
