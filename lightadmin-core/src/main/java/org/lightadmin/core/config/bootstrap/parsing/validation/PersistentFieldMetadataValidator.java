package org.lightadmin.core.config.bootstrap.parsing.validation;

import org.lightadmin.api.config.annotation.FileReference;
import org.lightadmin.core.config.LightAdminConfiguration;
import org.lightadmin.core.config.bootstrap.parsing.DomainConfigurationProblem;
import org.lightadmin.core.config.domain.field.PersistentFieldMetadata;
import org.lightadmin.core.persistence.metamodel.PersistentPropertyType;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.context.MappingContext;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.Collection;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.emptyList;
import static org.apache.commons.io.FileUtils.getFile;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.lightadmin.core.persistence.metamodel.PersistentPropertyType.isOfFileReferenceType;
import static org.lightadmin.core.persistence.metamodel.PersistentPropertyType.isSupportedAttributeType;

class PersistentFieldMetadataValidator implements FieldMetadataValidator<PersistentFieldMetadata> {

    PersistentFieldMetadataValidator() {
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<? extends DomainConfigurationProblem> validateFieldMetadata(PersistentFieldMetadata fieldMetadata, Class<?> domainType, DomainConfigurationValidationContext validationContext) {
        MappingContext mappingContext = validationContext.getMappingContext();
        final LightAdminConfiguration lightAdminConfiguration = validationContext.getLightAdminConfiguration();

        final PersistentEntity persistentEntity = mappingContext.getPersistentEntity(domainType);

        PersistentProperty persistentProperty = persistentEntity.getPersistentProperty(fieldMetadata.getField());

        if (persistentProperty == null) {
            return newArrayList(validationContext.notPersistableFieldProblem(fieldMetadata.getName()));
        }

        if (!isSupportedAttributeType(PersistentPropertyType.forPersistentProperty(persistentProperty))) {
            return newArrayList(validationContext.notSupportedTypeFieldProblem(fieldMetadata.getName()));
        }

        if (!isOfFileReferenceType(persistentProperty)) {
            return emptyList();
        }

        Annotation annotation = persistentProperty.findAnnotation(FileReference.class);

        FileReference fileReference = (FileReference) annotation;

        if (isEmpty(fileReference.baseDirectory())) {
            if (lightAdminConfiguration.getFileStorageDirectory() != null) {
                return emptyList();
            }
            return newArrayList(validationContext.missingBaseDirectoryInFileReferenceProblem(fieldMetadata.getName()));
        }

        final File directory = getFile(fileReference.baseDirectory());
        if (directory.exists() && directory.isDirectory()) {
            return emptyList();
        }

        return newArrayList(validationContext.missingBaseDirectoryInFileReferenceProblem(fieldMetadata.getName()));
    }
}