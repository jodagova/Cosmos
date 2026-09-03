import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Genera src/main/resources/static/img/favicon.png (64x64) a partir de logo.png:
 * recorta el centro a un cuadrado, escala y aplica mascara circular.
 *
 *   cd scripts && java MakeFavicon.java
 */
public class MakeFavicon {

    // El circulo naranja no esta perfectamente centrado en el screenshot:
    // estos factores ajustan el recorte (0.5 = centro).
    static final double CENTER_X = 0.52;
    static final double CENTER_Y = 0.50;
    static final double CROP = 0.92;   // porcion del lado menor a recortar
    static final int SIZE = 64;

    public static void main(String[] args) throws Exception {
        File dir = new File("../src/main/resources/static/img");
        BufferedImage src = ImageIO.read(new File(dir, "logo.png"));

        int side = (int) (Math.min(src.getWidth(), src.getHeight()) * CROP);
        int x = (int) (src.getWidth() * CENTER_X - side / 2.0);
        int y = (int) (src.getHeight() * CENTER_Y - side / 2.0);
        x = Math.max(0, Math.min(x, src.getWidth() - side));
        y = Math.max(0, Math.min(y, src.getHeight() - side));
        BufferedImage square = src.getSubimage(x, y, side, side);

        BufferedImage out = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = out.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setClip(new Ellipse2D.Float(0, 0, SIZE, SIZE));
        g.drawImage(square, 0, 0, SIZE, SIZE, null);
        g.dispose();

        File dest = new File(dir, "favicon.png");
        ImageIO.write(out, "png", dest);
        System.out.println("escrito " + dest.getCanonicalPath() + " (" + SIZE + "x" + SIZE + ")");
    }
}
