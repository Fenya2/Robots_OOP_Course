package course.oop.gui;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

import course.oop.locale.UserLocale;
import course.oop.locale.LocaleManager;
import course.oop.log.Logger;

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
        add(createViewModeMenu());
        add(createTestsMenu());
        add(createSettingMenu());
        add(createQuitMenu());
    }

    /**
     * Создает пункт меню с режимами отображения.
     */
    private JMenu createViewModeMenu() {
        LocaleManager localeManager = LocaleManager.getInstance();
        JMenu ret = new JMenu(localeManager.getString("view_mode_menu"));
        ret.setMnemonic(KeyEvent.VK_V);
        ret.getAccessibleContext().setAccessibleDescription(
                localeManager.getString("view_mode_menu.accessible_description"));

        JMenuItem systemSchemeMenuItem = new JMenuItem(localeManager.getString("system_scheme"), KeyEvent.VK_S);
        systemSchemeMenuItem.addActionListener((event) -> {
            mainFrame.setSystemLookAndFeel();
            this.invalidate();
        });
        ret.add(systemSchemeMenuItem);

        JMenuItem crossplatformLookAndFeel = new JMenuItem(localeManager.getString("universal_scheme"), KeyEvent.VK_U);
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
        LocaleManager localeManager = LocaleManager.getInstance();
        JMenu ret = new JMenu(localeManager.getString("test_menu"));
        ret.setMnemonic(KeyEvent.VK_T);
        ret.getAccessibleContext().setAccessibleDescription(
                localeManager.getString("test_menu.accessible_description"));
        JMenuItem addLogMessageItem = new JMenuItem(localeManager.getString("log_message"), KeyEvent.VK_M);
        addLogMessageItem.addActionListener((event) -> {
            Logger.debug(localeManager.getString("log.new_string"));
        });
        ret.add(addLogMessageItem);
        return ret;
    }

    /**
     * Создает пункт меню настроек
     */
    private JMenu createSettingMenu() {
        LocaleManager localeManager = LocaleManager.getInstance();
        JMenu ret = new JMenu(localeManager.getString("settings_menu"));
        ret.setMnemonic(KeyEvent.VK_I);
        ret.getAccessibleContext().setAccessibleDescription(
                localeManager.getString("settings_menu.accessible_description"));
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
        JMenuItem changeLanguageItem = new JMenuItem(localeManager.getString("settings_menu.change_lang"),
                KeyEvent.VK_M);
        changeLanguageItem.addActionListener((event) -> {
            SelectLanguageDialog userDialog = new SelectLanguageDialog(mainFrame);
            UserLocale result = userDialog.getChoosedUserLocale();
            if (result != null && localeManager.getCurrentLocale() != result) {
                localeManager.setUserLocale(result);
                mainFrame.dispose();
                SwingUtilities.invokeLater(() -> {
                    MainApplicationFrame frame = new MainApplicationFrame();
                    frame.setVisible(true);
                });
                Logger.debug(localeManager.getString("log.change_lang"));
            }
        });
        return changeLanguageItem;
    }

    /**
     * Создает пункт меню c функцией выхода из программы.
     */
    private JMenu createQuitMenu() {
        LocaleManager localeManager = LocaleManager.getInstance();
        JMenu ret = new JMenu(localeManager.getString("quit_menu"));
        ret.setMnemonic(KeyEvent.VK_Q);
        ret.getAccessibleContext().setAccessibleDescription(
                localeManager.getString("quit_menu.accessible_description"));
        JMenuItem quitItem = new JMenuItem(localeManager.getString("quit_menu"), KeyEvent.VK_Q);
        quitItem.addActionListener((event) -> {
            WindowEvent closeEvent = new WindowEvent(mainFrame, WindowEvent.WINDOW_CLOSING);
            Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(closeEvent);
        });
        ret.add(quitItem);
        return ret;
    }
}
