package com.example.demo.model.dto;

import jakarta.validation.constraints.NotBlank;

public class CachorroRequestDTO {

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotBlank(message = "Nome do dono é obrigatório")
    private String nomeDono;

    private String raca;
    private String imagem;

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getNomeDono() { return nomeDono; }
    public void setNomeDono(String nomeDono) { this.nomeDono = nomeDono; }

    public String getRaca() { return raca; }
    public void setRaca(String raca) { this.raca = raca; }

    public String getImagem() { return imagem; }
    public void setImagem(String imagem) { this.imagem = imagem; }
}