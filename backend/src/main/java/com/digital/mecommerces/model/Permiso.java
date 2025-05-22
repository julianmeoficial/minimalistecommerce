package com.digital.mecommerces.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "permiso")
@Data
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"rolPermisos", "permisosHijos"})
@ToString(exclude = {"rolPermisos", "permisosHijos"})
public class Permiso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "permiso_id", nullable = false)
    private Long permisoId;

    @Column(name = "codigo", nullable = false, unique = true, length = 50)
    private String codigo;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @Column(name = "nivel")
    private Integer nivel = 0;

    @Column(name = "permiso_padre_id")
    private Long permisoPadreId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permiso_padre_id", insertable = false, updatable = false)
    private Permiso permisoPadre;

    @OneToMany(mappedBy = "permisoPadre", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Permiso> permisosHijos = new ArrayList<>();

    @OneToMany(mappedBy = "permiso", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RolPermiso> rolPermisos = new ArrayList<>();

    public Permiso(String codigo, String descripcion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
    }

    public Permiso(String codigo, String descripcion, Integer nivel) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.nivel = nivel;
    }
}
