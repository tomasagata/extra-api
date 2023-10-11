package org.mojodojocasahouse.extra.model;

import jakarta.persistence.*;
import lombok.Getter;
import java.util.Set;

@Entity(name = "AUTHORITIES")
@Getter
public class Authority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ROLE")
    private String role;

    @ManyToMany(mappedBy = "authorities")
    private Set<ExtraUser> users;

    public Authority(String role){
        this.role = role;
    }

    public Authority(String role, Set<ExtraUser> users){
        this.role = role;
        this.users = users;
    }

    public Authority(){}

}
