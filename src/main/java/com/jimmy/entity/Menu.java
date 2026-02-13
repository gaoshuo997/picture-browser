package com.jimmy.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity(name = "menu")
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 64)
    private String name;

    @Column(name = "icon", length = 64)
    private String icon;

    @Column(name = "path", length = 128)
    private String path;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Menu parentMenu;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @Column(name = "create_at")
    private Date createAt;

    @Column(name = "update_at")
    private Date updateAt;

    @Column(name = "delete_flag")
    private Integer deleteFlag;
}
