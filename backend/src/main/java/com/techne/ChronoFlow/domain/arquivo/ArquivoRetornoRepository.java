package com.techne.ChronoFlow.domain.arquivo;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ArquivoRetornoRepository extends JpaRepository<ArquivoRetorno, Long> {

    List<ArquivoRetorno> findByStatus(String status);

}
