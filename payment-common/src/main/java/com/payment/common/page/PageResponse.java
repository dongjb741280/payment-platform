package com.payment.common.page;

import lombok.Data;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Data
public class PageResponse<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private int total;
    private int pageNum;
    private int pageSize;
    private int pages;
    private boolean hasNext;
    private List<T> records;

    public static <T> PageResponse<T> of(int total, int pageNum, int pageSize, List<T> records) {
        PageResponse<T> resp = new PageResponse<>();
        resp.total = total;
        resp.pageNum = pageNum;
        resp.pageSize = pageSize;
        resp.pages = pageSize <= 0 ? 0 : (int) Math.ceil(total / (double) pageSize);
        resp.hasNext = pageNum < resp.pages;
        resp.records = records == null ? Collections.emptyList() : records;
        return resp;
    }
}


