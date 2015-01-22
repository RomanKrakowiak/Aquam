CREATE SEQUENCE seq_animal;

CREATE TABLE Animal (
	animal_id INTEGER NOT NULL DEFAULT nextval ('seq_animal') PRIMARY KEY,
	nom VARCHAR(32) NOT NULL,
	prenom VARCHAR(32),
	race VARCHAR(32),
	naissance DATE,
	traitement VARCHAR(256)
);

CREATE SEQUENCE seq_experience;

CREATE TABLE Experience (
	experience_id INTEGER NOT NULL DEFAULT nextval ('seq_experience') PRIMARY KEY,
	date_experience DATE NOT NULL,
	nom VARCHAR(128),
	description VARCHAR(512)
);

