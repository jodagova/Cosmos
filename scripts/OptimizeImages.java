import javax.imageio.*;
import javax.imageio.stream.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.nio.file.*;
import java.util.Locale;

/**
 * Reduce imagenes JPEG de src/main/resources/static/img a un maximo de MAX px
 * en el lado mayor y las re-comprime al QUALITY indicado.
 *
 *   cd scripts && java OptimizeImages.java
 *
 * Sobrescribe los archivos en su lugar. Correr antes de commitear fotos nuevas.
 */
public class OptimizeImages {

    static final int MAX = 1200;
    static final float QUALITY = 0.82f;

    public static void main(String[] args) throws Exception {
        Path dir = Paths.get("..", "src", "main", "resources", "static", "img");
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(dir, "*.{jpg,jpeg}")) {
            for (Path p : ds) {
                BufferedImage src = ImageIO.read(p.toFile());
                if (src == null) { System.out.println("skip (no imagen): " + p.getFileName()); continue; }

                long before = Files.size(p);
                int w = src.getWidth(), h = src.getHeight();
                double scale = Math.min(1.0, (double) MAX / Math.max(w, h));
                int nw = (int) Math.round(w * scale), nh = (int) Math.round(h * scale);

                BufferedImage dst = new BufferedImage(nw, nh, BufferedImage.TYPE_INT_RGB);
                Graphics2D g = dst.createGraphics();
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g.drawImage(src, 0, 0, nw, nh, null);
                g.dispose();

                ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
                ImageWriteParam param = writer.getDefaultWriteParam();
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionQuality(QUALITY);

                File tmp = File.createTempFile("opt", ".jpg");
                try (ImageOutputStream ios = ImageIO.createImageOutputStream(tmp)) {
                    writer.setOutput(ios);
                    writer.write(null, new IIOImage(dst, null, null), param);
                }
                writer.dispose();

                long after = Files.size(tmp.toPath());
                if (after < before) {
                    Files.move(tmp.toPath(), p, StandardCopyOption.REPLACE_EXISTING);
                    System.out.printf(Locale.US, "%-22s %dx%d -> %dx%d  %,d -> %,d bytes%n",
                            p.getFileName(), w, h, nw, nh, before, after);
                } else {
                    Files.delete(tmp.toPath());
                    System.out.printf(Locale.US, "%-22s sin cambios (ya optimizada, %,d bytes)%n",
                            p.getFileName(), before);
                }
            }
        }
    }
}
