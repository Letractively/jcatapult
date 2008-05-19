@echo off

::
:: Get the directory where the batch file is executed from
::
set DIR=%~dp0

::
:: Build the deployer classpath
::
setlocal EnableDelayedExpansion
set cp=
for %%f in (%DIR%..\lib\*.jar) do set cp=!cp!;%%f
endlocal & set RUN_CLASSPATH=%cp%
:: echo deployer classpath is %RUN_CLASSPATH%

set deployXml=

::
:: Execute the deployer
::
java -cp "%JAVA_HOME%\lib\tools.jar;%RUN_CLASSPATH%" org.jcatapult.deployment.DeploymentManager %1 .\ deploy\remote\deploy.xml