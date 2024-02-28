# Frequently Asked Questions (FAQ)

## How can I delete passkeys in macOS?

`System Settings...` / `Passwords`; press info icons to discriminate
passkeys from passwords (passkeys typically bear domain names, whereas passwords
typically bear FQDNs); `Delete Passkey...` 

## Is `glauth-keycloak` always required?

You do not need `glauth-keycloak` and its propriertary piece of Go
[`keycloak.go`](https://github.com/christian-2/glauth-keycloak/blob/2aa23d32b480114584d3b1f523c1fcc1f4a6cfac/keycloak.go)
if your LDAP connection is to an AD or ADFS server. It serves cases where the
connection is e.g. to an OpenLDAP server (or to the
[Online LDAP Test Server](https://www.forumsys.com/2022/05/10/online-ldap-test-server/)).
