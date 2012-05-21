@echo off
REM
REM Copyright � 2001 Sun Microsystems, Inc.  All rights reserved.
REM Use is subject to license terms.
REM

REM Workfile:@(#)build_samples.bat	1.3
REM Version:1.3
REM Modified:03/26/01 13:37:49
REM Original author:  Oleg Danilov

 setlocal
 
 set JAVA_HOME=%~dp0\jdk1.3.1_28
 set JC21_HOME=%~dp0\java_card_kit-2_1_2

 if "%JAVA_HOME%" == "" goto warning1
 if "%JC21_HOME%" == "" goto warning2

:: Help

 if "%1" == "help" goto help
 if "%1" == "-help" goto help

 
:: Copy source
 if exist %JC21_HOME%\applet\src\nul rmdir /s/q %JC21_HOME%\applet\src
 mkdir %JC21_HOME%\applet\src
 xcopy /s src\*.* %JC21_HOME%\applet\src
 
 cd /d %JC21_HOME%\applet

:: Clean

 if exist classes\nul rmdir /s/q classes

 if "%1" == "clean" goto quit
 if "%1" == "-clean" goto quit

 set JC_PATH=.;%JC21_HOME%\applet\classes
 set JCFLAGS=-g -d %JC21_HOME%\applet\classes -classpath "%JC_PATH%"

:: Extract classes from api21.jar

 mkdir classes
 cd classes
 %JAVA_HOME%\bin\jar -xvf %JC21_HOME%\lib\api21.jar
 rmdir /s/q META-INF
 cd ..

:: Copy export files

 xcopy /s %JC21_HOME%\api21_export_files\*.* classes\

:: Compile samples

 %JAVA_HOME%\bin\javac %JCFLAGS% src\com\bellsolutions\javacard\SAM\*.java

:: Convert samples

 cd classes
 call %JC21_HOME%\bin\converter -config ..\src\com\bellsolutions\javacard\SAM\SAM.opt
 cd ..
 del ..\..\bin\SAM.cap
 xcopy /s/q classes\com\bellsolutions\javacard\SAM\javacard\SAM.cap ..\..\bin\

 goto quit

:warning1
 echo Set environment variable JAVA_HOME
 goto quit

:warning2
 echo Set environment variable JC21_HOME
 goto quit

:help
 echo Usage: build_samples [options]
 echo Where options include:
 echo        -help     print out this message
 echo        -clean    remove all produced files
 goto quit

:quit
 endlocal
