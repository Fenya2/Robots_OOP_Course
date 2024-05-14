package course.oop.saving;

import java.awt.Dimension;
import java.awt.Point;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import course.oop.controller.GameController;
import course.oop.gui.GameWindow;
import course.oop.gui.MainApplicationFrame;
import course.oop.model.GameModel;

/**
 * Тестирует класс course.oop.saving.FrameStatesManager
 */
public class FrameStatesManagerTest {
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    /**
     * Подготавливает контекст для тестирования класса:
     *  - перенаправляет стандартный поток вывода ошибок в никуда
     *  - удаляет файл с сохраненными окнами (новая сборка - новый файл)(если возможно)
     */
    @Before
    public void prepareTesting() {
        System.setErr(new PrintStream(new OutputStream() {
                @Override
                public void write(int arg0) throws IOException {
                        // Чтобы не забивать stdout
                }
        }));
    }

    /**
     * Проверяет, что корректно сохраняется внутренний map JFrame
     * и JInternalFrame с реализованными Saveable
     */
    @Test
    public void testCorrectSaving() throws IOException, PropertyVetoException, SaveException, ClassNotFoundException {
        FrameStatesManager frameStatesManager = new FrameStatesManager();
        frameStatesManager.setSaveLocation(tempFolder.newFile());

        MainApplicationFrame mainApplicationFrame = new MainApplicationFrame();
        mainApplicationFrame.setSize(new Dimension(1, 2));
        mainApplicationFrame.setLocation(new Point(3, 4));

        GameModel gameModel = new GameModel();
        GameController gameController = new GameController(gameModel);
        GameWindow gameWindow = new GameWindow(gameController, gameModel);
        gameWindow.setSize(new Dimension(5, 6));
        gameWindow.setLocation(new Point(7, 8));
        gameWindow.setIcon(true);

        frameStatesManager.addSaveableFrame((Saveable) mainApplicationFrame);
        frameStatesManager.addSaveableFrame((Saveable) gameWindow);
        frameStatesManager.save();

        Map<String, FrameConfig> actual;
        try (InputStream is = new FileInputStream(frameStatesManager.getSaveLocation());
                ObjectInputStream ois = new ObjectInputStream(is)) {
            actual = (HashMap) ois.readObject();
        }
        Assert.assertEquals(
                new FrameConfig(
                        new Pair(1, 2),
                        new Pair(3, 4),
                        false),
                actual.get("main"));
        Assert.assertEquals(
                new FrameConfig(
                        new Pair(5, 6),
                        new Pair(7, 8),
                        true),
                actual.get("game"));
    }

    /**
     * Проверяет, что корректно загружаются сохраненные ранее состояния окон
     */
    @Test
    public void testCorrectLoading() throws IOException, PropertyVetoException, SaveException, LoadException {
        FrameStatesManager frameStatesManager = new FrameStatesManager();
        frameStatesManager.setSaveLocation(tempFolder.newFile());

        File oldLocation = frameStatesManager.getSaveLocation();

        MainApplicationFrame mainApplicationFrame = new MainApplicationFrame();
        mainApplicationFrame.setSize(new Dimension(1, 2));
        mainApplicationFrame.setLocation(new Point(3, 4));

        GameModel gameModel = new GameModel();
        GameController gameController = new GameController(gameModel);
        GameWindow gameWindow = new GameWindow(gameController, gameModel);
        gameWindow.setSize(new Dimension(5, 6));
        gameWindow.setLocation(new Point(7, 8));
        gameWindow.setIcon(true);

        frameStatesManager.addSaveableFrame((Saveable) mainApplicationFrame);
        frameStatesManager.addSaveableFrame((Saveable) gameWindow);

        frameStatesManager.save();

        frameStatesManager = new FrameStatesManager();
        frameStatesManager.setSaveLocation(oldLocation);

        MainApplicationFrame mainApplicationFrameLoaded = new MainApplicationFrame();
        GameWindow gameWindowLoaded = new GameWindow(gameController, gameModel);

        frameStatesManager.loadStates();

        frameStatesManager.loadFrame(mainApplicationFrameLoaded);
        frameStatesManager.loadFrame(gameWindowLoaded);

        Assert.assertEquals(mainApplicationFrame.getSize(), mainApplicationFrameLoaded.getSize());
        Assert.assertEquals(mainApplicationFrame.getLocation(), mainApplicationFrameLoaded.getLocation());
        Assert.assertEquals(gameWindow.getLocation(), gameWindowLoaded.getLocation());
        Assert.assertEquals(gameWindow.getLocation(), gameWindowLoaded.getLocation());
        Assert.assertEquals(gameWindow.isIcon(), gameWindow.isIcon());
    }
}
