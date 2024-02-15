# kundk

This is a ready solution for employing Keycloak with FIDO2/WebAuthn and
OIDC (or SAML). Demos are included.

## Demos

| relying party (RP) | 2FA | 1FA |
| --- | :---: | :---: |
| Apache (`mod_auth_openidc`) | demo #1 | demo #2 |
| Apache (`mod_shib`) | demo #3 | demo #4 |
| VMware vSphere | n/a | demo #6 |

## Configuration

### Build-time variables

| `ARG` | example | description |
| --- | --- | --- |
| `KEYCLOAK_DB` | `postgres` | RDB for Keycloak |
| `KEYCLOAK_RELEASEVER` | 9 | release version of RHEL for Keycloak container |
| `KEYCLOAK_VERSION`| `latest` | Keycloak version |

### Environment variables

`kund` supports multiple tenants, e.g. both demos and production use cases.
Their common configuration resides in environment variables.

| `ENV` | | example |
| --- | --- | --- |
| `APP_IDS` | | `1 2 3 4 6` |
| `KEYCLOAK_DB_URL` | | `jdbc:postgres://localhost/keycloak` |
| `KEYCLOAK_DB_USERNAME` | | `keycloak` |
| `KEYCLOAK_EMAIL` | | `me@mydomain.com` |
| `KEYCLOAK_PORT` | 1. | 8444 |
| `REALM_IDS` | | `1 2 3 4 6` |
| `SMTP_SERVER` | | `mail.mydomain.com` |

1. optional; default is `8444`

The following environment variables are only required to support the demos.

| env | | example |
| --- | --- | --- |
| `APACHE_EMAIL` | | `me@mydomain.com` |
| [`APACHE_LOG_LEVEL`](https://httpd.apache.org/docs/2.4/en/mod/core.html#loglevel) | 1. | `debug` |
| [`KEYCLOAK_LOG_LEVEL`](https://www.keycloak.org/server/all-config?q=log-level) | 1. | `debug` |
| [`KEYCLOAK_OIDC_REMOTE_USER_CLAIM`](https://github.com/OpenIDC/mod_auth_openidc/blob/master/auth_openidc.conf) | | `given_name ^(.+?)(?:\s.+)?$ $1` |
| LDAP_PORT | | 3893 |
| VSPHERE_DOMAIN | 2. | `mydomain.com` |
| VSPHERE_SERVER | 2. | `https://vsphere.mydomain.com` |

1. optional; default is `info`
2. only required for demo #6

### Secrets

| secret | keys | |
| --- | --- | --- |
| `keycloak-admin-password` | `password` | 1. |
| `keycloak-db-password` | `password` | |

1. password for user `admin` on Keycloak Administration Console

### Factory keys

| key | description |
| --- | --- |
| `client_id` | see [`ClientRepresentation.id`](https://www.keycloak.org/docs-api/23.0.6/rest-api/index.html#ClientRepresentation) |
| `display_name` | see [`RealmRepresentation.displayName`](https://www.keycloak.org/docs-api/23.0.6/rest-api/index.html#RealmRepresentation) |
| `flow` | see [`AuthenticationFlowRepresentation.alias`](`https://www.keycloak.org/docs-api/latest/rest-api/index.html#AuthenticationFlowRepresentation) (`kundk-1fa` or `kundk-2fa`) |
| `ldap_attribute_first_name` | |
| `ldap_auth_type` | see [`UserFederationProviderRepresentation.config.authType`](https://www.keycloak.org/docs-api/23.0.6/rest-api/index.html#UserFederationProviderRepresentation) (for LDAP) |
| `ldap_bind_credential` | |
| `ldap_bind_dn` | see [`UserFederationProviderRepresentation.config.ldapBind`](https://www.keycloak.org/docs-api/23.0.6/rest-api/index.html#UserFederationProviderRepresentation) (for LDAP) |
| `ldap_connection_url` | see [`UserFederationProviderRepresentation.config.connectionUrl`](https://www.keycloak.org/docs-api/23.0.6/rest-api/index.html#UserFederationProviderRepresentation) (for LDAP) |
| `ldap_rdn_ldap_attribute` | |
| `ldap_username_ldap_attribute` | |
| `ldap_users_dn` | see [`UserFederationProviderRepresentation.config.userDn`](https://www.keycloak.org/docs-api/23.0.6/rest-api/index.html#UserFederationProviderRepresentation) (for LDAP) |
| `ldap_user_object_class` | |
| `ldap_uuid_ldap_attribute` | see [`UserFederationProviderRepresentation.config.uuidLDAPAttribute`](https://www.keycloak.org/docs-api/23.0.6/rest-api/index.html#UserFederationProviderRepresentation) |
| `post_logout_redirect_uri` | see [`ClientRepresentation.attributes."post.logout.redirect.uris"`](https://www.keycloak.org/docs-api/23.0.6/rest-api/index.html#ClientRepresentation) (for OIDC) |
| `protocol` | see [`ClientRepresentation.protocol`](https://www.keycloak.org/docs-api/23.0.6/rest-api/index.html#ClientRepresentation) |
| `realm` | see [`RealmRepresentation.realm`](https://www.keycloak.org/docs-api/23.0.6/rest-api/index.html#RealmRepresentation) |
| `redirect_uri` | [`ClientRepresentation.redirectUris`](https://www.keycloak.org/docs-api/23.0.6/rest-api/index.html#ClientRepresentation) (for OIDC) |
| `saml_assertion_consumer_url_redirect` | |
| `saml_single_logout_service_url_redirect` | |
| `vsphere_domain` | AD domain |

## Frequently asked questions

* [FAQ](FAQ.md)
