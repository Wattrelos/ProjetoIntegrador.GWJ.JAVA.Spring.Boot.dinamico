INSERT INTO `tab_agendamento` (
        cliente_nome,
        cliente_telefone,
        profissional_id,
        servico_id,
        data_agendamento,
        hora_inicio,
        hora_fim,
        status
    )
VALUES -- AGENDA DO CARLOS (ID 2)
    -- Cliente 1: Corte de Cabelo Masculino (30 min) -> Ocupa apenas o slot das 09:00
    (
        'Marcos Silva',
        '(11) 99999-1111',
        2,
        1,
        '2026-06-15',
        '09:00:00',
        ADDTIME('09:00:00', SEC_TO_TIME(30 * 60)),
        'Confirmado'
    ),
    -- Cliente 2: Combo Cabelo + Barba (110 min) -> Ocupa das 10:00 até às 11:50 (Bloqueia 10:00, 10:30, 11:00, 11:30)
    (
        'Arthur Rezende',
        '(11) 99999-2222',
        2,
        7,
        '2026-06-15',
        '10:00:00',
        ADDTIME('10:00:00', SEC_TO_TIME(110 * 60)),
        'Confirmado'
    ),
    -- Cliente 3: Barba Completa (20 min) -> Ocupa o slot das 14:00 (Libera às 14:20, próximo slot de 14:30 está livre)
    (
        'Bruno Oliveira',
        '(11) 99999-3333',
        2,
        2,
        '2026-06-15',
        '14:00:00',
        ADDTIME('14:00:00', SEC_TO_TIME(20 * 60)),
        'Confirmado'
    ),
    -- Cliente 4: Corte Moicano (90 min) -> Ocupa das 15:30 até às 17:00 (Bloqueia 15:30, 16:00, 16:30)
    (
        'Rodrigo Costa',
        '(11) 99999-4444',
        2,
        3,
        '2026-06-15',
        '15:30:00',
        ADDTIME('15:30:00', SEC_TO_TIME(90 * 60)),
        'Confirmado'
    );
INSERT INTO `tab_agendamentos` (
        cliente_nome,
        cliente_telefone,
        profissional_id,
        servico_id,
        data_agendamento,
        hora_inicio,
        hora_fim,
        status
    )
VALUES -- AGENDA DO TIAGO (ID 9)
    -- Cliente 5: Trança Rastafari (200 min) -> Ocupa das 09:00 até às 12:20 (Bloqueia a manhã inteira do Tiago: 09:00 às 11:30)
    (
        'Felipe Amorim',
        '(11) 99999-5555',
        9,
        4,
        '2026-06-15',
        '09:00:00',
        ADDTIME('09:00:00', SEC_TO_TIME(200 * 60)),
        'Confirmado'
    ),
    -- Cliente 6: Corte Infantil (40 min) -> Ocupa das 14:00 até às 14:40 (Bloqueia os slots de 14:00 e 14:30)
    (
        'Lucas (Pai: Roberto)',
        '(11) 99999-6666',
        9,
        8,
        '2026-06-15',
        '14:00:00',
        ADDTIME('14:00:00', SEC_TO_TIME(40 * 60)),
        'Confirmado'
    ),
    -- Cliente 7: Corte de Cabelo Masculino (30 min) -> Ocupa o slot das 15:00
    (
        'Gustavo Henrique',
        '(11) 99999-7777',
        9,
        1,
        '2026-06-15',
        '15:00:00',
        ADDTIME('15:00:00', SEC_TO_TIME(30 * 60)),
        'Confirmado'
    );