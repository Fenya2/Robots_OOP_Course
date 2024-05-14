package course.oop.gui;

import java.awt.BorderLayout;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;

import course.oop.controller.GameController;
import course.oop.locale.LocaleManager;
import course.oop.model.GameModel;
import course.oop.saving.Saveable;

public class GameWindow extends JInternalFrame implements Saveable {
    private final GameVisualizer m_visualizer;

    public GameWindow(GameController gameController, GameModel gameModel) {
        super(LocaleManager.getString("game_window.title"), true, true, true, true);
        setLocation(500, 0);
        setSize(500, 500);
        m_visualizer = new GameVisualizer(gameController, gameModel);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_visualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
    }

    /**
     * Возвращает свой уникальный идентификатор
     */
    @Override
    public String getFrameId() {
        return "game";
    }
}
