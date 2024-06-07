/**
 * @author Anton Havlovskyi
 * Abstraktní třída reprezentující robot (ToolRobot), který je Observable.
 */
package ija.ija2023.project.tool.common;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractObservableRobot implements ToolRobot {
    public final List<Observer> observers = new ArrayList<>();

    public void addObserver(Observable.Observer o) {
        this.observers.add(o);
    }

    public void notifyObservers() {
        observers.forEach(observer -> observer.update(null));
    }

    public void removeObserver(Observable.Observer o) {
        this.observers.remove(o);
    }
}
