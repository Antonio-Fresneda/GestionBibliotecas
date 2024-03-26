package com.gestion.search;

import lombok.Data;

import java.util.List;

@Data
public class BusquedaLibroRequest {

    private List<OrderCriteria> listOrderCriteria;
    private List<SearchCriteria> listSearchCriteria;
    private PageCriteria page;


}



