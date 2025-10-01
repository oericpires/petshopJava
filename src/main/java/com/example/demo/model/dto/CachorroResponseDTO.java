package com.example.demo.model.dto;

public class CachorroResponseDTO {
    private Long id;
    private String nome;
    private String nomeDono;
    private String raca;
    private String imagem;

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getNomeDono() { return nomeDono; }
    public void setNomeDono(String nomeDono) { this.nomeDono = nomeDono; }

    public String getRaca() { return raca; }
    public void setRaca(String raca) { this.raca = raca; }

    public String getImagem() { return imagem; }
    public void setImagem(String imagem) { this.imagem = imagem; }
}