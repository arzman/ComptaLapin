#---------------------------------------------------------------
#--        Script HSQLDB.  
#---------------------------------------------------------------


#***************************************************************
#  Table: COMPTE
#***************************************************************
CREATE TABLE COMPTE(
	ID              IDENTITY NOT NULL,
	nom             VARCHAR (25) NOT NULL,
	solde           FLOAT  NOT NULL,
	is_livret       BOOLEAN  NOT NULL,
	budget_allowed  BOOLEAN  NOT NULL ,
	CONSTRAINT COMPTE_Pk PRIMARY KEY (ID)
);

