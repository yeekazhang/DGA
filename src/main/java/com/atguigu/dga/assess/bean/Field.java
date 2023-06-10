package com.atguigu.dga.assess.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Field {
    private String name;
    private String comment;
    private String type;
}
