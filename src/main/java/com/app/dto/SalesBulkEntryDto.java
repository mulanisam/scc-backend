package com.app.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SalesBulkEntryDto {

	//private Long id;
    private LocalDate date;
    private Long vehicleId;
    private Long route;
    private Long driver;
    private List<Map<String, Object>> salesDetails;
}
