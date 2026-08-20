# Análise de Viabilidade Técnica: Hospedagem Spring Boot + MariaDB

**Status:** Aprovado  
**Data:** 20/08/2026  
**Contexto:** Avaliação de infraestrutura para produção.

**Resumo Executivo:**
A configuração proposta (**2 vCPU cores, 8 GB RAM, 100 GB NVMe disk space e 8 TB bandwidth**) é **totalmente viável e recomendada** para rodar o sistema em produção. A combinação de hardware moderno (NVMe, 8GB RAM) e arquitetura desacoplada (Nginx + Spring Boot + MariaDB) garantirá excelente performance e escalabilidade.

---

### 1. Análise Detalhada dos Recursos

| Recurso | Configuração | Consumo Estimado da Aplicação | Veredito |
| :--- | :--- | :--- | :--- |
| **Memória RAM** | **8 GB** | • JVM Java 21 / Spring Boot: **1,5 GB a 2,5 GB** (`-Xmx2g`)<br>• MariaDB (InnoDB Buffer Pool): **2,0 GB a 3,0 GB**<br>• Sistema Operacional (Linux) + Nginx: **0,5 GB a 1,0 GB**<br>• **Uso total esperado:** **4,0 GB a 6,5 GB** | **Sobram 1,5 GB a 4 GB de margem de segurança.** Evita erros de Out-Of-Memory (OOM) mesmo em picos. |
| **Processador** | **2 vCPU Cores** | Java 21 lida muito bem com concorrência usando threads do Tomcat/JVM. Suporta com tranquilidade entre **80 a 200 requisições simultâneas por segundo (RPS)** para páginas Thymeleaf e APIs JSON. | **Mais que suficiente** para pequenas e médias empresas ou milhares de usuários ativos por dia. |
| **Armazenamento** | **100 GB NVMe SSD** | • Sistema Operacional (Ubuntu/Debian): ~5-8 GB<br>• Aplicação (JAR compilado): < 150 MB<br>• Banco de Dados (tabelas e índices iniciais): < 1 GB<br>• Logs do Spring/Nginx + uploads de mídia: ~10-20 GB | **Muito amplo.** O NVMe entrega IOPS altíssimos, acelerando drasticamente as queries SQL do DataMapper e o startup da JVM. |
| **Tráfego/Banda** | **8 TB / mês** | ~260 GB/dia (ou ~24 Mbps contínuos). Com compressão Gzip/Brotli via Nginx, cada página HTML/JSON consome apenas ~50 KB a 300 KB. | **Abundante.** Suporta tranquilamente mais de **15 a 30 milhões de requisições por mês**. |

---

### 2. Arquiteturas Recomendadas de Implantação

#### Opção A: Servidor Único All-in-One (Custo-Benefício Máximo)
Você pode rodar tudo na mesma VPS de 2 vCPU / 8 GB RAM:
- **Reverse Proxy**: Nginx ou Caddy (gerenciando SSL/HTTPS automático com Let's Encrypt).
- **Backend**: Aplicação Spring Boot empacotada em `.jar` rodando como serviço `systemd` ou container Docker.
- **Banco de Dados**: MariaDB / MySQL local na mesma máquina.
- **Custo estimado no mercado**: \$15 a \$25 USD/mês (ex: Hetzner Cloud CPX31, DigitalOcean, Linode, AWS Lightsail).

#### Opção B: Arquitetura Desacoplada (Maior Alta Disponibilidade)
Se preferir isolar o banco de dados da aplicação:
- **VPS Aplicação (Spring Boot)**: 2 vCPU / 4 GB RAM (~ \$12 - \$18 / mês)
- **Banco de Dados Gerenciado (AWS RDS / DigitalOcean Managed DB)**: 1 vCPU / 2 GB RAM (~ \$15 - \$25 / mês)

---

### 3. Recomendações de Configuração no Servidor

Para extrair o melhor desempenho dessa instância de 8 GB RAM:

1. **Parâmetros da JVM (Spring Boot)**:
   ```bash
   java -Xms1g -Xmx2g -XX:+UseG1GC -jar projeto-0.0.1-SNAPSHOT.jar
   ```
2. **Configuração do MariaDB (`/etc/mysql/mariadb.conf.d/50-server.cnf`)**:
   ```ini
   innodb_buffer_pool_size = 2G
   innodb_log_file_size = 256M
   max_connections = 150
   ```
3. **Nginx como Reverse Proxy**:
   Ativar compressão de respostas e cache de arquivos estáticos (`/css`, `/js`, imagens) no Nginx para poupar CPU e banda do Spring Boot.