package com.api.domu.domu.repository;

import com.api.domu.domu.entity.Detalle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DetalleRepository extends JpaRepository<Detalle, Long> {

    List<Detalle> findByCotizacionId(Long cotizacionId);
}
