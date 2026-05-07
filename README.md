# HTTP Server with JavaFX GUI

A simple yet functional HTTP web server built with Java and JavaFX, featuring a graphical user interface for easy management and monitoring.

## 📋 Overview

This project implements a lightweight HTTP server that listens for HTTP requests and serves content from the `WebRoot` directory. The server includes a JavaFX-based GUI for starting, stopping, and monitoring server operations. It supports basic HTTP/1.1 protocol features and can handle concurrent client connections.

## ✨ Features

- **HTTP Server**: Handles GET/POST requests with HTTP/1.1 protocol support
- **JavaFX GUI**: User-friendly graphical interface for server management
- **Real-time Monitoring**: View server status, active connections, and request logs
- **Static File Serving**: Serve HTML, CSS, JavaScript, and other static assets from the `WebRoot` directory
- **Configurable Port**: Change server port through the GUI
- **Request Logging**: View incoming HTTP requests in real-time
- **Easy Control**: Start/stop server with simple button clicks

## 🛠️ Tech Stack

- **Language**: Java 11+
- **Build Tool**: Maven
- **GUI Framework**: JavaFX 21.0.1
- **JSON Processing**: Jackson (for potential JSON API support)
- **Logging**: SLF4J with Logback
- **Testing**: JUnit Jupiter 5

## 📦 Dependencies

```xml
<!-- Core Dependencies -->
- JavaFX Controls & FXML (21.0.1)
- Jackson Core & Databind (2.9.x)
- SLF4J API (1.7.29)
- Logback Classic (1.2.3)
- JUnit Jupiter (5.9.3+)
```

See `pom.xml` for complete dependency list.

## 🚀 Getting Started

### Prerequisites

- Java 11 or higher
- Maven 3.6+
- Git

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/HajarXXV/http-server-JAVA-JAVAFX.git
   cd http-server-JAVA-JAVAFX
   ```

2. **Build the project**
   ```bash
   mvn clean install
   ```

3. **Run with Maven**
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
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/JavaWebServer/
│   │   │       ├── httpserver/     # HTTP server implementation
│   │   │       │   └── HttpServer.java
│   │   │       └── ui/             # JavaFX GUI components
│   │   │           └── ServerGUI.java
│   │   └── resources/
│   └── test/                        # Unit tests
├── WebRoot/                         # Static files to serve
├── pom.xml                          # Maven configuration
├── Request.txt                      # Sample HTTP request
└── README.md                        # This file
```

## 💻 Usage

### Starting the Server

1. Run the application:
   ```bash
   mvn javafx:run
   ```

2. The JavaFX GUI window will open

3. Click the **Start Server** button to begin listening for requests

4. The server will listen on the configured port (default: 8080)

### Accessing the Server

Once running, access your server:

```bash
# Using curl
curl http://localhost:8080/

# Using a web browser
Open http://localhost:8080/ in your browser
```

### Configuration

Server settings can be adjusted through the GUI:
- **Port**: Change the listening port (default: 8080)
- **Web Root**: Configure the directory for static files

## 🔍 How It Works

### Server Architecture

1. **HttpServer**: The main server class that:
   - Creates a ServerSocket listening on a specified port
   - Accepts incoming client connections
   - Spawns threads to handle each client request
   - Processes HTTP requests and sends responses

2. **ServerGUI**: JavaFX interface that:
   - Provides controls to start/stop the server
   - Displays server status and statistics
   - Shows real-time request logs
   - Allows configuration of server settings

### Request Handling

When a client connects:
1. Server reads the HTTP request
2. Parses the request method (GET, POST, etc.)
3. Determines the requested resource
4. Looks up the file in the WebRoot directory
5. Returns appropriate HTTP response (200, 404, etc.)
6. Logs the request for monitoring

### Sample HTTP Request

```http
GET / HTTP/1.1
Host: localhost:8080
Connection: keep-alive
User-Agent: Mozilla/5.0
Accept: text/html,application/xhtml+xml
Accept-Language: en-US,en;q=0.9
```

## 🧪 Testing

Run tests using Maven:

```bash
mvn test
```

## 📝 Example Responses

### Successful Request (200 OK)
```http
HTTP/1.1 200 OK
Content-Type: text/html; charset=UTF-8
Content-Length: 1234
Connection: keep-alive

<html>
<!-- File content -->
</html>
```

### File Not Found (404)
```http
HTTP/1.1 404 Not Found
Content-Type: text/html; charset=UTF-8
Content-Length: 50

<html>
<h1>404 - File Not Found</h1>
</html>
```

## 🎨 Supported MIME Types

- `text/html` - HTML files
- `text/css` - CSS stylesheets
- `text/javascript` - JavaScript files
- `image/jpeg` - JPEG images
- `image/png` - PNG images
- `image/gif` - GIF images
- `application/json` - JSON data
- `application/pdf` - PDF documents
- And more...

## 🔧 Build Configuration

The project uses Maven with special plugins:

- **Maven JAR Plugin**: Creates executable JAR with main class
- **Maven Shade Plugin**: Packages all dependencies into a single uber-JAR
- **JavaFX Maven Plugin**: Enables JavaFX module support and compilation

```bash
# Build a standalone JAR
mvn clean package

# This creates a fat JAR with all dependencies included
java -jar target/simplehttpserver-1.0-SNAPSHOT.jar
```

## 📊 Logging

The application uses SLF4J with Logback for logging. Log configuration can be found in `logback.xml`.

### Log Levels
- `INFO`: General information about server operations
- `DEBUG`: Detailed debugging information
- `ERROR`: Error messages and stack traces

## 🐛 Troubleshooting

### Port Already in Use
If port 8080 is already in use:
```bash
# Change the port in the GUI and restart
# Or use a different port: lsof -i :8080 (on macOS/Linux)
```

### JavaFX Module Errors
If you encounter JavaFX module errors:
```bash
# The pom.xml includes necessary configurations
# Ensure Java 11+ is installed and properly configured
```

### Network Access Issues
- Ensure the firewall allows connections on your configured port
- Check that the server has started successfully in the GUI
- Verify the host is accessible (use localhost or 127.0.0.1)

## 📈 Performance Considerations

- **Concurrency**: Uses thread-per-client model for handling connections
- **Memory**: Suitable for low to moderate traffic scenarios
- **Scalability**: For production use, consider load balancing

## 🔐 Security Notes

⚠️ **This is a simple educational/development server. For production use:**
- Implement proper input validation
- Add HTTPS/SSL support
- Implement authentication and authorization
- Add security headers
- Use established production web servers (Apache, Nginx)

## 📚 Additional Resources

- [Java HTTP Handling](https://docs.oracle.com/javase/tutorial/networking/sockets/index.html)
- [JavaFX Documentation](https://openjfx.io/)
- [HTTP/1.1 Specification](https://tools.ietf.org/html/rfc7230)
- [Maven Documentation](https://maven.apache.org/guides/)

## 🤝 Contributing

Contributions are welcome! To contribute:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is open source and available under the [MIT License](LICENSE).

## 👨‍💻 Author

**HajarXXV** - [GitHub Profile](https://github.com/HajarXXV)

## 🎯 Future Enhancements

- [ ] HTTPS/SSL support
- [ ] Caching mechanisms
- [ ] Compression support (gzip)
- [ ] Virtual hosts
- [ ] Advanced logging and analytics
- [ ] Docker containerization
- [ ] REST API endpoints
- [ ] Database connectivity
- [ ] WebSocket support

## 📞 Support

For issues, questions, or suggestions:
1. Check existing [GitHub Issues](https://github.com/HajarXXV/http-server-JAVA-JAVAFX/issues)
2. Open a new issue with detailed information
3. Include error messages and steps to reproduce

---

**Last Updated**: 2026-05-07
**Version**: 1.0-SNAPSHOT
**Status**: Active Development
