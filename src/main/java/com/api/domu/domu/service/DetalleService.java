package com.api.domu.domu.service;

import com.api.domu.domu.dto.DetalleRequest;
import com.api.domu.domu.dto.DetalleResponse;

import java.util.List;

public interface DetalleService {

    List<DetalleResponse> listar();

    DetalleResponse obtenerPorId(Long id);

    List<DetalleResponse> listarPorCotizacionId(Long cotizacionId);

    DetalleResponse crear(DetalleRequest request);

    DetalleResponse actualizar(Long id, DetalleRequest request);

    void eliminar(Long id);
}
