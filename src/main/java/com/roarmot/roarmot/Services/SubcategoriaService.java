package com.roarmot.roarmot.Services;

import com.roarmot.roarmot.models.Categoria;
import com.roarmot.roarmot.models.Subcategoria;
import com.roarmot.roarmot.repositories.SubcategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SubcategoriaService {

    @Autowired
    private SubcategoriaRepository subcategoriaRepository;

    // Método para obtener todas las subcategorías
    public List<Subcategoria> findAllSubcategorias() {
        return subcategoriaRepository.findAll();
    }

    // Método para obtener una subcategoría por su ID
    public Optional<Subcategoria> findById(Long id) {
        return subcategoriaRepository.findById(id);
    }

    // Método para guardar o actualizar una subcategoría
    public Subcategoria save(Subcategoria subcategoria) {
        return subcategoriaRepository.save(subcategoria);
    }

    // Método para eliminar una subcategoría por su ID
    public void deleteById(Long id) {
        subcategoriaRepository.deleteById(id);
    }

    // Método personalizado para encontrar subcategorías por categoría (aún no se implementa, solo es un ejemplo)
    // public List<Subcategoria> findByCategoria(Categoria categoria) {
    //    return subcategoriaRepository.findByCategoria(categoria);
    // }
}
