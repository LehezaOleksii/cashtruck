package com.projects.oleksii.leheza.cashtruck.filter;


import com.projects.oleksii.leheza.cashtruck.domain.User;
import com.projects.oleksii.leheza.cashtruck.dto.filter.UserSearchCriteria;
import com.projects.oleksii.leheza.cashtruck.enums.ActiveStatus;
import com.projects.oleksii.leheza.cashtruck.enums.Role;
import com.projects.oleksii.leheza.cashtruck.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UserSpecification {

    private final UserRepository userRepository;

    public Page<User> getUsersWithCriterias(UserSearchCriteria criterias, int page, int size, Sort sort) {
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
        Specification<User> specification = Specification.where(Specification.allOf(specifications));
        Pageable pageable = PageRequest.of(page, size, sort);
        return userRepository.findAll(specification, pageable);
    }

    public Specification<User> roleLike(UserSearchCriteria criteria) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("role"), Role.valueOf(criteria.getRole()));
    }

    public Specification<User> planLike(UserSearchCriteria criteria) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("plan"), "%" + criteria.getPlan() + "%");
    }

    public Specification<User> emailLike(UserSearchCriteria criteria) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get("email"), "%" + criteria.getEmail() + "%");
    }

    public Specification<User> statusLike(UserSearchCriteria criteria) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("status"), ActiveStatus.valueOf(criteria.getStatus()));
    }
}
