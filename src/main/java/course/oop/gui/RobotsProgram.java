package course.oop.gui;

import javax.swing.SwingUtilities;

public class RobotsProgram {
  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      MainApplicationFrame frame = new MainApplicationFrame();
      frame.setVisible(true);
    });
  }
}