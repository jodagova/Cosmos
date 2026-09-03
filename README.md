# Cosmos Cafe Bar

Sitio web y administración para **Cosmos** — café de día, bar de noche.

> Estado: **borrador inicial** (draft). Estructura basada en la plantilla *Librantis*.

## Tecnología

- Java 21 · Spring Boot 4
- Spring MVC + Thymeleaf
- Spring Data JPA
- Spring Security (login por base de datos, contraseñas BCrypt)
- Bootstrap 5 + Font Awesome
- Base de datos: **H2 en memoria** para desarrollo/demo · **PostgreSQL** para producción

## Cómo ejecutar (demo, sin instalar nada)

```bash
mvnw spring-boot:run
```

Arranca con el perfil `dev` (H2 en memoria) y datos de ejemplo (menú + usuario admin).

- App: http://localhost:8080
- Consola H2: http://localhost:8080/h2-console — JDBC URL `jdbc:h2:mem:cosmos`, usuario `sa`, sin contraseña
- Usuario administrador: `admin` / `cambiar123` (configurable con `APP_ADMIN_USERNAME` / `APP_ADMIN_PASSWORD`)

## Ejecutar con PostgreSQL (producción)

**Opción A — Postgres local con Docker:**

```bash
docker compose up -d
set SPRING_PROFILES_ACTIVE=prod
mvnw spring-boot:run
```

Las credenciales por defecto del `docker-compose.yml` ya coinciden con `application-prod.properties`.

**Opción B — Neon (nube):**

1. Crear un proyecto en [neon.tech](https://neon.tech) y una base `cosmos`.
2. Copiar el connection string en formato JDBC.
3. (Opcional) crear el esquema con [`src/main/resources/db/schema.sql`](src/main/resources/db/schema.sql); con `ddl-auto=update` Hibernate también lo crea solo.
4. Definir variables de entorno:

```bash
set SPRING_PROFILES_ACTIVE=prod
set DB_URL=jdbc:postgresql://<host>.neon.tech/cosmos?sslmode=require
set DB_USERNAME=<usuario>
set DB_PASSWORD=<clave>
set APP_ADMIN_PASSWORD=una-clave-fuerte
mvnw spring-boot:run
```

Ninguna credencial se guarda en el repositorio. Para configuración local usar
`src/main/resources/application-local.properties` (ignorado por Git).

## Despliegue en Render

El repo incluye [`Dockerfile`](Dockerfile) y [`render.yaml`](render.yaml).

1. En [render.com](https://render.com) → **New** → **Blueprint** y conectá el repo `jodagova/Cosmos`.
2. Render lee `render.yaml` y crea un Web Service (plan free, Docker).
3. Al terminar el build queda en `https://cosmos-cafebar.onrender.com`.

**Demo:** perfil `dev` (H2 en memoria) — los datos se reinician en cada redeploy y
cuando el plan free suspende el servicio por inactividad (~15 min). La contraseña
del admin la genera Render (**Environment → `APP_ADMIN_PASSWORD`**).

**Producción:** crear la base en Neon, cambiar `SPRING_PROFILES_ACTIVE` a `prod` y
definir `DB_URL` / `DB_USERNAME` / `DB_PASSWORD` en el dashboard de Render.

## Estructura

```
src/main/java/com/cosmos
├── config      · seguridad, MVC/i18n, carga de datos inicial
├── controller  · Index, Menu, Admin, Home
├── domain      · Usuario, Rol, Categoria, Producto, Pedido, PedidoDetalle
├── repository  · repositorios Spring Data
└── service     · lógica de negocio
```

## Pendiente

- [ ] Logo del cliente en `src/main/resources/static/img/logo.png` (fondo transparente)
- [ ] Menú real (nombres, descripciones, precios) — los actuales son de ejemplo
- [ ] Imágenes de producto y de ambiente (optimizadas) en `static/img/`
- [ ] Contacto real: WhatsApp, dirección, horario
- [ ] Paleta/tipografía según identidad del cliente
- [ ] CRUD de productos y categorías desde el panel admin
- [ ] Carga de imágenes (evaluar Cloudinary cuando el personal gestione la galería)
