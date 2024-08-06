package osrs.dev.util.eventbus;

import osrs.dev.annotations.Subscribe;
import osrs.dev.util.Logger;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class EventBus
{
    private static final Map<Object, Set<Method>> subscribers = new ConcurrentHashMap<>();

    public static void register(Object listener) {
        for (Method method : listener.getClass().getDeclaredMethods()) {
            if (!method.isAnnotationPresent(Subscribe.class))
                continue;

            method.setAccessible(true);
            subscribers.computeIfAbsent(listener, k -> new HashSet<>()).add(method);
        }
    }

    public static void unregister(Object obj)
    {
        subscribers.remove(obj);
    }

    public static void post(Object sender, Object event)
    {
        for (Object key : subscribers.keySet())
        {
            for(Method method : subscribers.get(key))
            {
                if (method.getParameterTypes().length != 2)
                    continue;

                if (!method.getParameterTypes()[1].equals(event.getClass()))
                    continue;

                try
                {
                    method.invoke(key, sender, event);
                }
                catch (Exception ex)
                {
                    System.out.println(ex.getMessage());
                    ex.getCause().printStackTrace();
                }
            }
        }
    }
}
