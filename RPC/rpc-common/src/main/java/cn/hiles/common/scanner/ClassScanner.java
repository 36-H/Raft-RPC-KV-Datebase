package cn.hiles.common.scanner;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author Helios
 * Time：2024-03-10 22:39
 */
public class ClassScanner {
    /**
     * 扫描当前工程中指定包下的所有类信息
     */
    private static final String PROTOCOL_FILE = "file";
    /**
     * 扫描Jar文件中指定包下的所有类信息
     */
    private static final String PROTOCOL_JAR = "jar";
    /**
     * 扫描的过程中指定需要处理的文件后缀信息
     */
    private static final String CLASS_FILE_SUFFIX = ".class";

    /**
     * 扫描当前工程中指定包下的所有类信息
     *
     * @param packageName   包名
     * @param packagePath   包路径
     * @param recursive     是否递归
     * @param classNameList 类名列表
     */
    private static void findAndAddClassesInPackageByFile(String packageName, String packagePath,
                                                         final boolean recursive, List<String> classNameList) {
        // 获取此包的目录 建立一个File
        File dir = new File(packagePath);
        // 如果不存在或者也不是目录就直接返回
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        // 如果存在 就获取包下的所有文件 包括目录
        File[] dirFiles = dir.listFiles(
                // 过滤文件 如果是目录或者是以.class结尾的文件
                file -> (recursive && file.isDirectory()) || (file.getName().endsWith(CLASS_FILE_SUFFIX))
        );
        if (dirFiles == null) {
            return;
        }
        // 循环所有文件
        for (File file : dirFiles) {
            // 如果是目录 则继续扫描
            if (file.isDirectory()) {
                findAndAddClassesInPackageByFile(packageName + "." + file.getName(),
                        file.getAbsolutePath(), recursive, classNameList);
            } else {
                // 如果是java类文件 去掉后面的.class 只留下类名
                String className = file.getName().substring(0, file.getName().length() - 6);
                // 添加到集合中去
                classNameList.add(packageName + '.' + className);
            }
        }
    }

    /**
     * 扫描Jar中指定包下的所有类信息
     *
     * @param packageName    包名
     * @param classNameList  完成类名存放的列表
     * @param recursive      是否递归
     * @param packageDirName 当前包前缀
     * @param url            当前包的URL
     * @return 包名
     * @throws IOException IO异常
     */
    private static String findAndAddClassesInPackageByJar(String packageName, List<String> classNameList, final boolean recursive, String packageDirName, URL url) throws IOException {
        //如果是jar包文件
        //定义一个JarFile
        JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();
        //从此jar包 得到一个枚举类
        Enumeration<JarEntry> entries = jar.entries();
        //同样地进行循环迭代
        while (entries.hasMoreElements()) {
            //获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
            JarEntry entry = entries.nextElement();
            String name = entry.getName();
            //如果是以/开头的
            if (name.charAt(0) == '/') {
                //获取后面的字符串
                name = name.substring(1);
            }
            //如果前半部分和定义的包名相同
            if (name.startsWith(packageDirName)) {
                int idx = name.lastIndexOf('/');
                //如果以"/"结尾 是一个包
                if (idx != -1) {
                    //获取包名 把"/"替换成"."
                    packageName = name.substring(0, idx).replace('/', '.');
                }
                //如果可以迭代下去 并且是一个包
                if ((idx != -1) || recursive) {
                    //如果是一个.class文件 而且不是目录
                    if (name.endsWith(CLASS_FILE_SUFFIX) && !entry.isDirectory()) {
                        //去掉后面的".class" 获取真正的类名
                        String className = name.substring(packageName.length() + 1, name.length() - 6);
                        //添加到classes
                        classNameList.add(packageName + '.' + className);
                    }
                }
            }
        }
        return packageName;
    }

    public static List<String> getClassNameList(String packageName) throws Exception {
        List<String> classNameList = new ArrayList<>();
        boolean recursive = true;
        String packageDirName = packageName.replace('.', '/');
        Enumeration<URL> dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
        while (dirs.hasMoreElements()) {
            URL url = dirs.nextElement();
            String protocol = url.getProtocol();
            if (PROTOCOL_FILE.equals(protocol)) {
                String filePath = java.net.URLDecoder.decode(url.getFile(), "UTF-8");
                findAndAddClassesInPackageByFile(packageName, filePath, recursive, classNameList);
            } else if (PROTOCOL_JAR.equals(protocol)) {
                findAndAddClassesInPackageByJar(packageName, classNameList, recursive, packageDirName, url);
            }
        }
        return classNameList;
    }
}
