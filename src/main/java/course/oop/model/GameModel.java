package course.oop.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.vecmath.Vector2d;

/**
 * Класс, хранящий состояние игры,
 * предоставляющий интерфейс для изменения ее состояния.
 * Есть робот, преследующий цель.
 * У робота есть скорости: передвижения и поворота(угловая)
 * Движения робота происходят тактами. За один такт робот может:
 * <ul>
 * <li>Повернуться на угол меньший или равный максимальному
 * углу поворота робота</li>
 * <li>Передвинуться на расстояние меньшее или равное максимальному за такт</li>
 * </ul>
 */
public class GameModel {
    /**
     * Класс, делегирующий интерфейс Observable
     */
    private PropertyChangeSupport pcs;
    /**
     * При угле между направлением робота и вектором разности робота и цели
     * меньшем, чем это число, считаем, что угол между
     * направлением робота и вектором разности робота и цели равен 0
     */
    private final double angularEpsilon = 0.001;
    /**
     * <b>расстояние</b>, которое робот может пройти за такт
     */
    private final double maxVelocity;
    /**
     * Число радиан, на которые робот может повернуться за такт
     */
    private final double maxAngularVelocity;
    /**
     * При квадрате расстояния между роботом и целью меньшем, чем это число,
     * считаем, что робот достиг цели.
     */
    private final double epsilon;
    /**
     * Направление, в котором повернут робот (длина 1)
     */
    private Vector2d direction;
    /**
     * Положение робота
     */
    private Vector2d robot;
    /**
     * Положение цели
     */
    private Vector2d target;

    /**
     * Задает начальное состояние игры по переданным данным.
     */
    public GameModel(
            double maxVelocity,
            double maxAngularVelocity,
            double epsilon,
            Vector2d direction,
            Vector2d robot,
            Vector2d target) {
        this.maxVelocity = maxVelocity;
        this.maxAngularVelocity = maxAngularVelocity;
        this.epsilon = epsilon;
        this.direction = new Vector2d(direction.x, direction.y);
        this.robot = new Vector2d(robot.x, robot.y);
        this.target = new Vector2d(target.x, target.y);

        pcs = new PropertyChangeSupport(this);
    }

    /**
     * Задает начальное состояние игры такое же, как у Александра Владимировича
     */
    public GameModel() {
        this(
                1, // используются максимальные за такт расстояния
                0.01, // используется максимальный за такт поворот
                0.25, // сравниваются квадраты длин
                new Vector2d(1, 0),
                new Vector2d(100.0, 100.0),
                new Vector2d(150.0, 100.0));
    }

    /**
     * Пересчитывает положение робота (и цели) через переданное количество
     * времени(такт), затем уведомляет об изменении слушателей.
     */
    public void nextState(double time) {
        if (distance().lengthSquared() < epsilon) {
            return;
        }
        rotateRobot();
        moveRobot();
        notifyListeners();
    }

    public Vector2d getRobot() {
        return new Vector2d(robot);
    }

    public Vector2d getTarget() {
        return new Vector2d(target);
    }

    public Vector2d getDirection() {
        return new Vector2d(direction);
    }

    public void setTarget(Vector2d target) {
        this.target = new Vector2d(target.x, target.y);
    }

    /**
     * Метод для добавления слушателя изменения модели
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    /**
     * Метод для удаления слушателя модели
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    /**
     * Возвращает вектор расстояния между роботом и целью.
     */
    private Vector2d distance() {
        Vector2d distance = new Vector2d();
        distance.sub(target, robot);
        return distance;
    }

    /**
     * Поворачивает(direction) робота в сторону цели на максимально возможный
     * (Если то требуется) за такт угол.
     */
    private void rotateRobot() {
        Vector2d distance = distance();
        if (distance.angle(direction) <= maxAngularVelocity) {
            distance.normalize();
            direction = distance;
            return;
        }

        Vector2d clockwiseDirection = rotateVector(direction, maxAngularVelocity);
        Vector2d contrClockwiseDirection = rotateVector(direction, -maxAngularVelocity);

        if (distance.angle(clockwiseDirection) < distance.angle(contrClockwiseDirection)) {
            direction = clockwiseDirection;
            return;
        }

        direction = contrClockwiseDirection;
    }

    /**
     * Передвигает робота вперед на расстояние такое, чтобы не проскочить цель
     * или максимальное возможное
     */
    private void moveRobot() {
        Vector2d max_displacement = new Vector2d(direction);
        Vector2d distance = distance();
        max_displacement.scale(maxVelocity);

        if (distance.angle(max_displacement) < angularEpsilon
                && distance.lengthSquared() <= max_displacement.lengthSquared()) {
            robot = new Vector2d(target);
            return;
        }
        robot.add(robot, max_displacement);
    }

    /**
     * Возвращает переданный вектор, повернутый на переданный угол
     */
    private Vector2d rotateVector(Vector2d v, double a) {
        return new Vector2d(
                Math.cos(a) * v.x + -Math.sin(a) * v.y,
                Math.sin(a) * v.x + Math.cos(a) * v.y);
    }

    /**
     * Уведомляет слушателей об изменении состояния модели
     */
    private void notifyListeners() {
        pcs.firePropertyChange(GameModelEvents.UPDATE.toString(), null, this);
    }
}
