package course.oop.locale;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;

/**
 * Вспомогательные функции для работы LocaleManager (не требуют состояния)
 */
class LocaleUtils {
    /**
     * Возвращает строку из первых двух символов файла
     * $HOME/Robots/config/locale.conf (в кодировке UTF-8).
     * Если файл не существует, или слишком мал, или его содержимое некорректно,
     * возвращает локаль по умолчанию - английскую
     */
    public static UserLocale loadLocale(File localeFile) {
        try (Reader reader = new InputStreamReader(new FileInputStream(localeFile), StandardCharsets.UTF_8)) {
            char[] buff = new char[2];
            if (reader.read(buff, 0, 2) != 2) {
                System.err.println("locale file is too small");
                return UserLocale.EN;
            }
            try {
                return UserLocale.valueOf(new String(buff));
            } catch (IllegalArgumentException e) {
                System.err.println("Incorrect locale type");
                e.printStackTrace();
                return UserLocale.EN;
            }
        } catch (IOException e) {
            System.err.println("locale file is not exist.");
            e.printStackTrace();
            return UserLocale.EN;
        }
    }

    /**
     * Сохраняет локаль, предпочитаемую пользователем в файл конфигурации
     * $HOME/Robots/config/locale.conf (в кодировке UTF-8)
     */
    public static void saveLocale(UserLocale locale, File localeFile) {
        try {
            FileUtils.createParentDirectories(localeFile);
        } catch (IOException e) {
            System.err.println("can't create directories to localeFile");
            e.printStackTrace();
        }
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(localeFile), StandardCharsets.UTF_8))) {
            writer.write(locale.toString());
        } catch (IOException e) {
            System.err.println("can't save user locale");
            e.printStackTrace();
        }
    }
}
