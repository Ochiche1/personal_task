package com.usersauth.usersauth.model;

import com.usersauth.usersauth.Enum.Enum_Roles;
import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "roles")
public class Roles {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true)
    private Integer id;
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Enum_Roles name;
}
