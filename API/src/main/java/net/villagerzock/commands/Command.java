package net.villagerzock.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface Command<T> {
    boolean onExecute(T options);
    T getOptions();
    Map<String,Command<?>> commands = new HashMap<>();
}
