# ==============================================================================
# Script de Diagnóstico e Checagem de Ambiente - TGOS / GWJ (PowerShell)
# ==============================================================================

[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

Write-Host "=================================================================" -ForegroundColor Cyan
Write-Host "    🛠️  TGOS / GWJ - CHECADOR DE AMBIENTE PARA ALUNOS (PowerShell)" -ForegroundColor Cyan
Write-Host "=================================================================" -ForegroundColor Cyan
Write-Host ""

# 1. Checa Java
if (-not (Get-Command java -ErrorAction SilentlyContinue)) {
    Write-Host "❌ ERRO: O comando 'java' não foi encontrado no seu PATH!" -ForegroundColor Red
    Write-Host "👉 Instale o JDK 21 LTS e configure as variáveis de ambiente JAVA_HOME e PATH." -ForegroundColor Yellow
    exit 1
}

Write-Host "☕ Java Detectado no sistema." -ForegroundColor Green
Write-Host "Iniciando compilação e diagnóstico avançado..." -ForegroundColor Cyan
Write-Host ""

# 2. Executa Maven
if (Test-Path ".\mvnw.cmd") {
    .\mvnw.cmd compile -q exec:java -Dexec.mainClass="com.gwj.tools.CheckEnvironment" -Dexec.cleanupDaemonThreads=false
} elseif (Get-Command mvn -ErrorAction SilentlyContinue) {
    mvn compile -q exec:java -Dexec.mainClass="com.gwj.tools.CheckEnvironment" -Dexec.cleanupDaemonThreads=false
} else {
    Write-Host "❌ Maven ou Maven Wrapper não encontrados." -ForegroundColor Red
}
