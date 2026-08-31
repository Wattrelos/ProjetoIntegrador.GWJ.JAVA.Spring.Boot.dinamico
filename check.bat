@echo off
chcp 65001 > nul
setlocal enabledelayedexpansion

echo =================================================================
echo     🛠️  TGOS / GWJ - CHECADOR DE AMBIENTE PARA ALUNOS (Windows)  
echo =================================================================
echo.

:: 1. Checagem do Java
where java >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERRO] O comando 'java' não foi encontrado no seu PATH do Windows!
    echo 👉 Instale o JDK 21 LTS e configure as variáveis de ambiente JAVA_HOME e PATH.
    echo.
    pause
    exit /b 1
)

echo [INFO] Java detectado com sucesso.
echo [INFO] Iniciando checagem profunda de dependências e banco de dados...
echo.

:: 2. Execução do Maven Wrapper ou Maven
if exist "mvnw.cmd" (
    call mvnw.cmd compile -q exec:java -Dexec.mainClass="com.gwj.tools.CheckEnvironment" -Dexec.cleanupDaemonThreads=false
) else (
    where mvn >nul 2>&1
    if %errorlevel% equ 0 (
        call mvn compile -q exec:java -Dexec.mainClass="com.gwj.tools.CheckEnvironment" -Dexec.cleanupDaemonThreads=false
    ) else (
        echo [AVISO] Maven não encontrado no PATH. Execute 'mvnw.cmd compile' se disponível.
    )
)

echo.
pause
