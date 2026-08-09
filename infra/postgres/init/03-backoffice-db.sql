CREATE DATABASE backoffice_db;
CREATE USER backoffice_svc WITH PASSWORD 'backoffice_pw' LOGIN;
ALTER DATABASE backoffice_db OWNER TO backoffice_svc;