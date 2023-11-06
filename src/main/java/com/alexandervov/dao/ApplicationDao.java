package com.alexandervov.dao;

import com.alexandervov.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationDao {
    private Integer id;
    private String name;
    private String description;
    private String packageName;
    private String picture128Base64;
    private String picture512Base64;
    private Category category;
    private int downloadCounter;
}
