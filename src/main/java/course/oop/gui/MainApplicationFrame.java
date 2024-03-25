package course.oop.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import course.oop.log.Logger;
import course.oop.saving.Saveable;
import course.oop.saving.FrameLoader;
import course.oop.saving.FrameSaver;
import course.oop.saving.FrameStatesManager;
import course.oop.saving.LoadException;
import course.oop.saving.SaveException;

/**
 * Класс главного окна программы
 */
public class MainApplicationFrame extends JFrame implements Saveable {
    /**
     * Контейнер, хранящий ссылки на окна-потомки
     * (так как при сворачивании окон, swing оказывается их убивает.
     * Следовательно Нужно сохранять ссылки на них)
     */
    private final List<Component> childs;

    /**
     * Создает главное окно программы
     */
    public MainApplicationFrame() {
        childs = new ArrayList<>();

        // Make the big window be indented 50 pixels from each edge
        // of the screen.
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset, screenSize.width - inset * 2, screenSize.height - inset * 2);

        setJMenuBar(new MainMenuBar(this));

        setContentPane(new JDesktopPane());

        addWindow(createLogWindow());
        addWindow(createGameWindow());

        loadWindowStates();

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                startExitDialog();
            }
        });
    }

    /**
     * Устанавливает системный внешний вид для главного окна.
     */
    public void setSystemLookAndFeel() {
        setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }

    /**
     * Устанавливает универсальный внешний вид для главного окна.
     */
    public void setCrossPlatformLookAndFeel() {
        setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
    }

    /**
     * Устанавливает внешний вид для главного окна на основе предоставленного класса
     * внешнего вида.
     * Вызывает UIManager.setLookAndFeel(className) и
     * SwingUtilities.updateComponentTreeUI(this).
     *
     * @param className Класс внешнего вида, который следует установить.
     * @see UIManager#setLookAndFeel
     */
    private void setLookAndFeel(String className) {
        try {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        } catch (ClassNotFoundException | InstantiationException
                | IllegalAccessException | UnsupportedLookAndFeelException e) {
            // just ignore
        }
    }

    /**
     * Выполняет процедуру выхода из программы.
     */
    private void startExitDialog() {
        int userChoice = JOptionPane.showConfirmDialog(
                this,
                "Вы уверены?",
                "Выйти",
                JOptionPane.YES_NO_OPTION);
        if (userChoice == JOptionPane.YES_OPTION) {
            saveWindowStates();
            setDefaultCloseOperation(EXIT_ON_CLOSE);
        }
    }

    /**
     * Добавляет переданное окно в это (главное).
     * формально в {@code desktopPane} - контейнер внутри этого окна.
     * и поле childs. Делает его видимым
     */
    private void addWindow(JInternalFrame frame) {
        getContentPane().add(frame);
        childs.add(frame);
        frame.setVisible(true);
    }

    /**
     * Создает окно лога
     */
    private LogWindow createLogWindow() {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        logWindow.setLocation(0, 0);
        logWindow.setSize(300, 500);
        Logger.debug("Протокол работает");
        return logWindow;
    }

    /**
     * Создает игровое окно
     */
    private GameWindow createGameWindow() {
        GameWindow gameWindow = new GameWindow();
        gameWindow.setLocation(300, 0);
        gameWindow.setSize(500, 500);
        return gameWindow;
    }

    /**
     * Сохраняет состояния дочерних окон и главного окна.
     */
    private void saveWindowStates() {
        FrameSaver frameSaver = new FrameStatesManager();
        frameSaver.addSaveableFrame(this);
        for (Component component : childs)
            if (component instanceof Saveable)
                frameSaver.addSaveableFrame((Saveable) component);

        try {
            frameSaver.save();
        } catch (SaveException e) {
            System.err.println("Не удалось сохранить окна");
            e.printStackTrace();
        }
    }

    /**
     * Загружает состояние главного окна и состояния дочерних окон,
     * если возможно.
     */
    private void loadWindowStates() {
        FrameLoader frameLoader = new FrameStatesManager();
        try {
            frameLoader.loadStates();
        } catch (LoadException e) {
            e.printStackTrace();
            return;
        }

        try {
            frameLoader.loadFrame(this);
        } catch (LoadException e) {
            e.printStackTrace();
        }

        for (Component component : childs) {
            if (component instanceof Saveable) {
                try {
                    frameLoader.loadFrame((Saveable) component);
                } catch (LoadException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Возвращает свой уникальный идентификатор
     */
    @Override
    public String getFrameId() {
        return "main";
    }
}
