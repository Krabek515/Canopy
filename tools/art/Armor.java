import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

/**
 * Текстуры носимой брони (64x32, формат layer_1/layer_2).
 * База нейтральная-светлая: цвет даёт краситель предмета, как у ванильной кожи.
 */
public final class Armor {
    private Armor() {
    }

    static void all() throws IOException {
        // Волокно: плетение нити — светлый лён с редким тёмным стежком.
        fiber(1);
        fiber(2);
        // Шкура: выделанная кожа с швами — светлая основа под тонировку.
        hide(1);
        hide(2);
        // Респиратор: светлая резина под тёмную тонировку, клапан на передней грани.
        respirator();
    }

    static void respirator() throws IOException {
        BufferedImage img = Art.canvas(64, 32);
        Random rng = new Random(31);
        int light = Art.c('Y'), mid = Art.c('y'), dark = Art.c('u');
        for (int y = 0; y < 32; y++) {
            for (int x = 0; x < 64; x++) {
                int base = rng.nextInt(5) == 0 ? mid : light;
                if (rng.nextInt(11) == 0) base = Art.mix(base, dark, 0.35f);
                img.setRGB(x, y, base);
            }
        }
        seams(img, Art.c('p'), 1);
        // Клапан-фильтр: тёмный круг на передней грани шлема (x8-16, y8-15).
        int filter = Art.mix(mid, dark, 0.6f);
        for (int y = 11; y <= 14; y++) {
            for (int x = 10; x <= 13; x++) {
                Art.put(img, x, y, filter);
            }
        }
        Art.put(img, 10, 12, Art.c('t'));
        Art.put(img, 13, 12, Art.c('t'));
        Art.write("models/armor/respirator_layer_1", img);
    }

    static void fiber(int layer) throws IOException {
        BufferedImage img = Art.canvas(64, 32);
        Random rng = new Random(11 + layer);
        int light = Art.c('Y'), mid = Art.c('y'), dark = Art.c('u'), edge = Art.c('p');
        for (int y = 0; y < 32; y++) {
            for (int x = 0; x < 64; x++) {
                boolean over = ((x / 2) + (y / 2)) % 2 == 0;
                int base = over ? light : mid;
                if (over && y % 2 == 1) base = Art.mix(base, dark, 0.4f);
                if (rng.nextInt(14) == 0) base = Art.mix(base, dark, 0.55f);
                img.setRGB(x, y, base);
            }
        }
        seams(img, edge, layer);
        Art.write("models/armor/fiber_layer_" + layer, img);
    }

    static void hide(int layer) throws IOException {
        BufferedImage img = Art.canvas(64, 32);
        Random rng = new Random(23 + layer);
        int light = Art.c('P'), mid = Art.c('p'), dark = Art.c('u'), edge = Art.c('t');
        for (int y = 0; y < 32; y++) {
            for (int x = 0; x < 64; x++) {
                int base = rng.nextInt(4) == 0 ? mid : light;
                if (rng.nextInt(9) == 0) base = Art.mix(base, dark, 0.3f);
                img.setRGB(x, y, base);
            }
        }
        // Меховые потёртости-полосы по горизонтали.
        for (int i = 0; i < 10; i++) {
            int x0 = rng.nextInt(56), y0 = rng.nextInt(28), len = 3 + rng.nextInt(6);
            for (int k = 0; k < len; k++) {
                Art.put(img, x0 + k, y0, Art.mix(mid, dark, 0.4f));
            }
        }
        seams(img, edge, layer);
        Art.write("models/armor/hide_layer_" + layer, img);
    }

    /** Швы по граням боксов шлема/куртки/сапог/штанов, чтобы форма читалась. */
    static void seams(BufferedImage img, int color, int layer) {
        if (layer == 1) {
            // шлем: (0,0)-(31,15), куртка: (16,16)-(39,31), сапоги: (0,16)-(15,31)
            hline(img, 0, 31, 7, color);
            hline(img, 0, 31, 8, color);
            vline(img, 7, 0, 15, color);
            vline(img, 16, 0, 15, color);
            vline(img, 23, 0, 15, color);
            hline(img, 16, 39, 19, color);
            hline(img, 16, 39, 20, color);
            vline(img, 19, 16, 31, color);
            vline(img, 28, 16, 31, color);
            vline(img, 35, 16, 31, color);
            hline(img, 0, 15, 19, color);
            hline(img, 0, 15, 20, color);
            vline(img, 3, 16, 31, color);
            vline(img, 8, 16, 31, color);
            vline(img, 11, 16, 31, color);
        } else {
            // штаны: ноги (0,0)-(31,15) и (0,16)-(31,31), пояс (16,0)-(31,15)
            hline(img, 0, 31, 3, color);
            hline(img, 0, 31, 4, color);
            vline(img, 3, 0, 15, color);
            vline(img, 12, 0, 15, color);
            vline(img, 19, 0, 15, color);
            vline(img, 28, 0, 15, color);
            hline(img, 0, 31, 19, color);
            hline(img, 0, 31, 20, color);
            vline(img, 3, 16, 31, color);
            vline(img, 12, 16, 31, color);
            vline(img, 19, 16, 31, color);
            vline(img, 28, 16, 31, color);
        }
    }

    static void hline(BufferedImage img, int x0, int x1, int y, int c) {
        for (int x = x0; x <= x1; x++) {
            Art.put(img, x, y, c);
        }
    }

    static void vline(BufferedImage img, int x, int y0, int y1, int c) {
        for (int y = y0; y <= y1; y++) {
            Art.put(img, x, y, c);
        }
    }
}
