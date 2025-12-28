package com.JavaWebServer.httpserver;

import com.JavaWebServer.httpserver.config.Configuration;
import com.JavaWebServer.httpserver.config.ConfigurationManager;
import com.JavaWebServer.httpserver.core.ServerListenerThread;
import com.JavaWebServer.httpserver.core.io.WebRootNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;

/**
 *
 * Driver Class for the Http Server
 *
 */
public class HttpServer {

    private final static Logger LOGGER = LoggerFactory.getLogger(HttpServer.class);

    public static void main(String[] args) {

        LOGGER.info("Server starting...");

        ConfigurationManager configManager = ConfigurationManager.getInstance();
        
        if (args.length == 0) {
            LOGGER.info("No configuration file provided, using default http.json from resources");
            configManager.loadConfigurationFileFromResources("http.json");
        } else if (args.length == 1) {
            configManager.loadConfigurationFile(args[0]);
        } else {
            LOGGER.error("Too many arguments provided.");
            LOGGER.error("Syntax:  java -jar simplehttpserver-1.0-SNAPSHOT.jar  [config.json]");
            return;
        }
        
        Configuration conf = configManager.getCurrentConfiguration();

        LOGGER.info("Using Port: " + conf.getPort());
        LOGGER.info("Using WebRoot: " + conf.getWebroot());

        // Resolve webroot path - try relative path first, then absolute
        String webrootPath = resolveWebRootPath(conf.getWebroot());
        LOGGER.info("Resolved WebRoot path: " + webrootPath);

        try {
            ServerListenerThread serverListenerThread = new ServerListenerThread(conf.getPort(), webrootPath);
            serverListenerThread.start();
        } catch (IOException e) {
            e.printStackTrace();
            // TODO handle later.
        } catch (WebRootNotFoundException e) {
            LOGGER.error("Webroot folder not found",e);
        }


    }

    /**
     * Resolves the webroot path. Tries multiple strategies:
     * 1. If path is absolute and exists, use it
     * 2. Try relative to current working directory
     * 3. Try relative to project root (where pom.xml is)
     * 4. Try relative to where the JAR/classes are running from
     */
    private static String resolveWebRootPath(String webrootPath) {
        File webroot = new File(webrootPath);
        
        // If it's already an absolute path and exists, use it
        if (webroot.isAbsolute() && webroot.exists() && webroot.isDirectory()) {
            return webrootPath;
        }
        
        // Try relative to current working directory
        if (webroot.exists() && webroot.isDirectory()) {
            try {
                return webroot.getCanonicalPath();
            } catch (IOException e) {
                return webroot.getAbsolutePath();
            }
        }
        
        // Try relative to project root (look for pom.xml)
        File currentDir = new File(System.getProperty("user.dir"));
        File projectRoot = findProjectRoot(currentDir);
        if (projectRoot != null) {
            File projectWebroot = new File(projectRoot, webrootPath);
            if (projectWebroot.exists() && projectWebroot.isDirectory()) {
                try {
                    return projectWebroot.getCanonicalPath();
                } catch (IOException e) {
                    return projectWebroot.getAbsolutePath();
                }
            }
        }
        
        // Try relative to where classes are running from (target/classes or JAR location)
        try {
            java.net.URL codeSourceUrl = HttpServer.class.getProtectionDomain()
                    .getCodeSource().getLocation();
            String classPath = codeSourceUrl.getPath();
            // Decode URL-encoded paths (e.g., %20 -> space)
            if (classPath != null && classPath.contains("%")) {
                classPath = URLDecoder.decode(classPath, "UTF-8");
            }
            File classLocation = new File(classPath);
            if (classLocation.isFile()) {
                // Running from JAR - try sibling directory
                File jarDir = classLocation.getParentFile();
                File jarWebroot = new File(jarDir.getParentFile(), webrootPath);
                if (jarWebroot.exists() && jarWebroot.isDirectory()) {
                    return jarWebroot.getCanonicalPath();
                }
            } else {
                // Running from classes - go up to project root
                File classesDir = classLocation;
                while (classesDir != null && !new File(classesDir, "pom.xml").exists()) {
                    classesDir = classesDir.getParentFile();
                }
                if (classesDir != null) {
                    File classesWebroot = new File(classesDir, webrootPath);
                    if (classesWebroot.exists() && classesWebroot.isDirectory()) {
                        return classesWebroot.getCanonicalPath();
                    }
                }
            }
        } catch (Exception e) {
            // Ignore and continue
        }
        
        // Return original path if nothing worked (will throw error later)
        return webrootPath;
    }
    
    /**
     * Finds the project root by looking for pom.xml
     */
    private static File findProjectRoot(File startDir) {
        File current = startDir;
        int maxDepth = 10; // Prevent infinite loops
        int depth = 0;
        
        while (current != null && depth < maxDepth) {
            File pomFile = new File(current, "pom.xml");
            if (pomFile.exists()) {
                return current;
            }
            current = current.getParentFile();
            depth++;
        }
        return null;
    }

}
