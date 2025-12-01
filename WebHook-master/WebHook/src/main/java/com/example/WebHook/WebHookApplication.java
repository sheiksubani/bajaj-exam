package com.example.WebHook;

import com.example.WebHook.Service.WebhookService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WebHookApplication implements CommandLineRunner {

    private final WebhookService webhookService;

    public WebHookApplication(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    public static void main(String[] args) {
        SpringApplication.run(WebHookApplication.class, args);
    }

    @Override
    public void run(String... args) {

        webhookService.executeFlow();
    }
}
