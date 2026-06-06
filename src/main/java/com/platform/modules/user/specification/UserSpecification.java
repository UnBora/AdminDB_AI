package com.platform.modules.user.specification;

import com.platform.modules.user.entity.User;
import com.platform.modules.user.model.FilterCriteria;
import com.platform.modules.user.model.FilterCriteria.FilterOperation;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class UserSpecification {

    private UserSpecification() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Create a specification from a list of filter criteria
     */
    public static Specification<User> withFilters(List<FilterCriteria> filters) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (filters != null && !filters.isEmpty()) {
                for (FilterCriteria criteria : filters) {
                    Predicate predicate = buildPredicate(criteria, root, criteriaBuilder);
                    if (predicate != null) {
                        predicates.add(predicate);
                    }
                }
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Build a single predicate based on filter criteria
     */
    private static Predicate buildPredicate(FilterCriteria criteria, Root<User> root, CriteriaBuilder cb) {
        if (criteria == null || criteria.getFilterKey() == null) {
            return null;
        }

        return switch (criteria.getOperation()) {
            case EQUAL -> cb.equal(root.get(criteria.getFilterKey()), criteria.getValue());
            case NOT_EQUAL -> cb.notEqual(root.get(criteria.getFilterKey()), criteria.getValue());
            case GREATER_THAN -> cb.greaterThan(root.get(criteria.getFilterKey()), (Comparable) criteria.getValue());
            case LESS_THAN -> cb.lessThan(root.get(criteria.getFilterKey()), (Comparable) criteria.getValue());
            case GREATER_THAN_OR_EQUAL -> cb.greaterThanOrEqualTo(root.get(criteria.getFilterKey()), (Comparable) criteria.getValue());
            case LESS_THAN_OR_EQUAL -> cb.lessThanOrEqualTo(root.get(criteria.getFilterKey()), (Comparable) criteria.getValue());
            case LIKE -> cb.like(cb.lower(root.get(criteria.getFilterKey())), "%" + criteria.getValue().toString().toLowerCase() + "%");
            case IN -> root.get(criteria.getFilterKey()).in((Collection<?>) criteria.getValue());
            case IS_NULL -> cb.isNull(root.get(criteria.getFilterKey()));
            case IS_NOT_NULL -> cb.isNotNull(root.get(criteria.getFilterKey()));
            case BETWEEN -> {
                if (criteria.getValue() instanceof Object[] values && values.length == 2) {
                    yield cb.between(root.get(criteria.getFilterKey()), (Comparable) values[0], (Comparable) values[1]);
                }
                yield null;
            }
        };
    }

    /**
     * Filter by username
     */
    public static Specification<User> hasUsername(String username) {
        return (root, query, cb) -> {
            if (username == null || username.isEmpty()) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("username")), "%" + username.toLowerCase() + "%");
        };
    }

    /**
     * Filter by email
     */
    public static Specification<User> hasEmail(String email) {
        return (root, query, cb) -> {
            if (email == null || email.isEmpty()) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%");
        };
    }

    /**
     * Filter by active status
     */
    public static Specification<User> isActive(Boolean active) {
        return (root, query, cb) -> {
            if (active == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("active"), active);
        };
    }

    /**
     * Filter by created date range
     */
    public static Specification<User> createdBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), startDate));
            }
            
            if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), endDate));
            }
            
            if (predicates.isEmpty()) {
                return cb.conjunction();
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Filter by first name
     */
    public static Specification<User> hasFirstName(String firstName) {
        return (root, query, cb) -> {
            if (firstName == null || firstName.isEmpty()) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("firstName")), "%" + firstName.toLowerCase() + "%");
        };
    }

    /**
     * Filter by last name
     */
    public static Specification<User> hasLastName(String lastName) {
        return (root, query, cb) -> {
            if (lastName == null || lastName.isEmpty()) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("lastName")), "%" + lastName.toLowerCase() + "%");
        };
    }

    /**
     * Combine multiple specifications with AND logic
     */
    public static Specification<User> and(Specification<User> spec1, Specification<User> spec2) {
        return Specification.where(spec1).and(spec2);
    }
}
