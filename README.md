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

| demo | authentication | registration | user federation | protocol | OP | RP |
| --- | --- | --- | --- | --- | --- | --- |
| #1 | FIDO2 (1FA) | username/password | LDAP | OIDC | Keycloak | Apache ([`mod_auth_openidc`](https://github.com/OpenIDC/mod_auth_openidc)) |
| #2 | username/password + FIDO2 (2FA) | username/password | LDAP | OIDC | Keycloak | Apache ([`mod_auth_openidc`](https://github.com/OpenIDC/mod_auth_openidc)) |
| #3 | FIDO2 (1FA) | username/password | LDAP | SAML | Keycloak | Apache ([`mod_shib`](https://shibboleth.atlassian.net/wiki/spaces/SP3/pages/2065335062/Apache)) |
| #4 | username/password + FIDO2 (2FA) | username/password | LDAP | SAML | Keycloak | Apache ([`mod_shib`](https://shibboleth.atlassian.net/wiki/spaces/SP3/pages/2065335062/Apache))) |
| #6 | username/password + FIDO2 (2FA) | username/password | n/a | OIDC | Keycloak | vSphere ([ADFS provider](https://docs.vmware.com/en/VMware-vSphere/7.0/com.vmware.vsphere.authentication.doc/GUID-C5E998B2-1148-46DC-990E-A5DB71F93351.html)) |

### vSphere

Configure vCenter Server Identity Provider Federation for ADFS as follows
(tested with vSphere 8.1):

| option | value |
| --- | --- |
| Base distinguished name for users | `cn=users,dc=$(echo $VSPHERE_DOMAIN \| sed 's/\./,dc=/g')` |
| Base distinguished name for groups | `cn=users,dc=$(echo $VSPHERE_DOMAIN \| sed 's/\./,dc=/g')` |
| Username | `cn=demo-6-client,cn=bind,dc=$(echo $VSPHERE_DOMAIN | sed 's/\./,dc=/g')` |
| Password | client secret for client `demo-6-client` in realm `oidc-passkey-demo-6` |
| Primary server URL | `ldap://$KEYCLOAK_HOSTNAME:3893` |
| Secondary server URL | n/a |
| Certificates (for LDAPS) | n/a |
| Identity provider name | `demo-6-client` |
| Client identifier | `demo-6-client` |
| Share secret | client secret for client `demo-6-client` in realm `oidc-passkey-demo-6` |
| OpenID Address | `https://$KEYCLOAK_HOSTNAME:$KEYCLOAK_PORT/realms/oidc-passkey-demo-6/.well-known/openid-configuration` |

## Installation

```
sudo apt-get install xinetd
sudo sh -c "cat docs/xinetd.conf >> /etc/xinetd.d/services" # 1.
sudo systemctl reload xinetd.service
./scripts/build-base --no-cache
./scripts/build-demo --no-cache
cp docs/config.yaml .
editor config.yaml #2
./scripts/create-secrets #3
./scripts/reset-volumes
sudo loginctl enable-linger $(whoami)
./scripts/start-base
./scripts/start-base
podman pod ps
```

1. The reference deployment uses Podman as container runtime and
   `podman kube play` as orchestrator. Containers run in a rootless environment,
   hence ports 80 and 443 must be redirected to unprivileged ports.
2. see below
3. Podman 4.3.1 requires workaround for issue
   [#16269](https://github.com/containers/podman/issues/16269).

(tested with Debian 12.1, Podman 4.3.1)

### Environment variables

| env | | example |
| --- | --- | --- |
| `ACME_EMAIL` | | (email address) |
| `ACME_SERVER` | 1. | `https://acme.zerossl.com/v2/DV90` |
| `APACHE_EMAIL` | | (email address) |
| `APACHE_HOSTNAME` | 2. | (FQDN) |
| [`APACHE_LOG_LEVEL`](https://httpd.apache.org/docs/2.4/en/mod/core.html#loglevel) | 3. | `debug` |
| `APP_IDS` | | `1 2 3 4 6` |
| `KEYCLOAK_EMAIL` | | (email address) |
| `KEYCLOAK_HOSTNAME` | 2. | (FQDN) |
| [`KEYCLOAK_LOG_LEVEL`](https://www.keycloak.org/server/all-config?q=log-level) | 3. | `debug` |
| [`KEYCLOAK_OIDC_REMOTE_USER_CLAIM`](https://github.com/OpenIDC/mod_auth_openidc/blob/master/auth_openidc.conf) | | `given_name ^(.+?)(?:\s.+)?$ $1` |
| [`KEYCLOAK_OIDC_SCOPE`](https://github.com/OpenIDC/mod_auth_openidc/blob/master/auth_openidc.conf) | | `openid profile`
| `KEYCLOAK_PORT` | TODO | TODO |
| `LDAP_SERVER` | 4. | `ldap://ldap.forumsys.com:389` |
| `REALM_IDS` | | `1 2 3 4 6` |
| `SMTP_SERVER` | | |
| `VSPHERE_DOMAIN` | |
| `VSPHERE_SERVER` | |

1. optional; default is `https://acme.zerossl.com/v2/DV90`
2. optional; default is `$(hostname -f)`
3. optional; default is `info`
4. optional

### Secrets

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
