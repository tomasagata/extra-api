package org.mojodojocasahouse.extra.model;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import org.mojodojocasahouse.extra.dto.model.CategoryDTO;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "UniqueNameAndIconPerUser", columnNames = {"NAME", "ICON_ID", "OWNER_ID"})
})
@Getter
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "NAME", nullable = false)
    @NotNull(message = "Category name is mandatory")
    @Size(max = 50, message = "Category name cannot exceed 50 characters")
    @Pattern(regexp = "^([A-Za-z\\d]+(\\s[A-Za-z\\d])?)*$",
            message = "Category name must only contain letters, numbers or spaces")
    private String name;

    @Column(name = "ICON_ID", nullable = false)
    @NotNull(message = "IconId is mandatory")
    @Digits(integer = 3, fraction = 0, message = "IconId must limit to 3 integer places")
    @DecimalMin(value = "0", message = "IconId must be greater than 0")
    @DecimalMax(value = "15", message = "IconId must be less than 15")
    private Short iconId;

    @ManyToOne
    @JoinColumn(name = "OWNER_ID", nullable = false)
    private ExtraUser owner;

    public Category() {}

    @Valid
    public Category(String name, Short iconId, ExtraUser owner) {
        this.name = name;
        this.iconId = iconId;
        this.owner = owner;
    }

    public CategoryDTO asDto() {
        return new CategoryDTO(
                this.name,
                this.iconId
        );
    }
}
