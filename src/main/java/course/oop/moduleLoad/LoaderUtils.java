package course.oop.moduleLoad;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Функция для загрузки одного класса из jar-файла
 */
public class LoaderUtils {
    private LoaderUtils() {
    }

    /**
     * Функция, отвечающая за загрузку класса с полным именем ModelModule,
     * реализующий
     * интерфейс ModelService из jar-файла. Загрузка оставшихся классов делегируется
     * загруженному классу сервиса, так как он лучше знает, как устроен jar-файл,
     * в котором лежал сам.
     */
    public static IModelService loadModelServiceFromJar(File jarFile) throws ServiceLoadException {
        try {
            if (!jarFile.exists()) {
                throw new FileNotFoundException(String.format("jar file %s is not exist", jarFile));
            }
            URL[] url = new URL[] { jarFile.toURI().toURL() };
            URLClassLoader urlClassLoader = new URLClassLoader(url);
            Class<IModelService> realisationClass = (Class<IModelService>) urlClassLoader.loadClass("ModelModule");
            IModelService modelService = realisationClass.getDeclaredConstructor().newInstance();
            return modelService;
        } catch (Exception e) {
            throw new ServiceLoadException("Can't load ModelService from jar: " + jarFile, e);
        }
    }
}