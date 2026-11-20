INSERT INTO materia (nome, sigla, id_escola)
SELECT basica.nome, basica.sigla, escola.id
FROM escola
CROSS JOIN (
    SELECT 'Língua Portuguesa' AS nome, 'PORT' AS sigla
    UNION ALL SELECT 'Matemática', 'MAT'
    UNION ALL SELECT 'Língua Inglesa', 'ING'
    UNION ALL SELECT 'Educação Física', 'EF'
    UNION ALL SELECT 'Arte', 'ARTE'
    UNION ALL SELECT 'História', 'HIST'
    UNION ALL SELECT 'Geografia', 'GEO'
    UNION ALL SELECT 'Filosofia', 'FIL'
    UNION ALL SELECT 'Sociologia', 'SOC'
    UNION ALL SELECT 'Física', 'FIS'
    UNION ALL SELECT 'Química', 'QUI'
    UNION ALL SELECT 'Biologia', 'BIO'
) basica
WHERE NOT EXISTS (
    SELECT 1
    FROM materia existente
    WHERE existente.id_escola = escola.id
      AND existente.nome = basica.nome
);
