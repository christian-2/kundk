package org.keycloak.protocol.oidc.mappers;

import org.keycloak.models.GroupModel;
import org.keycloak.models.ProtocolMapperModel;
import org.keycloak.models.UserSessionModel;
import org.keycloak.models.utils.ModelToRepresentation;
import org.keycloak.protocol.ProtocolMapperUtils;
//import org.keycloak.protocol.oidc.OIDCLoginProtocol;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.representations.IDToken;

import java.util.ArrayList;
//import java.util.HashMap;
import java.util.List;
//import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GroupMembershipWithSuffixMapper extends GroupMembershipMapper {

    private static final List<ProviderConfigProperty> configProperties =
	   new ArrayList<ProviderConfigProperty>();

    static {
        OIDCAttributeMapperHelper.addTokenClaimNameConfig(configProperties);
        ProviderConfigProperty property;
       
	property = new ProviderConfigProperty();
        property.setName("full.path");
        property.setLabel("Full group path");
        property.setType(ProviderConfigProperty.BOOLEAN_TYPE);
        property.setDefaultValue("true");
        property.setHelpText("Include full path to group i.e. " +
		"/top/level1/level2, false will just specify the group name");
        configProperties.add(property);

	property = new ProviderConfigProperty();
        property.setName("suffix");
        property.setLabel("Suffix");
        property.setType(ProviderConfigProperty.STRING_TYPE);
        property.setHelpText("Suffix to add to the claim.");
        configProperties.add(property);

        OIDCAttributeMapperHelper.addIncludeInTokensConfig(configProperties,
		GroupMembershipMapper.class);
    }

    public static final String PROVIDER_ID = "oidc-group-membership-with-suffix-mapper";


    public List<ProviderConfigProperty> getConfigProperties() {
        return configProperties;
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public String getDisplayType() {
        return "Group Membership With Suffix";
    }

    @Override
    public String getHelpText() {
        return "Map user group membership where each group name includes " +
		"a configurable suffix.";
    }

    protected void setClaim(IDToken token, ProtocolMapperModel mappingModel, UserSessionModel userSession) {
        Function<GroupModel, String> toGroupRepresentation =
	       	useFullPath(mappingModel) ?
                ModelToRepresentation::buildGroupPath :
		GroupModel::getName;
        List<String> membership = userSession.getUser().getGroupsStream().
		map(toGroupRepresentation).
		map(s -> s + mappingModel.getConfig().get("suffix")).
		collect(Collectors.toList());

        mappingModel.getConfig().put(ProtocolMapperUtils.MULTIVALUED, "true");
        OIDCAttributeMapperHelper.mapClaim(token, mappingModel, membership);
    }
}
