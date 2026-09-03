package com.cosmos.config;

import com.cosmos.domain.Categoria;
import com.cosmos.domain.Producto;
import com.cosmos.domain.Rol;
import com.cosmos.domain.Usuario;
import com.cosmos.repository.CategoriaRepository;
import com.cosmos.repository.ProductoRepository;
import com.cosmos.repository.RolRepository;
import com.cosmos.repository.UsuarioRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Carga datos iniciales (roles, usuario administrador y un menu de ejemplo) la
 * primera vez que arranca la aplicacion. Util para la demo al cliente.
 *
 * Las credenciales del administrador se toman de las propiedades:
 *   app.admin.username  (por defecto: admin)
 *   app.admin.password  (por defecto: cambiar123)
 */
@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner seed(RolRepository rolRepo,
            UsuarioRepository usuarioRepo,
            CategoriaRepository categoriaRepo,
            ProductoRepository productoRepo,
            PasswordEncoder passwordEncoder,
            org.springframework.core.env.Environment env) {
        return args -> {

            Rol admin = rolRepo.findByRol("ADMIN").orElseGet(() -> nuevoRol(rolRepo, "ADMIN"));
            rolRepo.findByRol("CLIENTE").orElseGet(() -> nuevoRol(rolRepo, "CLIENTE"));

            String adminUser = env.getProperty("app.admin.username", "admin");
            if (usuarioRepo.findByUsername(adminUser).isEmpty()) {
                Usuario u = new Usuario();
                u.setUsername(adminUser);
                u.setPassword(passwordEncoder.encode(env.getProperty("app.admin.password", "cambiar123")));
                u.setNombre("Administrador");
                u.setApellidos("Cosmos");
                u.setCorreo("admin@cosmos.local");
                u.setActivo(true);
                u.setRoles(Set.of(admin));
                usuarioRepo.save(u);
            }

            if (categoriaRepo.count() == 0) {
                // Datos de ejemplo (placeholder). Reemplazar por el menu real del cliente.
                Categoria cafe = categoria(categoriaRepo, "Cafe");
                Categoria bebidas = categoria(categoriaRepo, "Bebidas & Cocteles");
                Categoria desayunos = categoria(categoriaRepo, "Desayunos");
                Categoria salado = categoria(categoriaRepo, "Para picar");
                Categoria dulce = categoria(categoriaRepo, "Dulces");

                productoRepo.saveAll(List.of(
                        producto("Espresso", "Doble shot de la casa", "1300", cafe, true),
                        producto("Cappuccino", "Espresso con leche texturizada", "2000", cafe, true),
                        producto("Latte", "Espresso con leche, frio o caliente", "2200", cafe, false),
                        producto("Cold brew", "Extraccion en frio 18 h", "2400", cafe, false),
                        producto("Chocolate caliente", "Cacao con leche y malvaviscos", "2100", cafe, false),
                        producto("Limonada Cosmos", "Limon, hierbabuena y soda", "1900", bebidas, true),
                        producto("Sangria de la casa", "Copa, vino tinto y frutas", "3200", bebidas, true),
                        producto("Cerveza artesanal", "Rotativa, preguntar por la del dia", "2800", bebidas, false),
                        producto("Gin tonic", "Gin premium, tonica y botanicos", "4200", bebidas, false),
                        producto("Tostadas de aguacate", "Pan de masa madre, huevo pochado", "3800", desayunos, true),
                        producto("Bowl de granola", "Yogurt, frutas de estacion y miel", "3200", desayunos, false),
                        producto("Gallo pinto Cosmos", "Con huevo, queso y platano maduro", "3500", desayunos, false),
                        producto("Nachos", "Con pico de gallo, guacamole y queso", "3900", salado, true),
                        producto("Tabla de quesos", "Seleccion con pan y mermelada", "5500", salado, false),
                        producto("Alitas BBQ", "8 unidades con salsa de la casa", "4300", salado, false),
                        producto("Brownie con helado", "Brownie tibio y helado de vainilla", "2600", dulce, true),
                        producto("Cheesecake", "Porcion con salsa de frutos rojos", "2800", dulce, false)
                ));
            }
        };
    }

    private static Rol nuevoRol(RolRepository repo, String nombre) {
        Rol r = new Rol();
        r.setRol(nombre);
        return repo.save(r);
    }

    private static Categoria categoria(CategoriaRepository repo, String descripcion) {
        Categoria c = new Categoria();
        c.setDescripcion(descripcion);
        c.setActivo(true);
        return repo.save(c);
    }

    private static Producto producto(String nombre, String descripcion, String precio,
            Categoria categoria, boolean destacado) {
        Producto p = new Producto();
        p.setNombre(nombre);
        p.setDescripcion(descripcion);
        p.setPrecio(precio == null ? null : new BigDecimal(precio));
        p.setBajoPedido(precio == null);
        p.setCategoria(categoria);
        p.setDisponible(true);
        p.setDestacado(destacado);
        return p;
    }
}
