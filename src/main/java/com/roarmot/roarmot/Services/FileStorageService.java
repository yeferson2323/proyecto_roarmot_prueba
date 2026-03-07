package com.roarmot.roarmot.Services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;



@Service
public class FileStorageService {

    // 1. INYECCIÓN: Obtiene la ruta de almacenamiento de 'application.properties'
    // La variable 'uploadDir' tomará el valor de ${file.upload-dir}
    private final Path fileStorageLocation;

    /**
     * Inicializa la ubicación de almacenamiento basándose en la configuración,
     * y crea el directorio si no existe.
     */
    public FileStorageService(@Value("${file.upload-dir}") String uploadDir) {
        // 2. USO DE LA RUTA INYECTADA: Construye la ruta absoluta y normalizada.
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        
        try {
            // Crea el directorio (e.g., data/uploads/products) si aún no existe.
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            // Lanza una excepción en caso de que no se pueda inicializar el almacenamiento.
            throw new RuntimeException("Error al intentar crear el directorio de almacenamiento: " + this.fileStorageLocation, ex);
        }
    }

    /**
     * Almacena un archivo en el sistema de archivos y devuelve el nombre único del archivo guardado.
     * @param file El archivo MultipartFile a guardar.
     * @return El nombre único del archivo (lo que se guardará en la columna 'IMAGEN' de la DB).
     */
    public String storeFile(MultipartFile file) throws IOException { // <-- ¡Añadido 'throws IOException'!
        // Genera un nombre de archivo único para evitar colisiones
        String originalFileName = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        
        String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
        
        // Comprobación de seguridad básica
        if (file.isEmpty()) {
            throw new IOException("El archivo está vacío.");
        }
        if (uniqueFileName.contains("..")) {
            throw new IOException("Nombre de archivo inválido.");
        }

        try {
            // Resuelve la ruta de destino completa
            Path targetLocation = this.fileStorageLocation.resolve(uniqueFileName);
            
            // Copia el archivo al destino, reemplazando si ya existe
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
            }
            
            // Retorna el nombre del archivo. 
            return uniqueFileName;

        } catch (IOException ex) {
            // CORRECCIÓN: Ahora relanzamos la IOException para que el controlador la atrape.
            throw new IOException("Fallo al intentar guardar el archivo " + uniqueFileName + ": " + ex.getMessage(), ex);
        }
    }

    /**
     * Elimina un archivo del sistema de almacenamiento.
     *
     * @param fileName El nombre único del archivo a eliminar (el valor de la columna 'IMAGEN' de la DB).
     * @return true si el archivo fue eliminado con éxito o si no existía (considerado éxito); false si falló la eliminación.
     */
    public boolean deleteFile(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            // No hay nombre de archivo para eliminar.
            return true;
        }

        try {
            // 1. Resuelve la ruta completa del archivo.
            Path fileToDelete = this.fileStorageLocation.resolve(fileName).normalize();

            // 2. Comprobación de seguridad para evitar ataques de cruce de directorios.
            if (!fileToDelete.startsWith(this.fileStorageLocation)) {
                System.err.println("Advertencia de seguridad: Intento de acceso a un archivo fuera del directorio de almacenamiento: " + fileName);
                return false;
            }

            // 3. Intenta eliminar el archivo. 
            // Files.deleteIfExists() devuelve true si se eliminó y false si el archivo no existe.
            boolean wasDeleted = Files.deleteIfExists(fileToDelete);

            if (wasDeleted) {
                System.out.println("Archivo eliminado con éxito: " + fileName);
            } else {
                System.out.println("El archivo no existía en el almacenamiento: " + fileName);
            }

            return true; // La operación de eliminación (o verificación de inexistencia) se manejó con éxito.

        } catch (IOException e) {
            System.err.println("Error fatal al intentar eliminar el archivo " + fileName + ": " + e.getMessage());
            return false; // Falló la eliminación por un error de I/O.
        }
    }

    // Puedes agregar un método para obtener el Path completo si lo necesitas
    public Path getFileStorageLocation() {
        return fileStorageLocation;
    }
}
