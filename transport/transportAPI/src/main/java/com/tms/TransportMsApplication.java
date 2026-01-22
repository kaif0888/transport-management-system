package com.tms;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.tms.JwtSecurity.entity.Role;
import com.tms.JwtSecurity.entity.User;
import com.tms.JwtSecurity.repository.UserRepository;
import com.tms.branch.entity.BranchEntity;
import com.tms.branch.repository.BranchRepository;
import com.tms.expenseType.entity.ExpenseTypeEntity;
import com.tms.expenseType.repository.ExpenseTypeRepository;
import com.tms.location.entity.LocationEntity;
import com.tms.location.repository.LocationRepository;
import com.tms.productcategory.entity.ProductCategoryEntity;
import com.tms.productcategory.repository.ProductCategoryRepository;
import com.tms.vehicletype.entity.VehicleTypeEntity;
import com.tms.vehicletype.repository.VehicleTypeRepository;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
@EnableScheduling
@EnableJpaRepositories(basePackages = "com.tms")
@EntityScan(basePackages = "com.tms")
public class TransportMsApplication implements CommandLineRunner, WebMvcConfigurer {

    @Autowired
    private UserRepository repo;
    @Autowired
    private VehicleTypeRepository vehicleTypeRepo;
    @Autowired
    private ExpenseTypeRepository expenseTypeRepo;
    @Autowired
    private ProductCategoryRepository productCategoryRepo;
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private BranchRepository branchRepository;

    public static void main(String[] args) {
        SpringApplication.run(TransportMsApplication.class, args);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("http://localhost:3000", "http://localhost:5000")
                .allowedMethods("POST", "GET", "PUT", "DELETE");
    }

    @Override
    public void run(String... args) throws Exception {
        User adminAcc = repo.findByRole(Role.ADMIN);
        if (adminAcc == null) {
            User user = new User();
            user.setUserId("ADMIN2025");
            user.setEmail("admin@gmail.com");
            user.setFirstName("admin");
            user.setSecondName("admin");
            user.setPassword(new BCryptPasswordEncoder().encode("admin"));
            user.setBranchIds("ADMIN2025");
            user.setRole(Role.ADMIN);
            repo.save(user);
        }

        String[][] vehicleTypes = {
                {"Truck", "Large vehicle for long-distance freight."},
                {"Mini Truck", "Compact vehicle for short deliveries."},
                {"Container", "Sealed container transport vehicle."},
                {"Trailer", "Trailer for heavy load hauling."},
                {"Pickup Van", "Light-duty vehicle with open bed."},
                {"Tanker", "Used for transporting liquids."},
                {"Refrigerated Truck", "For cold storage goods."},
                {"Flatbed Truck", "Flat platform for large items."},
                {"Tipper", "Dump truck for construction material."},
                {"Auto Rickshaw Cargo", "3-wheeler for last-mile delivery."}
        };

        for (int i = 0; i < vehicleTypes.length; i++) {
            if (!vehicleTypeRepo.existsByVehicleTypeName(vehicleTypes[i][0])) {
                VehicleTypeEntity entity = new VehicleTypeEntity();
                entity.setVehicleTypeId(generateId("TYPE", i + 1));
                entity.setVehicleTypeName(vehicleTypes[i][0]);
                entity.setDescription(vehicleTypes[i][1]);
                vehicleTypeRepo.save(entity);
            }
        }

        String[][] expenseTypes = {
                {"Fuel", "Expenses for diesel, petrol, or gas."},
                {"Maintenance", "Repair and service of vehicles."},
                {"Toll Charges", "Fees paid at toll booths."},
                {"Insurance", "Vehicle insurance costs."},
                {"Vehicle Wash", "Cleaning and washing services."},
                {"Permit Fees", "Charges for legal permits."},
                {"Tyre Replacement", "Cost of new tyres."},
                {"Battery Replacement", "Replacing vehicle batteries."},
                {"Oil Change", "Engine oil and filter replacement."},
                {"Miscellaneous", "Any other vehicle-related costs."}
        };

        for (int i = 0; i < expenseTypes.length; i++) {
            if (!expenseTypeRepo.existsByExpenseTypeName(expenseTypes[i][0])) {
                ExpenseTypeEntity entity = new ExpenseTypeEntity();
                entity.setExpenseTypeId(generateId("EXP", i + 1));
                entity.setExpenseTypeName(expenseTypes[i][0]);
                entity.setDescription(expenseTypes[i][1]);
                expenseTypeRepo.save(entity);
            }
        }

        String[][] productCategories = {
                {"Electronics", "Electronic goods like phones, TVs, etc."},
                {"Furniture", "Tables, chairs, sofas, and beds."},
                {"Clothing", "Apparel and fashion wear."},
                {"Food & Beverages", "Perishable and packaged food items."},
                {"Automotive Parts", "Spare parts for vehicles."},
                {"Building Materials", "Cement, bricks, pipes, etc."},
                {"Office Supplies", "Stationery, paper, printers."},
                {"Books", "Textbooks, novels, and publications."},
                {"Pharmaceuticals", "Medicines and medical equipment."},
                {"Home Appliances", "Washing machines, refrigerators, etc."}
        };

        for (int i = 0; i < productCategories.length; i++) {
            if (!productCategoryRepo.existsByCategoryName(productCategories[i][0])) {
                ProductCategoryEntity entity = new ProductCategoryEntity();
                entity.setCategoryId(generateId("CAT", i + 1));
                entity.setCategoryName(productCategories[i][0]);
                entity.setDescription(productCategories[i][1]);
                productCategoryRepo.save(entity);
            }
        }

        List<LocationEntity> locations = List.of(
                createLocation(generateId("LOC", 1), "ConnaughtPlace_Delhi (DL)-110001", "Connaught Place", "New Delhi", "Active", "North", "New Delhi", "Central", "Delhi", "India", "110001"),
                createLocation(generateId("LOC", 2), "MG_Road_Bangalore (KA)-560001", "MG Road", "Bangalore City", "Active", "South", "Bangalore", "East", "Karnataka", "India", "560001"),
                createLocation(generateId("LOC", 3), "Charminar_Hyderabad (TS)-500002", "Charminar", "Charminar Area", "Active", "South", "Hyderabad", "Old City", "Telangana", "India", "500002")
                // add more if needed
        );

        for (LocationEntity loc : locations) {
            if (!locationRepository.existsByLocationName(loc.getLocationName())) {
                locationRepository.save(loc);
            }
        }

        if (!branchRepository.existsByBranchName("Delhi Central")) {
            LocationEntity location = locationRepository.getByLocationId(locations.get(0).getLocationId());
            if (location != null) {
                BranchEntity branch = new BranchEntity();
                branch.setBranchId(generateId("BR", 1));
                branch.setBranchName("Delhi Central");
                branch.setLocation(location);
                branch.setContactInfo("011-1111-2222");
                branchRepository.save(branch);
            }
        }
    }

    private LocationEntity createLocation(String locationId, String locationName, String locationArea, String locationAddress,
                                          String status, String circle, String district, String block,
                                          String state, String country, String pincode) {
        LocationEntity loc = new LocationEntity();
        loc.setLocationId(locationId);
        loc.setLocationName(locationName);
        loc.setLocationArea(locationArea);
        loc.setLocationAddress(locationAddress);
        loc.setStatus(status);
        loc.setCircle(circle);
        loc.setDistrict(district);
        loc.setBlock(block);
        loc.setState(state);
        loc.setCountry(country);
        loc.setPincode(pincode);
        return loc;
    }

    private String generateId(String prefix, int index) {
        String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return prefix + currentDate + String.format("%03d", index);
    }
}
