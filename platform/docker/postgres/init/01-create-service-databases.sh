#!/usr/bin/env bash

set -Eeuo pipefail

echo "Initializing KFG service databases..."

required_variables=(
  KFG_CUSTOMER_DB_PASSWORD
  KFG_KYC_DB_PASSWORD
  KFG_ACCOUNT_DB_PASSWORD
  KFG_LEDGER_DB_PASSWORD
  KFG_BENEFICIARY_DB_PASSWORD
  KFG_PAYMENT_DB_PASSWORD
  KFG_NOTIFICATION_DB_PASSWORD
  KFG_BACKOFFICE_DB_PASSWORD
  KFG_AUDIT_DB_PASSWORD
  KFG_STATEMENT_DB_PASSWORD
)

for variable in "${required_variables[@]}"; do
  if [[ -z "${!variable:-}" ]]; then
    echo "Required environment variable '$variable' is not set."
    exit 1
  fi
done


create_service_database() {
  local database_name="$1"
  local database_user="$2"
  local database_password="$3"

  echo "Creating database '${database_name}' for '${database_user}'..."

  psql \
    --username "$POSTGRES_USER" \
    --dbname "$POSTGRES_DB" \
    --set=ON_ERROR_STOP=1 \
    --set=db_name="$database_name" \
    --set=db_user="$database_user" \
    --set=db_password="$database_password" <<'EOSQL'

SELECT format(
    'CREATE ROLE %I LOGIN PASSWORD %L',
    :'db_user',
    :'db_password'
) \gexec

SELECT format(
    'CREATE DATABASE %I OWNER %I',
    :'db_name',
    :'db_user'
) \gexec

SELECT format(
    'REVOKE CONNECT ON DATABASE %I FROM PUBLIC',
    :'db_name'
) \gexec

SELECT format(
    'GRANT CONNECT ON DATABASE %I TO %I',
    :'db_name',
    :'db_user'
) \gexec

EOSQL
}


# Prevent application users from using the administrative database.
psql \
  --username "$POSTGRES_USER" \
  --dbname "$POSTGRES_DB" \
  --set=ON_ERROR_STOP=1 \
  --set=admin_db="$POSTGRES_DB" <<'EOSQL'

SELECT format(
    'REVOKE CONNECT ON DATABASE %I FROM PUBLIC',
    :'admin_db'
) \gexec

EOSQL


create_service_database \
  "customer_db" \
  "kfg_customer" \
  "$KFG_CUSTOMER_DB_PASSWORD"

create_service_database \
  "kyc_db" \
  "kfg_kyc" \
  "$KFG_KYC_DB_PASSWORD"

create_service_database \
  "account_db" \
  "kfg_account" \
  "$KFG_ACCOUNT_DB_PASSWORD"

create_service_database \
  "ledger_db" \
  "kfg_ledger" \
  "$KFG_LEDGER_DB_PASSWORD"

create_service_database \
  "beneficiary_db" \
  "kfg_beneficiary" \
  "$KFG_BENEFICIARY_DB_PASSWORD"

create_service_database \
  "payment_db" \
  "kfg_payment" \
  "$KFG_PAYMENT_DB_PASSWORD"

create_service_database \
  "notification_db" \
  "kfg_notification" \
  "$KFG_NOTIFICATION_DB_PASSWORD"

create_service_database \
  "backoffice_db" \
  "kfg_backoffice" \
  "$KFG_BACKOFFICE_DB_PASSWORD"

create_service_database \
  "audit_db" \
  "kfg_audit" \
  "$KFG_AUDIT_DB_PASSWORD"

create_service_database \
  "statement_db" \
  "kfg_statement" \
  "$KFG_STATEMENT_DB_PASSWORD"

echo "KFG PostgreSQL initialization completed successfully."
