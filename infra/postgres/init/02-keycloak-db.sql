CREATE DATABASE keycloak_db;
CREATE USER keycloak_svc WITH PASSWORD 'keycloak_pw' LOGIN;
ALTER DATABASE keycloak_db OWNER TO keycloak_svc;