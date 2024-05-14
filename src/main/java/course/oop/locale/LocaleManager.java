package course.oop.locale;

import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Синглтон
 * - Хранит текущую локаль пользователя
 * - Возвращает ресурсы, связанные с текущей локалью пользователя
 * в любом месте в программе (фильтр над ResourceBundle)
 */
public class LocaleManager {
    /**
     * Экземпляр класса
     */
    private final static LocaleManager instance = new LocaleManager();

    /**
     * Текущая локаль пользователя
     */
    private UserLocale currentLocale;

    /**
     * Хранит ресурсы со связанной currentLocale
     */
    private ResourceBundle currentBundle;

    /**
     * Файл, куда сохраняется локаль пользователя
     */
    private File userLocaleFile;

    private LocaleManager() {
        userLocaleFile = new File(
                System.getProperty("user.home") +
                        File.separator + "Robots" +
                        File.separator + "config" +
                        File.separator + "locale.conf");
        currentLocale = LocaleFileUtils.loadLocale(userLocaleFile);
        currentBundle = getBundleWithCurrentLocale();
    }

    /**
     * Возвращает Ресурсы со связанной текущей локалью. Если
     * currentLocale null, возвращаются ресурсы на английском.
     */
    private ResourceBundle getBundleWithCurrentLocale() {
        switch (currentLocale) {
            case EN:
                return ResourceBundle.getBundle("locales/en", new Locale("en"));
            case RU:
                return ResourceBundle.getBundle("locales/ru", new Locale("ru"));
        }
        return ResourceBundle.getBundle("locales/en", new Locale("en"));
    }

    /**
     * Возвращает установленную локаль
     */
    public UserLocale getCurrentLocale() {
        return currentLocale;
    }

    /**
     * Изменяет текущую локаль и ресурсы, связанные с ней.
     * сохраняет ее в файл конфигурации
     */
    public void setAndSaveUserLocale(UserLocale userLocale) {
        currentLocale = userLocale;
        currentBundle = getBundleWithCurrentLocale();
        LocaleFileUtils.saveLocale(userLocale, userLocaleFile);
    }

    /**
     * Возвращает экземпляр класса (синглтон)
     */
    public static LocaleManager getInstance() {
        return instance;
    }

    /**
     * Возвращает строку ресурсов со связанной текущей локалью по ключу,
     * (для удобства)
     */
    public static String getString(String key) {
        return instance.getResourceString(key);
    }

    /**
     * Возвращает строку ресурсов со связанной текущей локалью по ключу
     */
    public String getResourceString(String key) {
        return currentBundle.getString(key);
    }
}
