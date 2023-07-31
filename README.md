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

## Installation

* tested with Debian 12.1, Podman 4.3.1

```
sudo apt-get install xinetd
sudo sh -c "cat docs/xinetd.conf >> /etc/xinetd.d/services" # 1.
sudo systemctl reload xinetd.service
./bin/build-images --no-cache
cp docs/config-{base,demo}.yaml .
editor config-base.yaml
editor config-demo.yaml
./bin/create-secrets #2
./bin/reset-volumes
./bin/start-pods
podman pod ps
```

1. The renference deployment uses Podman as container runtime and
   `podman kube play` as orchestrator. Containers run in a rootless environment,
   hence ports 80 and 443 have to be redirected to unprivileged ports.
2. Podman 4.3.1 requires workaround for issue
   [#16269](https://github.com/containers/podman/issues/16269).

### Base

#### Environment variables

| env | | example |
| --- | --- | --- |
| `ACME_EMAIL` | | (email address) |
| `ACME_SERVER` | 1. | `https://acme.zerossl.com/v2/DV90` |
| `APP_IDS` | | `1 2 3 4` |
| `APACHE_HOSTNAME` | 2. | (FQDN) |
| `KEYCLOAK_EMAIL` | | (email address) |
| `KEYCLOAK_HOSTNAME` | 2. | (FQDN) |
| [`KEYCLOAK_LOG_LEVEL`](https://www.keycloak.org/server/all-config?q=log-level) | 3. | `debug` |
| `LDAP_URL` | 4. | `ldap://ldap.forumsys.com:389` |
| `REALM_IDS` | | `1 2 3 4` |
| `SMTP_SERVER` | | |

1. optional; default is `https://acme.zerossl.com/v2/DV90`
2. optional; default is `$(hostname -f)`
3. optional; default is `info`
4. optional

#### Secrets

| secret | keys | |
| --- | --- | --- |
| `acme-eab` | `hmac_key`, `kid` | 1. |
| `keycloak-admin-password` | `password` | 2. |
| `postgres-keycloak-password` | `password` | 3. |
| `postgres-password` | `password` | 4. |

1. leave empty for ACME HTTP Challenge instead of External Account Binding (EAB)
2. password for user `admin` on Keycloak Administration Console
3. password for PostgreSQL role `keycloak`
4. password for PostgreSQL role `postgres`

### Demo

#### Environment variables

| env | | example |
| --- | --- | --- |
| `APACHE_EMAIL` | | (email address) |
| `APACHE_HOSTNAME` | 1. | (FQDN) |
| [`APACHE_LOG_LEVEL`](https://httpd.apache.org/docs/2.4/en/mod/core.html#loglevel) | 2. | `debug` |
| `KEYCLOAK_HOSTNAME` | 1. | (FQDN) |
| [`KEYCLOAK_OIDC_REMOTE_USER_CLAIM`](https://github.com/OpenIDC/mod_auth_openidc/blob/master/auth_openidc.conf) | | `given_name ^(.+?)(?:\s.+)?$ $1` |
| [`KEYCLOAK_OIDC_SCOPE`](https://github.com/OpenIDC/mod_auth_openidc/blob/master/auth_openidc.conf) | | `openid profile`

1. optional; default is `$(hostname -f)`
2. optional; default is `info`

## Current Limitations

* [Online LDAP Test Server](https://www.forumsys.com/2022/05/10/online-ldap-test-server/) `ldap.forumsys.com ` currently hardcoded
* Keycloak administration and account consoles not yet protected by 2FA
* no YubiKey PINs enforced via WebAuthn policies in any of the demos
* certificate renewal via ACME not yet fully implemented
* signature on `blob.jwt` not yet checked and `blow.jwt` not refreshed
