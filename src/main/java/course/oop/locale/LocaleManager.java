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

    private File userLocaleFile;

    private LocaleManager() {
        userLocaleFile = new File(
                System.getProperty("user.home") +
                        File.separator + "Robots" +
                        File.separator + "config" +
                        File.separator + "locale.conf");
        currentLocale = LocaleUtils.loadLocale(userLocaleFile);
    }

    /**
     * Возвращает текущие ресурсы со связанной текущей локалью.
     * ResourceBundle.getBundle() кешируется, так что его вызов не вызывает
     * частое чтение файла (хотя он еще и потокобезопасный, но я не заметил,
     * что это влияет на производительность). Если по каким-то причинам
     * currentUserLocale null, возвращаются ресурсы на английском.
     */
    private ResourceBundle getBundle() {
        switch (currentLocale) {
            case EN:
                return ResourceBundle.getBundle("locales/en", new Locale("en"));
            case RU:
                return ResourceBundle.getBundle("locales/ru", new Locale("ru"));
        }
        return ResourceBundle.getBundle("locales/en", new Locale("en"));
    }

    public UserLocale getCurrentLocale() {
        return currentLocale;
    }

    /**
     * Изменяет текущую локаль,
     * сохраняет ее в файл конфигурации
     */
    public void setUserLocale(UserLocale userLocale) {
        currentLocale = userLocale;
        LocaleUtils.saveLocale(userLocale, userLocaleFile);
    }

    /**
     * Возвращает экземпляр класса (синглтон)
     */
    public static LocaleManager getInstance() {
        return instance;
    }

    /**
     * Возвращает строку ресурсов со связанной текущей локалью по ключу
     */
    public String getString(String key) {
        return getBundle().getString(key);
    }
}
