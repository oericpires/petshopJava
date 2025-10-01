package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "cachorros")
public class Cachorro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Column(nullable = false)
    private String nome;

    @NotBlank(message = "Nome do dono é obrigatório")
    @Column(name = "nome_dono", nullable = false)
    private String nomeDono;

    @NotBlank(message = "Raça é obrigatória")
    @Column(nullable = false)
    private String raca;

    @Column(length = 1000)
    private String imagem;

    public Cachorro() {}

    public Cachorro(String nome, String nomeDono, String raca, String imagem) {
        this.nome = nome;
        this.nomeDono = nomeDono;
        this.raca = raca;
        this.imagem = imagem;
    }

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