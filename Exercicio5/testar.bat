@echo off
setlocal
cd /d "%~dp0"
if not exist out\original mkdir out\original
if not exist out\refatorado mkdir out\refatorado
javac -encoding UTF-8 -d out\original original\Locadora\*.java
if errorlevel 1 exit /b 1
javac -encoding UTF-8 -d out\refatorado Locadora\*.java testes\LocadoraTest.java
if errorlevel 1 exit /b 1
java -cp out\refatorado LocadoraTest out\original
if errorlevel 1 exit /b 1
