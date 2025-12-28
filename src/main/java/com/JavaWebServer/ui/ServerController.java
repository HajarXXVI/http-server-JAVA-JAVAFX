package com.JavaWebServer.ui;

import com.JavaWebServer.httpserver.ServerEventListener;
import com.JavaWebServer.httpserver.core.ServerListenerThread;
import com.JavaWebServer.httpserver.core.SessionManager;
import com.JavaWebServer.httpserver.core.io.WebRootNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

public class ServerController implements ServerEventListener {

    private final static Logger LOGGER = LoggerFactory.getLogger(ServerController.class);

    private ServerGUI gui;
    private ServerListenerThread serverThread;
    private boolean isRunning = false;
    private int totalRequests = 0;
    private LocalDateTime startTime;
    private Timer statisticsTimer;
    private AtomicInteger activeConnections = new AtomicInteger(0);

    public ServerController(ServerGUI gui) {
        this.gui = gui;
    }

    @Override
    public void onRequestProcessed(String method, String path, int statusCode, String clientIp, String sessionId) {
        totalRequests++;
        gui.addRequestLog(method, path, String.valueOf(statusCode), clientIp + " [" + sessionId + "]");

        // Update unique users count
        int uniqueUsers = SessionManager.getInstance().getActiveSessionCount();
        gui.updateUniqueUsersCount(uniqueUsers);
    }

    @Override
    public void onConnectionAccepted() {
        int count = activeConnections.incrementAndGet();
        gui.updateConnectionCount(count);
    }

    @Override
    public void onConnectionClosed() {
        int count = activeConnections.decrementAndGet();
        gui.updateConnectionCount(count);
    }

    @Override
    public void onLogMessage(String message) {
        gui.appendConsole(message);
    }

    public void startServer(int port, String webroot) {
        if (isRunning) {
            gui.appendConsole("Server is already running!");
            return;
        }

        try {
            String separator = "==================================================";
            gui.appendConsole(separator);
            gui.appendConsole("Starting HTTP Server...");
            gui.appendConsole("Port: " + port);
            gui.appendConsole("WebRoot: " + webroot);
            gui.appendConsole(separator);

            // Resolve webroot path using the same logic as HttpServer
            String resolvedWebroot = resolveWebRootPath(webroot);
            gui.appendConsole("Resolved WebRoot: " + resolvedWebroot);

            serverThread = new ServerListenerThread(port, resolvedWebroot);
            serverThread.setEventListener(this);
            serverThread.start();

            isRunning = true;
            startTime = LocalDateTime.now();
            totalRequests = 0;
            
            gui.updateStatus("RUNNING", true);
            gui.appendConsole("Server started successfully on port " + port);
            gui.appendConsole("Access the server at: http://localhost:" + port);
            
            // Start statistics update timer
            startStatisticsUpdater();
            
            LOGGER.info("Server started from GUI on port {}", port);
            
        } catch (IOException e) {
            isRunning = false;
            String errorMsg;
            if (e.getMessage() != null && e.getMessage().contains("Address already in use")) {
                errorMsg = "ERROR: Port " + port + " is already in use!\n" +
                          "Please either:\n" +
                          "1. Stop the other server using this port\n" +
                          "2. Change the port number and try again";
            } else {
                errorMsg = "ERROR: Failed to start server - " + e.getMessage();
            }
            gui.appendConsole(errorMsg);
            gui.updateStatus("FAILED", false);
            LOGGER.error("Failed to start server", e);
        } catch (WebRootNotFoundException e) {
            isRunning = false;
            gui.appendConsole("ERROR: WebRoot folder not found - " + e.getMessage());
            gui.updateStatus("FAILED", false);
            LOGGER.error("WebRoot not found", e);
        }
    }

    public void stopServer() {
        if (!isRunning) {
            gui.appendConsole("Server is not running!");
            return;
        }

        try {
            gui.appendConsole("Stopping server...");

            if (serverThread != null) {
                serverThread.stopServer();
                serverThread.interrupt();
                serverThread = null;
            }

            isRunning = false;
            activeConnections.set(0);

            if (statisticsTimer != null) {
                statisticsTimer.cancel();
                statisticsTimer = null;
            }

            gui.updateStatus("STOPPED", false);
            gui.updateConnectionCount(0);
            gui.appendConsole("Server stopped successfully.");

            LOGGER.info("Server stopped from GUI");

        } catch (Exception e) {
            gui.appendConsole("ERROR: Failed to stop server - " + e.getMessage());
            LOGGER.error("Failed to stop server", e);
        }
    }

    public void restartServer(int port, String webroot) {
        gui.appendConsole("Restarting server...");
        stopServer();
        
        // Wait a moment before restarting
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        startServer(port, webroot);
    }

    public void logRequest(String method, String path, String status, String clientIp) {
        totalRequests++;
        gui.addRequestLog(method, path, status, clientIp);
        gui.appendConsole(String.format("[REQUEST] %s %s - Status: %s - From: %s", 
            method, path, status, clientIp));
    }

    private void startStatisticsUpdater() {
        statisticsTimer = new Timer(true);
        statisticsTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (isRunning && startTime != null) {
                    Duration uptime = Duration.between(startTime, LocalDateTime.now());
                    String uptimeStr = formatUptime(uptime);
                    
                    double requestsPerSecond = 0;
                    if (uptime.getSeconds() > 0) {
                        requestsPerSecond = (double) totalRequests / uptime.getSeconds();
                    }
                    
                    gui.updateStatistics(totalRequests, requestsPerSecond, uptimeStr);
                }
            }
        }, 1000, 1000); // Update every second
    }

    private String formatUptime(Duration uptime) {
        long hours = uptime.toHours();
        long minutes = uptime.toMinutes() % 60;
        long seconds = uptime.getSeconds() % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    private String resolveWebRootPath(String webrootPath) {
        // Use the same resolution logic as HttpServer
        java.io.File webroot = new java.io.File(webrootPath);
        
        if (webroot.isAbsolute() && webroot.exists() && webroot.isDirectory()) {
            return webrootPath;
        }
        
        if (webroot.exists() && webroot.isDirectory()) {
            try {
                return webroot.getCanonicalPath();
            } catch (IOException e) {
                return webroot.getAbsolutePath();
            }
        }
        
        // Try relative to current working directory
        java.io.File currentDir = new java.io.File(System.getProperty("user.dir"));
        java.io.File projectRoot = findProjectRoot(currentDir);
        if (projectRoot != null) {
            java.io.File projectWebroot = new java.io.File(projectRoot, webrootPath);
            if (projectWebroot.exists() && projectWebroot.isDirectory()) {
                try {
                    return projectWebroot.getCanonicalPath();
                } catch (IOException e) {
                    return projectWebroot.getAbsolutePath();
                }
            }
        }
        
        return webrootPath;
    }

    private java.io.File findProjectRoot(java.io.File startDir) {
        java.io.File current = startDir;
        int maxDepth = 10;
        int depth = 0;
        
        while (current != null && depth < maxDepth) {
            java.io.File pomFile = new java.io.File(current, "pom.xml");
            if (pomFile.exists()) {
                return current;
            }
            current = current.getParentFile();
            depth++;
        }
        return null;
    }

    public boolean isRunning() {
        return isRunning;
    }
}
