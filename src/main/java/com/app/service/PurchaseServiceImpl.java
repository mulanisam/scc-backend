package com.app.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.app.dto.DcDetailDTO;
import com.app.dto.PurchaseDTO;
import com.app.entity.DcDetail;
import com.app.entity.Driver;
import com.app.entity.Purchase;
import com.app.entity.Supplier;
import com.app.entity.Vehicle;
import com.app.repository.DriverRepository;
import com.app.repository.PurchaseRepository;
import com.app.repository.SupplierRepository;
import com.app.repository.VehicleRepository;

import io.jsonwebtoken.io.IOException;


@Service
public class PurchaseServiceImpl implements PurchaseService{

	@Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public Purchase createPurchase(PurchaseDTO purchaseDTO, List<MultipartFile> files) {
        Purchase purchase = new Purchase();
        BeanUtils.copyProperties(purchaseDTO, purchase);

        Vehicle vehicle = vehicleRepository.findById(purchaseDTO.getVehicle())
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));
        Driver driver = driverRepository.findById(purchaseDTO.getDriver())
                .orElseThrow(() -> new RuntimeException("Driver not found"));
        Supplier supplier = supplierRepository.findById(purchaseDTO.getSupplier())
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        purchase.setVehicle(vehicle);
        purchase.setDriver(driver);
        purchase.setSupplier(supplier);
        for (int i = 0; i < purchaseDTO.getDcDetails().size(); i++) {
            DcDetailDTO dcDetailDTO = purchaseDTO.getDcDetails().get(i);
            DcDetail dcDetail = new DcDetail();
            BeanUtils.copyProperties(dcDetailDTO, dcDetail);
            dcDetail.setPurchase(purchase);
            if (files.get(i) != null && !files.get(i).isEmpty()) {
                MultipartFile file = files.get(i);
                try {
                    String filePath = saveFile(file, dcDetailDTO.getDcNo(), supplier.getId().toString());
                    dcDetail.setFilePath(filePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            purchase.getDcDetails().add(dcDetail);
        }
        return purchaseRepository.save(purchase);
    }

    private String saveFile(MultipartFile file, String dcNo, String supplierId) throws IOException {
        String currentYear = String.valueOf(Year.now().getValue());
        String currentMonth = Month.of(LocalDate.now().getMonthValue()).name();
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("ddMMyyyy"));
        String fileExtension = file.getOriginalFilename() != null ? file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.')) : "";
        String fileName = String.format("DC-%s-%s-%s%s", dcNo, supplierId, datePart,fileExtension);

        Path fileStorageLocation = Paths.get(uploadDir).resolve(currentYear).resolve(currentMonth);
        try {
			Files.createDirectories(fileStorageLocation);
		} catch (java.io.IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        Path targetLocation = fileStorageLocation.resolve(fileName);
        try {
			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
			
			
		} catch (java.io.IOException e) {
		
			e.printStackTrace();
		}

        return targetLocation.toString();
    }
}