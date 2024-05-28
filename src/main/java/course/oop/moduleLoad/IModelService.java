package course.oop.moduleLoad;

import java.io.File;

import javax.swing.JInternalFrame;

/**
 * Сервис, предоставляющий окна для отрисовки со связанной с ними моделью.
 * В контексте задачи предполагаем, что сервис загружается из jar-файла.
 */
public interface IModelService {

    /**
     * Инициализирует сервис из связанного с сервисом jar-файла
     * (загружает классы, создает объекты, связывает их,
     * чтобы можно было пользоваться).
     */
    public void init(File jarFile);

    
    /**
     * Возвращает представления, предоставляемые сервисом, связанные с моделью,
     * инкапсулированной в сервисе.
     */
    public JInternalFrame[] getViews();

    /**
     * Запускает сервис
     */
    public void start();
}
