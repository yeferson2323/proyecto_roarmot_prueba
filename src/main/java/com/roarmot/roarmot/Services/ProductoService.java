package com.roarmot.roarmot.Services;

import com.roarmot.roarmot.dto.ProductoFormDTO;
import com.roarmot.roarmot.dto.ProductoQuickEditDTO;
import com.roarmot.roarmot.models.Producto;
import com.roarmot.roarmot.models.Subcategoria; 
import com.roarmot.roarmot.repositories.ProductoRepository;
import com.roarmot.roarmot.repositories.SubcategoriaRepository; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.roarmot.roarmot.models.Usuario; 
import com.roarmot.roarmot.repositories.UsuarioRepository; 

import jakarta.validation.Valid;

import org.springframework.transaction.annotation.Transactional; 

import java.util.List;
import java.util.Optional;
// Necesario para manejar el tipo de dato del DTO
import java.math.BigDecimal; 


@Service
public class ProductoService {

	@Autowired
	private ProductoRepository productoRepository;

	@Autowired
	private SubcategoriaRepository subcategoriaRepository; 

	@Autowired
	private UsuarioRepository usuarioRepository; 

	// -----------------------------------------------------------------
	// MÉTODO PARA GUARDAR PRODUCTO DESDE DTO
	// -----------------------------------------------------------------
	/**
	 * Crea y guarda una nueva entidad Producto, asociándola al usuario vendedor.
	 */
	public Producto save(ProductoFormDTO productoForm, String nombreArchivo, Long idUsuario) {
		
		Usuario usuario = usuarioRepository.findById(idUsuario)
				.orElseThrow(() -> new RuntimeException("Usuario con ID " + idUsuario + " no encontrado."));

		Subcategoria subcategoria = subcategoriaRepository.findById(productoForm.getIdSubcategoria())
				.orElseThrow(() -> new RuntimeException("Subcategoría con ID " + productoForm.getIdSubcategoria() + " no encontrada."));

		Producto nuevoProducto = new Producto();
		
		// Mapeo de campos
		nuevoProducto.setNombre(productoForm.getNombre()); 
		nuevoProducto.setMarca(productoForm.getMarca());
		nuevoProducto.setDescripcion(productoForm.getDescripcion());
		
		// ********** CONVERSIÓN DE TIPO DE DATO (BigDecimal -> Double) **********
		// El DTO usa BigDecimal, pero la Entidad Producto usa Double. 
		if (productoForm.getPrecio() != null) {
            // Convertimos el BigDecimal a Double (perdiendo la precisión extra de BigDecimal, 
            // pero compatible con el decimal(10, 2) de la BD y la entidad Double)
            nuevoProducto.setPrecio(productoForm.getPrecio().doubleValue());
		} else {
            nuevoProducto.setPrecio(null);
        }
		// **********************************************************************
		
		// El stock (cantidad) se debe asignar.
		nuevoProducto.setCantidad(productoForm.getStock());
		
		nuevoProducto.setLote(productoForm.getLote());

		// Asumiendo que getTalla() en el DTO devuelve el Enum o un String convertible:
		nuevoProducto.setTalla(productoForm.getTalla());
		
		// Mapeo de relaciones y archivo
		nuevoProducto.setImagen(nombreArchivo); 
		nuevoProducto.setSubcategoria(subcategoria); 
		nuevoProducto.setUsuario(usuario); 
		
		// Guardar y retornar la entidad persistida
		return productoRepository.save(nuevoProducto);
	}

	// -----------------------------------------------------------------
	//  MÉTODO PARA BUSCAR PRODUCTOS POR VENDEDOR
	// -----------------------------------------------------------------
	public List<Producto> findAllByVendedorId(Long idUsuario) {
		return productoRepository.findByUsuario_IdUsuario(idUsuario);
	}

	// -----------------------------------------------------------------
	// MÉTODO PARA CONVERTIR ENTIDAD A DTO
	// -----------------------------------------------------------------
	/**
	 * Convierte la entidad Producto (Double) a un DTO (BigDecimal) para edición.
	 */
	public ProductoFormDTO convertToDTO(Producto producto) {
		ProductoFormDTO dto = new ProductoFormDTO();
		
		dto.setNombre(producto.getNombre());
		dto.setDescripcion(producto.getDescripcion());
		dto.setMarca(producto.getMarca());
		
		// Mapeo inverso: Convertir Double (Entidad) a BigDecimal (DTO)
        if (producto.getPrecio() != null) {
            // Usamos BigDecimal.valueOf() para mantener la escala del double de forma segura
            dto.setPrecio(BigDecimal.valueOf(producto.getPrecio())); 
        } else {
            dto.setPrecio(null);
        }
		
		dto.setStock(producto.getCantidad()); 
		dto.setLote(producto.getLote());
		dto.setTalla(producto.getTalla());
		
		if (producto.getSubcategoria() != null) {
			dto.setIdSubcategoria(producto.getSubcategoria().getId());
		}
		
		return dto;
	}
	
	// -----------------------------------------------------------------
	// MÉTODO PARA ACTUALIZAR PRODUCTO 
	// -----------------------------------------------------------------
	@Transactional 
	public void update(ProductoFormDTO productoForm, String nombreArchivo, Long vendedorId, Long idProducto) {
		// PASO 1: Buscar y verificar el producto existente en la base de datos
		Producto productoExistente = productoRepository.findById(idProducto)
				.orElseThrow(() -> new RuntimeException("Producto con ID " + idProducto + " no encontrado para actualizar."));

		// PASO 2: Verificación de seguridad - CRUCIAL
		if (!productoExistente.getUsuario().getIdUsuario().equals(vendedorId)) {
			throw new SecurityException("Acceso denegado. El producto no pertenece a este vendedor.");
		}

		// PASO 3: Obtener la Subcategoría
		Subcategoria nuevaSubcategoria = subcategoriaRepository.findById(productoForm.getIdSubcategoria())
				.orElseThrow(() -> new RuntimeException("Subcategoría con ID " + productoForm.getIdSubcategoria() + " no encontrada."));

		// PASO 4: Mapear los campos actualizados
		productoExistente.setNombre(productoForm.getNombre());
		productoExistente.setMarca(productoForm.getMarca());
		productoExistente.setDescripcion(productoForm.getDescripcion());
		
		// ********** CONVERSIÓN DE TIPO DE DATO (BigDecimal -> Double) **********
		if (productoForm.getPrecio() != null) {
			// Convertimos el BigDecimal a Double.
            productoExistente.setPrecio(productoForm.getPrecio().doubleValue());
		} else {
            productoExistente.setPrecio(null);
        }
		// ************************************************
		
		productoExistente.setCantidad(productoForm.getStock());
		productoExistente.setLote(productoForm.getLote());
		productoExistente.setTalla(productoForm.getTalla());
		productoExistente.setImagen(nombreArchivo); 
		productoExistente.setSubcategoria(nuevaSubcategoria);

		// PASO 5: Guardar la entidad actualizada
		productoRepository.save(productoExistente);
	}

	// -----------------------------------------------------------------
	// MÉTODO PARA ACTUALIZACIÓN RÁPIDA (VÍA AJAX)
	// -----------------------------------------------------------------
	/**
	 * Actualiza rápidamente los campos Nombre, Marca, Stock y Precio de un producto.
	 * Se asume que ProductoQuickEditDTO.getPrecio() YA devuelve Double.
	 */
	@Transactional 
	public void updateQuick(ProductoQuickEditDTO quickEditDTO, Long vendedorId) {
		
		Long idProducto = quickEditDTO.getIdProducto();

		Producto productoExistente = productoRepository.findById(idProducto)
				.orElseThrow(() -> new RuntimeException("Producto con ID " + idProducto + " no encontrado para la edición rápida."));

		if (!productoExistente.getUsuario().getIdUsuario().equals(vendedorId)) {
			throw new SecurityException("Acceso denegado. El producto no pertenece a este vendedor.");
		}
		
		productoExistente.setNombre(quickEditDTO.getNombre());
		productoExistente.setMarca(quickEditDTO.getMarca());
		
		// Asumimos que ProductoQuickEditDTO ya usa Double (o lo ajustará a Double).
		// Si quickEditDTO usa BigDecimal, también se requerirá aquí la conversión .doubleValue().
		productoExistente.setPrecio(quickEditDTO.getPrecio());
		
		productoExistente.setCantidad(quickEditDTO.getStock()); 
		
		productoRepository.save(productoExistente);
	}


	// -----------------------------------------------------------------
	// MÉTODOS VARIOS (Delete, FindAll, etc.)
	// -----------------------------------------------------------------
	@Transactional 
	public void delete(Long idProducto, Long vendedorId) throws SecurityException {
		Producto productoExistente = productoRepository.findById(idProducto)
				.orElseThrow(() -> new RuntimeException("Producto con ID " + idProducto + " no encontrado."));
		
		if (!productoExistente.getUsuario().getIdUsuario().equals(vendedorId)) {
			throw new SecurityException("Acceso denegado. No tiene permiso para eliminar este producto.");
		}
		
		productoRepository.delete(productoExistente);
	}

	public List<Producto> findAll() { return productoRepository.findAll(); }
	public Optional<Producto> findById(Long id) { return productoRepository.findById(id); }
	public Producto save(Producto producto) { return productoRepository.save(producto); }
	public void deleteById(Long id) { productoRepository.deleteById(id); }
}
