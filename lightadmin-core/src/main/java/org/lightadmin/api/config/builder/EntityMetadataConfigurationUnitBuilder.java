package org.lightadmin.api.config.builder;

import org.lightadmin.api.config.unit.EntityMetadataConfigurationUnit;
import org.lightadmin.api.config.utils.EntityNameExtractor;
import org.lightadmin.api.config.utils.EnumElement;
import org.lightadmin.core.config.domain.unit.ConfigurationUnitBuilder;
import org.springframework.data.rest.core.event.AbstractRepositoryEventListener;

public interface EntityMetadataConfigurationUnitBuilder extends ConfigurationUnitBuilder<EntityMetadataConfigurationUnit> {

    EntityMetadataConfigurationUnitBuilder nameField(String nameField);

    EntityMetadataConfigurationUnitBuilder nameExtractor(EntityNameExtractor<?> nameExtractor);

    EntityMetadataConfigurationUnitBuilder singularName(String singularName);

    EntityMetadataConfigurationUnitBuilder pluralName(String pluralName);

    EntityMetadataConfigurationUnitBuilder field(String fieldName);

    EntityMetadataConfigurationUnitBuilder enumeration(EnumElement... elements);

    EntityMetadataConfigurationUnitBuilder repositoryEventListener(Class<? extends AbstractRepositoryEventListener> listenerClass);
}
