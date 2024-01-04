package org.mojodojocasahouse.extra.dto;

import lombok.Data;

@Data
public class CategoryWithIconDTO {
    private String category;
    private Short iconId;

    public CategoryWithIconDTO(String category, Short iconId){
        this.category = category;
        this.iconId = iconId;
    }
}
