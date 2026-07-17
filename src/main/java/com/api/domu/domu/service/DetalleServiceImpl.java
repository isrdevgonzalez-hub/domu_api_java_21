package com.api.domu.domu.service;

import com.api.domu.domu.dto.DetalleRequest;
import com.api.domu.domu.dto.DetalleResponse;
import com.api.domu.domu.entity.Detalle;
import com.api.domu.domu.exception.ResourceNotFoundException;
import com.api.domu.domu.repository.DetalleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.math.BigDecimal;

@Service
public class DetalleServiceImpl implements DetalleService {

    private final DetalleRepository detalleRepository;

    public DetalleServiceImpl(DetalleRepository detalleRepository) {
        this.detalleRepository = detalleRepository;
    }

    @Override
    public List<DetalleResponse> listar() {
        return detalleRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public DetalleResponse obtenerPorId(Long id) {
        return toResponse(buscarEntidad(id));
    }

    @Override
    public List<DetalleResponse> listarPorCotizacionId(Long cotizacionId) {
        return detalleRepository.findByCotizacionId(cotizacionId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public DetalleResponse crear(DetalleRequest request) {
        Detalle detalle = new Detalle();
        copiarRequestAEntidad(request, detalle);
        return toResponse(detalleRepository.save(detalle));
    }

    @Override
    public DetalleResponse actualizar(Long id, DetalleRequest request) {
        Detalle detalle = buscarEntidad(id);
        copiarRequestAEntidad(request, detalle);
        return toResponse(detalleRepository.save(detalle));
    }

    @Override
    public void eliminar(Long id) {
        Detalle detalle = buscarEntidad(id);
        detalleRepository.delete(detalle);
    }

    private Detalle buscarEntidad(Long id) {
        return detalleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe detalle con id " + id));
    }

    private void copiarRequestAEntidad(DetalleRequest request, Detalle detalle) {
        detalle.setCotizacionId(request.getCotizacionId());
        detalle.setItem(request.getItem());
        detalle.setDescripcion(request.getDescripcion());
        detalle.setCantidad(request.getCantidad() != null ? request.getCantidad() : BigDecimal.ZERO);
        detalle.setValorUnitario(request.getValorUnitario() != null ? request.getValorUnitario() : BigDecimal.ZERO);
        detalle.setDescuento(request.getDescuento() != null ? request.getDescuento() : BigDecimal.ZERO);

        detalle.setTotal(calcularTotal(
                request.getCantidad(),
                request.getValorUnitario(),
                request.getDescuento()
                ));
    }

    private DetalleResponse toResponse(Detalle detalle) {
        DetalleResponse response = new DetalleResponse();
        response.setId(detalle.getId());
        response.setCotizacionId(detalle.getCotizacionId());
        response.setItem(detalle.getItem());
        response.setDescripcion(detalle.getDescripcion());
        response.setCantidad(detalle.getCantidad());
        response.setValorUnitario(detalle.getValorUnitario());
        response.setDescuento(detalle.getDescuento());

        response.setTotal(detalle.getTotal());
        return response;
    }

    private BigDecimal calcularTotal(BigDecimal cantidad, BigDecimal valorUnitario, BigDecimal descuento) {
        BigDecimal cantidadSegura = cantidad != null ? cantidad : BigDecimal.ZERO;
        BigDecimal valorUnitarioSeguro = valorUnitario != null ? valorUnitario : BigDecimal.ZERO;
        BigDecimal descuentoSeguro = descuento != null ? descuento : BigDecimal.ZERO;
        BigDecimal total = cantidadSegura.multiply(valorUnitarioSeguro).subtract(descuentoSeguro);
        return total.compareTo(BigDecimal.ZERO) > 0 ? total : BigDecimal.ZERO;
    }
}
