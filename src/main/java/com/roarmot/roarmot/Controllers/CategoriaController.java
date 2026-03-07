package com.roarmot.roarmot.Controllers;

import com.roarmot.roarmot.models.Categoria;
import com.roarmot.roarmot.Services.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

// Anotación que marca esta clase como un controlador REST.
@RestController
// Mapea todas las peticiones a la ruta base "/api/categorias".
@RequestMapping("/api/categorias")
public class CategoriaController {

    // Inyecta la dependencia del servicio CategoriaService.
    @Autowired
    private CategoriaService categoriaService;

    // Obtener todas las categorías.
    @GetMapping
    public List<Categoria> findAll() {
        return categoriaService.findAllCategorias();
    }

    // Obtener una categoría por ID.
    @GetMapping("/{id}")
    public ResponseEntity<Categoria> findById(@PathVariable Long id) {
        Optional<Categoria> categoria = categoriaService.findById(id);
        return categoria.map(ResponseEntity::ok)
                       .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Guardar una nueva categoría.
    @PostMapping
    public ResponseEntity<Categoria> save(@RequestBody Categoria categoria) {
        Categoria nuevaCategoria = categoriaService.save(categoria);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaCategoria);
    }

    // Actualizar una categoría existente.
    @PutMapping("/{id}")
    public ResponseEntity<Categoria> update(@PathVariable Long id, @RequestBody Categoria categoria) {
        if (!categoriaService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        categoria.setId(id);
        Categoria categoriaActualizada = categoriaService.save(categoria);
        return ResponseEntity.ok(categoriaActualizada);
    }

    // Eliminar una categoría.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!categoriaService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        categoriaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
