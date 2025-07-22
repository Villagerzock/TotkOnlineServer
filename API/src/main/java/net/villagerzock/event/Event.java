package net.villagerzock.event;

import javassist.ClassPath;
import net.villagerzock.Plugin;
import net.villagerzock.PluginLoader;
import net.villagerzock.Server;
import org.reflections.Configuration;
import org.reflections.Reflections;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.lang.reflect.Modifier;
import java.util.Set;

public abstract class Event {
    private boolean cancelled;
    private static final Set<Method> eventHandlerMethods;
    static {
        String[] pluginPackages = new String[Server.getInstance().getPluginLoader().getLoadedPlugins().length];
        for (int i = 0; i < pluginPackages.length; i++) {
            PluginLoader loader = Server.getInstance().getPluginLoader();
            pluginPackages[i] = loader.getDescription(loader.getLoadedPlugins()[i]).getMainClass();
        }
        Reflections reflections = new Reflections(
                new ConfigurationBuilder()
                        .setUrls(ClasspathHelper.forClassLoader(Server.getInstance().getPluginLoader().getLoader()))
                        .addClassLoaders(Server.getInstance().getPluginLoader().getLoader())
                        .setScanners(new TypeAnnotationsScanner())
        );
        eventHandlerMethods = reflections.getMethodsAnnotatedWith(EventHandler.class);
    }

    public String getEventName() {
        return this.getClass().getSimpleName();
    }
    public boolean isCancelled(){
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
    public static <T extends Event> boolean emit(T event){
        for (Method method : eventHandlerMethods){
            if (event.isCancelled()){
                return true;
            }
            if (method.getParameterCount() == 1 && method.getParameters()[0].getType() == event.getClass() && Modifier.isStatic(method.getModifiers())){
                try {
                    method.invoke(null,event);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }catch (Throwable e){
                    System.out.println("\u001B[31mAn Error Occured while Executing event: " + event.getEventName() + "\u001B[0m");
                    throw new RuntimeException(e);
                }
            }
        }
        return false;
    }
}
