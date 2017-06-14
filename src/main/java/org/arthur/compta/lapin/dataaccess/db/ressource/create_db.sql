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

#**************************************************************
# Table: BUDGET
#***************************************************************
CREATE TABLE BUDGET(
	ID              IDENTITY NOT NULL,
	nom             VARCHAR (25) NOT NULL,
	objectif        FLOAT  NOT NULL,
	utilise        	FLOAT  NOT NULL,
	is_actif       	BOOLEAN  NOT NULL,
	priority		INTEGER NOT NULL,
	CONSTRAINT BUDGET_Pk PRIMARY KEY (ID)
);

#***************************************************************
#  Table: EXERCICE_MENSUEL
#***************************************************************
CREATE TABLE EXERCICE_MENSUEL(
	ID            IDENTITY NOT NULL,
	resultat_moyen_prevu	FLOAT NOT NULL,
	date_debut    DATE  NOT NULL,
	date_fin      DATE  NOT NULL,
);

#***************************************************************
#  Table: TRIMESTRE
#***************************************************************
CREATE TABLE TRIMESTRE(
	ID  				IDENTITY NOT NULL ,
	premier_mois_id  	INTEGER   ,
	deux_mois_id  		INTEGER   ,
	trois_mois_id  		INTEGER   ,
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


#***************************************************************
#   Table: TEMPLATE
#***************************************************************
CREATE TABLE TEMPLATE(
	ID  				IDENTITY NOT NULL ,
	nom					VARCHAR (25) NOT NULL,
	montant     		FLOAT  NOT NULL,
	type_ope  			VARCHAR (25) NOT NULL   ,
	frequence  			VARCHAR (25) NOT NULL   ,
	occurence  			INTEGER   ,
	compte_source_id	INTEGER,
	compte_cible_id		INTEGER,
	CONSTRAINT TEMPLATE_Pk PRIMARY KEY (ID),
	CONSTRAINT compte_source_id_fk FOREIGN KEY (compte_source_id) REFERENCES COMPTE(ID),
	CONSTRAINT compte_cible_id_fk FOREIGN KEY (compte_cible_id) REFERENCES COMPTE(ID)
);

#***************************************************************
#   Table: OPERATION
#***************************************************************
CREATE TABLE OPERATION(
	ID  				IDENTITY NOT NULL ,
	nom					VARCHAR (25) NOT NULL,
	montant     		FLOAT  NOT NULL,
	type_ope 			VARCHAR (25) NOT NULL,
	etat  				VARCHAR (25) NOT NULL,
	compte_source_id	INTEGER, 
	compte_cible_id		INTEGER,
	mois_id				INTEGER,
	CONSTRAINT TEMPLATE_OP_Pk PRIMARY KEY (ID),
	CONSTRAINT mois_id_fk FOREIGN KEY (mois_id) REFERENCES EXERCICE_MENSUEL(ID),
	CONSTRAINT op_compte_source_id_fk FOREIGN KEY (compte_source_id) REFERENCES COMPTE(ID),
	CONSTRAINT op_compte_cible_id_fk FOREIGN KEY (compte_cible_id) REFERENCES COMPTE(ID)
);





