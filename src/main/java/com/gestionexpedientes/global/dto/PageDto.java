package com.gestionexpedientes.global.dto;

import java.util.List;

public class PageDto<T> {

    private final List<T> items;
    private final int pageIndex;
    private final int pageSize;
    private final long totalRecords;

    public PageDto(List<T> items, int pageIndex, int pageSize, long totalRecords) {
        this.items = items;
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
        this.totalRecords = totalRecords;
    }

    public List<T> getItems() {
        return items;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public long getTotalRecords() {
        return totalRecords;
    }

    public int getTotalPages() {
        return (int) Math.ceil((double) totalRecords / pageSize);
    }

    public boolean isHasPreviousPage() {
        return pageIndex > 1;
    }

    public boolean isHasNextPage() {
        return pageIndex < getTotalPages();
    }
}
