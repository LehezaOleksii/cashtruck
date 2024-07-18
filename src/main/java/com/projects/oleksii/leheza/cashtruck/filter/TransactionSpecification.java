//package com.projects.oleksii.leheza.cashtruck.filter;
//
//import com.projects.oleksii.leheza.cashtruck.domain.Category;
//import com.projects.oleksii.leheza.cashtruck.domain.Transaction;
//import jakarta.persistence.criteria.Join;
//import jakarta.persistence.criteria.JoinType;
//import jakarta.persistence.criteria.Predicate;
//import org.springframework.data.jpa.domain.Specification;
//import org.springframework.stereotype.Component;
//
//@Component
//public class TransactionSpecification {
//
//    public Specification<Transaction> withClientId(Long clientId) {
//        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("user").get("id"), clientId);
//
//    }
//
//    public static Specification<Transaction> withCategoryName(String categoryName) {
//        return (root, query, criteriaBuilder) -> {
//            Join<Transaction, Category> categoryJoin = root.join("category", JoinType.INNER);
//            Predicate categoryNamePredicate = criteriaBuilder.equal(categoryJoin.get("name"), categoryName);
//            return categoryNamePredicate;
//        };
//    }
//}
