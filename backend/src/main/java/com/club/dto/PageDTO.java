package com.club.dto;

import lombok.Data;

@Data
public class PageDTO {
    private Integer current = 1;
    private Integer size = 10;
    private String keyword;
}
