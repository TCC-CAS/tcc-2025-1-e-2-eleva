-- Garante no banco existente que cada cliente tenha no maximo
-- uma avaliacao por estacionamento.

WITH AvaliacoesDuplicadas AS (
    SELECT
        id,
        ROW_NUMBER() OVER (
            PARTITION BY estacionamentoId, clienteId
            ORDER BY dataAvaliacao DESC, id DESC
        ) AS rn
    FROM dbo.Avaliacao
)
DELETE FROM AvaliacoesDuplicadas
WHERE rn > 1;
GO

IF NOT EXISTS (
    SELECT 1
    FROM sys.key_constraints
    WHERE name = 'UQ_Avaliacao_Estacionamento_Cliente'
      AND parent_object_id = OBJECT_ID('dbo.Avaliacao')
)
BEGIN
    ALTER TABLE dbo.Avaliacao
    ADD CONSTRAINT UQ_Avaliacao_Estacionamento_Cliente
        UNIQUE (estacionamentoId, clienteId);
END
GO
