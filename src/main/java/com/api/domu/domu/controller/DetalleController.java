package com.api.domu.domu.controller;

import com.api.domu.domu.dto.DetalleRequest;
import com.api.domu.domu.dto.DetalleResponse;
import com.api.domu.domu.service.DetalleService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/detalles")
public class DetalleController {

    private final DetalleService detalleService;

    public DetalleController(DetalleService detalleService) {
        this.detalleService = detalleService;
    }

    @GetMapping
    public List<DetalleResponse> listar(@RequestParam(required = false) Long cotizacionId) {
        System.out.println("*************");
        System.out.println("*************");
        System.out.println("aqui222");
        try{

            
            if (cotizacionId != null) {
                return detalleService.listarPorCotizacionId(cotizacionId);
            }
            return detalleService.listar();
        } catch (Exception  e) {
            System.out.println("-----------ERROR 33");
            e.printStackTrace();
            return detalleService.listar();

        }

    }

    @GetMapping("/{id}")
    public DetalleResponse obtenerPorId(@PathVariable Long id) {
        return detalleService.obtenerPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DetalleResponse crear(@RequestBody DetalleRequest request) {
        return detalleService.crear(request);
    }

    @PutMapping("/{id}")
    public DetalleResponse actualizar(@PathVariable Long id, @RequestBody DetalleRequest request) {
        return detalleService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        detalleService.eliminar(id);
    }
}
