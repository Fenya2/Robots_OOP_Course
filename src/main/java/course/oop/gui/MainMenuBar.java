package course.oop.gui;

import java.io.File;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.JFileChooser;

import course.oop.locale.UserLocale;
import course.oop.locale.LocaleManager;
import course.oop.log.Logger;
import course.oop.moduleLoad.IModelService;
import course.oop.moduleLoad.LoaderUtils;
import course.oop.moduleLoad.ServiceLoadException;

/**
 * Главная строка меню программы.
 * Добавляется к окну {@link MainApplicationFrame}
 */
public class MainMenuBar extends JMenuBar {
    private final MainApplicationFrame mainFrame;

    /**
     * Создает меню для переданного главного окна
     */
    public MainMenuBar(MainApplicationFrame mainFrame) {
        this.mainFrame = mainFrame;
        add(createLoadModelMenu());
        add(createViewModeMenu());
        add(createTestsMenu());
        add(createSettingMenu());
        add(createQuitMenu());
    }

    /**
     * Возвращает пункт меню с навешанными обработчиками для загрузки модели.
     * Также добавляет пункт меню - вернуться к модели по умолчанию.
     */
    private JMenu createLoadModelMenu() {
        JMenu ret = new JMenu(LocaleManager.getString("load_model_menu"));
        ret.setMnemonic(KeyEvent.VK_L);
        JMenuItem loadFromJarItem = new JMenuItem(LocaleManager.getString("load_model_menu_from_jar"), KeyEvent.VK_J);
        loadFromJarItem.addActionListener((event) -> {
            JFileChooser fileDialog = new JFileChooser();
            int callback = fileDialog.showDialog(null, LocaleManager.getString("load_model_menu_open_file"));
            if (callback == JFileChooser.APPROVE_OPTION) {
                File file = fileDialog.getSelectedFile();
                IModelService modelService = null;
                try {
                    modelService = LoaderUtils.loadModelServiceFromJar(file);
                } catch (ServiceLoadException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(mainFrame, "Can't load jar-file", "Error", JOptionPane.DEFAULT_OPTION);
                    return;
                }
                if (modelService != null) {
                    modelService.init(file);
                    mainFrame.changeModel(modelService);
                    modelService.start();
                }
            }
        });

        JMenuItem resetItem = new JMenuItem(LocaleManager.getString("load_model_menu_reset"), KeyEvent.VK_R);
        resetItem.addActionListener((event) -> {
            mainFrame.dispose();
            SwingUtilities.invokeLater(() -> {
                MainApplicationFrame frame = new MainApplicationFrame();
                frame.setVisible(true);
            });
        });
        ret.add(loadFromJarItem);
        ret.add(resetItem);
        return ret;
    }

    /**
     * Создает пункт меню с режимами отображения.
     */
    private JMenu createViewModeMenu() {
        JMenu ret = new JMenu(LocaleManager.getString("view_mode_menu"));
        ret.setMnemonic(KeyEvent.VK_V);
        ret.getAccessibleContext().setAccessibleDescription(
                LocaleManager.getString("view_mode_menu.accessible_description"));

        JMenuItem systemSchemeMenuItem = new JMenuItem(LocaleManager.getString("system_scheme"), KeyEvent.VK_S);
        systemSchemeMenuItem.addActionListener((event) -> {
            mainFrame.setSystemLookAndFeel();
            this.invalidate();
        });
        ret.add(systemSchemeMenuItem);

        JMenuItem crossplatformLookAndFeel = new JMenuItem(LocaleManager.getString("universal_scheme"), KeyEvent.VK_U);
        crossplatformLookAndFeel.addActionListener((event) -> {
            mainFrame.setCrossPlatformLookAndFeel();
            this.invalidate();
        });
        ret.add(crossplatformLookAndFeel);
        return ret;
    }

    /**
     * Создает пункт меню с тестированием программы.
     */
    private JMenu createTestsMenu() {
        JMenu ret = new JMenu(LocaleManager.getString("test_menu"));
        ret.setMnemonic(KeyEvent.VK_T);
        ret.getAccessibleContext().setAccessibleDescription(
                LocaleManager.getString("test_menu.accessible_description"));
        JMenuItem addLogMessageItem = new JMenuItem(LocaleManager.getString("log_message"), KeyEvent.VK_M);
        addLogMessageItem.addActionListener((event) -> {
            Logger.debug(LocaleManager.getString("log.new_string"));
        });
        ret.add(addLogMessageItem);
        return ret;
    }

    /**
     * Создает пункт меню настроек
     */
    private JMenu createSettingMenu() {
        JMenu ret = new JMenu(LocaleManager.getString("settings_menu"));
        ret.setMnemonic(KeyEvent.VK_I);
        ret.getAccessibleContext().setAccessibleDescription(
                LocaleManager.getString("settings_menu.accessible_description"));
        ret.add(createChangeLanguageItem());
        return ret;
    }

    /**
     * Возвращает пункт меню для смены языка с написанным обработчиком:
     * Открывает диалог с пользователем на выбор языка. Если пользователь
     * выбирает язык, сохраняет окно, убивает его и создает новое.
     * Старое чистится сборщиком мусора и уходит с миром.
     */
    private JMenuItem createChangeLanguageItem() {
        LocaleManager localeManager = LocaleManager.getInstance();
        JMenuItem changeLanguageItem = new JMenuItem(LocaleManager.getString("settings_menu.change_lang"),
                KeyEvent.VK_M);
        changeLanguageItem.addActionListener((event) -> {
            SelectLanguageDialog userDialog = new SelectLanguageDialog(mainFrame);
            UserLocale result = userDialog.getChoosedUserLocale();
            if (result != null && localeManager.getCurrentLocale() != result) {
                localeManager.setAndSaveUserLocale(result);
                mainFrame.dispose();
                SwingUtilities.invokeLater(() -> {
                    MainApplicationFrame frame = new MainApplicationFrame();
                    frame.setVisible(true);
                });
                Logger.debug(LocaleManager.getString("log.change_lang"));
            }
        });
        return changeLanguageItem;
    }

    /**
     * Создает пункт меню c функцией выхода из программы.
     */
    private JMenu createQuitMenu() {
        JMenu ret = new JMenu(LocaleManager.getString("quit_menu"));
        ret.setMnemonic(KeyEvent.VK_Q);
        ret.getAccessibleContext().setAccessibleDescription(
                LocaleManager.getString("quit_menu.accessible_description"));
        JMenuItem quitItem = new JMenuItem(LocaleManager.getString("quit_menu"), KeyEvent.VK_Q);
        quitItem.addActionListener((event) -> {
            WindowEvent closeEvent = new WindowEvent(mainFrame, WindowEvent.WINDOW_CLOSING);
            Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(closeEvent);
        });
        ret.add(quitItem);
        return ret;
    }
}
