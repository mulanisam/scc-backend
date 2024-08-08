package com.app.dto;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class ReportResponseDTO {

	private String errorMessage;
    private List<Map<String, Object>> data;
}
