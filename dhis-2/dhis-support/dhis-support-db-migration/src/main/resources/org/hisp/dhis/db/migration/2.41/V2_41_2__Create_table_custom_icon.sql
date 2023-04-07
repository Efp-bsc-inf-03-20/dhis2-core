CREATE TABLE IF NOT EXISTS customicon (
    customiconid int8 NOT NULL,
    "key" VARCHAR(100) NOT NULL,
    fileresourceid int8 NOT NULL,
    description text,
    keywords text,
    userid int8,
    created TIMESTAMP,
    lastupdated TIMESTAMP,
    createdby bigint,
    lastupdatedby bigint,
    CONSTRAINT customicon_pkey PRIMARY KEY (customiconid),
    CONSTRAINT customicon_ukey UNIQUE ("key"),
    CONSTRAINT customicon_fileresource_ukey UNIQUE (fileresourceid)
);

CREATE TABLE IF NOT EXISTS keywords (
    customiconid int8 NOT NULL,
    keyword VARCHAR(255) NULL,
    CONSTRAINT keyword_pkey PRIMARY KEY (customiconid,keyword)
);

ALTER TABLE customicon ADD CONSTRAINT fk_customicon_userid FOREIGN KEY (userid) REFERENCES userinfo(userinfoid);
ALTER TABLE customicon ADD CONSTRAINT fk_customicon_file_resource FOREIGN KEY (fileresourceid) REFERENCES fileresource(fileresourceid) ON DELETE CASCADE;
ALTER TABLE customicon ADD CONSTRAINT fk_createdby_userid FOREIGN KEY (createdby) REFERENCES userinfo(userinfoid);
ALTER TABLE customicon ADD CONSTRAINT fk_lastupdatedby_userid FOREIGN KEY (lastupdatedby) REFERENCES userinfo(userinfoid);
ALTER TABLE keywords ADD CONSTRAINT fk_keyword_customiconid FOREIGN KEY (customiconid) REFERENCES customicon(customiconid) ON DELETE CASCADE;