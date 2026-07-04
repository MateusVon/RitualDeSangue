-- Execute este script no banco `ritual_de_sangue` antes de rodar o jogo
-- com o novo sistema de Sangue e Pontuação.
--
-- Adiciona a coluna de pontuação persistida do jogador. O sangue (custo
-- de cartas) já existe na tabela `carta` (coluna custo_sangue) e o sangue
-- "em jogo" de cada partida é controlado apenas em memória (sempre
-- reiniciado em 5 a cada nova partida), então não precisa de coluna.

ALTER TABLE jogador
    ADD COLUMN pontuacao INT NOT NULL DEFAULT 0;
