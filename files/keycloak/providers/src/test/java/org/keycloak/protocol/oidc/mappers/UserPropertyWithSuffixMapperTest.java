package org.keycloak.protocol.oidc.mappers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.Test;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.List;
import java.util.stream.Collectors;

public class UserPropertyWithSuffixMapperTest {

    String[] names = {
        "user.attribute",
        "suffix",
        "claim.name",
        "jsonType.label",
        "id.token.claim",
        "access.token.claim",
        "userinfo.token.claim"
    };

    @Test
    public void testConfigProperties() {
	UserPropertyWithSuffixMapper mapper =
	    new UserPropertyWithSuffixMapper();
	List<ProviderConfigProperty> configProperties =
	    mapper.getConfigProperties();
        assertThat(
            configProperties.stream().
                map(configProperty -> configProperty.getName()).
                toArray(String[]::new),
            is(names));
    }
}
