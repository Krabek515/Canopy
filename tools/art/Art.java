import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Генератор текстур Canopy. Каждый спрайт описан картой символов: один символ — один пиксель.
 * Палитра общая, чтобы все предметы и блоки были в одном стиле.
 *
 * Сборка и запуск из корня проекта:
 *   javac -d tools/art/out tools/art/*.java && java -cp tools/art/out Art
 */
public final class Art {
    static final String OUT = "src/main/resources/assets/canopy/textures/";

    // Общая палитра. Буква -> цвет ARGB.
    static final Map<Character, Integer> PALETTE = new HashMap<>();

    static {
        p('.', 0x00000000); // прозрачный
        p('k', 0xFF1A1410); // контур
        // камень
        p('S', 0xFFA3A39A);
        p('s', 0xFF767670);
        p('z', 0xFF4C4C47);
        // бамбук
        p('B', 0xFFD3CF72);
        p('b', 0xFFA2AF4D);
        p('n', 0xFF6E7F30);
        p('N', 0xFF4A5A1E);
        // дерево
        p('W', 0xFF8F6539);
        p('w', 0xFF5F4123);
        p('d', 0xFF3B2814);
        // листва
        p('G', 0xFF74B43B);
        p('g', 0xFF418F2B);
        p('e', 0xFF245D1D);
        p('E', 0xFF143E12);
        // сырое мясо
        p('R', 0xFFE5696D);
        p('r', 0xFFB73C41);
        p('q', 0xFF7C2023);
        p('F', 0xFFF1D8C2);
        // жареное
        p('C', 0xFFB97E4C);
        p('c', 0xFF8C562A);
        p('v', 0xFF5B341B);
        // тухлое
        p('O', 0xFF8E9B67);
        p('o', 0xFF6C7A4F);
        p('l', 0xFF4A5736);
        // шкура
        p('H', 0xFFDDB573);
        p('h', 0xFFB98D47);
        p('t', 0xFF7F5C2C);
        p('j', 0xFF3A2A14);
        // бумага
        p('P', 0xFFF1E5C4);
        p('p', 0xFFDBCA9C);
        p('u', 0xFFA99B71);
        // вода
        p('M', 0xFF7D814F); // мутная
        p('m', 0xFF5D6339);
        p('A', 0xFF72B9DB); // чистая
        p('a', 0xFF4288B2);
        // металл
        p('I', 0xFFDADAD2);
        p('i', 0xFFABABA2);
        // волокно
        p('Y', 0xFFF3EBD2);
        p('y', 0xFFDACCA2);
        // шёлк
        p('X', 0xFFF9F9F5);
        p('x', 0xFFDEDED6);
        // уголь
        p('K', 0xFF2B2B2B);
        p('Q', 0xFF474747);
        // яд / отвар
        p('V', 0xFF83D34C);
        p('L', 0xFFD2EAE2); // бледный побег
        p('T', 0xFF72A99B);
        // дурной отвар
        p('D', 0xFF6B4A5E);
        p('f', 0xFF8B6A7E);
        // красная ткань
        p('Z', 0xFF8E2B24);
        // орхидея
        p('J', 0xFFE08ACB);
        p('U', 0xFF9E3D8A);
        // спелый банан
        p('2', 0xFFF7D94A);
        p('3', 0xFFCFA93A);
    }

    private static void p(char c, int argb) {
        PALETTE.put(c, argb);
    }

    public static void main(String[] args) throws IOException {
        Items.all();
        Blocks.all();
        Gui.all();
        Beasts.all();
        Armor.all();
        contactSheet();
        entitySheet();
        System.out.println("done");
    }

    /** Контрольный лист: все 16x16 спрайты, увеличенные в 6 раз, на тёмном фоне. Только для проверки глазами. */
    static void contactSheet() throws IOException {
        java.util.List<File> files = new java.util.ArrayList<>();
        for (String dir : new String[]{"item", "block"}) {
            File[] list = new File(OUT + dir).listFiles((d, n) -> n.endsWith(".png"));
            if (list != null) {
                java.util.Arrays.sort(list);
                files.addAll(java.util.Arrays.asList(list));
            }
        }
        int scale = 6, cols = 8, cell = 16 * scale + 8;
        int rows = (files.size() + cols - 1) / cols;
        BufferedImage sheet = new BufferedImage(cols * cell, rows * cell, BufferedImage.TYPE_INT_ARGB);
        fill(sheet, 0, 0, sheet.getWidth(), sheet.getHeight(), rgb(0x3A, 0x4A, 0x36));
        for (int i = 0; i < files.size(); i++) {
            BufferedImage src = ImageIO.read(files.get(i));
            int ox = (i % cols) * cell + 4, oy = (i / cols) * cell + 4;
            for (int y = 0; y < src.getHeight() && y < 16; y++) {
                for (int x = 0; x < src.getWidth() && x < 16; x++) {
                    int argb = src.getRGB(x, y);
                    if ((argb >>> 24) == 0) continue;
                    fill(sheet, ox + x * scale, oy + y * scale, scale, scale, argb);
                }
            }
        }
        File out = new File("tools/art/preview.png");
        ImageIO.write(sheet, "png", out);
        System.out.println("  preview " + files.size() + " sprites");
    }

    /** Развёртки зверей, увеличенные в 4 раза, для проверки глазами. */
    static void entitySheet() throws IOException {
        File[] list = new File(OUT + "entity").listFiles((d, n) -> n.endsWith(".png"));
        if (list == null) return;
        java.util.Arrays.sort(list);
        int scale = 4, cell = 64 * scale + 8;
        BufferedImage sheet = new BufferedImage(cell * list.length, 64 * scale, BufferedImage.TYPE_INT_ARGB);
        fill(sheet, 0, 0, sheet.getWidth(), sheet.getHeight(), rgb(0x3A, 0x4A, 0x36));
        for (int i = 0; i < list.length; i++) {
            BufferedImage src = ImageIO.read(list[i]);
            for (int y = 0; y < src.getHeight(); y++) for (int x = 0; x < src.getWidth(); x++) {
                int argb = src.getRGB(x, y);
                if ((argb >>> 24) == 0) continue;
                fill(sheet, i * cell + x * scale, y * scale, scale, scale, argb);
            }
        }
        ImageIO.write(sheet, "png", new File("tools/art/preview_entities.png"));
    }

    /** Пишет спрайт из карты символов. Ширина берётся из первой строки, недостающие строки добиваются прозрачным. */
    static void sprite(String path, int width, int height, String... rows) throws IOException {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < height; y++) {
            String row = y < rows.length ? rows[y] : "";
            for (int x = 0; x < width; x++) {
                char c = x < row.length() ? row.charAt(x) : '.';
                Integer color = PALETTE.get(c);
                if (color == null) {
                    throw new IllegalArgumentException(path + ": неизвестный символ '" + c + "' в строке " + y);
                }
                img.setRGB(x, y, color);
            }
        }
        write(path, img);
    }

    static void sprite16(String path, String... rows) throws IOException {
        sprite(path, 16, 16, rows);
    }

    static void write(String path, BufferedImage img) throws IOException {
        File file = new File(OUT + path + ".png");
        file.getParentFile().mkdirs();
        ImageIO.write(img, "png", file);
        System.out.println("  " + path + " " + img.getWidth() + "x" + img.getHeight());
    }

    static BufferedImage canvas(int w, int h) {
        return new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
    }

    /** Заливка шумом из набора цветов с весами. */
    static void noise(BufferedImage img, Random rng, int x0, int y0, int w, int h, int[] colors, int[] weights) {
        int total = 0;
        for (int wgt : weights) total += wgt;
        for (int y = y0; y < y0 + h; y++) {
            for (int x = x0; x < x0 + w; x++) {
                int roll = rng.nextInt(total);
                int idx = 0;
                while (roll >= weights[idx]) {
                    roll -= weights[idx];
                    idx++;
                }
                img.setRGB(x, y, colors[idx]);
            }
        }
    }

    static int c(char ch) {
        return PALETTE.get(ch);
    }

    static int rgb(int r, int g, int b) {
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }

    static int mix(int a, int b, float t) {
        int ar = (a >> 16) & 255, ag = (a >> 8) & 255, ab = a & 255;
        int br = (b >> 16) & 255, bg = (b >> 8) & 255, bb = b & 255;
        return rgb(Math.round(ar + (br - ar) * t), Math.round(ag + (bg - ag) * t), Math.round(ab + (bb - ab) * t));
    }

    static int shade(int color, float f) {
        int r = Math.min(255, Math.round(((color >> 16) & 255) * f));
        int g = Math.min(255, Math.round(((color >> 8) & 255) * f));
        int b = Math.min(255, Math.round((color & 255) * f));
        return rgb(r, g, b);
    }

    static void fill(BufferedImage img, int x0, int y0, int w, int h, int color) {
        for (int y = y0; y < y0 + h; y++) for (int x = x0; x < x0 + w; x++) img.setRGB(x, y, color);
    }

    static void put(BufferedImage img, int x, int y, int color) {
        if (x >= 0 && y >= 0 && x < img.getWidth() && y < img.getHeight()) img.setRGB(x, y, color);
    }
}
