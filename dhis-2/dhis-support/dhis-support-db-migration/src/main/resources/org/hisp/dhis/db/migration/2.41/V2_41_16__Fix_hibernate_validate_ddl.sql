ALTER TABLE programstageworkinglist alter column description type text;
ALTER TABLE userrolerestrictions alter column userroleid type int8;
DROP CONSTRAINT IF EXISTS fk_userrolerestrictions_userroleid;
ALTER TABLE userrolerestrictions
    ADD CONSTRAINT fk_userrolerestrictions_userroleid FOREIGN KEY (userroleid) REFERENCES userrole (userroleid);