package org.lightadmin.core.config.domain;

import org.lightadmin.core.config.domain.configuration.DefaultEntityMetadataConfigurationUnitBuilder;
import org.lightadmin.core.config.domain.configuration.support.EntityNameExtractorFactory;
import org.lightadmin.core.config.domain.unit.ConfigurationUnits;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.repository.support.Repositories;

import javax.persistence.EntityManager;

@SuppressWarnings({"rawtypes", "unchecked"})
public class DomainTypeAdministrationConfigurationFactory {

    private Repositories repositories;
    private EntityManager entityManager;
    private MappingContext<?, ?> mappingContext;

    public DomainTypeAdministrationConfigurationFactory(Repositories repositories, EntityManager entityManager, MappingContext<?, ?> mappingContext) {
        this.entityManager = entityManager;
        this.mappingContext = mappingContext;

        this.repositories = repositories;
    }

    public DomainTypeAdministrationConfiguration createAdministrationConfiguration(ConfigurationUnits configurationUnits) {
        return new DomainTypeAdministrationConfiguration(repositories, configurationUnits);
    }

    public DomainTypeBasicConfiguration createNonManagedDomainTypeConfiguration(Class<?> domainType) {
        JpaRepository repository = new SimpleJpaRepository(domainType, entityManager);

        PersistentEntity persistentEntity = mappingContext.getPersistentEntity(domainType);

        DefaultEntityMetadataConfigurationUnitBuilder builder = new DefaultEntityMetadataConfigurationUnitBuilder(domainType);

        builder.nameExtractor(EntityNameExtractorFactory.forPersistentEntity(persistentEntity));

        return new NonManagedDomainTypeConfiguration(builder.build(), persistentEntity, repository);
    }
}