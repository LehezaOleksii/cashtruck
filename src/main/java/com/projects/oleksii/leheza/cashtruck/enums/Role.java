package com.projects.oleksii.leheza.cashtruck.enums;

import com.projects.oleksii.leheza.cashtruck.domain.CustomUser;
import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;

@Entity
@Table(name = "t_role")
public class Role implements GrantedAuthority {
    @Getter
    @Id
    private Long id;
    @Getter
    private String name;
    @Transient
    @ManyToMany(mappedBy = "roles")
    private Set<CustomUser> users;

    public Role() {
    }

    public Role(Long id) {
        this.id = id;
    }

    public Role(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<CustomUser> getUsers() {
        return users;
    }

    public void setUsers(Set<CustomUser> users) {
        this.users = users;
    }

    @Override
    public String getAuthority() {
        return getName();
    }
}
