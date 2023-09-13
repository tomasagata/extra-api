package org.mojodojocasahouse.extra.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class UsuarioRegistroDto {

    @NotBlank
    @NotNull
    private String nombre;
    @NotBlank
    @NotNull
    private String apellido;
    @Email
    @NotBlank
    @NotNull
    private String email;
    @NotBlank
    @NotNull
    private String password;

    public UsuarioRegistroDto(String nombre, String apellido, String email, String password) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.password = password;
    }

    public UsuarioRegistroDto(String email) {
        this.email = email;
    }

    public UsuarioRegistroDto() {
    }

}
