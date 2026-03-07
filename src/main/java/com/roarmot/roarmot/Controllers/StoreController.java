package com.roarmot.roarmot.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

@Controller
public class StoreController {

    // Define la URL (ruta) que activará este método GET
    @GetMapping("/store") 
    public String mostrarVista(Model model) {
        
        // **Opcional:** Puedes agregar datos al modelo si la vista los necesita
        // model.addAttribute("titulo", "Bienvenido a la Tienda");
        
        // El String devuelto es el nombre lógico de tu vista (p. ej., un archivo store.html)
        return "store"; 
    }
}