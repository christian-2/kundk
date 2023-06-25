# oidc-passkey

There are lots of [FIDO2](https://fidoalliance.org/fido2)/[WebAuthn](https://www.w3.org/TR/webauthn-2) demos out there.
So what aspects are different with this one:

* allows FIDO passkey as 1st (for 1FA) or 2nd (for 2FA) factor
  during authentication
* employs LDAP during registration and as 1st (for 2FA) factor
  during authentication
* fully implemented with Keycloak
* allows further integration via OIDC (and SAML)
* implementation resides in six OCI containers
  (Keycloak, PostgreSQL, acme.sh, multiple Apaches for demos)
* under Apache License
  (like [Keycloak](https://github.com/keycloak/keycloak/blob/main/LICENSE.txt))

## Installation

```
./bin/build
cp docs/env.sample .env
editor .env
curl -o blob.jwt -L https://mds3.fidoalliance.org
./bin/reset
sudo ./bin/start
```

| `ENV` | example |
| --- | --- |
| `ACME_EMAIL` | (email address) |
| `APACHE_HOSTNAME` | (FQDN) |
| [`APACHE_LOG_LEVEL`](https://httpd.apache.org/docs/2.4/en/mod/core.html#loglevel) | `info` |
| `KEYCLOAK_ADMIN_PASSWORD` | (password) |
| `KEYCLOAK_HOSTNAME` | (FQDN) |
| [`KEYCLOAK_LOG_LEVEL`](https://www.keycloak.org/server/all-config?q=log-level) | `info` |
| [`KEYCLOAK_OIDC_REMOTE_USER_CLAIM`](https://github.com/OpenIDC/mod_auth_openidc/blob/master/auth_openidc.conf) | `given_name ^(.+?)(?:\s.+)?$ $1` |
| [`KEYCLOAK_OIDC_SCOPE`](https://github.com/OpenIDC/mod_auth_openidc/blob/master/auth_openidc.conf) | `openid profile`
| `POSTGRES_KEYCLOAK_PASSWORD` | (password) |
| `POSTGRES_PASSWORD` | (password) |

## Current Limitations

* [Online LDAP Test Server](https://www.forumsys.com/2022/05/10/online-ldap-test-server/) `ldap.forumsys.com ` currently hardcoded
* certificate renewal via ACME not yet fully implemented
* signature on `blob.jwt` not yet checked
