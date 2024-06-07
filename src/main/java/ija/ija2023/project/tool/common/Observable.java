/**
 * @author Anton Havlovskyi
 * Rozhraní Observable reprezentující objekty, které mohou notifikovat závislé objekty (observers) o změnách.
 */
package ija.ija2023.project.tool.common;

public interface Observable {
    /**
     * @author Anton Havlovskyi
     * Rozhraní Observer reprezentující objekty, které mohou registrovány u objektů Observable a přijímají notifikace o jejich změnách.
     */
    public static interface Observer {
        void update(Observable o);
    }
    void addObserver(Observable.Observer o);
    void notifyObservers();
    void removeObserver(Observable.Observer o);
}
