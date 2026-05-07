# HTTP Server with JavaFX GUI

A lightweight HTTP web server built with Java and JavaFX, featuring a graphical user interface for managing and monitoring server operations.

## 📋 Overview

This project implements a functional HTTP server that handles HTTP requests and serves static content from the `WebRoot` directory. The server includes a JavaFX-based GUI for easy configuration, control, and real-time monitoring of server activity.

## ✨ Features

- **HTTP Server Core**: Full HTTP/1.1 protocol support with GET/POST request handling
- **JavaFX Control Panel**: User-friendly GUI for server management
- **Real-time Monitoring**: Live request logs, connection statistics, and server status
- **Static File Serving**: Serve HTML, CSS, JavaScript, images, and other static assets
- **Dynamic Port Configuration**: Change server port through the GUI interface
- **Request Logging**: View detailed incoming HTTP requests with metadata
- **Server Control**: Start, stop, and restart server operations with button clicks
- **Statistics Dashboard**: Track total requests, requests per second, and uptime

## 🛠️ Tech Stack

- **Language**: Java 11+
- **GUI Framework**: JavaFX 21.0.1
- **Build Tool**: Maven 3.6+
- **JSON Processing**: Jackson Core & Databind (2.9.x)
- **Logging**: SLF4J API with Logback
- **Testing**: JUnit Jupiter 5

## 📦 Dependencies

The project uses Maven for dependency management. Key dependencies include:

- **JavaFX Controls & FXML** (21.0.1) - GUI components
- **Jackson Core & Databind** (2.9.x) - JSON parsing
- **SLF4J API** (1.7.29) - Logging facade
- **Logback Classic** (1.2.3) - Logging implementation
- **JUnit Jupiter** (5.9.3+) - Unit testing framework

See `pom.xml` for the complete dependency list and build configuration.

## 🚀 Getting Started

### Prerequisites

- Java 11 or higher
- Maven 3.6 or later
- Git (for cloning the repository)

### Installation & Build

1. **Clone the repository**
   ```bash
   git clone https://github.com/HajarXXV/http-server-JAVA-JAVAFX.git
   cd http-server-JAVA-JAVAFX
   ```

2. **Build the project**
   ```bash
   mvn clean install
   ```

3. **Run with Maven (GUI)**
   ```bash
   mvn javafx:run
   ```

4. **Or run the compiled JAR**
   ```bash
   mvn package
   java -jar target/simplehttpserver-1.0-SNAPSHOT.jar
   ```

## 📁 Project Structure

```
http-server-JAVA-JAVAFX/
├── 📁 src/
│   ├── main/
│   │   ├── java/com/JavaWebServer/
│   │   │   │
│   │   │   ├── 🌐 http/                # HTTP Protocol Implementation
│   │   │   │       BadHttpVersionException.java
│   │   │   │       HttpHeaderName.java
│   │   │   │       HttpMessage.java
│   │   │   │       HttpMethod.java
│   │   │   │       HttpParser.java
│   │   │   │       HttpParsingException.java
│   │   │   │       HttpRequest.java
│   │   │   │       HttpResponse.java
│   │   │   │       HttpStatusCode.java
│   │   │   │       HttpVersion.java
│   │   │   │
│   │   │   ├── 🖥️ httpserver/          # Server Core
│   │   │   │   │   HttpServer.java
│   │   │   │   │   ServerEventListener.java
│   │   │   │   │
│   │   │   │   ├── config/            # Configuration management
│   │   │   │   │       Configuration.java
│   │   │   │   │       ConfigurationManager.java
│   │   │   │   │       HttpConfigurationException.java
│   │   │   │   │
│   │   │   │   ├── core/              # Threading & connection handling
│   │   │   │   │   │   HttpConnectionWorkerThread.java
│   │   │   │   │   │   ServerListenerThread.java
│   │   │   │   │   │   SessionManager.java
│   │   │   │   │   │
│   │   │   │   │   └── io/            # File serving
│   │   │   │   │           ReadFileException.java
│   │   │   │   │           WebRootHandler.java
│   │   │   │   │           WebRootNotFoundException.java
│   │   │   │   │
│   │   │   │   └── util/              # JSON utilities
│   │   │   │           Json.java
│   │   │   │
│   │   │   └── 🎨 ui/                  # JavaFX User Interface
│   │   │           ServerController.java
│   │   │           ServerGUI.java
│   │   │
│   │   └── resources/
│   │           http.json              # Server configuration
│   │           logback.xml            # Logging configuration
│   │
│   └── test/                          # Unit tests
│       └── java/com/JavaWebServer/
│           ├── http/
│           │       HttpHeadersParserTest.java
│           │       HttpParserTest.java
│           │       HttpVersionTest.java
│           │
│           └── httpserver/
│               └── core/
│                   └── io/
│                           WebRootHandlerTest.java
│
├── 📁 WebRoot/                         # Static files to serve
│       favicon.ico
│       index.html
│       logo.png
│
└── 📁 target/                          # Maven compiled output (auto-generated)
    ├── classes/
    ├── generated-sources/
    ├── maven-status/
    └── test-classes/
├── pom.xml                                       # Maven configuration
├── Request.txt                                   # Sample HTTP request
└── README.md                                     # This file
```

## 💻 Usage

### Starting the Server (GUI Mode)

1. **Build and run the application**
   ```bash
   mvn javafx:run
   ```

2. **JavaFX GUI window opens** with configuration panel

3. **Configure server settings**:
   - Set listening port (default: 8080)
   - Select WebRoot directory (default: `./WebRoot`)

4. **Click "Start Server"** button to begin accepting requests

5. **Monitor in real-time**:
   - View active connections
   - Check incoming requests in the log table
   - Track server statistics

### Accessing the Server

Once running, access your server:

```bash
# Using curl
curl http://localhost:8080/

# Using a web browser
Open http://localhost:8080/ in your browser

# Request a specific file
curl http://localhost:8080/index.html
```

### Configuration

Configuration can be done through:

1. **GUI Interface**:
   - Port field - Change listening port
   - WebRoot field - Select content directory
   - Browse button - File chooser for WebRoot

2. **Configuration File** (`http.json`):
   ```json
   {
     "port": 8080,
     "webroot": "./WebRoot"
   }
   ```

### Command Line Usage

```bash
# Run with default configuration (http.json)
java -jar target/simplehttpserver-1.0-SNAPSHOT.jar

# Run with custom configuration file
java -jar target/simplehttpserver-1.0-SNAPSHOT.jar path/to/config.json
```

## 🔍 How It Works

### Server Architecture

The server uses a multi-threaded architecture:

1. **HttpServer Class**
   - Main entry point
   - Loads configuration
   - Resolves WebRoot path
   - Initializes ServerListenerThread

2. **ServerListenerThread**
   - Listens on configured port via ServerSocket
   - Accepts incoming client connections
   - Spawns new thread for each client request

3. **HttpRequestHandler**
   - Processes individual HTTP requests
   - Parses HTTP headers and method
   - Locates requested resource
   - Generates appropriate HTTP response
   - Handles different status codes (200, 404, 400, etc.)

4. **ServerGUI (JavaFX)**
   - Provides user interface for server management
   - Displays real-time request logs
   - Shows server statistics
   - Enables configuration changes
   - Manages server lifecycle (start/stop/restart)

5. **ServerController**
   - Bridges GUI and server logic
   - Handles user interactions
   - Manages thread lifecycle
   - Updates UI with server events

### Request Processing Flow

```
Client Connection → ServerListenerThread → HttpRequestHandler
                                              ↓
                                    Parse HTTP Request
                                              ↓
                                    Validate Request
                                              ↓
                                    Locate Resource
                                              ↓
                                    Check File Exists
                                              ↓
                                    Read & Serve File
                                              ↓
                                    Send HTTP Response
                                              ↓
                                    Log Request
```

### Sample HTTP Request

```http
GET /index.html HTTP/1.1
Host: localhost:8080
Connection: keep-alive
User-Agent: Mozilla/5.0
Accept: text/html,application/xhtml+xml
Accept-Language: en-US,en;q=0.9
```

### Sample HTTP Responses

**Successful Request (200 OK)**
```http
HTTP/1.1 200 OK
Content-Type: text/html; charset=UTF-8
Content-Length: 1234
Connection: close

<html>
<!-- File content here -->
</html>
```

**File Not Found (404)**
```http
HTTP/1.1 404 Not Found
Content-Type: text/html; charset=UTF-8
Content-Length: 50
Connection: close

<html>
<h1>404 - File Not Found</h1>
</html>
```

**Bad Request (400)**
```http
HTTP/1.1 400 Bad Request
Content-Type: text/plain
Content-Length: 20
Connection: close

Bad Request
```

## 🎨 Supported MIME Types

The server recognizes and serves various file types:

| Type | MIME Type |
|------|-----------|
| HTML | `text/html` |
| CSS | `text/css` |
| JavaScript | `text/javascript` |
| Plain Text | `text/plain` |
| JPEG | `image/jpeg` |
| PNG | `image/png` |
| GIF | `image/gif` |
| JSON | `application/json` |
| PDF | `application/pdf` |
| XML | `application/xml` |

## 🔧 Build Configuration

Maven plugins used in the build process:

1. **Maven JAR Plugin** - Creates executable JAR with main class
2. **Maven Shade Plugin** - Packages all dependencies into a single uber-JAR
3. **JavaFX Maven Plugin** - Enables JavaFX module support and compilation

### Build Commands

```bash
# Clean build
mvn clean install

# Package into JAR
mvn clean package

# Run tests
mvn test

# Run with JavaFX
mvn javafx:run

# Compile only
mvn compile
```

## 📊 Logging

The application uses **SLF4J** with **Logback** for logging output.

### Log Configuration

Log configuration is defined in `src/main/resources/logback.xml`.

### Log Levels

- `INFO` - General information about server operations and startup
- `DEBUG` - Detailed debugging information for development
- `ERROR` - Error messages and exception stack traces
- `WARN` - Warning messages for potential issues

### Example Log Output

```
[INFO] Server starting...
[INFO] No configuration file provided, using default http.json from resources
[INFO] Using Port: 8080
[INFO] Using WebRoot: ./WebRoot
[INFO] Resolved WebRoot path: /home/user/project/WebRoot
[INFO] Server listening on port: 8080
[DEBUG] Client connected from: 127.0.0.1
[INFO] Request: GET /index.html HTTP/1.1
[INFO] Serving file: index.html (Content-Type: text/html)
```

## 🧪 Testing

The project includes unit tests using JUnit Jupiter.

### Running Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=HttpServerTest

# Run with verbose output
mvn test -X
```

### Test Coverage

Tests are located in `src/test/java` and cover:
- HTTP request parsing
- File serving functionality
- Configuration management
- Server lifecycle operations

## 🐛 Troubleshooting

### Port Already in Use

**Error**: `Address already in use`

**Solution**:
```bash
# Find process using port 8080
# macOS/Linux:
lsof -i :8080

# Windows:
netstat -ano | findstr :8080

# Kill the process and retry
# Or change port in GUI before starting
```

### JavaFX Module Errors

**Error**: `module javafx.graphics not found`

**Solution**:
- Ensure Java 11+ is installed
- Check `pom.xml` includes JavaFX plugins
- Run Maven clean: `mvn clean install`

### WebRoot Directory Not Found

**Error**: `Webroot folder not found`

**Solution**:
1. Verify `WebRoot` directory exists in project root
2. Use absolute path in configuration if relative path fails
3. Check file permissions (directory must be readable)

### Connection Issues

**Problem**: Cannot connect to server

**Checklist**:
- [ ] Server has started successfully (no error messages)
- [ ] Port number is correct
- [ ] Firewall allows connections on the port
- [ ] Using correct hostname (localhost, 127.0.0.1, or machine IP)

## 📈 Performance Considerations

- **Concurrency Model**: Thread-per-client (spawns new thread for each request)
- **Memory Usage**: Suitable for low to moderate traffic (< 100 concurrent connections)
- **Latency**: Low latency for static file serving
- **Throughput**: Depends on system resources and network bandwidth
- **Scalability**: Consider load balancing or thread pool improvements for production

### Optimization Tips

- Minimize WebRoot file sizes
- Compress static assets (CSS, JavaScript)
- Use CDN for frequently accessed files
- Monitor memory usage with high traffic

## 🔐 Security Notes

⚠️ **This is an educational/development server. For production use:**

- [ ] **Input Validation**: Implement request validation and sanitization
- [ ] **HTTPS/SSL**: Add SSL/TLS support for encrypted connections
- [ ] **Authentication**: Implement user authentication if needed
- [ ] **Authorization**: Add access control for sensitive resources
- [ ] **Security Headers**: Add HTTP security headers (CSP, X-Frame-Options, etc.)
- [ ] **Path Traversal**: Prevent directory traversal attacks (../ attacks)
- [ ] **Rate Limiting**: Implement request rate limiting
- [ ] **Use Production Servers**: Deploy with Apache, Nginx, or Tomcat for production

## 📚 Additional Resources

- [Java Networking with Sockets](https://docs.oracle.com/javase/tutorial/networking/sockets/index.html)
- [JavaFX Documentation](https://openjfx.io/)
- [HTTP/1.1 Specification (RFC 7230)](https://tools.ietf.org/html/rfc7230)
- [Maven Documentation](https://maven.apache.org/guides/)
- [Jackson Documentation](https://github.com/FasterXML/jackson)
- [Logback Documentation](http://logback.qos.ch/)

## 🤝 Contributing

Contributions are welcome! To contribute:

1. **Fork the repository**
2. **Create a feature branch**
   ```bash
   git checkout -b feature/YourAmazingFeature
   ```
3. **Commit your changes**
   ```bash
   git commit -m 'Add some AmazingFeature'
   ```
4. **Push to the branch**
   ```bash
   git push origin feature/YourAmazingFeature
   ```
5. **Open a Pull Request**

## 📄 License

This project is open source and available under the [MIT License](LICENSE).

## 👨‍💻 Author

**HajarXXV** - [GitHub Profile](https://github.com/HajarXXV)

## 🎯 Future Enhancements

- [ ] HTTPS/SSL/TLS support for encrypted connections
- [ ] Caching mechanisms for improved performance
- [ ] HTTP compression support (gzip, deflate)
- [ ] Virtual hosts support
- [ ] Advanced logging and analytics dashboard
- [ ] Docker containerization
- [ ] REST API endpoints
- [ ] Database connectivity
- [ ] WebSocket support for real-time communication
- [ ] File upload functionality
- [ ] Directory browsing
- [ ] Custom error pages

## 📞 Support & Issues

For issues, questions, or suggestions:

1. **Check existing issues**: [GitHub Issues](https://github.com/HajarXXV/http-server-JAVA-JAVAFX/issues)
2. **Open a new issue** with:
   - Clear title describing the problem
   - Detailed description of the issue
   - Error messages and logs
   - Steps to reproduce
   - Your environment (OS, Java version, Maven version)
3. **Include environment details**:
   - Java version: `java -version`
   - Maven version: `mvn -version`
   - Operating System

---

**Last Updated**: 2026-05-07  
**Version**: 1.0-SNAPSHOT  
**Status**: Active Development  
**Repository**: [HajarXXV/http-server-JAVA-JAVAFX](https://github.com/HajarXXV/http-server-JAVA-JAVAFX)
