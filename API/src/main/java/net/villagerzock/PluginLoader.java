package net.villagerzock;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class PluginLoader {
    private final Map<Plugin,PluginDescription> descriptionMap = new HashMap<>();
    private URLClassLoader loader = new URLClassLoader(new URL[0],Plugin.class.getClassLoader());
    public Plugin[] loadFolder(File file){
        File[] jars = file.listFiles((dir,name)->name.endsWith(".jar"));
        if (jars == null) return new Plugin[]{};
        ObjectMapper mapper = new ObjectMapper();
        List<Plugin> plugins = new ArrayList<>();
        for (File jar : jars){
            try (JarFile jarFile = new JarFile(jar)) {
                ZipEntry entry = jarFile.getEntry("plugin.json");
                if (entry == null){
                    continue;
                }


                InputStream jsonStream = jarFile.getInputStream(entry);
                PluginDescription desc = mapper.readValue(jsonStream,PluginDescription.class);

                System.out.println(" - " + desc.getName());

                URL[] urls = new URL[loader.getURLs().length + 1];
                for (int i = 0; i < loader.getURLs().length; i++) {
                    urls[i] = loader.getURLs()[i];
                }
                urls[loader.getURLs().length] = jar.toURI().toURL();
                loader = new URLClassLoader(urls,Plugin.class.getClassLoader());

                Class<?> pluginClass = loader.loadClass(desc.getMainClass());
                Plugin pluginInstance = (Plugin) pluginClass.getDeclaredConstructor().newInstance();
                pluginInstance.onLoad();
                plugins.add(pluginInstance);
                descriptionMap.put(pluginInstance,desc);
            } catch (IOException | NoSuchMethodException | InstantiationException | IllegalAccessException |
                     InvocationTargetException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return plugins.toArray(Plugin[]::new);
    }

    public URLClassLoader getLoader() {
        return loader;
    }

    public Plugin loadFile(File file) {
        ObjectMapper mapper = new ObjectMapper();
        if (!file.exists()){
            return null;
        }
        if (!file.getName().endsWith(".jar"))
            return null;
        try (JarFile jarFile = new JarFile(file)) {
            ZipEntry entry = jarFile.getEntry("plugin.json");
            if (entry == null){
                return null;
            }

            InputStream jsonStream = jarFile.getInputStream(entry);
            PluginDescription desc = mapper.readValue(jsonStream,PluginDescription.class);

            URLClassLoader loader = new URLClassLoader(new URL[]{file.toURI().toURL()},Plugin.class.getClassLoader());

            Class<?> pluginClass = loader.loadClass(desc.getMainClass());

            Plugin pluginInstance = (Plugin) pluginClass.getDeclaredConstructor().newInstance();
            pluginInstance.onLoad();
            descriptionMap.put(pluginInstance,desc);
            return pluginInstance;
        } catch (IOException | NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public Plugin[] getLoadedPlugins() {
        return descriptionMap.keySet().toArray(Plugin[]::new);
    }

    public PluginDescription getDescription(Plugin plugin){
        return descriptionMap.get(plugin);
    }
}
