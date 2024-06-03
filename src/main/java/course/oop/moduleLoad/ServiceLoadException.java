package course.oop.moduleLoad;

/**
 * Исключение, выбрасывающееся при неудачной загрузке сервиса из jar-файла
 */
public class ServiceLoadException extends Exception {

    /**
     * Конструктор с сообщением исключения
     */
    public ServiceLoadException(String message) {
        super(message);
    }

    /**
     * Конструктор с причиной (для инкапсуляции исключений)
     */
    public ServiceLoadException(String message, Exception cause) {
        super(message, cause);
    }
}
