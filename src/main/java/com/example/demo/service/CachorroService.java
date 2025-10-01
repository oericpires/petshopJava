package com.example.demo.service;


import com.example.demo.model.Cachorro;
import com.example.demo.model.dto.CachorroRequestDTO;
import com.example.demo.model.dto.CachorroResponseDTO;
import com.example.demo.repository.CachorroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CachorroService {

    @Autowired
    private CachorroRepository repository;

    private final RestTemplate restTemplate = new RestTemplate();
    private final String DOG_API_BASE_URL = "https://dog.ceo/api";

    // Lista de raças comuns como fallback
    private final List<String> COMMON_BREEDS = Arrays.asList(
            "Labrador", "Golden Retriever", "Bulldog", "Poodle", "Beagle",
            "German Shepherd", "Boxer", "Siberian Husky", "Dachshund", "Chihuahua",
            "Rottweiler", "Yorkshire Terrier", "Shih Tzu", "Pug", "Border Collie"
    );

    public List<CachorroResponseDTO> findAll() {
        return repository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public CachorroResponseDTO findById(Long id) {
        Cachorro cachorro = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cachorro não encontrado com id: " + id));
        return toResponseDTO(cachorro);
    }

    public CachorroResponseDTO create(CachorroRequestDTO request) {
        Cachorro cachorro = new Cachorro();
        cachorro.setNome(request.getNome());
        cachorro.setNomeDono(request.getNomeDono());

        if (request.getRaca() == null || request.getRaca().isEmpty()) {
            cachorro.setRaca(getRandomBreed());
        } else {
            cachorro.setRaca(request.getRaca());
        }

        if (request.getImagem() == null || request.getImagem().isEmpty()) {
            cachorro.setImagem(getRandomDogImage());
        } else {
            cachorro.setImagem(request.getImagem());
        }

        Cachorro saved = repository.save(cachorro);
        return toResponseDTO(saved);
    }

    public CachorroResponseDTO update(Long id, CachorroRequestDTO request) {
        Cachorro cachorro = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cachorro não encontrado com id: " + id));

        cachorro.setNome(request.getNome());
        cachorro.setNomeDono(request.getNomeDono());

        if (request.getRaca() != null && !request.getRaca().isEmpty()) {
            cachorro.setRaca(request.getRaca());
        }

        if (request.getImagem() != null && !request.getImagem().isEmpty()) {
            cachorro.setImagem(request.getImagem());
        }

        Cachorro updated = repository.save(cachorro);
        return toResponseDTO(updated);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Cachorro não encontrado com id: " + id);
        }
        repository.deleteById(id);
    }

    public List<CachorroResponseDTO> findByNome(String nome) {
        return repository.findByNomeContainingIgnoreCase(nome).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<CachorroResponseDTO> findByRaca(String raca) {
        return repository.findByRacaContainingIgnoreCase(raca).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    private String getRandomBreed() {
        try {
            String imageUrl = getRandomDogImage();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                return extractBreedFromImageUrl(imageUrl);
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar raça: " + e.getMessage());
        }

        Random random = new Random();
        return COMMON_BREEDS.get(random.nextInt(COMMON_BREEDS.size()));
    }

    private String getRandomDogImage() {
        try {
            String url = DOG_API_BASE_URL + "/breeds/image/random";
            var response = restTemplate.getForObject(url, DogImageResponse.class);
            if (response != null && "success".equals(response.getStatus())) {
                return response.getMessage();
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar imagem: " + e.getMessage());
        }
        return "";
    }

    private String extractBreedFromImageUrl(String imageUrl) {
        try {
            String[] parts = imageUrl.split("/");
            if (parts.length >= 5) {
                String breedWithSub = parts[4]; // "hound-afghan"
                String[] breedParts = breedWithSub.split("-");
                String baseBreed = breedParts[0]; // "hound"

                return baseBreed.substring(0, 1).toUpperCase() + baseBreed.substring(1);
            }
        } catch (Exception e) {
            System.err.println("Erro ao extrair raça do URL: " + e.getMessage());
        }
        return "Vira-lata";
    }

    private CachorroResponseDTO toResponseDTO(Cachorro cachorro) {
        CachorroResponseDTO dto = new CachorroResponseDTO();
        dto.setId(cachorro.getId());
        dto.setNome(cachorro.getNome());
        dto.setNomeDono(cachorro.getNomeDono());
        dto.setRaca(cachorro.getRaca());
        dto.setImagem(cachorro.getImagem());
        return dto;
    }

    public static class DogImageResponse {
        private String message;
        private String status;

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}