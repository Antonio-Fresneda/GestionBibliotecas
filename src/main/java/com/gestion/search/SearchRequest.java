package com.gestion.search;


import lombok.Data;
import org.springframework.data.domain.PageRequest;

import java.util.List;

@Data
public class SearchRequest {


    private List<OrderCriteria> listOrderCriteria;
    private List<SearchCriteria> listSearchCriteria;
    private PageRequest page;


}




