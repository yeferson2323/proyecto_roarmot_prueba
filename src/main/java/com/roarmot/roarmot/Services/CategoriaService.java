package com.roarmot.roarmot.Services;

import com.roarmot.roarmot.models.Categoria;
import com.roarmot.roarmot.repositories.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    // Método para obtener todas las categorías
    public List<Categoria> findAllCategorias() {
        return categoriaRepository.findAll();
    }

    // Método para obtener una categoría por su ID
    public Optional<Categoria> findById(Long id) {
        return categoriaRepository.findById(id);
    }

    // Método para guardar o actualizar una categoría
    public Categoria save(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }

    // Método para eliminar una categoría por su ID
    public void deleteById(Long id) {
        categoriaRepository.deleteById(id);
    }
}
