# ---------------------------------------------------------------
# --        Script HSQLDB.  
#---------------------------------------------------------------


#**************************************************************
# Table: COMPTE
#***************************************************************
CREATE TABLE COMPTE(
	ID              IDENTITY NOT NULL,
	nom             VARCHAR (25) NOT NULL,
	solde           FLOAT  NOT NULL,
	is_livret       BOOLEAN  NOT NULL,
	budget_allowed  BOOLEAN  NOT NULL ,
	CONSTRAINT COMPTE_Pk PRIMARY KEY (ID)
);

#***************************************************************
#  Table: EXERCICE_MENSUEL
#***************************************************************
CREATE TABLE EXERCICE_MENSUEL(
	ID            IDENTITY NOT NULL,
	date_debut    DATE  NOT NULL,
	date_fin      DATE  NOT NULL,
);

#***************************************************************
#  Table: TRIMESTRE
#***************************************************************
CREATE TABLE TRIMESTRE(
	ID  IDENTITY NOT NULL ,
	premier_mois_id  INTEGER   ,
	deux_mois_id  INTEGER   ,
	trois_mois_id  INTEGER   ,
	CONSTRAINT TRIMESTRE_Pk PRIMARY KEY (ID),
	CONSTRAINT premier_mois_id_fk FOREIGN KEY (premier_mois_id) REFERENCES EXERCICE_MENSUEL(ID),
	CONSTRAINT deux_mois_id_fk FOREIGN KEY (deux_mois_id) REFERENCES EXERCICE_MENSUEL(ID),
	CONSTRAINT trois_mois_id_fk FOREIGN KEY (trois_mois_id) REFERENCES EXERCICE_MENSUEL(ID)
);

#***************************************************************
#   Table: CONFIGURATION
#***************************************************************
CREATE TABLE CONFIGURATION(
	ID            IDENTITY NOT NULL,
	date_verif    DATE  ,
	ID_TRIMESTRE  INTEGER   ,
	CONSTRAINT CONFIGURATION_Pk PRIMARY KEY (ID)
);


