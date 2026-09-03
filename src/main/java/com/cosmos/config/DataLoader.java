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
                Categoria matcha = categoria(categoriaRepo, "Matcha");
                Categoria cafe = categoria(categoriaRepo, "Cafe");
                Categoria frios = categoria(categoriaRepo, "Frios de autor");
                Categoria reposteria = categoria(categoriaRepo, "Reposteria");

                productoRepo.saveAll(List.of(
                        producto("Matcha marshmallow", "Matcha ceremonial, leche y malvavisco tostado", "3200", matcha, true, "/img/drink-matcha.jpg"),
                        producto("Iced matcha latte", "Matcha batido sobre hielo y leche", "2900", matcha, true, "/img/drinks-duo.jpg"),
                        producto("Matcha fresa", "Matcha con pure de fresa natural", "3100", matcha, false, null),
                        producto("Espresso", "Doble shot de origen", "1300", cafe, false, null),
                        producto("Cappuccino", "Espresso con leche texturizada", "2000", cafe, true, "/img/drinks-tray.jpg"),
                        producto("Latte de vainilla", "Espresso, leche y vainilla, frio o caliente", "2300", cafe, false, null),
                        producto("Cold brew", "Extraccion en frio 18 h", "2400", cafe, false, null),
                        producto("Cosmos frappe", "Bebida helada batida de la casa", "3300", frios, true, "/img/cup-cosmos.jpg"),
                        producto("Limonada Cosmos", "Limon, hierbabuena y soda", "1900", frios, false, null),
                        producto("Cinnamon roll con arandanos", "Horneado del dia con glaseado y arandanos", "2800", reposteria, true, "/img/pastry-cinnamon.jpg"),
                        producto("Brownie con helado", "Brownie tibio y helado de vainilla", "2600", reposteria, false, null),
                        producto("Cheesecake", "Porcion con salsa de frutos rojos", "2800", reposteria, false, null)
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
            Categoria categoria, boolean destacado, String rutaImagen) {
        Producto p = new Producto();
        p.setNombre(nombre);
        p.setDescripcion(descripcion);
        p.setPrecio(precio == null ? null : new BigDecimal(precio));
        p.setBajoPedido(precio == null);
        p.setCategoria(categoria);
        p.setDisponible(true);
        p.setDestacado(destacado);
        p.setRutaImagen(rutaImagen);
        return p;
    }
}
