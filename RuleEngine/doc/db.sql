DROP TABLE RULE_SET_CONF;
DROP TABLE RULE_CONDITION_MAP;
DROP TABLE RULE_SET;
DROP TABLE RULE;
DROP TABLE "CONDITION";

CREATE TABLE "CONDITION" (
    CONDITION_ID INTEGER NOT NULL,
    DESCRIPTION VARCHAR(200) NOT NULL,
    "PARAMETER_NAME" VARCHAR(200),
    EXPRESSION VARCHAR(500),
    CONDITION_CLASS VARCHAR(200),
        PRIMARY KEY (CONDITION_ID)
);

CREATE TABLE RULE (
    RULE_ID INTEGER NOT NULL,
    "NAME" VARCHAR(200) NOT NULL,
    DESCRIPTION VARCHAR(500) NOT NULL,
    CONSEQUENCE VARCHAR(5000) NOT NULL,
    LOGICAL_OPERATOR VARCHAR(10) NOT NULL,
        PRIMARY KEY (RULE_ID)
);

CREATE TABLE RULE_CONDITION_MAP (
    RULE_ID INTEGER NOT NULL,
    CONDITION_ID INTEGER NOT NULL,
        PRIMARY KEY (RULE_ID, CONDITION_ID)
);

CREATE TABLE RULE_SET (
    RULE_SET_ID INTEGER NOT NULL,
    "NAME" VARCHAR(200) NOT NULL,
        PRIMARY KEY (RULE_SET_ID)
);

CREATE TABLE RULE_SET_CONF (
    RULE_SET_ID INTEGER NOT NULL,
    RULE_ID INTEGER NOT NULL,
    PRIORITY INTEGER NOT NULL,
        PRIMARY KEY (RULE_SET_ID, RULE_ID)
);


ALTER TABLE RULE_CONDITION_MAP ADD CONSTRAINT RULE_CONDITION_MAP_FK_1 FOREIGN KEY (CONDITION_ID) REFERENCES CONDITION (CONDITION_ID);
ALTER TABLE RULE_CONDITION_MAP ADD CONSTRAINT RULE_CONDITION_MAP_FK_2 FOREIGN KEY (RULE_ID) REFERENCES RULE (RULE_ID);

ALTER TABLE RULE_SET_CONF ADD CONSTRAINT RULE_SET_CONF_FK_1 FOREIGN KEY (RULE_SET_ID) REFERENCES RULE_SET (RULE_SET_ID);
ALTER TABLE RULE_SET_CONF ADD CONSTRAINT RULE_SET_CONF_FK_2 FOREIGN KEY (RULE_ID) REFERENCES RULE (RULE_ID);

INSERT INTO RULE_SET (RULE_SET_ID, "NAME") VALUES (1, 'GO MVNO Decision');

INSERT INTO RULE (RULE_ID, "NAME", DESCRIPTION, CONSEQUENCE, LOGICAL_OPERATOR) VALUES (1, 'IMSI and MSISDN check', 'Either IMSI or MSISDN have to be specified', 'SET-UNSUCCESSFUL|ADD-MESSAGE Either IMSI or MSISDN have to be specified', 'AND');
INSERT INTO RULE (RULE_ID, "NAME", DESCRIPTION, CONSEQUENCE, LOGICAL_OPERATOR) VALUES (2, 'IMSI Finder', 'Find IMSI from the MSISDN parameter', 'RUN mt.com.go.rule.engine.consequence.IMSIFinder|SET-PARAM notes=Imsi found from msisdn|RESUBMIT 5', 'AND');
INSERT INTO RULE (RULE_ID, "NAME", DESCRIPTION, CONSEQUENCE, LOGICAL_OPERATOR) VALUES (3, 'GO IMSI', 'Mark the request as GO', 'RUN mt.com.go.rule.engine.consequence.GoMobileIMSI', 'AND');
INSERT INTO RULE (RULE_ID, "NAME", DESCRIPTION, CONSEQUENCE, LOGICAL_OPERATOR) VALUES (4, 'MVNO 1 IMSI', 'Mark the request as MVNO 1', 'RUN mt.com.go.rule.engine.consequence.Mvno1IMSI', 'AND');
INSERT INTO RULE (RULE_ID, "NAME", DESCRIPTION, CONSEQUENCE, LOGICAL_OPERATOR) VALUES (5, 'MVNO 2 IMSI', 'Mark the request as MVNO 2', 'RUN mt.com.go.rule.engine.consequence.Mvno2IMSI', 'AND');
INSERT INTO RULE (RULE_ID, "NAME", DESCRIPTION, CONSEQUENCE, LOGICAL_OPERATOR) VALUES (6, 'MNVO 3 N/A', 'Warn that MVNO 3 is not yet available', 'RUN mt.com.go.rule.engine.consequence.Mvno3IMSI', 'OR');
INSERT INTO RULE (RULE_ID, "NAME", DESCRIPTION, CONSEQUENCE, LOGICAL_OPERATOR) VALUES (7, 'Operator not found', 'The operator code was not found', 'SET-UNSUCCESSFUL|ADD-MESSAGE The operator code was not derived', 'AND');

INSERT INTO RULE_SET_CONF (RULE_SET_ID, RULE_ID, PRIORITY) VALUES (1,1,1);
INSERT INTO RULE_SET_CONF (RULE_SET_ID, RULE_ID, PRIORITY) VALUES (1,2,2);
INSERT INTO RULE_SET_CONF (RULE_SET_ID, RULE_ID, PRIORITY) VALUES (1,3,3);
INSERT INTO RULE_SET_CONF (RULE_SET_ID, RULE_ID, PRIORITY) VALUES (1,4,4);
INSERT INTO RULE_SET_CONF (RULE_SET_ID, RULE_ID, PRIORITY) VALUES (1,5,5);
INSERT INTO RULE_SET_CONF (RULE_SET_ID, RULE_ID, PRIORITY) VALUES (1,6,6);
INSERT INTO RULE_SET_CONF (RULE_SET_ID, RULE_ID, PRIORITY) VALUES (1,7,7);


INSERT INTO "CONDITION" (CONDITION_ID, DESCRIPTION, "PARAMETER_NAME", EXPRESSION, CONDITION_CLASS) VALUES (1, 'IMSI not in Parameter list', 'imsi', '!exists', NULL);
INSERT INTO "CONDITION" (CONDITION_ID, DESCRIPTION, "PARAMETER_NAME", EXPRESSION, CONDITION_CLASS) VALUES (2, 'MSISDN not in Parameter list', 'msisdn', '!exists', NULL);
INSERT INTO "CONDITION" (CONDITION_ID, DESCRIPTION, "PARAMETER_NAME", EXPRESSION, CONDITION_CLASS) VALUES (3, 'GO Mobile IMSI Condition', 'imsi', '500A\d{5}', NULL);
INSERT INTO "CONDITION" (CONDITION_ID, DESCRIPTION, "PARAMETER_NAME", EXPRESSION, CONDITION_CLASS) VALUES (4, 'MVNO 1 IMSI Condition', 'imsi', '500B\d{5}', NULL);
INSERT INTO "CONDITION" (CONDITION_ID, DESCRIPTION, "PARAMETER_NAME", EXPRESSION, CONDITION_CLASS) VALUES (5, 'MVNO 2 IMSI Condition', 'imsi', '500C\d{5}', NULL);
INSERT INTO "CONDITION" (CONDITION_ID, DESCRIPTION, "PARAMETER_NAME", EXPRESSION, CONDITION_CLASS) VALUES (6, 'MVNO 3 IMSI Condition', NULL, NULL, 'mt.com.go.rule.engine.condition.Mvno3NotAvailableCondition');
INSERT INTO "CONDITION" (CONDITION_ID, DESCRIPTION, "PARAMETER_NAME", EXPRESSION, CONDITION_CLASS) VALUES (7, 'True', NULL, NULL, 'mt.com.go.rule.engine.condition.TrueCondition');

INSERT INTO RULE_CONDITION_MAP (RULE_ID, CONDITION_ID) VALUES (1, 1);
INSERT INTO RULE_CONDITION_MAP (RULE_ID, CONDITION_ID) VALUES (1, 2);
INSERT INTO RULE_CONDITION_MAP (RULE_ID, CONDITION_ID) VALUES (2, 1);
INSERT INTO RULE_CONDITION_MAP (RULE_ID, CONDITION_ID) VALUES (3, 3);
INSERT INTO RULE_CONDITION_MAP (RULE_ID, CONDITION_ID) VALUES (4, 4);
INSERT INTO RULE_CONDITION_MAP (RULE_ID, CONDITION_ID) VALUES (5, 5);
INSERT INTO RULE_CONDITION_MAP (RULE_ID, CONDITION_ID) VALUES (6, 6);
INSERT INTO RULE_CONDITION_MAP (RULE_ID, CONDITION_ID) VALUES (7, 7);

DROP VIEW RULE_ENGINE_CONFIG_V;

CREATE VIEW RULE_ENGINE_CONFIG_V AS
SELECT
    RULE_SET.RULE_SET_ID RULE_SET_ID,
    RULE_SET."NAME" RULE_SET_NAME,
    RULE.RULE_ID RULE_ID,
    RULE_SET_CONF.PRIORITY RULE_SET_PRIORITY,
    RULE."NAME" RULE_NAME,
    RULE.DESCRIPTION RULE_DESCRIPTION,
    RULE.CONSEQUENCE RULE_CONSEQUENCE,
    RULE.LOGICAL_OPERATOR RULE_OPERATOR,
    CONDITION.CONDITION_ID CONDITION_ID,
    CONDITION.DESCRIPTION CONDITION_DESCRIPTION,
    CONDITION."PARAMETER_NAME" CONDITION_PARAMETER_NAME,
    CONDITION.EXPRESSION CONDITION_EXPRESSION,
    CONDITION.CONDITION_CLASS CONDITION_CLASS
FROM
    "CONDITION",
    RULE,
    RULE_CONDITION_MAP,
    RULE_SET,
    RULE_SET_CONF
WHERE
    RULE_CONDITION_MAP.RULE_ID = RULE.RULE_ID
    AND RULE_CONDITION_MAP.CONDITION_ID = "CONDITION".CONDITION_ID
    AND RULE_SET_CONF.RULE_ID = RULE.RULE_ID
    AND RULE_SET.RULE_SET_ID = RULE_SET_CONF.RULE_SET_ID