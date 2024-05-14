package course.oop.saving;

import java.beans.PropertyVetoException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JInternalFrame;

import org.apache.commons.io.FileUtils;

/**
 * Осуществляет сохранение и загрузку окон
 */
public class FrameStatesManager {
    /**
     * Файл, куда сохраняются состояния окон.
     */
    private File saveLocation;
    /**
     * Map для хранения состояний окон
     */
    private Map<String, FrameConfig> states = new HashMap<>();

    /**
     * Пустой конструктор. Устанавливает путь для сохранения такой, какой
     * требует вторая задача: $HOME/Robots/config/windowStates.conf
     */
    public FrameStatesManager() {
        saveLocation = new File(
                System.getProperty("user.home") +
                        File.separator + "Robots" +
                        File.separator + "config" +
                        File.separator + "windowStates.conf");
    }

    /**
     * Конструктор с указанием файла для сохранения
     */
    public FrameStatesManager(File saveLocation) {
        this.saveLocation = saveLocation;
    }

    public void setSaveLocation(File location) {
        saveLocation = location;
    }

    public File getSaveLocation() {
        return saveLocation;
    }

    /**
     * Извлекает данные для сохранения у переданного окна и сохраняет
     * их во внутренний map.
     */
    public void addSaveableFrame(Saveable saveable) {
        if (saveable instanceof JFrame jf) {
            states.put(saveable.getFrameId(), getFrameConfigForJFrame(jf));
            return;
        }
        if (saveable instanceof JInternalFrame jif) {
            states.put(saveable.getFrameId(), getFrameConfigForJInternalFrame(jif));
            return;
        }
        throw new RuntimeException("Переданный тип нельзя сохранять");
    }

    /**
     * сохраняет поле states в указанный в полях файл.
     */
    public void save() throws SaveException {
        try {
            FileUtils.forceMkdir(saveLocation.getParentFile());
        } catch (IOException e) {
            throw new SaveException("Не удалось создать иерархию директорий для сохранения");
        }

        try {
            saveLocation.createNewFile();
        } catch (IOException e) {
            throw new SaveException("Не удалось создать конфигурационный файл");
        }

        try (OutputStream os = new FileOutputStream(saveLocation);
                ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(os))) {
            oos.writeObject(states);
        } catch (FileNotFoundException e) {
            throw new SaveException("Не удалось создать поток вывода. Файл конфига не найден");
        } catch (IOException e) {
            throw new SaveException("Не удалось записать состояния в файл");
        }
    }

    /**
     * Возвращает состояние переданного JFrame
     */
    private FrameConfig getFrameConfigForJFrame(JFrame jFrame) {
        return new FrameConfig(
                jFrame.getSize(),
                jFrame.getLocation(),
                false);
    }

    /**
     * Возвращает состояние переданного JInternalFrame
     */
    private FrameConfig getFrameConfigForJInternalFrame(JInternalFrame jInternalFrame) {
        return new FrameConfig(
                jInternalFrame.getSize(),
                jInternalFrame.getLocation(),
                jInternalFrame.isIcon());
    }

    /**
     * Читает указанный конфигурационный файл, заполняя внутреннее поле states
     */
    @SuppressWarnings("unchecked")
    public void loadStates() throws LoadException {
        try {
            InputStream is = new FileInputStream(saveLocation);
            ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(is));
            try {
                states = (HashMap<String, FrameConfig>) ois.readObject();
            } catch (ClassNotFoundException e) {
                throw new LoadException("Не удалось прочитать файл. класс hashmap не нашелся.");
            } catch (ClassCastException e) {
                throw new LoadException("не удалось выполнить преобразование к типу");
            } catch (Exception e) {
                throw new LoadException("unsupported exception");
            }
            finally {
                ois.close();
                is.close();
            }
        } catch (FileNotFoundException e) {
            throw new LoadException("Не удалось найти файл конфигурации");
        } catch (IOException e) {
            throw new LoadException("Не удалось создать иерархию фильтров");
        }
    }

    /**
     * Пытается найти сохраненное состояние для указанного окна. Если его нет,
     * поднимает ошибку, что загружать нечего.
     */
    public void loadFrame(Saveable saveable) throws LoadException {
        if (!states.containsKey(saveable.getFrameId())) {
            System.err.println("Нет данных для загрузки окна %s".formatted(saveable.getFrameId()));
            return;
        }
        if (saveable instanceof JFrame jf) {
            loadJFrame(jf, states.get(saveable.getFrameId()));
        } else if (saveable instanceof JInternalFrame jif) {
            loadJInternalFrame(jif, states.get(saveable.getFrameId()));
        } else {
            System.err.println("Переданный тип нельзя загрузить");
        }
    }

    /**
     * Загружает переданное состояние в переданный JFrame
     */
    private void loadJFrame(JFrame jFrame, FrameConfig frameConfig) {
        jFrame.setSize(frameConfig.getSize().toDimension());
        jFrame.setLocation(frameConfig.getLocation().toPoint());
    }

    /**
     * Загружает переданное состояние в переданный JinternalFrame
     */
    private void loadJInternalFrame(JInternalFrame jInternalFrame, FrameConfig frameConfig) {
        jInternalFrame.setSize(frameConfig.getSize().toDimension());
        jInternalFrame.setLocation(frameConfig.getLocation().toPoint());
        try {
            jInternalFrame.setIcon(frameConfig.isIcon());
        } catch (PropertyVetoException e) {
            System.err.println("Не удалось свернуть окно");
            e.printStackTrace();
        }
    }
}
