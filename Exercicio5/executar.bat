@echo off
setlocal
cd /d "%~dp0"
if not exist out\refatorado mkdir out\refatorado
javac -encoding UTF-8 -d out\refatorado Locadora\*.java
if errorlevel 1 exit /b 1
java -cp out\refatorado Locadora
if errorlevel 1 exit /b 1
