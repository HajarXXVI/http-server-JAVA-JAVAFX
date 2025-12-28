# Testing Guide for Simple Java HTTP Server

## Prerequisites
- Java 8 or higher installed
- Maven installed (or use Maven wrapper if available)

## Step 1: Build the Project

### Option A: Using Maven (if installed)
```bash
cd simple-java-http-server-master
mvn clean package
```

### Option B: Using Maven Wrapper (if available)
```bash
cd simple-java-http-server-master
./mvnw clean package
```

This will:
- Compile all Java files
- Run tests (if any)
- Create a JAR file in `target/` directory
- The JAR will be named: `simplehttpserver-1.0-SNAPSHOT.jar`

## Step 2: Run the Server

### Option A: Using Default Configuration (Recommended)
```bash
java -jar target/simplehttpserver-1.0-SNAPSHOT.jar
```

This will use the default `http.json` from resources (port 8080, webroot: WebRoot)

### Option B: Using Custom Configuration File
```bash
java -jar target/simplehttpserver-1.0-SNAPSHOT.jar myconfig.json
```

**Expected Output:**
```
Server starting...
No configuration file provided, using default http.json from resources
Using Port: 8080
Using WebRoot: WebRoot
 * Connection accepted: /127.0.0.1
```

## Step 3: Test the Server

### Test 1: Using Web Browser (Easiest)
1. Open your web browser
2. Navigate to: `http://localhost:8080`
3. You should see the index.html page with "Welcome to our first rendered page!"

### Test 2: Using curl (Command Line)

**Test GET request:**
```bash
curl http://localhost:8080
```

**Test GET request with verbose output:**
```bash
curl -v http://localhost:8080
```

**Test HEAD request:**
```bash
curl -I http://localhost:8080
```

**Test specific file:**
```bash
curl http://localhost:8080/index.html
curl http://localhost:8080/logo.png
```

**Test 404 (file not found):**
```bash
curl -v http://localhost:8080/nonexistent.html
```

### Test 3: Using PowerShell (Windows)

**Test GET request:**
```powershell
Invoke-WebRequest -Uri http://localhost:8080
```

**Test with response details:**
```powershell
$response = Invoke-WebRequest -Uri http://localhost:8080
$response.StatusCode
$response.Content
```

**Test HEAD request:**
```powershell
Invoke-WebRequest -Uri http://localhost:8080 -Method Head
```

### Test 4: Using telnet (Raw HTTP)

```bash
telnet localhost 8080
```

Then type:
```
GET / HTTP/1.1
Host: localhost:8080

```
(Press Enter twice after Host line)

## Step 4: Test Different Scenarios

### ✅ Valid Requests (Should Work)
- `GET /` → Returns index.html
- `GET /index.html` → Returns index.html
- `GET /logo.png` → Returns logo.png image
- `HEAD /` → Returns headers only (no body)
- `HEAD /index.html` → Returns headers only

### ❌ Invalid Requests (Should Return Errors)
- `POST /` → Returns 501 Not Implemented
- `GET /nonexistent.html` → Returns 404 Not Found
- `GET /../` → Should be blocked (path traversal protection)

## Step 5: Check Server Logs

Watch the console output. You should see:
- Connection accepted messages
- GET Request messages
- Connection Processing Finished messages
- Error messages for bad requests

## Troubleshooting

### Port Already in Use
**Error:** `java.net.BindException: Address already in use`

**Solution:**
- Change port in `http.json` to a different port (e.g., 8081)
- Or stop the process using port 8080

### WebRoot Folder Not Found
**Error:** `Webroot folder not found`

**Solution:**
- Make sure `WebRoot` folder exists in the project root
- Or update the path in `http.json`

### Cannot Find Main Class
**Error:** `Error: Could not find or load main class`

**Solution:**
- Make sure you built the project: `mvn clean package`
- Use the JAR from `target/` directory
- Check that the JAR includes dependencies (should be a "fat JAR")

### Connection Refused
**Error:** `Connection refused` when testing

**Solution:**
- Make sure the server is running
- Check the port number matches
- Check firewall settings

## Expected Test Results

### Successful GET Request
```
HTTP/1.1 200 OK
Content-Type: text/html
Content-Length: [file size]

[HTML content]
```

### Successful HEAD Request
```
HTTP/1.1 200 OK
Content-Type: text/html
Content-Length: [file size]

[No body]
```

### 404 Not Found
```
HTTP/1.1 404 Not Found

[No body]
```

### 501 Not Implemented (POST request)
```
HTTP/1.1 501 Not Implemented

[No body]
```

## Quick Test Script

Save this as `test-server.sh` (Linux/Mac) or `test-server.bat` (Windows):

**Windows (test-server.bat):**
```batch
@echo off
echo Testing HTTP Server...
echo.
echo Test 1: GET /
curl http://localhost:8080
echo.
echo Test 2: HEAD /
curl -I http://localhost:8080
echo.
echo Test 3: GET /logo.png
curl -I http://localhost:8080/logo.png
echo.
echo Test 4: 404 Test
curl -v http://localhost:8080/nonexistent.html
```

Run it: `test-server.bat`

