@echo off

title Compilar

mkdir bin

@echo Compilando...

javac -d bin -Xlint ^
 -cp .\lib\ij.jar;.\lib\commons-io-2.4.jar;.\lib\weka.jar;.\lib\jh.jar;.\lib\ejml-0.21.jar;.\lib\junit-4.11.jar ^
src\es\ubu\XRayDetector\utils\*.java ^
src\ij\plugin\frame\*.java ^
src\es\ubu\XRayDetector\datos\*.java ^
src\es\ubu\XRayDetector\modelo\preprocesamiento\*.java ^
src\es\ubu\XRayDetector\modelo\feature\*.java ^
src\es\ubu\XRayDetector\modelo\ventana\*.java ^
src\es\ubu\XRayDetector\modelo\*.java ^
src\es\ubu\XRayDetector\interfaz\*.java ^
test\es\ubu\XRayDetector\test\utils\*.java ^
test\es\ubu\XRayDetector\test\modelo\feature\*.java ^
test\es\ubu\XRayDetector\test\modelo\preprocesamiento\*.java ^
test\es\ubu\XRayDetector\test\modelo\*.java ^
test\es\ubu\XRayDetector\test\*.java

@echo Compilacion realizada con exito!!!

