package com.cosmos.repository;

import com.cosmos.domain.Categoria;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {

    List<Categoria> findByActivoTrue();

    boolean existsByDescripcionIgnoreCase(String descripcion);
}
