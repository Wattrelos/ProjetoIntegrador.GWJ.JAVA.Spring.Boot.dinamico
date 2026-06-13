CREATE TABLE tab_grade_horarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    dia_funcionamento_id INT,
    horario_inicio TIME NOT NULL,
    horario_fim TIME NOT NULL,
    FOREIGN KEY (dia_funcionamento_id) REFERENCES tab_dias_funcionamento(id) ON DELETE CASCADE
);