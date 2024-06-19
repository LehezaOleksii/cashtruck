package com.projects.oleksii.leheza.cashtruck.repository;

import com.projects.oleksii.leheza.cashtruck.domain.User;
import com.projects.oleksii.leheza.cashtruck.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends
        JpaRepository<User, Long>, JpaSpecificationExecutor<User>,
        PagingAndSortingRepository<User, Long> {

//    @EntityGraph(attributePaths = "authorities")
    Optional<User> findByEmail(String email);

    User findByEmailIgnoreCase(String email); // TODO Optional<User>

    Boolean existsByEmailIgnoreCase(String email);

    @Query("SELECT u.avatar.imageBytes FROM User u WHERE u.id = :userId")
    byte[] findAvatarByUserId(@Param("userId") Long userId);

    List<User> findAll();

    Page<User> findAll(Specification<User> specification, Pageable pageable);

    Page<User> findAll(Pageable pageable);

    @Query("SELECT u.id FROM User u WHERE u.subscriptionFinishDate <= CURRENT_DATE")
    List<Long> findUserIdsWithExpiredSubscriptions();

    List<User> findByRole(Role role);

    List<User> findByEmailContaining(String partialEmail);

    @Query("SELECT u.email FROM User u WHERE u.role = :role")
    List<String> findAllEmailsByRole(@Param("role") Role role);
}
