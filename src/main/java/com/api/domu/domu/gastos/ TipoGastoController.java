package com.api.domu.domu.gastos;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@RestController
public class TipoGastoController {
    @GetMapping("/api/tipos-gasto")
    String hola() {
        return "HolaMundo";
    }
}