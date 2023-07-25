# oidc-passkey

There are lots of [FIDO2](https://fidoalliance.org/fido2)/[WebAuthn](https://www.w3.org/TR/webauthn-2) demos out there.
So what aspects are different with this one:

* allows FIDO passkey as 1st (for 1FA) or 2nd (for 2FA) factor
  during authentication
* employs LDAP during registration and as 1st (for 2FA) factor
  during authentication
* fully implemented with [Keycloak](https://github.com/keycloak/keycloak/blob/main/LICENSE.txt)
* allows further integration via OIDC (and SAML)
* implementation resides in eight OCI containers
  (Keycloak, PostgreSQL, acme.sh, five Apaches for demos)
* under Apache License (like Keycloak)

## Requirements

* e.g. Debian 12.1
* e.g. Podman 4.3.1

## Installation

```
./bin/build --no-cache
mkdir k8s
cp docs/{base,demo}.yaml k8s
editor k8s/{base,demo}.yaml # 1.
echo '{"password":"HIDDEN"}' | \
  podman secret create --driver file keycloak-admin-password - # 2.
echo '{"password":"HIDDEN"}' | \
  podman secret create --driver file postgres-keycloak-password - # 2.
echo '{"password":"HIDDEN"}' | \
  podman secret create --driver file postgres-password - # 2.
./bin/reset
./bin/start
```

1. see sections Configuration for base, Configuration for demo
2. e.g. Podman 4.3.1 requires workaround for issue [#16269](https://github.com/containers/podman/issues/16269);
   see section Secrets for base

### Configuration for base

| env | | example |
| --- | --- | --- |
| `ACME_EMAIL` | | (email address) |
| `APACHE_HOSTNAME` | 1. | (FQDN) |
| `KEYCLOAK_HOSTNAME` | 1. | (FQDN) |
| [`KEYCLOAK_LOG_LEVEL`](https://www.keycloak.org/server/all-config?q=log-level) | | `info` |
| `LDAP_URL` | 2. | `ldap://ldap.forumsys.com:389` |

1. optional; default: `$(hostname -f)`
2. optional

### Secrets for base

| secret |
| --- |
| `keycloak-admin-password` |
| `postgres-keycloak-password` |
| `postgres-password` |

### Configuration for demo

| env | | example |
| --- | --- | --- |
| `APACHE_EMAIL` | | (email address) |
| `APACHE_HOSTNAME` | | 1. |
| [`APACHE_LOG_LEVEL`](https://httpd.apache.org/docs/2.4/en/mod/core.html#loglevel) | | `info` |
| `KEYCLOAK_HOSTNAME` | | 1. |
| [`KEYCLOAK_OIDC_REMOTE_USER_CLAIM`](https://github.com/OpenIDC/mod_auth_openidc/blob/master/auth_openidc.conf) | | `given_name ^(.+?)(?:\s.+)?$ $1` |
| [`KEYCLOAK_OIDC_SCOPE`](https://github.com/OpenIDC/mod_auth_openidc/blob/master/auth_openidc.conf) | | `openid profile`

1. optional; default: `$(hostname -f)`

## Demos

All demos are accessible from a common landing page `https://$APACHE_HOSTNAME`;
they allow removal of registrations and addition of authenticators in
specific account consoles;
and they allow read-only inspection of Keycloak configurations in specific realm admin consoles.

| demo | authentication | registration | IdP | SP |
| --- | --- | --- | --- | --- |
| #1 | FIDO passkey (1FA) | LDAP | Keycloak (OIDC) | Apache ([`mod_auth_openidc`](https://github.com/OpenIDC/mod_auth_openidc)) |
| #2 | LDAP + FIDO passkey (2FA) | LDAP | Keycloak (OIDC) | Apache ([`mod_auth_openidc`](https://github.com/OpenIDC/mod_auth_openidc)) |
| #3 | FIDO passkey (1FA)  | LDAP | Keycloak (SAML 2.0) | Apache ([`mod_shib`](https://shibboleth.atlassian.net/wiki/spaces/SP3/pages/2065335062/Apache)) |
| #4 | LDAP + FIDO passkey (2FA) | LDAP | Keycloak (SAML 2.0) | Apache ([`mod_shib`](https://shibboleth.atlassian.net/wiki/spaces/SP3/pages/2065335062/Apache)) |

## Current Limitations

* [Online LDAP Test Server](https://www.forumsys.com/2022/05/10/online-ldap-test-server/) `ldap.forumsys.com ` currently hardcoded
* Keycloak administration and account consoles not yet protected by 2FA
* no YubiKey PINs enforced via WebAuthn policies in any of the demos
* certificate renewal via ACME not yet fully implemented
* signature on `blob.jwt` not yet checked and `blow.jwt` not refreshed
