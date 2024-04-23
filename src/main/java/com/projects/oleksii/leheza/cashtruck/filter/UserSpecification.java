package com.projects.oleksii.leheza.cashtruck.filter;


import com.projects.oleksii.leheza.cashtruck.domain.User;
import com.projects.oleksii.leheza.cashtruck.dto.filter.UserSearchCriteria;
import com.projects.oleksii.leheza.cashtruck.enums.ActiveStatus;
import com.projects.oleksii.leheza.cashtruck.enums.Role;
import com.projects.oleksii.leheza.cashtruck.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UserSpecification {

    private final UserRepository userRepository;

    public List<User> getUsersWithCriterias(UserSearchCriteria criterias) {
        List<Specification<User>> specifications = new ArrayList<>();
        if (criterias.getStatus() != null && !criterias.getStatus().isEmpty()) {
            specifications.add(statusLike(criterias));
        }
        if (criterias.getEmail() != null && !criterias.getEmail().isEmpty()) {
            specifications.add(emailLike(criterias));
        }
        if (criterias.getPlan() != null && !criterias.getPlan().isEmpty()) {
            specifications.add(planLike(criterias));
        }
        if (criterias.getRole() != null && !criterias.getRole().isEmpty()) {
            specifications.add(roleLike(criterias));
        }

        Specification<User> combinedSpec = Specification.where(null);
        for (Specification<User> spec : specifications) {
            combinedSpec = combinedSpec.and(spec);
        }
        return userRepository.findAll(combinedSpec);
    }

    private Specification<User> roleLike(UserSearchCriteria criteria) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("role"), Role.valueOf(criteria.getRole()));
    }

    private Specification<User> planLike(UserSearchCriteria criteria) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("plan"), "%" + criteria.getPlan() + "%");
    }

    private Specification<User> emailLike(UserSearchCriteria criteria) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get("email"), "%" + criteria.getEmail() + "%");
    }

    private Specification<User> statusLike(UserSearchCriteria criteria) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("status"), ActiveStatus.valueOf(criteria.getStatus()));
    }
}
