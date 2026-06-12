# Diretrizes de Engenharia do Projeto

## Estilo de Código e Stack
- **Linguagem:** Java 21 LTS (Utilizar Records, Pattern Matching e Virtual Threads sempre que aplicável).
- **Camada de Visão:** Thymeleaf 3.0. Evite embutir lógica complexa nas tags; prefira processar os dados inteiramente no Controller/Service.
- **Padrões de Projeto (GoF):** Priorize o uso de [Design Patterns](https://digitalocean.com) explícitos para desacoplamento:
  - Utilize *Strategy* para alternar algoritmos de validação.
  - Utilize *Factory* ou *Builder* para construção de agregados complexos.
