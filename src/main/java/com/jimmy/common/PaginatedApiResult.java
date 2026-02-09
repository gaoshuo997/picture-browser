package com.jimmy.common;

import lombok.Data;

import java.util.List;

@Data
public class PaginatedApiResult<T> {

    private Integer page;

    private Integer pageSize;

    private Integer count;

    private Long total;

    private List<T> list;

    /**
     * Constructor without total.
     *
     * @param page page index
     * @param size page size
     * @param count count
     * @param data content
     */
    public PaginatedApiResult(Integer page, Integer size, Integer count, List<T> data) {
        this(page, size, count, null, data);
    }

    /**
     * Constructor with total.
     *
     * @param page page index.
     * @param size page size.
     * @param count count in this page.
     * @param total total count.
     * @param data data.
     */
    public PaginatedApiResult(Integer page, Integer size, Integer count, Long total, List<T> data) {
        this.page = page + 1;
        this.pageSize = size;
        this.count = count;
        this.total = total;
        this.list = data;
    }
}
