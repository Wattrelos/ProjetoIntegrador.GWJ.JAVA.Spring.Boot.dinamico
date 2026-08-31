package com.gwj.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gwj.model.domain.entities.Setting;
import com.gwj.service.transaction.UnitOfWork;

public class SettingService extends GenericService<Setting> {

    public SettingService() {
        super(Setting.class);
    }

    /**
     * Retorna todas as configurações como um mapa chave-valor.
     */
    public Map<String, String> getAllAsMap() {
        Map<String, String> map = new HashMap<>();
        try {
            List<Setting> list = this.read(new Setting());
            for (Setting s : list) {
                if (s.getChave() != null) {
                    map.put(s.getChave(), s.getValor() != null ? s.getValor() : "");
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar configurações como mapa: " + e.getMessage());
        }
        return map;
    }

    /**
     * Atualiza ou insere em lote as configurações da loja em uma única transação.
     */
    public void updateSettings(Map<String, String> newSettings) {
        if (newSettings == null || newSettings.isEmpty()) {
            return;
        }

        try (UnitOfWork uow = new UnitOfWork()) {
            UnitOfWork.beginTransaction();
            Connection conn = UnitOfWork.getConnection();

            String sql = "INSERT INTO `tab_setting` (`chave`, `valor`) VALUES (?, ?) " +
                         "ON DUPLICATE KEY UPDATE `valor` = VALUES(`valor`);";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                for (Map.Entry<String, String> entry : newSettings.entrySet()) {
                    String chave = entry.getKey();
                    if (chave == null || chave.startsWith("_")) {
                        continue;
                    }

                    stmt.setString(1, chave);
                    stmt.setString(2, entry.getValue() != null ? entry.getValue() : "");
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }

            UnitOfWork.commit();
        } catch (Exception e) {
            UnitOfWork.rollback();
            throw new RuntimeException("Erro ao atualizar configurações da loja: " + e.getMessage(), e);
        }
    }
}
