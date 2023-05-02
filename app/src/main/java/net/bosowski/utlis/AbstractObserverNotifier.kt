package net.bosowski.utlis

abstract class AbstractObserverNotifier: ObserverNotifier {

    val observers = HashSet<Observer>()

    override fun registerObserver(observer: Observer) {
        observers.add(observer)
    }
}
