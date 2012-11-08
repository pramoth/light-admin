package org.lightadmin.core.config.domain.scope;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import org.springframework.data.jpa.domain.Specification;

public final class ScopeMetadataUtils {

	private ScopeMetadataUtils() {
	}

	@SuppressWarnings( "unchecked" )
	public static ScopeMetadata scope( ScopeMetadata scope ) {
		if ( PredicateScopeMetadata.class.isAssignableFrom( scope.getClass() ) ) {
			return new PredicateScopeMetadata( ( PredicateScopeMetadata ) scope );
		}

		if ( SpecificationScopeMetadata.class.isAssignableFrom( scope.getClass() ) ) {
			return new SpecificationScopeMetadata( ( SpecificationScopeMetadata ) scope );
		}

		return new DefaultScopeMetadata( scope );
	}

	public static ScopeMetadata all() {
		return new DefaultScopeMetadata().name( "All" );
	}

	public static <T> ScopeMetadata filter( Predicate<T> filter ) {
		return new PredicateScopeMetadata<T>( filter ).name( filter.getClass().getSimpleName() );
	}

	public static <T> ScopeMetadata specification( Specification<T> specification ) {
		return new SpecificationScopeMetadata<T>( specification ).name( specification.getClass().getSimpleName() );
	}

	public static class SpecificationScopeMetadata<T> extends AbstractScope {

		private transient final Specification<T> specification;

		private SpecificationScopeMetadata( final Specification<T> specification ) {
			this.specification = specification;
		}

		private SpecificationScopeMetadata( final SpecificationScopeMetadata<T> scope ) {
			super( scope );
			this.specification = scope.specification();
		}

		public Specification<T> specification() {
			return specification;
		}
	}

	public static class PredicateScopeMetadata<T> extends AbstractScope {

		private transient Predicate<T> predicate = Predicates.alwaysTrue();

		private PredicateScopeMetadata( final Predicate<T> predicate ) {
			this.predicate = predicate;
		}

		public PredicateScopeMetadata( PredicateScopeMetadata<T> scope ) {
			super( scope );
			this.predicate = scope.predicate();
		}

		public Predicate<T> predicate() {
			return predicate;
		}
	}

	public static class DefaultScopeMetadata extends AbstractScope {

		private DefaultScopeMetadata() {
		}

		public DefaultScopeMetadata( ScopeMetadata scope ) {
			super( scope );
		}
	}

	public static abstract class AbstractScope implements ScopeMetadata {

		private String name = "Undefined";

		private boolean defaultScope = false;

		protected AbstractScope() {
		}

		public AbstractScope( ScopeMetadata scope ) {
			this.name = scope.getName();
			this.defaultScope = scope.isDefaultScope();
		}

		@Override
		public ScopeMetadata name( final String name ) {
			this.name = name;
			return this;
		}

		@Override
		public String getName() {
			return this.name;
		}

		@Override
		public boolean isDefaultScope() {
			return this.defaultScope;
		}

		@Override
		public ScopeMetadata defaultScope( final boolean defaultScope ) {
			this.defaultScope = defaultScope;
			return this;
		}
	}
}