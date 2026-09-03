package com.cosmos.controller;

import com.cosmos.service.CategoriaService;
import com.cosmos.service.ProductoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    private final ProductoService productoService;
    private final CategoriaService categoriaService;

    public IndexController(ProductoService productoService, CategoriaService categoriaService) {
        this.productoService = productoService;
        this.categoriaService = categoriaService;
    }

    @GetMapping("/")
    public String portada(Model model) {
        model.addAttribute("destacados", productoService.getDestacados());
        model.addAttribute("categorias", categoriaService.getCategorias(true));
        return "index";
    }
}
