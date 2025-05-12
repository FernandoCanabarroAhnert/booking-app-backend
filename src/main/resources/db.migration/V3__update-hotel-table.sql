ALTER TABLE hotels ADD COLUMN IF NOT EXISTS description TEXT;

UPDATE hotels SET description = 'Descrição indisponível' WHERE description IS NULL;