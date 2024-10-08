package osrs.dev.modder.model;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import lombok.Getter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * our in-memory manager for gamepack mappings
 */
public class Mappings
{
    @Getter
    private static final List<CtClass> classes = new ArrayList<>();
    @Getter
    private static final Set<String> usedMethods = new HashSet<>();
    @Getter
    private static final List<Mapping> mappings = new ArrayList<>();

    public static void addField(String name, String obfuscatedName, String obfuscatedClass, String descriptor)
    {
        System.out.println("Found Field: " + name + " : <" + descriptor + "> " + obfuscatedClass + "." + obfuscatedName);
        mappings.add(new Mapping(name, obfuscatedName, obfuscatedClass, descriptor, MappedType.FIELD));
    }

    public static void addMethod(String name, String obfuscatedName, String obfuscatedClass, String descriptor)
    {
        System.out.println("Found Method: " + name + " : " + obfuscatedClass + "." + obfuscatedName + descriptor);
        mappings.add(new Mapping(name, obfuscatedName, obfuscatedClass, descriptor, MappedType.METHOD));
    }

    public static void addMethodNoGarbage(String name, String obfuscatedName, String obfuscatedClass, String descriptor)
    {
        System.out.println("Found Method: " + name + " : " + obfuscatedClass + "." + obfuscatedName + descriptor);
        Mapping mapping = new Mapping(name, obfuscatedName, obfuscatedClass, descriptor, MappedType.METHOD);
        mapping.setDone(true);
        mappings.add(mapping);
    }

    public static void addClass(String name, String obfuscatedName)
    {
        System.out.println("Found class: " + name);
        mappings.add(new Mapping(name, obfuscatedName, obfuscatedName, "", MappedType.CLASS));
    }

    /**
     * find a Mapping by our name we give it
     * @param name name
     * @return Mapping
     */
    public static Mapping findByTag(String name)
    {
        return mappings.stream()
                .filter(m -> m.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    /**
     * find a class by our name we give it, if not found it will look for a class directly matching the supplied name.
     * @param name name
     * @return CtClass
     */
    public static CtClass getClazz(String name)
    {
        Mapping mapping = findByTag(name);
        if(mapping == null)
        {
            return getClasses().stream().filter(c -> c.getName().equals(name)).findFirst().orElse(null);
        }
        return getClasses().stream().filter(c -> c.getName().equals(mapping.getObfuscatedClass())).findFirst().orElse(null);
    }

    public static void dumpInjectedGPAsJar(String path) throws CannotCompileException, IOException {
        File pathAsFile = new File(path);
        pathAsFile.mkdir();

        File jarFile = new File(path + File.separatorChar + "InjectedGamePack.jar");
        if (jarFile.exists())
            jarFile.delete();
        jarFile.createNewFile();

        FileOutputStream fos = new FileOutputStream(jarFile);
        ZipOutputStream zos = new ZipOutputStream(fos);
        for (CtClass clazz : classes) {
            String className = clazz.getName().replace(".", "/") + ".class";
            ZipEntry entry = new ZipEntry(className);
            zos.putNextEntry(entry);
            byte[] asBytes = clazz.toBytecode();
            zos.write(asBytes, 0, asBytes.length);
        }

        zos.close();
        System.out.println("Jar output finished. Check path " + path);
    }

    public static Number getFieldGetter(String obfuClass, String obfuField)
    {
        Mapping mapping = mappings.stream()
                .filter(m -> m.getObfuscatedName().equals(obfuField) && m.getObfuscatedClass().equals(obfuClass))
                .findFirst()
                .orElse(null);
        if(mapping == null)
        {
            return 0;
        }
        Garbage garb = new Garbage(mapping.getGarbage().getValue(), true);
        return garb.getGetterValue();
    }

    public static Number getFieldSetter(String obfuClass, String obfuField)
    {
        Mapping mapping = mappings.stream()
                .filter(m -> m.getObfuscatedName().equals(obfuField) && m.getObfuscatedClass().equals(obfuClass))
                .findFirst()
                .orElse(null);

        if(mapping == null)
        {
            return 0;
        }

        Garbage garb = new Garbage(mapping.getGarbage().getValue(), true);
        return garb.getSetterValue();
    }
}
