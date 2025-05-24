package com.digital.mecommerces.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

public class ListaDeseosDTO {

    private Long listaId;

    @NotNull(message = "El ID del usuario es obligatorio")
    private Long usuarioId;

    @NotBlank(message = "El nombre de la lista es obligatorio")
    @Size(min = 1, max = 100, message = "El nombre debe tener entre 1 y 100 caracteres")
    private String nombre;

    private LocalDateTime createdat;

    private List<ListaDeseosItemDTO> items;

    private Integer totalItems;

    // Constructor vacío
    public ListaDeseosDTO() {}

    // Constructor con parámetros
    public ListaDeseosDTO(Long listaId, Long usuarioId, String nombre, LocalDateTime createdat) {
        this.listaId = listaId;
        this.usuarioId = usuarioId;
        this.nombre = nombre;
        this.createdat = createdat;
    }

    // Getters y Setters
    public Long getListaId() {
        return listaId;
    }

    public void setListaId(Long listaId) {
        this.listaId = listaId;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public LocalDateTime getCreatedat() {
        return createdat;
    }

    public void setCreatedat(LocalDateTime createdat) {
        this.createdat = createdat;
    }

    public List<ListaDeseosItemDTO> getItems() {
        return items;
    }

    public void setItems(List<ListaDeseosItemDTO> items) {
        this.items = items;
    }

    public Integer getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(Integer totalItems) {
        this.totalItems = totalItems;
    }
}
