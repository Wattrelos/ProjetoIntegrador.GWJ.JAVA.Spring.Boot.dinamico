# Projeto Integrador GWJ JAVA dinâmico, agora, utilizando o padrão Spring Boot MVC.
## Padrões de projeto (GoF) aplicados até o momento:
- Singleton (conexão com o BD)
- Factory (construtor de objetos)
- DAO  (persistir objetos no BD dinamicamente, inclusive recursivamente, persistindo classes filhas)
- DTO (preenche objetos dinamicamente com dados do formulário HTTP ou com dados do BD)
- Templante ou Decorator (Para construír páginas web).
Esta é a outra forma de CRUD mais dinâmica e enxuta.

1. Primeiro, instale o banco de dados de testes:
gwj2.SQL

2. Crie um usuário. A boa prática é criar um usuário específico para o banco de dados, ao invés de usar root:
```
-- MySQL 8.0+: O comando GRANT ... IDENTIFIED BY foi removido. Primeiro você deve criar o usuário e depois dar as permissões:
-- 1. Cria o usuário primeiro
CREATE USER 'desenvolvedor'@'%' IDENTIFIED BY 'b2#FbXPQTu4FYw';
-- 2. Garante privilégios totais apenas no banco gwj2
GRANT ALL PRIVILEGES ON `gwj2`.* TO 'desenvolvedor'@'%';
-- 6. Aplica as mudanças
FLUSH PRIVILEGES;
```

Agora, basta executar o arquivo src/main/java/com/gwj/AppConfig.java (botão rum).
Atenção: A porta padrão do projeto é 8080. S der erro na inicialização, basta trovar por uma porta livre.

Se não houver mensagens de erro, basta testar no navegador:\
Thymeleaf:
```
http://localhost:8080/
```

3. Depois de implementar o banco de dados e inicializar o teu servidor, tu podes utilizar os seguintes comandos no navegador:\
Ver a lista de clientes;
```
http://localhost:8080/read-json?entity=Cliente
```
Ver um cliente específico pelo número ID:
```
http://localhost:8080/read-json?entity=Cliente&id=29
```
Ver endereços:
```
http://localhost:8080/read-json?entity=Endereco
```

Agora, para simular as requisições POST, recomendo o uso de um aplicativo, por exemplo, o Postman.
Isso é necessário para enviar dados via POST, para simular os comandos create, update e delete:
```
http://localhost:8080/create-json?entity=Cliente
```
acrescentar parâmetros no corpo da requisição (devem ser iguais aos nomes de atributos das classes domínio).
```
http://localhost:8080/update-json?entity=Cliente
```
acrescentar parâmetros no corpo da requisição (devem ser iguais aos nomes de atributos das classes domínio).
```
http://localhost:8080/delete-json?entity=Cliente
```
(acrescentar parâmetros no corpo da requisição com o key=id e Value= [número id do registro].
