@echo off
title ResQNet - Smart Emergency Response System Starter
color 0A

echo =========================================================================
echo             ResQNet - Smart Emergency Response System
echo =========================================================================
echo.

set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-25.0.4.101-hotspot
set CATALINA_HOME=C:\apache-tomcat-10.1
set PATH=%JAVA_HOME%\bin;C:\apache-maven-3.9.9\bin;%PATH%

echo [1/3] Checking MySQL Daemon...
netstat -ano | findstr 3306 >nul
if %errorlevel% neq 0 (
    echo Starting MySQL from XAMPP...
    start /B "" "C:\xampp\mysql\bin\mysqld.exe" --defaults-file="C:\xampp\mysql\bin\my.ini" --standalone
    timeout /t 3 /nobreak >nul
) else (
    echo MySQL is already active on port 3306.
)

echo [2/3] Building and Packaging ResQNet with Maven...
cd /d "C:\Users\aitha\Desktop\project\ResQNet"
call mvn clean package -DskipTests
if %errorlevel% neq 0 (
    echo.
    echo ERROR: Maven build failed. Check error messages above.
    pause
    exit /b %errorlevel%
)

echo.
echo [3/3] Deploying to Apache Tomcat 10.1...
copy /Y "target\ResQNet.war" "C:\apache-tomcat-10.1\webapps\ResQNet.war"

echo.
echo =========================================================================
echo   SUCCESS! Starting Tomcat Server...
echo   Once Tomcat starts, open: http://localhost:8080/ResQNet/
echo =========================================================================
echo.

cd /d "C:\apache-tomcat-10.1\bin"
call catalina.bat run
