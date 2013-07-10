@echo off
title Chequear
mkdir docs\doccheck
@echo Chequeando documentacion...
javadoc  -sourcepath src;test -classpath .\lib\ij.jar;.\lib\commons-io-2.4.jar;.\lib\weka.jar;.\lib\jh.jar;.\lib\ejml-0.21.jar;.\lib\junit-4.11.jar ^
 -d docs\doccheck -doclet com.sun.tools.doclets.doccheck.DocCheck -docletpath .\lib\doccheck.jar ^
-subpackages es.ubu.XRayDetector.utils ^
es.ubu.XRayDetector.datos ^
es.ubu.XRayDetector.modelo.preprocesamiento ^
es.ubu.XRayDetector.modelo.feature ^
es.ubu.XRayDetector.modelo.ventana ^
es.ubu.XRayDetector.modelo ^
es.ubu.XRayDetector.interfaz ^
es.ubu.XRayDetector.test.utils ^
es.ubu.XRayDetector.test.modelo.preprocesamiento ^
es.ubu.XRayDetector.test.modelo.feature ^
es.ubu.XRayDetector.test.modelo.preprocesamiento ^
es.ubu.XRayDetector.test.modelo ^
es.ubu.XRayDetector.test
@echo Documentacion chequeada con exito!!!!
