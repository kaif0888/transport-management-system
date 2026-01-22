package com.tms.driver.scheduler;

import com.tms.driver.entity.DriverEntity;
import com.tms.driver.repository.DriverRepository;
import com.tms.sms.service.SmsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class DriverLicenseExpiryScheduler {

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private SmsService smsService;


    @Scheduled(cron = "0 0 9 * * ?") // Runs every day at 9 AM
    public void checkLicenseExpiry() {
        LocalDate today = LocalDate.now();
        List<DriverEntity> drivers = driverRepository.findAll();

        for (DriverEntity driver : drivers) {
            LocalDate expiryDate = driver.getLicenseExpiry();
            String contact = driver.getContactNumber();

            if (expiryDate == null || contact == null) continue;

            long daysToExpiry = java.time.temporal.ChronoUnit.DAYS.between(today, expiryDate);

            if (daysToExpiry == 30) {
            	sendSmsToDriver(driver, "Reminder: Your license will expire in 30 days on " + expiryDate + ".");
            } else if (daysToExpiry == 15) {
            	sendSmsToDriver(driver, "Reminder: Your license will expire in 15 days on " + expiryDate + ".");
            } else if (daysToExpiry == 10) {
            	sendSmsToDriver(driver, "Reminder: Your license will expire in 10 days on " + expiryDate + ".");
            } else if (daysToExpiry <= 5 && daysToExpiry > 0) {
            	sendSmsToDriver(driver, "Urgent: Only " + daysToExpiry + " day(s) left until your license expires on " + expiryDate + ".");
            } else if (daysToExpiry == 0) {
            	sendSmsToDriver(driver, "Important: Your license expires today (" + expiryDate + "). Please renew immediately.");
            }
        }
    }


    private void sendSmsToDriver(DriverEntity driver, String message) {
        try {
            smsService.sendSms(driver.getContactNumber(), message);
            System.out.println("SMS sent to " + driver.getName() + ": " + message);
        } catch (Exception e) {
            System.err.println("Failed to send SMS to " + driver.getName() + ": " + e.getMessage());
        }
    }
}
