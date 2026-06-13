SELECT *
FROM tab_agendamento
WHERE data_agendamento = '2026-06-15'
    AND profissional_id = 2
    AND status = 'Confirmado'
ORDER BY hora_inicio;