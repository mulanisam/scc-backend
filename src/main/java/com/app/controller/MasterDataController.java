package com.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.app.dto.CityDTO;
import com.app.dto.CustomerDTO;
import com.app.entity.City;
import com.app.entity.Customer;
import com.app.entity.Driver;
import com.app.entity.Route;
import com.app.entity.Supplier;
import com.app.entity.Vehicle;
import com.app.repository.CityRepository;
import com.app.repository.CustomerRepository;
import com.app.repository.DriverRepository;
import com.app.repository.RouteRepository;
import com.app.repository.SupplierRepository;
import com.app.repository.VehicleRepository;

import cutsomException.ResourceNotFoundException;

@RestController
//@RequestMapping("/api/masterdata")
public class MasterDataController {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private CityRepository cityRepository;
    
    @Autowired
    private VehicleRepository vehicleRepository;
    

    // Customer Endpoints

    @GetMapping("/user/customers")
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @PostMapping("/user/customers")
    public ResponseEntity<Customer> createCustomer(@RequestBody CustomerDTO customerDto) {
    	 Customer customer = new Customer();
    	    customer.setName(customerDto.getName());
    	    customer.setAddress(customerDto.getAddress());
    	    customer.setMobileNo(customerDto.getMobileNo());
    	    customer.setShopName(customerDto.getShopName());
    	    customer.setBalanceAmount(Double.parseDouble(customerDto.getBalanceAmount()));
    	    customer.setObsolete(customerDto.isObsolete());
    	    City city = cityRepository.findById(customerDto.getCity())
    	            .orElseThrow(() -> new RuntimeException("City not found"));
    	    customer.setCity(city);
    	    
    	    Customer savedCustomer = customerRepository.save(customer);
    	    return ResponseEntity.ok(savedCustomer);
    }

    @GetMapping("/user/customers/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id " + id));
        return ResponseEntity.ok(customer);
    }

    @PutMapping("/user/customers/{id}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable Long id, @RequestBody CustomerDTO customerDto) {
		Customer customer = customerRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Customer not found with id " + id));

		customer.setName(customerDto.getName());
		customer.setAddress(customerDto.getAddress());
		customer.setMobileNo(customerDto.getMobileNo());
		customer.setShopName(customerDto.getShopName());
		customer.setBalanceAmount(Double.parseDouble(customerDto.getBalanceAmount()));
		customer.setObsolete(customerDto.isObsolete());

		City city = cityRepository.findById(customerDto.getCity())
				.orElseThrow(() -> new RuntimeException("City not found"));
		customer.setCity(city);

		Customer updatedCustomer = customerRepository.save(customer);
		return ResponseEntity.ok(updatedCustomer);
    }

    @DeleteMapping("/user/customers/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id " + id));

        customerRepository.delete(customer);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/user/customers/byRoute/{routeId}")
    public List<Customer> getCustomersByRoute(@PathVariable Long routeId) {
    	List<Customer> customerList = customerRepository.findByRouteId(routeId)
                .orElseThrow(() -> new ResourceNotFoundException("Customers not found with Route id " + routeId));
        return customerList;
    }


    // Supplier Endpoints

    @GetMapping("/user/suppliers")
    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    @PostMapping("/user/suppliers")
    public Supplier createSupplier(@RequestBody Supplier supplier) {
        return supplierRepository.save(supplier);
    }

    @GetMapping("/user/suppliers/{id}")
    public ResponseEntity<Supplier> getSupplierById(@PathVariable Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id " + id));
        return ResponseEntity.ok(supplier);
    }

    @PutMapping("/user/suppliers/{id}")
    public ResponseEntity<Supplier> updateSupplier(@PathVariable Long id, @RequestBody Supplier supplierDetails) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id " + id));

        supplier.setName(supplierDetails.getName());
        supplier.setBranch(supplierDetails.getBranch());
        supplier.setObsolete(supplierDetails.isObsolete());
        // Update other fields as necessary

        Supplier updatedSupplier = supplierRepository.save(supplier);
        return ResponseEntity.ok(updatedSupplier);
    }

    @DeleteMapping("/user/suppliers/{id}")
    public ResponseEntity<Void> deleteSupplier(@PathVariable Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id " + id));

        supplierRepository.delete(supplier);
        return ResponseEntity.noContent().build();
    }

    // Driver Endpoints

    @GetMapping("/user/drivers")
    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    @PostMapping("/user/drivers")
    public Driver createDriver(@RequestBody Driver driver) {
        return driverRepository.save(driver);
    }

    @GetMapping("/user/drivers/{id}")
    public ResponseEntity<Driver> getDriverById(@PathVariable Long id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id " + id));
        return ResponseEntity.ok(driver);
    }

    @PutMapping("/user/drivers/{id}")
    public ResponseEntity<Driver> updateDriver(@PathVariable Long id, @RequestBody Driver driverDetails) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id " + id));

        driver.setName(driverDetails.getName());
        driver.setMobileNo(driverDetails.getMobileNo());
        driver.setAddress(driverDetails.getAddress());
        // Update other fields as necessary

        Driver updatedDriver = driverRepository.save(driver);
        return ResponseEntity.ok(updatedDriver);
    }

    @DeleteMapping("/user/drivers/{id}")
    public ResponseEntity<Void> deleteDriver(@PathVariable Long id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id " + id));

        driverRepository.delete(driver);
        return ResponseEntity.noContent().build();
    }

    // Route Endpoints

    @GetMapping("/user/routes")
    public List<Route> getAllRoutes() {
        return routeRepository.findAll();
    }

    @PostMapping("/user/routes")
    public Route createRoute(@RequestBody Route route) {
        return routeRepository.save(route);
    }

    @GetMapping("/user/routes/{id}")
    public ResponseEntity<Route> getRouteById(@PathVariable Long id) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found with id " + id));
        return ResponseEntity.ok(route);
    }

    @PutMapping("/user/routes/{id}")
    public ResponseEntity<Route> updateRoute(@PathVariable Long id, @RequestBody Route routeDetails) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found with id " + id));

        route.setName(routeDetails.getName());
        // Update other fields as necessary

        Route updatedRoute = routeRepository.save(route);
        return ResponseEntity.ok(updatedRoute);
    }

    @DeleteMapping("/user/routes/{id}")
    public ResponseEntity<Void> deleteRoute(@PathVariable Long id) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found with id " + id));

        routeRepository.delete(route);
        return ResponseEntity.noContent().build();
    }

    // City Endpoints

    @GetMapping("/user/cities")
    public List<City> getAllCities() {
        return cityRepository.findAll();
    }

    @PostMapping("/user/cities")
    public City createCity(@RequestBody CityDTO cityDto) {
    	City city = new City();
    	city.setName(cityDto.getName());
    	city.setObsolete(cityDto.isObsolete());
    	Route route = routeRepository.findById(cityDto.getRoute())
				.orElseThrow(() -> new RuntimeException("Route not found"));
    	city.setRoute(route);
        return cityRepository.save(city);
    }

    @GetMapping("/user/cities/{id}")
    public ResponseEntity<City> getCityById(@PathVariable Long id) {
        City city = cityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("City not found with id " + id));
        return ResponseEntity.ok(city);
    }

    @PutMapping("/user/cities/{id}")
    public ResponseEntity<City> updateCity(@PathVariable Long id, @RequestBody CityDTO cityDetails) {
        City city = cityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("City not found with id " + id));

        city.setName(cityDetails.getName());
        Route route = routeRepository.findById(cityDetails.getRoute())
				.orElseThrow(() -> new RuntimeException("Route not found"));
        city.setRoute(route);
        // Update other fields as necessary

        City updatedCity = cityRepository.save(city);
        return ResponseEntity.ok(updatedCity);
    }

    @DeleteMapping("/user/cities/{id}")
    public ResponseEntity<Void> deleteCity(@PathVariable Long id) {
        City city = cityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("City not found with id " + id));

        cityRepository.delete(city);
        return ResponseEntity.noContent().build();
    }
    
    // vehicle end points
    
    @GetMapping("/user/vehicles")
    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    @PostMapping("/user/vehicles")
    public Vehicle createVehicle(@RequestBody Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    @GetMapping("/user/vehicles/{id}")
    public ResponseEntity<Vehicle> getVehicleById(@PathVariable Long id) {
    	Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("vehicle not found with id " + id));
        return ResponseEntity.ok(vehicle);
    }

    @PutMapping("/user/vehicles/{id}")
    public ResponseEntity<Vehicle> updateVehicle(@PathVariable Long id, @RequestBody Vehicle vehicleDetails) {
    	Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id " + id));

    	vehicle.setVehicleNo(vehicleDetails.getVehicleNo());
    	vehicle.setModel(vehicleDetails.getModel());
    	vehicle.setPassingDate(vehicleDetails.getPassingDate());
    	vehicle.setFitnessDate(vehicleDetails.getFitnessDate());
    	vehicle.setInsuranceDate(vehicleDetails.getInsuranceDate());
    	vehicle.setObsolete(vehicleDetails.isObsolete());

    	Vehicle updatedVehicle = vehicleRepository.save(vehicle);
        return ResponseEntity.ok(updatedVehicle);
    }

    @DeleteMapping("/user/vehicles/{id}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id " + id));

        vehicleRepository.delete(vehicle);
        return ResponseEntity.noContent().build();
    }
}
