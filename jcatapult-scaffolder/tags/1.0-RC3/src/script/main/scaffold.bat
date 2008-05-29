@echo off

::
:: Run ant jar first
::
echo Running ant build
call ant jar> output
del output

::
:: Build up the scaffolding classpath
::
set DIR=%~dp0

::
:: Build the scaffolders classpath
::
setlocal EnableDelayedExpansion
set JarsDir=D:\ant\apache-ant-1.6.1\lib
set cp=
for %%f in (%DIR%..\lib\*.jar) do set cp=!cp!;%%f
endlocal & set RUN_CLASSPATH=%cp%
:: echo Scaffolders classpath is %RUN_CLASSPATH%

::
:: Grab the project JARs
::
setlocal EnableDelayedExpansion
set JarsDir=D:\ant\apache-ant-1.6.1\lib
set cp=
for %%f in (target\jars\*.jar) do set cp=!cp!;%%f
endlocal & set LOCAL_CLASSPATH=%cp%
:: echo Local classpath is %LOCAL_CLASSPATH%

::
:: Grab the deps and transitive deps for the project
::
echo Using Savant to determine project classpath
set PROJECT_CLASSPATH=
FOR /F "delims==" %%G IN ('java -cp "%RUN_CLASSPATH%" org.inversoft.savant.dep.DependencyPathMain') DO set PROJECT_CLASSPATH=%%G
:: echo Savant classpath is %PROJECT_CLASSPATH%

::
:: Execute the scaffolder
::
java -cp "%JAVA_HOME%\lib\tools.jar;%RUN_CLASSPATH%;%LOCAL_CLASSPATH%;%PROJECT_CLASSPATH%" -Dscaffolder.home=%DIR%\.. org.jcatapult.scaffolder.Main %1