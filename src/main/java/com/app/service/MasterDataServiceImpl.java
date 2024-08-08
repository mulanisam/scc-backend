package com.app.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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


@Service
@Transactional
public class MasterDataServiceImpl implements MasterDataService {

    private static final Logger logger = LoggerFactory.getLogger(MasterDataServiceImpl.class);

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

    @Override
    public List<Customer> getAllCustomers() {
        logger.info("Entering getAllCustomers method");
        List<Customer> customers = customerRepository.findAll();
        logger.info("Retrieved {} customers", customers.size());
        logger.info("Exiting getAllCustomers method");
        return customers;
    }

    @Override
    public Customer createCustomer(CustomerDTO customerDto) {
        logger.info("Entering createCustomer method with DTO: {}", customerDto);
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
        logger.info("Created customer with ID: {}", savedCustomer.getId());
        logger.info("Exiting createCustomer method");
        return savedCustomer;
    }

    @Override
    public Customer getCustomerById(Long id) {
        logger.info("Entering getCustomerById method with ID: {}", id);
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id " + id));
        logger.info("Retrieved customer: {}", customer);
        logger.info("Exiting getCustomerById method");
        return customer;
    }

    @Override
    public Customer updateCustomer(Long id, CustomerDTO customerDto) {
        logger.info("Entering updateCustomer method with ID: {} and DTO: {}", id, customerDto);
        Customer customer = getCustomerById(id);
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
        logger.info("Updated customer with ID: {}", updatedCustomer.getId());
        logger.info("Exiting updateCustomer method");
        return updatedCustomer;
    }

    @Override
    public void deleteCustomer(Long id) {
        logger.info("Entering deleteCustomer method with ID: {}", id);
        Customer customer = getCustomerById(id);
        customerRepository.delete(customer);
        logger.info("Deleted customer with ID: {}", id);
        logger.info("Exiting deleteCustomer method");
    }

    @Override
    public List<Customer> getCustomersByRoute(Long routeId) {
        logger.info("Entering getCustomersByRoute method with Route ID: {}", routeId);
        List<Customer> customerList = customerRepository.findByRouteId(routeId)
                .orElseThrow(() -> new ResourceNotFoundException("Customers not found with Route id " + routeId));
        logger.info("Retrieved {} customers for Route ID: {}", customerList.size(), routeId);
        logger.info("Exiting getCustomersByRoute method");
        return customerList;
    }

    @Override
    public Supplier createSupplier(Supplier supplier) {
        logger.info("Entering createSupplier method with Supplier: {}", supplier);
        Supplier savedSupplier = supplierRepository.save(supplier);
        logger.info("Created supplier with ID: {}", savedSupplier.getId());
        logger.info("Exiting createSupplier method");
        return savedSupplier;
    }

    @Override
    public Supplier getSupplierById(Long id) {
        logger.info("Entering getSupplierById method with ID: {}", id);
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id " + id));
        logger.info("Retrieved supplier: {}", supplier);
        logger.info("Exiting getSupplierById method");
        return supplier;
    }

    @Override
    public Supplier updateSupplier(Long id, Supplier supplierDetails) {
        logger.info("Entering updateSupplier method with ID: {} and Supplier details: {}", id, supplierDetails);
        Supplier supplier = getSupplierById(id);
        supplier.setName(supplierDetails.getName());
        supplier.setBranch(supplierDetails.getBranch());
        supplier.setObsolete(supplierDetails.isObsolete());
        supplier.setPendingPayment(supplierDetails.getPendingPayment());
        // Update other fields as necessary
        Supplier updatedSupplier = supplierRepository.save(supplier);
        logger.info("Updated supplier with ID: {}", updatedSupplier.getId());
        logger.info("Exiting updateSupplier method");
        return updatedSupplier;
    }

    @Override
    public void deleteSupplier(Long id) {
        logger.info("Entering deleteSupplier method with ID: {}", id);
        Supplier supplier = getSupplierById(id);
        supplierRepository.delete(supplier);
        logger.info("Deleted supplier with ID: {}", id);
        logger.info("Exiting deleteSupplier method");
    }

    @Override
    public List<Driver> getAllDrivers() {
        logger.info("Entering getAllDrivers method");
        List<Driver> drivers = driverRepository.findAll();
        logger.info("Retrieved {} drivers", drivers.size());
        logger.info("Exiting getAllDrivers method");
        return drivers;
    }

    @Override
    public Driver createDriver(Driver driver) {
        logger.info("Entering createDriver method with Driver: {}", driver);
        Driver savedDriver = driverRepository.save(driver);
        logger.info("Created driver with ID: {}", savedDriver.getId());
        logger.info("Exiting createDriver method");
        return savedDriver;
    }

    @Override
    public Driver getDriverById(Long id) {
        logger.info("Entering getDriverById method with ID: {}", id);
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id " + id));
        logger.info("Retrieved driver: {}", driver);
        logger.info("Exiting getDriverById method");
        return driver;
    }

    @Override
    public Driver updateDriver(Long id, Driver driverDetails) {
        logger.info("Entering updateDriver method with ID: {} and Driver details: {}", id, driverDetails);
        Driver driver = getDriverById(id);
        driver.setName(driverDetails.getName());
        driver.setMobileNo(driverDetails.getMobileNo());
        driver.setAddress(driverDetails.getAddress());
        // Update other fields as necessary
        Driver updatedDriver = driverRepository.save(driver);
        logger.info("Updated driver with ID: {}", updatedDriver.getId());
        logger.info("Exiting updateDriver method");
        return updatedDriver;
    }

    @Override
    public void deleteDriver(Long id) {
        logger.info("Entering deleteDriver method with ID: {}", id);
        Driver driver = getDriverById(id);
        driverRepository.delete(driver);
        logger.info("Deleted driver with ID: {}", id);
        logger.info("Exiting deleteDriver method");
    }

    @Override
    public List<Route> getAllRoutes() {
        logger.info("Entering getAllRoutes method");
        List<Route> routes = routeRepository.findAll();
        logger.info("Retrieved {} routes", routes.size());
        logger.info("Exiting getAllRoutes method");
        return routes;
    }

    @Override
    public Route createRoute(Route route) {
        logger.info("Entering createRoute method with Route: {}", route);
        Route savedRoute = routeRepository.save(route);
        logger.info("Created route with ID: {}", savedRoute.getId());
        logger.info("Exiting createRoute method");
        return savedRoute;
    }

    @Override
    public Route getRouteById(Long id) {
        logger.info("Entering getRouteById method with ID: {}", id);
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found with id " + id));
        logger.info("Retrieved route: {}", route);
        logger.info("Exiting getRouteById method");
        return route;
    }

    @Override
    public Route updateRoute(Long id, Route routeDetails) {
        logger.info("Entering updateRoute method with ID: {} and Route details: {}", id, routeDetails);
        Route route = getRouteById(id);
        route.setName(routeDetails.getName());
        // Update other fields as necessary
        Route updatedRoute = routeRepository.save(route);
        logger.info("Updated route with ID: {}", updatedRoute.getId());
        logger.info("Exiting updateRoute method");
        return updatedRoute;
    }

    @Override
    public void deleteRoute(Long id) {
        logger.info("Entering deleteRoute method with ID: {}", id);
        Route route = getRouteById(id);
        routeRepository.delete(route);
        logger.info("Deleted route with ID: {}", id);
        logger.info("Exiting deleteRoute method");
    }

    @Override
    public List<City> getAllCities() {
        logger.info("Entering getAllCities method");
        List<City> cities = cityRepository.findAll();
        logger.info("Retrieved {} cities", cities.size());
        logger.info("Exiting getAllCities method");
        return cities;
    }

    @Override
    public City createCity(CityDTO cityDto) {
        logger.info("Entering createCity method with DTO: {}", cityDto);
        City city = new City();
        city.setName(cityDto.getName());
        city.setObsolete(cityDto.isObsolete());
        Route route = routeRepository.findById(cityDto.getRoute())
                .orElseThrow(() -> new RuntimeException("Route not found"));
        city.setRoute(route);
        City savedCity = cityRepository.save(city);
        logger.info("Created city with ID: {}", savedCity.getId());
        logger.info("Exiting createCity method");
        return savedCity;
    }

    @Override
    public City getCityById(Long id) {
        logger.info("Entering getCityById method with ID: {}", id);
        City city = cityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("City not found with id " + id));
        logger.info("Retrieved city: {}", city);
        logger.info("Exiting getCityById method");
        return city;
    }

    @Override
    public City updateCity(Long id, CityDTO cityDetails) {
        logger.info("Entering updateCity method with ID: {} and DTO: {}", id, cityDetails);
        City city = getCityById(id);
        city.setName(cityDetails.getName());
        Route route = routeRepository.findById(cityDetails.getRoute())
                .orElseThrow(() -> new RuntimeException("Route not found"));
        city.setRoute(route);
        // Update other fields as necessary
        City updatedCity = cityRepository.save(city);
        logger.info("Updated city with ID: {}", updatedCity.getId());
        logger.info("Exiting updateCity method");
        return updatedCity;
    }

    @Override
    public void deleteCity(Long id) {
        logger.info("Entering deleteCity method with ID: {}", id);
        City city = getCityById(id);
        cityRepository.delete(city);
        logger.info("Deleted city with ID: {}", id);
        logger.info("Exiting deleteCity method");
    }

    @Override
    public List<Vehicle> getAllVehicles() {
        logger.info("Entering getAllVehicles method");
        List<Vehicle> vehicles = vehicleRepository.findAll();
        logger.info("Retrieved {} vehicles", vehicles.size());
        logger.info("Exiting getAllVehicles method");
        return vehicles;
    }

    @Override
    public Vehicle createVehicle(Vehicle vehicle) {
        logger.info("Entering createVehicle method with Vehicle: {}", vehicle);
        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        logger.info("Created vehicle with ID: {}", savedVehicle.getId());
        logger.info("Exiting createVehicle method");
        return savedVehicle;
    }

    @Override
    public Vehicle getVehicleById(Long id) {
        logger.info("Entering getVehicleById method with ID: {}", id);
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id " + id));
        logger.info("Retrieved vehicle: {}", vehicle);
        logger.info("Exiting getVehicleById method");
        return vehicle;
    }

    @Override
    public Vehicle updateVehicle(Long id, Vehicle vehicleDetails) {
        logger.info("Entering updateVehicle method with ID: {} and Vehicle details: {}", id, vehicleDetails);
        Vehicle vehicle = getVehicleById(id);
        vehicle.setVehicleNo(vehicleDetails.getVehicleNo());
        vehicle.setModel(vehicleDetails.getModel());
        vehicle.setPassingDate(vehicleDetails.getPassingDate());
        vehicle.setFitnessDate(vehicleDetails.getFitnessDate());
        vehicle.setInsuranceDate(vehicleDetails.getInsuranceDate());
        vehicle.setObsolete(vehicleDetails.isObsolete());
        // Update other fields as necessary
        Vehicle updatedVehicle = vehicleRepository.save(vehicle);
        logger.info("Updated vehicle with ID: {}", updatedVehicle.getId());
        logger.info("Exiting updateVehicle method");
        return updatedVehicle;
    }

    @Override
    public void deleteVehicle(Long id) {
        logger.info("Entering deleteVehicle method with ID: {}", id);
        Vehicle vehicle = getVehicleById(id);
        vehicleRepository.delete(vehicle);
        logger.info("Deleted vehicle with ID: {}", id);
        logger.info("Exiting deleteVehicle method");
    }

	@Override
	public List<Supplier> getAllSuppliers() {
        logger.info("Entering getAllSuppliers method");
        List<Supplier> suppliers = supplierRepository.findAll();
        logger.info("Retrieved {} customers", suppliers.size());
        logger.info("Exiting getAllCustomers method");
        return suppliers;
	}

}
