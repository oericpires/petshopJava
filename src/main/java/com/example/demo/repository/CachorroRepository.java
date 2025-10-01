package com.example.demo.repository;

import com.example.demo.model.Cachorro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CachorroRepository extends JpaRepository<Cachorro, Long> {
    List<Cachorro> findByNomeContainingIgnoreCase(String nome);
    List<Cachorro> findByRacaContainingIgnoreCase(String raca);
    List<Cachorro> findByNomeDonoContainingIgnoreCase(String nomeDono);
}