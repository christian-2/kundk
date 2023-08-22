package org.keycloak.protocol.oidc.mappers;

import org.keycloak.models.ProtocolMapperModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.UserSessionModel;
import org.keycloak.protocol.ProtocolMapperUtils;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.representations.IDToken;

import java.util.ArrayList;
import java.util.List;

public class UserPropertyWithSuffixMapper extends UserPropertyMapper {

    private static final List<ProviderConfigProperty> configProperties =
        new ArrayList<ProviderConfigProperty>();

    static {
        ProviderConfigProperty property;

        property = new ProviderConfigProperty();
        property.setName(ProtocolMapperUtils.USER_ATTRIBUTE);
        property.setLabel(ProtocolMapperUtils.USER_MODEL_PROPERTY_LABEL);
        property.setType(ProviderConfigProperty.STRING_TYPE);
        property.setHelpText(ProtocolMapperUtils.USER_MODEL_PROPERTY_HELP_TEXT);
        configProperties.add(property);

        property = new ProviderConfigProperty();
        property.setName("suffix");
        property.setLabel("Suffix");
        property.setType(ProviderConfigProperty.STRING_TYPE);
        property.setHelpText("Suffix to add to the claim.");
        configProperties.add(property);

        OIDCAttributeMapperHelper.addAttributeConfig(configProperties,
	    UserPropertyWithSuffixMapper.class);
    }

    public static final String PROVIDER_ID =
        "oidc-usermodel-property-with-suffix-mapper";

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return configProperties;
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public String getDisplayType() {
        return "User Property With Suffix";
    }

    @Override
    public String getHelpText() {
        return "Map a built in user property plus a configurable suffix " +
            "to a token claim.";
    }

    @Override
    protected void setClaim(IDToken token, ProtocolMapperModel mappingModel,
        UserSessionModel userSession) {
        UserModel user = userSession.getUser();
        String propertyName = mappingModel.getConfig().get(
	    ProtocolMapperUtils.USER_ATTRIBUTE);

        if (propertyName == null || propertyName.trim().isEmpty()) return;

        String propertyValue = ProtocolMapperUtils.getUserModelValue(user,
	    propertyName) + mappingModel.getConfig().get("suffix");
        OIDCAttributeMapperHelper.mapClaim(token, mappingModel, propertyValue);
    }
}
