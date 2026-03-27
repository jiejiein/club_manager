package com.club.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ClubDTO {
    private Long id;

    @NotBlank(message = "社团名称不能为空")
    private String name;

    @NotNull(message = "社团类型不能为空")
    private Long typeId;

    private String description;

    private String logo;
}
