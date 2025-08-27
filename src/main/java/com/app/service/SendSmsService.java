package com.app.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@EnableRetry
public class SendSmsService {

    private static final Logger logger = LoggerFactory.getLogger(SendSmsService.class);
    private static final String API_KEY = "3knDPbxuECVNp4M1LfAeqdzwighW27SaRHyOjQBvlUXKtcYZsGVhPMba5omHg2qFBOX6SRQyUjzwNr1L"; // Replace with your real Fast2SMS key
    private static final String MSG_ID = "195555";
    private static final String SENDER_ID = "SHLCKN";

    @Async
    @Retryable(
        value = { IOException.class },
        maxAttempts = 3,
        backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    public void sendSms(String customerName, String number,double balAmount,LocalDate date) throws IOException {
        try {
            //String encodedMessage = URLEncoder.encode(message, "UTF-8");
        	
        	// Get today's date dynamically
        	//String todayDate = new SimpleDateFormat("dd-MMM-yyyy").format(date);  
        	// Example → "21-Aug-2025"
        	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
        	String formattedDate = date.format(formatter);
        	// Build variables_values
        	String variablesValues = String.format("%s|%s|%s", customerName,formattedDate, balAmount);

        	// Encode for URL safety
        	String encodedValues = URLEncoder.encode(variablesValues, StandardCharsets.UTF_8);

        	String urlStr = String.format(
        	    "https://www.fast2sms.com/dev/bulkV2?authorization=%s&route=dlt&sender_id=%s&message=%s&variables_values=%s&flash=0&numbers=%s",
        	    API_KEY, SENDER_ID, MSG_ID, encodedValues, number
        	);

            //https://www.fast2sms.com/dev/bulkV2?authorization=YOUR_API_KEY&sender_id=DLT_SENDER_ID&message=YOUR_MESSAGE_ID&variables_values=12345|asdaswdx&route=dlt&numbers=9999999999,8888888888,7777777777"
            URL url = new URL(urlStr);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            con.setRequestProperty("cache-control", "no-cache");

            int responseCode = con.getResponseCode();
            logger.info("SMS Response Code: {}", responseCode);

            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                response.append(line);
            }

            logger.info("SMS sent to {}. Response: {}", number, response.toString());
        } catch (IOException e) {
            logger.warn("IOException during SMS to {}: {}", number, e.getMessage());
            throw e; // Important for retry to work
        } catch (Exception e) {
            logger.error("Unexpected error sending SMS to {}: {}", number, e.getMessage());
        }
    }

    // Called after all retry attempts fail
    @Recover
    public void recover(IOException e, String message, String number) {
        logger.error("❌ SMS failed permanently to {}. Message: {}. Error: {}", number, message, e.getMessage());
        // TODO: Optionally save to DB or alert admin
    }
}
