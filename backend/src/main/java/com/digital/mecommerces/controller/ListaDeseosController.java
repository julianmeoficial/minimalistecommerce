package com.digital.mecommerces.controller;

import com.digital.mecommerces.dto.ListaDeseosDTO;
import com.digital.mecommerces.dto.ListaDeseosItemDTO;
import com.digital.mecommerces.service.ListaDeseosService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/lista-deseos")
public class ListaDeseosController {

    private final ListaDeseosService listaDeseosService;

    public ListaDeseosController(ListaDeseosService listaDeseosService) {
        this.listaDeseosService = listaDeseosService;
    }

    @PostMapping
    public ResponseEntity<ListaDeseosDTO> crearLista(@Valid @RequestBody ListaDeseosDTO listaDTO) {
        ListaDeseosDTO nuevaLista = listaDeseosService.crearLista(listaDTO);
        return new ResponseEntity<>(nuevaLista, HttpStatus.CREATED);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<ListaDeseosDTO>> obtenerListasPorUsuario(@PathVariable Long usuarioId) {
        List<ListaDeseosDTO> listas = listaDeseosService.obtenerListasPorUsuario(usuarioId);
        return ResponseEntity.ok(listas);
    }

    @GetMapping("/{listaId}")
    public ResponseEntity<ListaDeseosDTO> obtenerListaPorId(@PathVariable Long listaId) {
        ListaDeseosDTO lista = listaDeseosService.obtenerListaPorId(listaId);
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/usuario/{usuarioId}/principal")
    public ResponseEntity<ListaDeseosDTO> obtenerListaPrincipal(@PathVariable Long usuarioId) {
        ListaDeseosDTO lista = listaDeseosService.obtenerListaPrincipal(usuarioId);
        return ResponseEntity.ok(lista);
    }

    @PostMapping("/{listaId}/productos/{productoId}")
    public ResponseEntity<ListaDeseosItemDTO> agregarProducto(
            @PathVariable Long listaId,
            @PathVariable Long productoId,
            @RequestParam(required = false, defaultValue = "0") Integer prioridad,
            @RequestParam(required = false) String notas) {

        ListaDeseosItemDTO item = listaDeseosService.agregarProductoALista(listaId, productoId, prioridad, notas);
        return new ResponseEntity<>(item, HttpStatus.CREATED);
    }

    @DeleteMapping("/{listaId}/productos/{productoId}")
    public ResponseEntity<Map<String, String>> eliminarProducto(
            @PathVariable Long listaId,
            @PathVariable Long productoId) {

        listaDeseosService.eliminarProductoDeLista(listaId, productoId);

        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Producto eliminado de la lista de deseos exitosamente");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<ListaDeseosItemDTO> actualizarItem(
            @PathVariable Long itemId,
            @RequestParam(required = false) Integer prioridad,
            @RequestParam(required = false) String notas) {

        ListaDeseosItemDTO item = listaDeseosService.actualizarItemLista(itemId, prioridad, notas);
        return ResponseEntity.ok(item);
    }

    @DeleteMapping("/{listaId}")
    public ResponseEntity<Map<String, String>> eliminarLista(@PathVariable Long listaId) {
        listaDeseosService.eliminarLista(listaId);

        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Lista de deseos eliminada exitosamente");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{listaId}/items")
    public ResponseEntity<List<ListaDeseosItemDTO>> obtenerItems(@PathVariable Long listaId) {
        List<ListaDeseosItemDTO> items = listaDeseosService.obtenerItemsPorLista(listaId);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/{listaId}/contiene/{productoId}")
    public ResponseEntity<Map<String, Boolean>> verificarProducto(
            @PathVariable Long listaId,
            @PathVariable Long productoId) {

        boolean contiene = listaDeseosService.verificarProductoEnLista(listaId, productoId);

        Map<String, Boolean> response = new HashMap<>();
        response.put("contiene", contiene);
        return ResponseEntity.ok(response);
    }
}