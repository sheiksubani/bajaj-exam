package com.example.WebHook.Service;

import com.example.WebHook.DTO.Request;
import com.example.WebHook.DTO.Response;
import com.example.WebHook.DTO.SubmitRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WebhookService {

    private static final Logger log = LoggerFactory.getLogger(WebhookService.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String GENERATE_WEBHOOK_URL =
            "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

    public WebhookService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void executeFlow() {

        Response response = callGenerateWebhook();

        if (response == null) {
            log.error("No response received");
            return;
        }

        String webhookUrl = response.getWebhook();
        String token = response.getAccessToken();

        log.info("WEBHOOK URL: {}", webhookUrl);
        log.info("ACCESS TOKEN: {}", token);

        if (webhookUrl == null || token == null) {
            log.error("Webhook or Token missing in response");
            return;
        }

        String finalSql = buildFinalSqlQuery();

        submitToWebhook(webhookUrl, token, finalSql);
    }

    private Response callGenerateWebhook() {
        try {
            Request body = new Request(
                    "sheik Mahaboob Subani",
                    "22BCE9845",
                    "subanisheikmahaboob@gmail.com"
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Request> entity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    GENERATE_WEBHOOK_URL,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            log.info("RAW RESPONSE: {}", response.getBody());

            return objectMapper.readValue(response.getBody(), Response.class);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String buildFinalSqlQuery() {

        return """
               SELECT 
                   d.DEPARTMENT_NAME,
                   s.TOTAL_SALARY AS SALARY,
                   CONCAT(e.FIRST_NAME, ' ', e.LAST_NAME) AS EMPLOYEE_NAME,
                   TIMESTAMPDIFF(YEAR, e.DOB, CURDATE()) AS AGE
               FROM (
                   SELECT 
                       e.DEPARTMENT,
                       p.EMP_ID,
                       SUM(p.AMOUNT) AS TOTAL_SALARY,
                       ROW_NUMBER() OVER (
                           PARTITION BY e.DEPARTMENT
                           ORDER BY SUM(p.AMOUNT) DESC
                       ) AS RN
                   FROM PAYMENTS p
                   JOIN EMPLOYEE e ON p.EMP_ID = e.EMP_ID
                   WHERE DAY(p.PAYMENT_TIME) <> 1
                   GROUP BY e.DEPARTMENT, p.EMP_ID
               ) s
               JOIN EMPLOYEE e ON s.EMP_ID = e.EMP_ID
               JOIN DEPARTMENT d ON s.DEPARTMENT = d.DEPARTMENT_ID
               WHERE s.RN = 1;
               """;
    }

    private void submitToWebhook(String url, String token, String query) {
        try {
            SubmitRequest body = new SubmitRequest(query);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", token);   // JWT added here

            HttpEntity<SubmitRequest> entity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            log.info("✅ SUBMISSION STATUS: {}", response.getStatusCode());
            log.info("✅ RESPONSE BODY: {}", response.getBody());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

