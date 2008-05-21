@echo off

rem Ensure JCATAPULT_HOME is defined in developer's environment.

if not "%JCATAPULT_HOME%" == "" goto foundJcatHome
  echo The JCATAPULT_HOME environment variable is not defined correctly.
  echo This environment variable is needed to run this program.
goto end

:foundJcatHome
  rem echo Found JCATAPULT_HOME = %JCATAPULT_HOME%

rem Define directory where the ant build script will be executed from
set dir=%JCATAPULT_HOME%\tools\makeproject
rem echo dir=%dir%

if "%1" == "help" goto displayUsageMessage
if "%1" == "usage" goto displayUsageMessage

if not "%*" == "" goto cmdLineArgsFound

:displayUsageMessage
  rem echo No command line arguments were found.
  echo info:  make-project is a shell script that provides a convenient execution wrapper for the JCatapult Ant tools makeproject suite.
  echo usage: make-project ^<ant-target^>
  echo.
  echo executing 'ant -p' to display available makeproject ant targets...
  echo.
  ant -f %dir%\build.xml -p
goto end

:cmdLineArgsFound
  rem echo Found command line arguments: %*
  ant -f %dir%\build.xml %1

:end
