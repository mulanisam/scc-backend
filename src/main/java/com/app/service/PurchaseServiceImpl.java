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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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


@Service
@Transactional
public class PurchaseServiceImpl implements PurchaseService {

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

    private static final Logger logger = LoggerFactory.getLogger(PurchaseServiceImpl.class);

    public Purchase createPurchase(PurchaseDTO purchaseDTO, List<MultipartFile> files) {
        logger.info("Creating purchase with DTO: {}", purchaseDTO);
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
                } catch ( java.io.IOException e) {
                    logger.error("Failed to save file for DC No: {}", dcDetailDTO.getDcNo(), e);
                }
            }

            purchase.getDcDetails().add(dcDetail);
        }
        Purchase savedPurchase = purchaseRepository.save(purchase);
        logger.info("Purchase created successfully with ID: {}", savedPurchase.getId());
        return savedPurchase;
    }

    private String saveFile(MultipartFile file, String dcNo, String supplierId) throws java.io.IOException {
        logger.info("Saving file for DC No: {} and Supplier ID: {}", dcNo, supplierId);
        String currentYear = String.valueOf(Year.now().getValue());
        String currentMonth = Month.of(LocalDate.now().getMonthValue()).name();
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("ddMMyyyy"));
        String fileExtension = file.getOriginalFilename() != null ? file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.')) : "";
        String fileName = String.format("DC-%s-%s-%s%s", dcNo, supplierId, datePart, fileExtension);

        Path fileStorageLocation = Paths.get(uploadDir).resolve(currentYear).resolve(currentMonth);
        Files.createDirectories(fileStorageLocation);

        Path targetLocation = fileStorageLocation.resolve(fileName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        logger.info("File saved at: {}", targetLocation.toString());
        return targetLocation.toString();
    }
}
