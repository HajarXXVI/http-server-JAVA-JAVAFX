package com.JavaWebServer.httpserver.core;

import com.JavaWebServer.http.*;
import com.JavaWebServer.httpserver.ServerEventListener;
import com.JavaWebServer.httpserver.core.io.ReadFileException;
import com.JavaWebServer.httpserver.core.io.WebRootHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class HttpConnectionWorkerThread extends Thread {
    private final static Logger LOGGER = LoggerFactory.getLogger(HttpConnectionWorkerThread.class);
    private Socket socket;
    private WebRootHandler webRootHandler;
    private HttpParser httpParser = new HttpParser();
    private ServerEventListener eventListener;

    public HttpConnectionWorkerThread(Socket socket, WebRootHandler webRootHandler) {
        this.socket = socket;
        this.webRootHandler = webRootHandler;
    }

    public HttpConnectionWorkerThread(Socket socket, WebRootHandler webRootHandler, ServerEventListener eventListener) {
        this.socket = socket;
        this.webRootHandler = webRootHandler;
        this.eventListener = eventListener;
    }

    @Override
    public void run() {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        String clientIp = socket.getInetAddress().getHostAddress();
        String method = "UNKNOWN";
        String path = "/";
        int statusCode = 200;

        // Get or create session for this user
        SessionManager sessionManager = SessionManager.getInstance();
        String sessionId = sessionManager.getOrCreateSession(clientIp);

        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();

            HttpRequest request = httpParser.parseHttpRequest(inputStream);
            method = request.getMethod() != null ? request.getMethod().name() : "UNKNOWN";
            path = request.getRequestTarget();

            HttpResponse response = handleRequest(request);
            statusCode = response.getStatusCode().STATUS_CODE;

            outputStream.write(response.getResponseBytes());

            LOGGER.info(" * Connection Processing Finished.");

            if (eventListener != null) {
                eventListener.onRequestProcessed(method, path, statusCode, clientIp, sessionId);
                eventListener.onLogMessage(String.format("[User:%s] %s %s - %d from %s", sessionId, method, path, statusCode, clientIp));
            }

        } catch (IOException e) {
            LOGGER.error("Problem with communication", e);
            if (eventListener != null) {
                eventListener.onLogMessage("ERROR: Problem with communication - " + e.getMessage());
            }
        } catch (HttpParsingException e) {
            LOGGER.info("Bad Request", e);
            statusCode = e.getErrorCode().STATUS_CODE;

            HttpResponse response = new HttpResponse.Builder()
                    .httpVersion(HttpVersion.HTTP_1_1.LITERAL)
                    .statusCode(e.getErrorCode())
                    .build();
            try {
                outputStream.write(response.getResponseBytes());

                if (eventListener != null) {
                    eventListener.onRequestProcessed(method, path, statusCode, clientIp, sessionId);
                    eventListener.onLogMessage(String.format("[User:%s] BAD REQUEST %s %s - %d from %s", sessionId, method, path, statusCode, clientIp));
                }
            } catch (IOException ex) {
                LOGGER.error("Problem with communication", e);
            }

        } finally {
            if (inputStream!= null) {
                try {
                    inputStream.close();
                } catch (IOException e) {}
            }
            if (outputStream!=null) {
                try {
                    outputStream.close();
                } catch (IOException e) {}
            }
            if (socket!= null) {
                try {
                    socket.close();
                    if (eventListener != null) {
                        eventListener.onConnectionClosed();
                    }
                } catch (IOException e) {}
            }
        }
    }

    private HttpResponse handleRequest(HttpRequest request) {

        //added part
        if (request.getMethod() == null) {
            return new HttpResponse.Builder()
                    .httpVersion(HttpVersion.HTTP_1_1.LITERAL)
                    .statusCode(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST)
                    .build();
        }
        //

        switch (request.getMethod()) {
            case GET:
                LOGGER.info(" * GET Request");
                return handleGetRequest(request, true);
            case HEAD:
                LOGGER.info(" * HEAD Request");
                return handleGetRequest(request, false);
            default:
                return new HttpResponse.Builder()
                        .httpVersion(request.getBestCompatibleHttpVersion().LITERAL)
                        .statusCode(HttpStatusCode.SERVER_ERROR_501_NOT_IMPLEMENTED)
                        .build();
        }

    }

    private HttpResponse handleGetRequest(HttpRequest request, boolean setMessageBody) {
        try {

            HttpResponse.Builder builder = new HttpResponse.Builder()
                    .httpVersion(request.getBestCompatibleHttpVersion().LITERAL)
                    .statusCode(HttpStatusCode.OK)
                    .addHeader(HttpHeaderName.CONTENT_TYPE.headerName, webRootHandler.getFileMimeType(request.getRequestTarget()));

            if (setMessageBody) {
                byte[] messageBody = webRootHandler.getFileByteArrayData(request.getRequestTarget());
                builder.addHeader(HttpHeaderName.CONTENT_LENGTH.headerName, String.valueOf(messageBody.length))
                        .messageBody(messageBody);
            }

            return builder.build();

        } catch (FileNotFoundException e) {

            return new HttpResponse.Builder()
                    .httpVersion(request.getBestCompatibleHttpVersion().LITERAL)
                    .statusCode(HttpStatusCode.CLIENT_ERROR_404_NOT_FOUND)
                    .build();

        } catch (ReadFileException e) {

            return new HttpResponse.Builder()
                    .httpVersion(request.getBestCompatibleHttpVersion().LITERAL)
                    .statusCode(HttpStatusCode.SERVER_ERROR_500_INTERNAL_SERVER_ERROR)
                    .build();
        }

    }
}
