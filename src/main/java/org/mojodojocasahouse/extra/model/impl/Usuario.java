package org.mojodojocasahouse.extra.model.impl;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.mojodojocasahouse.extra.model.UsuarioEntity;

@Entity
@Table(name = "usuario", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
@Getter
public class Usuario implements UsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(name = "nombre")
    private String nombre;

    @Setter
    @Column(name = "apellido")
    private String apellido;

    @Setter
    @Column(name = "email", unique = true)
    private String email;

    @Setter
    @Column(name = "password")
    private String password;


    public Usuario() {}

    public Usuario(String nombre, String apellido, String email, String password) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.password = password;
    }
    
}