import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

/** Экран дневника и иконки эффектов. */
final class Gui {
    private Gui() {
    }

    static void all() throws IOException {
        journal();
        venom();
    }

    /** Лист 176x208 в левом верхнем углу атласа 256x256: кожаный корешок слева, бумага с лёгким зерном, тень у края. */
    static void journal() throws IOException {
        Random rng = new Random(11);
        BufferedImage img = Art.canvas(256, 256);
        int w = 176, h = 208;
        int paperA = Art.rgb(0xEC, 0xDF, 0xBD), paperB = Art.rgb(0xE4, 0xD6, 0xB2), paperC = Art.rgb(0xF2, 0xE8, 0xCC);
        int edge = Art.rgb(0xB9, 0xA4, 0x78), shadow = Art.rgb(0xCF, 0xBE, 0x92);
        int leather = Art.rgb(0x5B, 0x3E, 0x22), leatherL = Art.rgb(0x74, 0x50, 0x2C), leatherD = Art.rgb(0x3E, 0x29, 0x15);
        int outline = Art.rgb(0x2A, 0x1E, 0x12);

        // бумага
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int r = rng.nextInt(10);
                int col = r < 6 ? paperA : r < 9 ? paperB : paperC;
                img.setRGB(x, y, col);
            }
        }
        // мягкая тень по правому и нижнему краю, как у лежащего листа
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int dx = w - 1 - x, dy = h - 1 - y;
                int d = Math.min(dx, dy);
                if (d < 6) img.setRGB(x, y, Art.mix(img.getRGB(x, y), shadow, (6 - d) / 7f));
            }
        }
        // корешок: кожа с прошивкой
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < 14; x++) {
                int col = x < 2 ? leatherD : x < 12 ? leather : leatherL;
                if (rng.nextInt(9) == 0) col = Art.mix(col, leatherL, 0.5f);
                img.setRGB(x, y, col);
            }
            if (y % 8 == 3) {
                img.setRGB(6, y, Art.rgb(0xC9, 0xB0, 0x7C));
                img.setRGB(7, y, Art.rgb(0xC9, 0xB0, 0x7C));
            }
        }
        // стык кожи и бумаги: темная тень, потом светлая кромка
        for (int y = 0; y < h; y++) {
            img.setRGB(14, y, Art.mix(paperA, leatherD, 0.55f));
            img.setRGB(15, y, Art.mix(paperA, leatherD, 0.25f));
        }
        // контур листа
        for (int x = 0; x < w; x++) {
            img.setRGB(x, 0, outline);
            img.setRGB(x, h - 1, outline);
            img.setRGB(x, 1, edge);
            img.setRGB(x, h - 2, edge);
        }
        for (int y = 0; y < h; y++) {
            img.setRGB(0, y, outline);
            img.setRGB(w - 1, y, outline);
            img.setRGB(w - 2, y, edge);
        }
        // загнутый уголок внизу справа
        for (int i = 0; i < 10; i++) {
            for (int k = 0; k <= i; k++) {
                img.setRGB(w - 2 - k, h - 2 - (i - k), i == 9 ? edge : Art.rgb(0xD8, 0xC8, 0x9E));
            }
        }
        // пара пятен от воды
        blot(img, rng, 120, 40, 9, Art.rgb(0xD9, 0xC9, 0xA0));
        blot(img, rng, 48, 170, 6, Art.rgb(0xDD, 0xCD, 0xA6));
        Art.write("gui/journal", img);
    }

    static void blot(BufferedImage img, Random rng, int cx, int cy, int r, int col) {
        for (int y = -r; y <= r; y++) {
            for (int x = -r; x <= r; x++) {
                double d = Math.sqrt(x * x + y * y) / r;
                if (d > 1) continue;
                float t = d > 0.8 ? 0.6f : 0.25f;
                if (rng.nextInt(4) == 0) t *= 0.5f;
                Art.put(img, cx + x, cy + y, Art.mix(img.getRGB(cx + x, cy + y), col, t));
            }
        }
    }

    /** Иконка яда 18x18: два клыка и зелёная капля. */
    static void venom() throws IOException {
        Art.sprite("mob_effect/venom", 18, 18,
                "..................",
                "...kk........kk...",
                "...kXk......kXk...",
                "...kXxk....kxXk...",
                "....kXk....kXk....",
                "....kxk....kxk....",
                ".....kk....kk.....",
                "........kk........",
                ".......kVVk.......",
                "......kVVVVk......",
                ".....kVVgVVVk.....",
                ".....kVggVVVk.....",
                ".....kVgggVVk.....",
                ".....kVggggVk.....",
                "......kVgggk......",
                ".......kkkk.......",
                "..................",
                "..................");
    }
}
