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

import course.oop.controller.GameController;
import course.oop.locale.LocaleManager;
import course.oop.log.Logger;
import course.oop.model.GameModel;
import course.oop.saving.Saveable;
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
        LocaleManager localeManager = LocaleManager.getInstance();
        try {
            UIManager.put("OptionPane.yesButtonText",
                    localeManager.getString("option_pane_yes"));
            UIManager.put("OptionPane.noButtonText", localeManager.getString("option_pane_no"));
        } catch (Exception e) {
            System.err.println("Не удалось изменить заголовки JOptionPane.");
            e.printStackTrace();
        }
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            System.err.println("Не удалось задать визуальный стиль программы");
            e.printStackTrace();
        }
        childs = new ArrayList<>();

        // Make the big window be indented 50 pixels from each edge
        // of the screen.
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset, screenSize.width - inset * 2, screenSize.height - inset * 2);

        setJMenuBar(new MainMenuBar(this));

        setContentPane(new JDesktopPane());

        GameModel gameModel = new GameModel();
        GameController gameController = new GameController(gameModel);
        addWindow(new LogWindow(Logger.getDefaultLogSource()));
        addWindow(new GameWindow(gameController, gameModel));
        addWindow(new RobotLocationWindow(gameModel));

        loadWindowStates();

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                startExitDialog();
            }
        });

        gameController.start();
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
                LocaleManager.getString("exit_dialog.are_you_sure"),
                LocaleManager.getString("exit_dialog.exit"),
                JOptionPane.YES_NO_OPTION);
        if (userChoice == JOptionPane.YES_OPTION) {
            this.dispose();
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
     * Сохраняет состояния дочерних окон и главного окна.
     */
    private void saveWindowStates() {
        FrameStatesManager frameSaver = new FrameStatesManager();
        frameSaver.addSaveableFrame(this);
        for (Component component : childs)
            if (component instanceof Saveable saveable)
                frameSaver.addSaveableFrame(saveable);

        try {
            frameSaver.save();
        } catch (SaveException e) {
            System.err.println("Не удалось сохранить окна в файл конфигурации");
            e.printStackTrace();
        }
    }

    /**
     * Загружает состояние главного окна и состояния дочерних окон,
     * если возможно.
     */
    private void loadWindowStates() {
        FrameStatesManager frameLoader = new FrameStatesManager();
        try {
            frameLoader.loadStates();
        } catch (LoadException e) {
            System.err.println("Не удалось загрузить состояния окон из файла конфигурации");
            e.printStackTrace();
            return;
        }

        try {
            frameLoader.loadFrame(this);
        } catch (LoadException e) {
            System.err.println("Не удалось загрузить состояние для "
                    + getFrameId());
            e.printStackTrace();
        }

        for (Component component : childs) {
            if (component instanceof Saveable saveable) {
                try {
                    frameLoader.loadFrame(saveable);
                } catch (LoadException e) {
                    System.err.println("Не удалось загрузить состояние для "
                            + saveable.getFrameId());
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Переопределяет удаление окна с обязательным сохранением окон
     * Сохраняет состояния окон перед удалением
     */
    @Override
    public void dispose() {
        saveWindowStates();
        super.dispose();
    }

    /**
     * Возвращает свой уникальный идентификатор
     */
    @Override
    public String getFrameId() {
        return "main";
    }
}
