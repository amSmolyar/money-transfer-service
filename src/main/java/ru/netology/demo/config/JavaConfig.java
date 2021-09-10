package ru.netology.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.netology.demo.repository.CardRepository;
import ru.netology.demo.service.TransferService;

@Configuration
public class JavaConfig {

    @Bean
    public CardRepository repository() {
        return new CardRepository();
    }

    @Bean
    public TransferService service() {
        return new TransferService(repository());
    }

}
