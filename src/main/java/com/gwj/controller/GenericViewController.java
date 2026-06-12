package com.gwj.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.gwj.model.dataAccessObject.DataAccessObject;
import com.gwj.model.dataTransferObject.EntityMapper;
import com.gwj.model.domain.IEntity;
import com.gwj.model.domain.factory.SimpleObjectFactory;

import java.util.Collection;
import java.util.List; // Para o List
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Field;

import jakarta.servlet.http.HttpServletRequest;

/* Por que essa solução é ideal para este caso:
* Filtro Automático: Como o seu dao.read já ignora nulos, se o usuário chamar /listar?entity=Cliente&nome=João, o DAO fará um WHERE nome = 'João'. Se chamar apenas /listar?entity=Cliente, ele trará todos.
* Herança e Coleções: Como o SimpleObjectFactory e o EntityMapper já tratam a complexidade dos objetos, o Controller atua apenas como um "maestro", sem precisar saber se está lidando com um Cliente ou um Fornecedor.
* Manutenção Zero: Se você criar uma nova classe Transportadora amanhã, não precisa mexer no Java. Basta acessar /listar?entity=Transportadora, por exemplo.
*/

@Controller
public class GenericViewController {
    private final DataAccessObject dao = new DataAccessObject();

    // Helper global injetado em todas as Views para exibir um "Nome Amigável"
    // dinamicamente nos Dropdowns
    @ModelAttribute("displayHelper")
    public Function<Object, String> displayHelper() {
        return obj -> {
            if (obj == null)
                return "—";

            // Tenta buscar por um campo de identificação comum em ordem de prioridade
            String[] metodos = { "getNome", "getNomeUsuario", "getTitulo", "getDescricao", "getRazaoSocial" };
            for (String m : metodos) {
                try {
                    Object value = obj.getClass().getMethod(m).invoke(obj);
                    if (value != null && !value.toString().isBlank()) {
                        return value.toString();
                    }
                } catch (Exception ignored) {
                    // Método não existe nesta classe, tenta o próximo da lista
                }
            }

            // Fallback: se nenhum dos nomes amigáveis existir, retorna o ID
            try {
                Object id = obj.getClass().getMethod("getId").invoke(obj);
                if (id != null)
                    return "ID: " + id.toString();
            } catch (Exception ignored) {
            }

            return obj.toString();
        };
    }

    // Método auxiliar recursivo para buscar atributos da classe atual e de suas
    // superclasses
    private List<Field> getAllFields(Class<?> type) {
        List<Field> fields = new ArrayList<>();
        Class<?> current = type;
        while (current != null && IEntity.class.isAssignableFrom(current) && current != Object.class) {
            // Insere no início (posição 0) para que os campos da classe pai fiquem no topo
            // do formulário
            fields.addAll(0, Arrays.asList(current.getDeclaredFields()));
            current = current.getSuperclass();
        }
        return fields;
    }

    @GetMapping("/MRYnZpAsC9sp/create/{entity}")
    public String create(@PathVariable("entity") String entityName, HttpServletRequest request, Model model) {

        if (entityName != null && !entityName.isBlank()) {
            IEntity entidadeBase = SimpleObjectFactory.create(entityName); // Instanciar um objeto vazio (o corpo será
                                                                           // utilizado para montar o formulário).

            // Reflexão para os cabeçalhos
            List<String> colunas = getAllFields(entidadeBase.getClass()).stream()
                    .filter(field -> !Collection.class.isAssignableFrom(field.getType()))
                    .map(Field::getName)
                    .toList();

            // Mapeia chaves estrangeiras (ManyToOne) para preencher os Selects
            Map<String, List<IEntity>> foreignKeys = new HashMap<>();
            for (Field field : getAllFields(entidadeBase.getClass())) {
                if (IEntity.class.isAssignableFrom(field.getType())
                        && !Collection.class.isAssignableFrom(field.getType())) {
                    try {
                        IEntity fkInstance = (IEntity) field.getType().getDeclaredConstructor().newInstance();
                        foreignKeys.put(field.getName(), dao.read(fkInstance)); // Busca todos os registros
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            model.addAttribute("foreignKeys", foreignKeys);
            model.addAttribute("obj", entidadeBase); // Envia o objeto vazio para o formulário

            model.addAttribute("title", "Formulario de cadastro");
            model.addAttribute("colunas", colunas);
            model.addAttribute("entidadeNome", entityName);

            return "admin/create";
        }

        return "error"; // Ou redirecionamento para lista
    }

    @GetMapping("/MRYnZpAsC9sp/listar/{entity}")
    public String listar(@PathVariable("entity") String entityName, HttpServletRequest request, Model model) {
        IEntity entidadeBase = SimpleObjectFactory.create(entityName);
        IEntity filtro = EntityMapper.fillEntity(entidadeBase, request); // request para preencher os atributos das
                                                                         // classes com valores, que serão utilizado
                                                                         // para formar o WHERE. Se requeste não tiver
                                                                         // valores, traz todos os registros.
        List<IEntity> resultados = dao.read(filtro); // Traz todos os registros ou os encontrados no critério de busca.

        // Extrai os nomes dos atributos da classe para usar como cabeçalho
        List<String> colunas = getAllFields(entidadeBase.getClass()).stream()
                .map(Field::getName)
                .collect(Collectors.toList());

        model.addAttribute("lista", resultados);
        model.addAttribute("colunas", colunas);
        model.addAttribute("entidadeNome", entityName);
        return "admin/listagem-dinamica";
    }

    @GetMapping("MRYnZpAsC9sp/detalhe/{entity}")
    public String detalhe(@PathVariable("entity") String entityName, HttpServletRequest request, Model model) {
        // 1. Pega o ID do request (parâmetro da URL ou form)
        String idParam = request.getParameter("id");

        // 2. Tenta converter com segurança
        Long entityId = null;
        try {
            if (idParam != null && !idParam.isBlank()) {
                entityId = Long.valueOf(idParam.trim());
            }
        } catch (NumberFormatException e) {
            // ID inválido (ex: "abc")
        }

        if (entityName != null && !entityName.isBlank() && entityId != null) {
            IEntity entidadeBase = SimpleObjectFactory.create(entityName);

            // Preenche a entidade com os dados do request
            IEntity filtro = EntityMapper.fillEntity(entidadeBase, request);

            List<IEntity> resultados = dao.read(filtro);

            // Reflexão para os cabeçalhos
            List<String> colunas = getAllFields(entidadeBase.getClass()).stream()
                    .filter(field -> !Collection.class.isAssignableFrom(field.getType()))
                    .map(Field::getName)
                    .toList();

            model.addAttribute("detalhe", resultados);
            model.addAttribute("colunas", colunas);
            model.addAttribute("entidadeNome", entityName);

            return "admin/detalhe";
        }

        return "error"; // Ou redirecionamento para lista
    }

    @GetMapping("MRYnZpAsC9sp/editar/{entity}")
    public String editar(@PathVariable("entity") String entityName, HttpServletRequest request, Model model) {
        // 1. Pega o ID do request (parâmetro da URL ou form)
        String idParam = request.getParameter("id");

        // 2. Tenta converter com segurança
        Long entityId = null;
        try {
            if (idParam != null && !idParam.isBlank()) {
                entityId = Long.valueOf(idParam.trim());
            }
        } catch (NumberFormatException e) {
            // ID inválido (ex: "abc")
        }

        if (entityName != null && !entityName.isBlank() && entityId != null) {
            IEntity entidadeBase = SimpleObjectFactory.create(entityName);

            // Preenche a entidade com os dados do request
            IEntity filtro = EntityMapper.fillEntity(entidadeBase, request);

            List<IEntity> resultados = dao.read(filtro);

            // Reflexão para os cabeçalhos
            List<String> colunas = getAllFields(entidadeBase.getClass()).stream()
                    .map(Field::getName)
                    .toList();

            // Mapeia chaves estrangeiras (ManyToOne) para preencher os Selects
            Map<String, List<IEntity>> foreignKeys = new HashMap<>();
            for (Field field : getAllFields(entidadeBase.getClass())) {
                if (IEntity.class.isAssignableFrom(field.getType())
                        && !Collection.class.isAssignableFrom(field.getType())) {
                    try {
                        IEntity fkInstance = (IEntity) field.getType().getDeclaredConstructor().newInstance();
                        foreignKeys.put(field.getName(), dao.read(fkInstance)); // Busca todos os registros
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            model.addAttribute("foreignKeys", foreignKeys);

            model.addAttribute("detalhe", resultados);
            model.addAttribute("colunas", colunas);
            model.addAttribute("entidadeNome", entityName);

            return "admin/edit";
        }

        return "error"; // Ou redirecionamento para lista
    }
}
