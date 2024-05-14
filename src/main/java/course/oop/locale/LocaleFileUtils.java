package course.oop.locale;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.commons.io.FileUtils;

/**
 * Вспомогательные функции для работы LocaleManager (не требуют состояния)
 */
class LocaleFileUtils {
    /**
     * Возвращает строку из первых двух символов файла
     * $HOME/Robots/config/locale.conf (в кодировке UTF-8).
     * Если файл не существует, или слишком мал, или его содержимое некорректно,
     * возвращает локаль по умолчанию - английскую
     */
    public static UserLocale loadLocale(File localeFile) {
        try {
            return UserLocale.valueOf(Files.readString(localeFile.toPath()));
        } catch (IOException e) {
            System.err.println("can't load locale");
            e.printStackTrace();
            return UserLocale.EN;
        } catch (IllegalArgumentException e) {
            System.err.println("can't recover locale from file data");
            e.printStackTrace();
            return UserLocale.EN;
        }
    }

    /**
     * Сохраняет локаль, предпочитаемую пользователем в файл конфигурации
     */
    public static void saveLocale(UserLocale locale, File localeFile) {
        try {
            FileUtils.createParentDirectories(localeFile);
        } catch (IOException e) {
            System.err.println("can't create directories to localeFile");
            e.printStackTrace();
            return;
        }

        try {
            Files.writeString(localeFile.toPath(), locale.toString());
        } catch (IOException e) {
            System.err.println("can't save locale to file");
            e.printStackTrace();
        }
    }
}
