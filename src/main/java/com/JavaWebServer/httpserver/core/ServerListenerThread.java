package com.JavaWebServer.httpserver.core;

import com.JavaWebServer.httpserver.ServerEventListener;
import com.JavaWebServer.httpserver.core.io.WebRootHandler;
import com.JavaWebServer.httpserver.core.io.WebRootNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerListenerThread extends Thread {

    private final static Logger LOGGER = LoggerFactory.getLogger(ServerListenerThread.class);

    private int port;
    private String webroot;
    private ServerSocket serverSocket;

    private WebRootHandler webRootHandler;
    private ServerEventListener eventListener;

    public ServerListenerThread(int port, String webroot) throws IOException, WebRootNotFoundException {
        this.port = port;
        this.webroot = webroot;
        this.webRootHandler = new WebRootHandler(webroot);
        this.serverSocket = new ServerSocket(this.port);
    }

    public void setEventListener(ServerEventListener listener) {
        this.eventListener = listener;
    }

    public void stopServer() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                LOGGER.info("Server socket closed");
                if (eventListener != null) {
                    eventListener.onLogMessage("Server socket closed");
                }
            }
        } catch (IOException e) {
            LOGGER.error("Error closing server socket", e);
        }
    }

    @Override
    public void run() {

        try {

            while ( serverSocket.isBound() && !serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();

                LOGGER.info(" * Connection accepted: " + socket.getInetAddress());

                if (eventListener != null) {
                    eventListener.onConnectionAccepted();
                    eventListener.onLogMessage("Connection accepted: " + socket.getInetAddress());
                }

                HttpConnectionWorkerThread workerThread = new HttpConnectionWorkerThread(socket, webRootHandler, eventListener);
                workerThread.start();

            }

        } catch (IOException e) {
            LOGGER.error("Problem with setting socket", e);
            if (eventListener != null) {
                eventListener.onLogMessage("ERROR: Problem with setting socket - " + e.getMessage());
            }
        } finally {
            if (serverSocket!=null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {}
            }
        }

    }
}
