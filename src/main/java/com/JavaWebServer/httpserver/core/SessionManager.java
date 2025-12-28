package com.JavaWebServer.httpserver.core;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages user sessions for tracking unique users
 */
public class SessionManager {
    
    private static SessionManager instance;
    private Map<String, String> ipToSessionMap; // Maps IP -> SessionID
    private Map<String, Long> sessionLastAccess; // Tracks last access time
    
    private static final long SESSION_TIMEOUT = 30 * 60 * 1000; // 30 minutes
    
    private SessionManager() {
        this.ipToSessionMap = new ConcurrentHashMap<>();
        this.sessionLastAccess = new ConcurrentHashMap<>();
    }
    
    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }
    
    /**
     * Gets or creates a session ID for a client IP
     */
    public String getOrCreateSession(String clientIp) {
        cleanupExpiredSessions();
        
        String sessionId = ipToSessionMap.get(clientIp);
        long currentTime = System.currentTimeMillis();
        
        // Create new session if doesn't exist or expired
        if (sessionId == null || isSessionExpired(sessionId)) {
            sessionId = UUID.randomUUID().toString().substring(0, 8);
            ipToSessionMap.put(clientIp, sessionId);
        }
        
        // Update last access time
        sessionLastAccess.put(sessionId, currentTime);
        
        return sessionId;
    }
    
    /**
     * Gets the number of active sessions (unique users)
     */
    public int getActiveSessionCount() {
        cleanupExpiredSessions();
        return sessionLastAccess.size();
    }
    
    /**
     * Checks if a session has expired
     */
    private boolean isSessionExpired(String sessionId) {
        Long lastAccess = sessionLastAccess.get(sessionId);
        if (lastAccess == null) {
            return true;
        }
        return (System.currentTimeMillis() - lastAccess) > SESSION_TIMEOUT;
    }
    
    /**
     * Removes expired sessions
     */
    private void cleanupExpiredSessions() {
        long currentTime = System.currentTimeMillis();
        sessionLastAccess.entrySet().removeIf(entry -> 
            (currentTime - entry.getValue()) > SESSION_TIMEOUT
        );
        
        // Remove IPs with expired sessions
        ipToSessionMap.entrySet().removeIf(entry -> 
            !sessionLastAccess.containsKey(entry.getValue())
        );
    }
    
    /**
     * Clears all sessions (for testing)
     */
    public void clearAllSessions() {
        ipToSessionMap.clear();
        sessionLastAccess.clear();
    }
    
    /**
     * Gets all active session IDs
     */
    public Map<String, String> getActiveSessions() {
        cleanupExpiredSessions();
        return new ConcurrentHashMap<>(ipToSessionMap);
    }
}
