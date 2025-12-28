package com.JavaWebServer.httpserver;

/**
 * Interface for listening to server events (for UI updates)
 */
public interface ServerEventListener {
    
    /**
     * Called when a new HTTP request is received and processed
     * @param method HTTP method (GET, POST, etc.)
     * @param path Request path
     * @param statusCode HTTP status code
     * @param clientIp Client IP address
     * @param sessionId User session ID
     */
    void onRequestProcessed(String method, String path, int statusCode, String clientIp, String sessionId);
    
    /**
     * Called when a connection is accepted
     */
    void onConnectionAccepted();
    
    /**
     * Called when a connection is closed
     */
    void onConnectionClosed();
    
    /**
     * Called when a log message is generated
     * @param message Log message
     */
    void onLogMessage(String message);
}
