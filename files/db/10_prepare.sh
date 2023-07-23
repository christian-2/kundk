#!/bin/bash
set -e
trap 'echo "error: $0:$LINENO"' ERR

echo "POSTGRES_KEYCLOAK_PASSWORD=$(echo $POSTGRES_KEYCLOAK_PASSWORD | \
  sed s/./\*/g)"
echo "POSTGRES_PASSWORD=$(echo $POSTGRES_PASSWORD | sed s/./\*/g)"

[[ -n "$POSTGRES_KEYCLOAK_PASSWORD" ]]
[[ -n "$POSTGRES_PASSWORD" ]]

# create USER, DATABASE

psql -U postgres << EOF
CREATE USER keycloak WITH PASSWORD '$POSTGRES_KEYCLOAK_PASSWORD';
CREATE DATABASE keycloak OWNER keycloak;
EOF

# update state

. /usr/loca/bin/state

set_state DATABASE_READY /mnt/state/state.db

psql -U postgres keycloak << EOF
CREATE TABLE ready ();
EOF
