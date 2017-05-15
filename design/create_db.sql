#------------------------------------------------------------
#        Script MySQL.
#------------------------------------------------------------


#------------------------------------------------------------
# Table: COMPTE
#------------------------------------------------------------

CREATE TABLE COMPTE(
        ID             int (11) Auto_increment  NOT NULL ,
        nom            Varchar (25) NOT NULL ,
        solde          Float NOT NULL ,
        is_livret      Bool NOT NULL ,
        budget_allowed Bool NOT NULL ,
        PRIMARY KEY (ID )
);

