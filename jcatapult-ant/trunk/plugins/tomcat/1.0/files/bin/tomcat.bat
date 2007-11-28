@echo off

rem ----------
rem Verify CATALINA_HOME is defined
rem ----------
if not "%CATALINA_HOME%" == "" goto catHomeOk
echo The CATALINA_HOME environment variable is not defined correctly
echo This environment variable is needed to run this program
goto end
:catHomeOk

rem ----------
rem set current and bin directory
rem ----------
SET CURRENT_DIR=%cd%
echo CURRENT_DIR = %CURRENT_DIR%
set BIN_DIR=%~dp0
echo BIN_DIR = %BIN_DIR%
cd %BIN_DIR%

rem ----------
rem Calculate the webroot path
rem ----------
cd ../../../
set PROJECT_ROOT=%cd%
echo PROJECT_ROOT = %PROJECT_ROOT%

rem ----------
rem Set the catalina base
rem ----------
set CATALINA_BASE=%PROJECT_ROOT%\target\tomcat
echo CATALINA_BASE = %CATALINA_BASE%

rem ----------
rem set the web app directory
rem ----------
set WEBAPP_ROOT=%PROJECT_ROOT%\@WEB_DIR@
echo WEBAPP_ROOT = %WEBAPP_ROOT%

rem ----------
rem Call catalina.bat
rem ----------
set JAVA_OPTS="-Drundir=%WEBAPP_ROOT%"
echo Starting Tomcat from %CATALINA_BASE%
echo Starting webapp from %WEBAPP_ROOT%
%CATALINA_HOME%/bin/catalina.bat %1 %2

:end
