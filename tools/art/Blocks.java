import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

/** Блоки 16x16: растения крестом и процедурные поверхности. */
final class Blocks {
    private Blocks() {
    }

    static void all() throws IOException {
        plants();
        forest();
        surfaces();
    }

    static void plants() throws IOException {
        // Таро: два сердцевидных листа на длинных черешках.
        Art.sprite16("block/taro",
                "................",
                "..kkkk....kkkk..",
                ".kGGGGk..kGGGGk.",
                "kGGgggGk.kGgggGk",
                "kGgggegk.kgeggGk",
                "kGggeegk.kgeeggk",
                ".kgeeekk.kkeeek.",
                "..kkekk...kkek..",
                "....ke....ke....",
                "....ke...ke.....",
                ".....ke.ke......",
                ".....kekke......",
                "......keke......",
                "......kek.......",
                "......ke........",
                "......kk........");

        // Горький лист: кустик с узкими зубчатыми листьями и бледным цветком.
        Art.sprite16("block/bitter_herb",
                "......kk........",
                ".....kXxk.......",
                "....kkgek.......",
                "..kkGgkekkk.....",
                ".kGggek.kGgek...",
                "..kkgek.kgeek...",
                "....ke..kek.....",
                ".kkkkekkke......",
                "kGggekGggek.....",
                ".kkeekkgeek.....",
                "....kekek.......",
                ".kkkkekekkk.....",
                "kGggekeeGgek....",
                ".kkeekekkeek....",
                ".....kek........",
                ".....kk.........");

        // Колючий куст: тёмные переплетённые ветви, редкие листья, светлые шипы.
        Art.sprite16("block/thorny_brush",
                ".k......k.....k.",
                ".kd....kdY...kd.",
                "..kd..kd..k.kd..",
                "...kdkdk.kdkd...",
                ".kY.kddk.kdd....",
                ".kd.kd.kkdgkd...",
                "..kddkY.kd..kd..",
                "k..kd..k.kd.g.k.",
                "kd.kdgkd..kd.kd.",
                ".kdkdkd..Y.kdkd.",
                "..kdddk..k..kdk.",
                "...kdk..kd...kd.",
                "..kYkd.kd.kg.kd.",
                ".kd.kdkd.kd..kd.",
                ".kd..kdk.kd.kd..",
                "..kd.kd...kdkd..");

        // Дыхательный побег: бледная розетка на тонком стебле.
        Art.sprite16("block/air_plant",
                "................",
                "......k.k.......",
                ".....kLkLk......",
                "....kLTkLTk.....",
                "....kTLkLTk.....",
                "..kkkLTkTLkkk...",
                ".kLLTkLkLkTLLk..",
                "..kkLTkLkTLkk...",
                "....kLLkLLk.....",
                ".....kTkTk......",
                "......kek.......",
                "......kek.......",
                ".....kgeek......",
                "....kgeeek......",
                ".....kkkk.......",
                "................");
    }

    static void forest() throws IOException {
        // Геликония: красно-оранжевые гребни на высоком стебле, широкий лист сбоку.
        Art.sprite16("block/heliconia",
                "........kk......",
                ".......kZZk.....",
                "......kZZZk.....",
                "......kZZkk.....",
                ".....kZZkCk.....",
                ".....kZZkCCk....",
                "....kZZk.kCk....",
                "....kZZk..k.....",
                "...kZZkkk.......",
                "...kZk.ke.kkk...",
                "....k..kekGggk..",
                ".kkk...kekgeek..",
                "kGggk..ke.kkk...",
                "kgeeek.ke.......",
                ".kkkk..ke.......",
                ".......kk.......");

        // Имбирная лилия: белые лепестки на тонких стеблях, узкие листья.
        Art.sprite16("block/ginger_lily",
                "................",
                "....kk....kk....",
                "...kXxk..kXXk...",
                "..kXXXXkkXxXXk..",
                "...kxXPkkPXxk...",
                "....kXk..kXk....",
                ".....ke..ke.....",
                ".kk..ke..ke.kk..",
                "kGgk.ke..kekgGk.",
                "kgeek.ke.ke.keek",
                ".kkek.kekke.kek.",
                "...kek.kek.kek..",
                "....kekke.kek...",
                ".....keke.ke....",
                "......kekke.....",
                "......kkkk......");

        String[] bromeliad = {
                "................",
                "................",
                "................",
                "..k..........k..",
                ".kGk........kGk.",
                ".kGgk..kk..kgGk.",
                "..kGgk.kk.kgGk..",
                "..kGggkkkkggGk..",
                "...kGgZZZZgGk...",
                "...kGgZZZZgGk...",
                "....kgZZZZgk....",
                "....kggeeggk....",
                ".....kgeegk.....",
                "......keek......",
                "......kkkk......",
                "................",
        };
        Art.sprite16("block/bromeliad", bromeliad);
        // Заполненная: вода блестит между листьями.
        String[] filled = bromeliad.clone();
        filled[7] = "..kGggkAAkkggGk.".substring(0, 16);
        filled[8] = "...kGgAAAAgGk...";
        filled[9] = "...kGgZAAZgGk...";
        Art.sprite16("block/bromeliad_filled", filled);

        // Трутовик: оранжево-бурые полки на короткой ножке.
        Art.sprite16("block/log_fungus",
                "................",
                "................",
                "................",
                "................",
                "................",
                "....kkkkkkkk....",
                "..kkCCCCCCCCkk..",
                ".kCCcCCCCCCcCCk.",
                ".kccccccccccccck".substring(0, 16),
                "..kkkkCcckkkkk..",
                "...kkCCCCCCk....",
                "..kCCcCCCCCck...",
                "..kcccccccccck..",
                "...kkkkcckkkk...",
                "......kvvk......",
                "......kkkk......");

        // Лиана-водонос: толстый витой стебель во всю высоту, мелкие листья, тайлится по вертикали.
        Art.sprite16("block/water_liana",
                ".....kwdk.......",
                ".....kwdk.kk....",
                "....kwdkkkGgk...",
                "....kwdk.kgek...",
                "....kwdk..kk....",
                ".....kwdk.......",
                ".....kwdk.......",
                "..kk..kwdk......",
                ".kGgkkkwdk......",
                ".kgek.kwdk......",
                "..kk.kwdk.......",
                ".....kwdk.......",
                ".....kwdk..kk...",
                "....kwdkkkkGgk..",
                "....kwdk..kgek..",
                ".....kwdk..kk...");
    }

    static void surfaces() throws IOException {
        Random rng = new Random(7);

        // Шёлковый узел: белая паутина с серыми нитями.
        BufferedImage silk = Art.canvas(16, 16);
        Art.noise(silk, rng, 0, 0, 16, 16, new int[]{Art.c('X'), Art.c('x'), Art.rgb(0xC9, 0xC9, 0xC0)}, new int[]{6, 3, 1});
        for (int i = 0; i < 5; i++) {
            int x = rng.nextInt(16), y = rng.nextInt(16);
            for (int k = 0; k < 6; k++) Art.put(silk, (x + k) & 15, (y + k / 2) & 15, Art.rgb(0xB8, 0xB8, 0xAE));
        }
        Art.write("block/silk_pod", silk);

        // Открытая жила: серая порода с ржавыми и светлыми вкраплениями.
        BufferedImage vein = Art.canvas(16, 16);
        int[] stone = {Art.rgb(0x7E, 0x7E, 0x78), Art.rgb(0x8A, 0x8A, 0x83), Art.rgb(0x6F, 0x6F, 0x69), Art.rgb(0x62, 0x62, 0x5D)};
        Art.noise(vein, rng, 0, 0, 16, 16, stone, new int[]{4, 3, 3, 1});
        int[][] ore = {{2, 3}, {9, 2}, {12, 7}, {5, 9}, {10, 12}, {3, 13}, {14, 13}, {7, 6}};
        for (int[] o : ore) {
            int rust = Art.rgb(0xB0, 0x6A, 0x3A);
            int shine = Art.rgb(0xDD, 0xDD, 0xD0);
            Art.put(vein, o[0], o[1], rust);
            Art.put(vein, o[0] + 1, o[1], shine);
            Art.put(vein, o[0], o[1] + 1, Art.rgb(0x7A, 0x45, 0x25));
            Art.put(vein, o[0] + 1, o[1] + 1, rust);
        }
        Art.write("block/vein", vein);

        // Тяжёлый ил: сине-чёрная жижа с пузырями.
        BufferedImage seepTop = Art.canvas(16, 16);
        int[] mud = {Art.rgb(0x23, 0x27, 0x2C), Art.rgb(0x2C, 0x31, 0x36), Art.rgb(0x1B, 0x1E, 0x22), Art.rgb(0x35, 0x3B, 0x3F)};
        Art.noise(seepTop, rng, 0, 0, 16, 16, mud, new int[]{4, 3, 2, 1});
        int[][] bubbles = {{3, 4}, {10, 3}, {7, 9}, {12, 11}, {4, 12}};
        for (int[] b : bubbles) {
            Art.put(seepTop, b[0], b[1], Art.rgb(0x4C, 0x58, 0x5C));
            Art.put(seepTop, b[0] + 1, b[1], Art.rgb(0x5E, 0x6C, 0x70));
            Art.put(seepTop, b[0], b[1] + 1, Art.rgb(0x3E, 0x48, 0x4C));
            Art.put(seepTop, b[0] + 1, b[1] + 1, Art.rgb(0x4C, 0x58, 0x5C));
        }
        Art.write("block/seep_top", seepTop);
        BufferedImage seepSide = Art.canvas(16, 16);
        Art.noise(seepSide, rng, 0, 0, 16, 16, mud, new int[]{4, 2, 3, 1});
        // подтёки сверху
        for (int x = 0; x < 16; x += 3) {
            int len = 2 + rng.nextInt(5);
            for (int y = 0; y < len; y++) Art.put(seepSide, x, y, Art.rgb(0x2E, 0x36, 0x3A));
        }
        Art.write("block/seep_side", seepSide);

        // Гнилой настил: слежавшаяся листва сверху, бурая гниль сбоку.
        BufferedImage litterTop = Art.canvas(16, 16);
        int[] litter = {Art.rgb(0x6A, 0x4F, 0x2A), Art.rgb(0x56, 0x3F, 0x22), Art.rgb(0x7C, 0x66, 0x33), Art.rgb(0x3F, 0x52, 0x24), Art.rgb(0x4A, 0x33, 0x1A)};
        Art.noise(litterTop, rng, 0, 0, 16, 16, litter, new int[]{4, 3, 2, 2, 2});
        // прожилки листьев
        for (int i = 0; i < 7; i++) {
            int x = rng.nextInt(16), y = rng.nextInt(16);
            int dir = rng.nextBoolean() ? 1 : -1;
            for (int k = 0; k < 4; k++) Art.put(litterTop, (x + k) & 15, (y + k * dir) & 15, Art.rgb(0x8B, 0x72, 0x3C));
        }
        Art.write("block/litter_top", litterTop);
        BufferedImage litterSide = Art.canvas(16, 16);
        Art.noise(litterSide, rng, 0, 0, 16, 16, new int[]{Art.rgb(0x5A, 0x42, 0x24), Art.rgb(0x4A, 0x35, 0x1C), Art.rgb(0x6B, 0x52, 0x2E), Art.rgb(0x3A, 0x2A, 0x16)}, new int[]{4, 3, 2, 1});
        // горизонтальные слои
        for (int y = 3; y < 16; y += 5) {
            for (int x = 0; x < 16; x++) if (rng.nextInt(3) > 0) Art.put(litterSide, x, y, Art.rgb(0x33, 0x25, 0x12));
        }
        Art.write("block/litter_side", litterSide);

        // Холст: плотное плетение двух оттенков. Тёмный вариант для свёртка.
        BufferedImage canvas = weave(Art.rgb(0xC9, 0xB5, 0x86), Art.rgb(0xB8, 0xA3, 0x74), Art.rgb(0xA3, 0x8E, 0x60), rng);
        Art.write("block/canvas", canvas);
        BufferedImage canvasDark = weave(Art.rgb(0x7D, 0x66, 0x44), Art.rgb(0x6C, 0x57, 0x39), Art.rgb(0x58, 0x45, 0x2C), rng);
        Art.write("block/canvas_dark", canvasDark);

        // Волокно верёвки: витая прядь, две нити по 2 пикселя слева в UV (0..2).
        BufferedImage fiber = Art.canvas(16, 16);
        for (int y = 0; y < 16; y++) {
            for (int x = 0; x < 16; x++) {
                int twist = (x + y) % 4;
                int col = twist == 0 ? Art.c('u') : twist == 1 ? Art.c('y') : Art.c('Y');
                fiber.setRGB(x, y, col);
            }
        }
        Art.write("block/fiber", fiber);

        // Срез бамбука: горизонтальная перекладина с узлами.
        BufferedImage bamboo = Art.canvas(16, 16);
        for (int y = 0; y < 16; y++) {
            for (int x = 0; x < 16; x++) {
                int col = (y % 5 == 0) ? Art.c('N') : (x % 7 == 0) ? Art.c('n') : (y % 5 == 1) ? Art.c('B') : Art.c('b');
                bamboo.setRGB(x, y, col);
            }
        }
        Art.write("block/bamboo_cut", bamboo);
    }

    static BufferedImage weave(int light, int mid, int dark, Random rng) {
        BufferedImage img = Art.canvas(16, 16);
        for (int y = 0; y < 16; y++) {
            for (int x = 0; x < 16; x++) {
                boolean over = ((x / 2) + (y / 2)) % 2 == 0;
                int base = over ? light : mid;
                if ((over && y % 2 == 1) || (!over && x % 2 == 1)) base = Art.mix(base, dark, 0.35f);
                if (rng.nextInt(12) == 0) base = Art.mix(base, dark, 0.5f);
                img.setRGB(x, y, base);
            }
        }
        return img;
    }
}
