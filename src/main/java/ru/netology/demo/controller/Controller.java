package ru.netology.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.netology.demo.card.Card;
import ru.netology.demo.requestObjects.TransferParameters;
import ru.netology.demo.service.Service;

import javax.validation.Valid;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@RestController
@Validated
public class Controller {
    private Service service;

    public Controller(Service service) {
        this.service = service;
    }

    @PostMapping("/transfer")
    public ResponseEntity<Card> getFromCard(@RequestBody TransferParameters parameters) {
        Card card = service.getFromCard(parameters);
        return ResponseEntity.ok(card);
    }

    //@PostMapping("/transfer")
    //public Card getFromCard(@Valid @TransferDeserializer TransferParameters parameters) {
     //   return service.getFromCard(parameters);
    //}


    //@Retention(RetentionPolicy.RUNTIME)
   // @Target(ElementType.PARAMETER)
    //public @interface TransferDeserializer {
//
    //}
}
