package com.app.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.app.dto.PurchaseDTO;
import com.app.entity.Purchase;

public interface PurchaseService {

	public Purchase createPurchase(PurchaseDTO purchaseDTO, List<MultipartFile> files);
}
