package com.jimmy.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaginatedApiResult<T> {

    // 分页查询页码
    private Integer page;

    // 分页查询个数
    private Integer pageSize;

    // 当前页的元素个数
    private Integer count;

    // 分页查询到的总元素数
    private Long total;

    // 分页查询到的元素列表
    private List<T> list;

    // 页数
    private Integer totalPages;

}
