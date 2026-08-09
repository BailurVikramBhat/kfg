CREATE DATABASE onboarding_db;
CREATE USER onboarding_svc WITH PASSWORD 'onboarding_pw' LOGIN;
ALTER DATABASE onboarding_db OWNER TO onboarding_svc;