package com.roarmot.roarmot.repositories;

import com.roarmot.roarmot.models.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // Nuevo import necesario
import org.springframework.data.repository.query.Param; // Nuevo import necesario
import org.springframework.stereotype.Repository;

import java.util.List; // Importar List

/**
 * Interfaz de Repositorio para la entidad Producto.
 * Extiende JpaRepository, proporcionando métodos CRUD listos para usar.
 */
@Repository // Indica a Spring que esta interfaz es un repositorio
public interface ProductoRepository extends JpaRepository<Producto, Long> {
     
    // MÉTODOS EXISTENTES
    
    /**
     * Busca todos los productos que pertenecen a un usuario específico.
     */
    List<Producto> findByUsuario_IdUsuario(Long idUsuario);

    /**
     * Busca productos cuyo nombre contenga la palabra clave, ignorando mayúsculas/minúsculas,
     * y filtra solo por los productos del vendedor con el ID proporcionado.
     * * @param keyword La palabra o fragmento de nombre a buscar.
     * @param idUsuario El ID del vendedor autenticado.
     * @return Una lista de productos que coinciden con la búsqueda.
     */
    @Query("SELECT p FROM Producto p WHERE p.usuario.idUsuario = :idUsuario AND LOWER(p.nombre) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Producto> buscarPorNombre(
        @Param("keyword") String keyword, 
        @Param("idUsuario") Long idUsuario // <-- Nuevo parámetro en la firma
    );


    // CONSULTAS NUEVAS - PARA GENERAR LOS REPORTES ESTADÍSTICOS 
    // Productos por marca
    @Query("SELECT p.marca, COUNT(p) FROM Producto p GROUP BY p.marca")
    List<Object[]> contarProductosPorMarca();

    // Productos por talla
    @Query("SELECT p.talla, COUNT(p) FROM Producto p WHERE p.talla IS NOT NULL GROUP BY p.talla")  
    List<Object[]> contarProductosPorTalla();

    // Productos por subcategoría
    @Query("SELECT p.subcategoria.nombre, COUNT(p) FROM Producto p GROUP BY p.subcategoria.nombre")
    List<Object[]> contarProductosPorSubcategoria();

}