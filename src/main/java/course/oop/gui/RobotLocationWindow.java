package course.oop.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;

import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.vecmath.Vector2d;

import course.oop.locale.LocaleManager;
import course.oop.model.GameModel;
import course.oop.model.GameModelEvents;
import course.oop.saving.Saveable;

/**
 * Окно, выводящее текущие координаты робота
 */
public class RobotLocationWindow extends JInternalFrame implements Saveable, PropertyChangeListener {
    /** Координата x робота */
    private JLabel xCoordValue;
    /** Координата y робота */
    private JLabel yCoordValue;

    /**
     * Формат вывода координат робота
     */
    private DecimalFormat doubleFormatter = new DecimalFormat("#.##");

    /**
     * Создает окно, подписывается на изменения переданной модели.
     */
    public RobotLocationWindow(GameModel gameModel) {
        super(LocaleManager.getInstance().getString("robot_location_window.title"), true, true, true, true);
        gameModel.addPropertyChangeListener(this);

        setLocation(1000, 0);
        setSize(300, 100);

        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.add(new JLabel("x"));
        xCoordValue = new JLabel("0");
        panel.add(xCoordValue);
        panel.add(new JLabel("y"));
        yCoordValue = new JLabel("0");
        panel.add(yCoordValue);

        setLayout(new BorderLayout());
        add(new JLabel(LocaleManager.getInstance().getString("robot_location_window.title"), SwingConstants.CENTER),
                BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if (event.getPropertyName().equals(GameModelEvents.UPDATE.toString())) {
            GameModel gameModel = (GameModel) event.getNewValue();
            updateModelView(gameModel);
        }
    }

    @Override
    public String getFrameId() {
        return "robot_location";
    }

    /**
     * Обновляет текстовые поля gui координатами робота у указанной модели
     */
    private void updateModelView(GameModel gameModel) {
        Vector2d robot = gameModel.getRobot();
        SwingUtilities.invokeLater(() -> {
            xCoordValue.setText(doubleFormatter.format(robot.x));
            yCoordValue.setText(doubleFormatter.format(robot.y));
        });
    }
}
