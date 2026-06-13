package com.gwj.service;

import com.gwj.model.domain.IEntity;
import com.gwj.model.domain.factory.SimpleObjectFactory;
import java.util.HashMap;
import java.util.Map;

public class ServiceRegistry {
    private static final Map<String, IService<? extends IEntity>> registry = new HashMap<>();

    static {
        // Registrar explicitamente serviços especializados
        registry.put("Usuario", new UsuarioService());
        registry.put("Agenda", new AgendaService());
        registry.put("Agendamento", new AgendamentoService());
    }

    @SuppressWarnings("unchecked")
    public static <T extends IEntity> IService<T> getService(String entityName) {
        IService<? extends IEntity> service = registry.get(entityName);
        if (service == null) {
            // Fallback dinâmico para entidade desconhecida
            IEntity entity = SimpleObjectFactory.create(entityName);
            service = new GenericService<>(entity.getClass());
        }
        return (IService<T>) service;
    }
}
