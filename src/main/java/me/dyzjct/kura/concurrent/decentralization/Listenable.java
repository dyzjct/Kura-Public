package me.dyzjct.kura.concurrent.decentralization;

import me.dyzjct.kura.concurrent.task.EventTask;

import java.util.concurrent.ConcurrentHashMap;

public interface Listenable {

    ConcurrentHashMap<DecentralizedEvent<? extends EventData>, EventTask<? extends EventData>> listenerMap();

    void subscribe();

    void unsubscribe();

}
