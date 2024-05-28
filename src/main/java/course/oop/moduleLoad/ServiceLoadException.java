package course.oop.moduleLoad;

/**
 * Исключение, выбрасывающееся при неудачной загрузке сервиса из jar-файла
 */
public class ServiceLoadException extends Exception {
    public ServiceLoadException(String message) {
        super(message);
    }
}
