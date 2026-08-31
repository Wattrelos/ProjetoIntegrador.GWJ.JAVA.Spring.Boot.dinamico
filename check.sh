#!/usr/bin/env bash

# ==============================================================================
# Script de Diagnóstico e Checagem de Ambiente - TGOS / GWJ (Linux / macOS / WSL)
# ==============================================================================

# Cores ANSI
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
BOLD='\033[1m'
NC='\033[0m' # Sem Cor

echo -e "${CYAN}${BOLD}=================================================================${NC}"
echo -e "${CYAN}${BOLD}    🛠️  TGOS / GWJ - CHECADOR DE AMBIENTE PARA ALUNOS (Linux/Mac)  ${NC}"
echo -e "${CYAN}${BOLD}=================================================================${NC}"
echo ""

# 1. Checa comando java no SO
if ! command -v java &> /dev/null; then
    echo -e "${RED}❌ ERRO: O comando 'java' não foi encontrado no seu PATH!${NC}"
    echo -e "👉 Instale o JDK 21 LTS (OpenJDK / Eclipse Temurin / Oracle) e configure o JAVA_HOME."
    echo -e "   Exemplo Ubuntu/Debian: sudo apt update && sudo apt install openjdk-21-jdk"
    exit 1
fi

JAVA_VER_FULL=$(java -version 2>&1 | head -n 1)
echo -e "${BOLD}☕ Java Detectado:${NC} $JAVA_VER_FULL"

# Checa se o mvnw tem permissão de execução
if [ -f "./mvnw" ] && [ ! -x "./mvnw" ]; then
    chmod +x ./mvnw 2>/dev/null
fi

# Define qual comando Maven usar
if [ -f "./mvnw" ]; then
    MVN_CMD="./mvnw"
elif command -v mvn &> /dev/null; then
    MVN_CMD="mvn"
else
    echo -e "${YELLOW}⚠️ Aviso: Maven Wrapper (./mvnw) e Maven global não encontrados.${NC}"
    echo -e "Tentando executar compilação direta via javac/java..."
fi

echo -e "${CYAN}Compilando e executando testes profundos de diagnóstico...${NC}"
echo ""

if [ -n "$MVN_CMD" ]; then
    # Executa silenciosamente a compilação e depois roda a classe CheckEnvironment
    $MVN_CMD compile -q exec:java -Dexec.mainClass="com.gwj.tools.CheckEnvironment" -Dexec.cleanupDaemonThreads=false
    STATUS=$?
else
    # Fallback caso não tenha mvn: compilar manualmente as classes necessárias
    mkdir -p target/classes
    javac -cp "target/classes:src/main/resources" -d target/classes $(find src/main/java -name "*.java") 2>/dev/null
    java -cp "target/classes:src/main/resources" com.gwj.tools.CheckEnvironment
    STATUS=$?
fi

exit $STATUS
