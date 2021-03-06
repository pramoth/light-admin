package org.lightadmin.core.view.preparer;

import org.apache.tiles.Attribute;
import org.apache.tiles.AttributeContext;
import org.apache.tiles.context.TilesRequestContext;
import org.apache.tiles.preparer.ViewPreparer;
import org.apache.tiles.servlet.context.ServletTilesRequestContext;
import org.apache.tiles.servlet.context.ServletUtil;
import org.lightadmin.core.config.domain.DomainTypeAdministrationConfiguration;
import org.lightadmin.core.config.domain.GlobalAdministrationConfiguration;
import org.lightadmin.core.config.domain.GlobalAdministrationConfigurationAware;
import org.lightadmin.core.web.ApplicationController;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;

import static org.lightadmin.core.util.NamingUtils.entityId;
import static org.lightadmin.core.util.NamingUtils.entityName;

public abstract class ConfigurationAwareViewPreparer implements ViewPreparer, GlobalAdministrationConfigurationAware {

    private static final String DOMAIN_TYPE_ADMINISTRATION_CONFIGURATION_KEY = ApplicationController.DOMAIN_TYPE_ADMINISTRATION_CONFIGURATION_KEY;

    private GlobalAdministrationConfiguration globalAdministrationConfiguration;

    public final void execute(TilesRequestContext tilesContext, AttributeContext attributeContext) {
        execute(tilesContext, attributeContext, globalAdministrationConfiguration);
        DomainTypeAdministrationConfiguration domainTypeAdministrationConfiguration = domainTypeConfiguration(tilesContext);
        if (domainTypeAdministrationConfiguration != null) {
            execute(tilesContext, attributeContext, domainTypeAdministrationConfiguration);
        }
    }

    protected void execute(TilesRequestContext tilesContext, AttributeContext attributeContext, GlobalAdministrationConfiguration configuration) {
    }

    protected void execute(TilesRequestContext tilesContext, AttributeContext attributeContext, DomainTypeAdministrationConfiguration configuration) {
        addAttribute(attributeContext, DOMAIN_TYPE_ADMINISTRATION_CONFIGURATION_KEY, configuration, true);

        addAttribute(attributeContext, "persistentEntity", configuration.getPersistentEntity(), true);
        addAttribute(attributeContext, "entityPluralName", configuration.getEntityConfiguration().getPluralName());
        addAttribute(attributeContext, "entitySingularName", entitySingularName(tilesContext, configuration));

        addAttribute(attributeContext, "entity", entityFromRequest(tilesContext));
        addAttribute(attributeContext, "entityId", entityId(configuration, entityFromRequest(tilesContext)));
    }

    protected DomainTypeAdministrationConfiguration domainTypeConfiguration(final TilesRequestContext tilesContext) {
        return (DomainTypeAdministrationConfiguration) attributeFromRequest(tilesContext, DOMAIN_TYPE_ADMINISTRATION_CONFIGURATION_KEY);
    }

    private String entitySingularName(final TilesRequestContext tilesContext, final DomainTypeAdministrationConfiguration configuration) {
        final Object entity = entityFromRequest(tilesContext);
        if (entity == null) {
            return configuration.getEntityConfiguration().getSingularName();
        }
        return entityName(configuration, entity);
    }

    private Object entityFromRequest(TilesRequestContext tilesContext) {
        return attributeFromRequest(tilesContext, "entity");
    }

    protected Object attributeFromRequest(TilesRequestContext tilesContext, String attributeName) {
        final ServletTilesRequestContext servletTilesRequestContext = ServletUtil.getServletRequest(tilesContext);
        final HttpServletRequest httpServletRequest = servletTilesRequestContext.getRequest();

        return httpServletRequest.getAttribute(attributeName);
    }

    protected void addAttribute(AttributeContext attributeContext, String attributeName, Object attributeValue) {
        addAttribute(attributeContext, attributeName, attributeValue, false);
    }

    protected void addAttribute(AttributeContext attributeContext, String attributeName, Object attributeValue, boolean cascade) {
        attributeContext.putAttribute(attributeName, new Attribute(attributeValue), cascade);
    }

    @Override
    @Autowired
    public void setGlobalAdministrationConfiguration(final GlobalAdministrationConfiguration configuration) {
        this.globalAdministrationConfiguration = configuration;
    }
}