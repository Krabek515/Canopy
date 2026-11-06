import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

/**
 * Текстуры зверей под развёртку кубов Bedrock/GeckoLib.
 * Куб размером w,h,d с uv (u,v) раскладывается так:
 *   верх (u+d, v, w, d), низ (u+d+w, v, w, d),
 *   право (u, v+d, d, h), перед (u+d, v+d, w, h), лево (u+d+w, v+d, d, h), зад (u+d+w+d, v+d, w, h).
 */
final class Beasts {
    private Beasts() {
    }

    static void all() throws IOException {
        jaguar();
        monkey();
        snake();
        agouti();
        capybara();
        tapir();
        caiman();
        gorilla();
        toucan();
    }

    static final int CAP_BASE = Art.rgb(0x8C, 0x6A, 0x3E), CAP_LIGHT = Art.rgb(0xA6, 0x82, 0x50), CAP_BELLY = Art.rgb(0xB9, 0x9A, 0x6A), CAP_DARK = Art.rgb(0x4A, 0x36, 0x1E);
    static final int TAP_BASE = Art.rgb(0x3E, 0x33, 0x2C), TAP_LIGHT = Art.rgb(0x52, 0x45, 0x3C), TAP_BELLY = Art.rgb(0x5E, 0x52, 0x48), TAP_MANE = Art.rgb(0x1E, 0x18, 0x14), TAP_EAR = Art.rgb(0xD8, 0xD0, 0xC4);
    static final int CAI_BASE = Art.rgb(0x46, 0x52, 0x2E), CAI_LIGHT = Art.rgb(0x5C, 0x6A, 0x3A), CAI_BELLY = Art.rgb(0xC9, 0xC4, 0x8A), CAI_DARK = Art.rgb(0x23, 0x2B, 0x14), CAI_TOOTH = Art.rgb(0xEC, 0xE8, 0xD8);
    static final int GOR_BASE = Art.rgb(0x22, 0x1F, 0x1E), GOR_LIGHT = Art.rgb(0x33, 0x2F, 0x2D), GOR_SILVER = Art.rgb(0x7A, 0x76, 0x72), GOR_SKIN = Art.rgb(0x15, 0x12, 0x11), GOR_FACE = Art.rgb(0x2A, 0x24, 0x22);
    static final int TOU_BASE = Art.rgb(0x16, 0x14, 0x12), TOU_LIGHT = Art.rgb(0x24, 0x22, 0x1E), TOU_BIB = Art.rgb(0xF4, 0xD9, 0x4A), TOU_BEAK = Art.rgb(0xF0, 0xA0, 0x20), TOU_BEAK_TIP = Art.rgb(0x2A, 0x1E, 0x14), TOU_RED = Art.rgb(0xC8, 0x2A, 0x24);

    static void capybara() throws IOException {
        Random rng = new Random(21);
        BufferedImage img = Art.canvas(64, 64);
        box(img, rng, 0, 0, 10, 8, 16, CAP_BASE, CAP_LIGHT, CAP_BELLY, 0.9f);
        box(img, rng, 0, 24, 8, 7, 7, CAP_BASE, CAP_LIGHT, CAP_BELLY, 0.9f);
        // перед головы (7,31) 8x7: глаза высоко и по бокам
        eye(img, 7 + 1, 31 + 2, CAP_LIGHT);
        eye(img, 7 + 6, 31 + 2, CAP_LIGHT);
        // морда 6x4x2 at (30,24): перед (32,26) 6x4, ноздри
        box(img, rng, 30, 24, 6, 4, 2, CAP_BASE, CAP_LIGHT, CAP_BELLY, 0.9f);
        Art.put(img, 33, 27, CAP_DARK);
        Art.put(img, 36, 27, CAP_DARK);
        box(img, rng, 46, 24, 2, 2, 1, CAP_DARK, CAP_BASE, CAP_BASE, 0.9f);
        box(img, rng, 0, 38, 3, 5, 3, CAP_BASE, CAP_LIGHT, CAP_DARK, 0.88f);
        box(img, rng, 12, 38, 3, 5, 3, CAP_BASE, CAP_LIGHT, CAP_DARK, 0.88f);
        Art.write("entity/capybara", img);
    }

    static void tapir() throws IOException {
        Random rng = new Random(23);
        BufferedImage img = Art.canvas(64, 64);
        box(img, rng, 0, 0, 10, 9, 17, TAP_BASE, TAP_LIGHT, TAP_BELLY, 0.9f);
        box(img, rng, 0, 26, 8, 2, 10, TAP_MANE, TAP_MANE, TAP_MANE, 0.95f);
        box(img, rng, 0, 38, 7, 7, 7, TAP_BASE, TAP_LIGHT, TAP_BELLY, 0.9f);
        eye(img, 7 + 1, 45 + 2, TAP_LIGHT);
        eye(img, 7 + 5, 45 + 2, TAP_LIGHT);
        box(img, rng, 28, 38, 4, 3, 3, TAP_BASE, TAP_LIGHT, TAP_BELLY, 0.9f); // хобот
        Art.fill(img, 31, 41, 4, 1, TAP_MANE);
        box(img, rng, 42, 38, 2, 3, 1, TAP_BASE, TAP_BASE, TAP_BASE, 0.9f);
        Art.fill(img, 43, 38, 2, 1, TAP_EAR); // белая кромка уха сверху
        box(img, rng, 36, 26, 3, 7, 3, TAP_BASE, TAP_LIGHT, TAP_MANE, 0.88f);
        box(img, rng, 48, 26, 3, 7, 3, TAP_BASE, TAP_LIGHT, TAP_MANE, 0.88f);
        box(img, rng, 44, 44, 2, 3, 1, TAP_BASE, TAP_BASE, TAP_BASE, 0.9f);
        Art.write("entity/tapir", img);
    }

    static void caiman() throws IOException {
        Random rng = new Random(29);
        BufferedImage img = Art.canvas(64, 64);
        box(img, rng, 0, 0, 8, 4, 14, CAI_BASE, CAI_LIGHT, CAI_BELLY, 0.9f);
        // поперечные тёмные полосы по спине (верх (14,0) 8x14)
        for (int z = 1; z < 14; z += 3) for (int x = 0; x < 8; x++) if (rng.nextInt(4) > 0) Art.put(img, 14 + x, z, CAI_DARK);
        box(img, rng, 0, 18, 6, 1, 12, CAI_DARK, CAI_DARK, CAI_DARK, 0.95f); // гребень
        box(img, rng, 0, 31, 6, 3, 5, CAI_BASE, CAI_LIGHT, CAI_BELLY, 0.9f);  // голова
        box(img, rng, 22, 31, 2, 1, 2, CAI_DARK, CAI_DARK, CAI_DARK, 0.95f); // глазные бугры
        Art.put(img, 24, 31, SNK_EYE);
        Art.put(img, 25, 31, SNK_EYE);
        box(img, rng, 0, 39, 5, 2, 7, CAI_BASE, CAI_LIGHT, CAI_BELLY, 0.9f); // верхняя челюсть
        box(img, rng, 24, 39, 5, 2, 7, CAI_BELLY, CAI_BASE, CAI_BELLY, 0.9f); // нижняя челюсть
        // зубы по краям низа верхней челюсти: грани право (0,46) и лево (12,46) 7x2 — нижний ряд
        for (int z = 0; z < 7; z += 2) {
            Art.put(img, 0 + z, 47, CAI_TOOTH);
            Art.put(img, 12 + z, 47, CAI_TOOTH);
            Art.put(img, 24 + z, 46, CAI_TOOTH);
            Art.put(img, 36 + z, 46, CAI_TOOTH);
        }
        box(img, rng, 48, 39, 3, 2, 3, CAI_BASE, CAI_LIGHT, CAI_DARK, 0.9f);
        box(img, rng, 48, 44, 3, 2, 3, CAI_BASE, CAI_LIGHT, CAI_DARK, 0.9f);
        box(img, rng, 36, 18, 6, 3, 8, CAI_BASE, CAI_LIGHT, CAI_BELLY, 0.9f);
        box(img, rng, 36, 29, 4, 2, 8, CAI_BASE, CAI_LIGHT, CAI_BELLY, 0.9f);
        box(img, rng, 44, 0, 2, 1, 7, CAI_BASE, CAI_LIGHT, CAI_BELLY, 0.9f);
        for (int z = 0; z < 8; z += 2) {
            Art.put(img, 36 + 8 + 2, 18 + z, CAI_DARK);
            Art.put(img, 36 + 8 + 1, 29 + z, CAI_DARK);
        }
        Art.write("entity/caiman", img);
    }

    static void gorilla() throws IOException {
        Random rng = new Random(31);
        BufferedImage img = Art.canvas(64, 64);
        box(img, rng, 0, 0, 12, 12, 8, GOR_BASE, GOR_LIGHT, GOR_BASE, 0.9f);
        // серебряная спина: задняя грань (u+d+w+d, v+d) = (8+12+8, 8) = (28,8) 12x12, нижняя половина
        for (int y = 5; y < 12; y++) for (int x = 0; x < 12; x++) if (rng.nextInt(5) > 0) Art.put(img, 28 + x, 8 + y, GOR_SILVER);
        box(img, rng, 0, 20, 10, 3, 6, GOR_BASE, GOR_BASE, GOR_LIGHT, 0.9f);
        box(img, rng, 32, 20, 7, 7, 6, GOR_BASE, GOR_LIGHT, GOR_BASE, 0.9f);
        // лицо: перед головы (38,26) 7x7 — кожа
        Art.fill(img, 38 + 1, 26 + 1, 5, 6, GOR_FACE);
        eye(img, 38 + 2, 26 + 3, GOR_FACE);
        eye(img, 38 + 4, 26 + 3, GOR_FACE);
        box(img, rng, 40, 0, 5, 2, 4, GOR_BASE, GOR_BASE, GOR_BASE, 0.9f); // гребень
        box(img, rng, 40, 6, 6, 3, 2, GOR_FACE, GOR_FACE, GOR_FACE, 0.95f); // морда
        Art.put(img, 43, 7, GOR_SKIN);
        Art.put(img, 45, 7, GOR_SKIN);
        box(img, rng, 0, 29, 4, 18, 4, GOR_BASE, GOR_LIGHT, GOR_SKIN, 0.9f);
        Art.fill(img, 0, 29 + 4 + 15, 16, 3, GOR_SKIN); // кисти
        box(img, rng, 16, 29, 4, 8, 4, GOR_BASE, GOR_LIGHT, GOR_SKIN, 0.9f);
        Art.fill(img, 16, 29 + 4 + 6, 16, 2, GOR_SKIN);
        Art.write("entity/gorilla", img);
    }

    static void toucan() throws IOException {
        Random rng = new Random(37);
        BufferedImage img = Art.canvas(64, 32);
        box(img, rng, 0, 0, 4, 5, 6, TOU_BASE, TOU_LIGHT, TOU_BASE, 0.9f);
        // жёлтая грудка: перед тела (6,6) 4x5 и низ (10,0) 4x6 частично
        Art.fill(img, 6, 6, 4, 5, TOU_BIB);
        for (int x = 0; x < 4; x++) Art.put(img, 6 + x, 10, TOU_RED);
        box(img, rng, 20, 0, 1, 4, 5, TOU_BASE, TOU_LIGHT, TOU_BASE, 0.9f);
        box(img, rng, 0, 11, 4, 4, 4, TOU_BASE, TOU_LIGHT, TOU_BIB, 0.9f);
        Art.fill(img, 4, 15, 4, 4, TOU_BIB); // лицо жёлтое
        Art.put(img, 5, 16, EYE_DARK);
        Art.put(img, 6, 16, EYE_DARK);
        // клюв 3x3x6 at (32,0): всё оранжевое, кончик тёмный (перед (38,6) 3x3)
        box(img, rng, 32, 0, 3, 3, 6, TOU_BEAK, TOU_BEAK, Art.shade(TOU_BEAK, 0.85f), 0.95f);
        Art.fill(img, 38, 6, 3, 3, TOU_BEAK_TIP);
        for (int z = 0; z < 6; z++) Art.put(img, 32 + z, 6 + 2, Art.shade(TOU_BEAK, 0.7f));
        box(img, rng, 0, 20, 3, 1, 5, TOU_BASE, TOU_LIGHT, TOU_RED, 0.9f);
        box(img, rng, 16, 20, 1, 2, 1, Art.rgb(0x5A, 0x6E, 0x8A), Art.rgb(0x5A, 0x6E, 0x8A), Art.rgb(0x5A, 0x6E, 0x8A), 0.9f);
        Art.write("entity/toucan", img);
    }

    // ---- палитры ----
    static final int JAG_BASE = Art.rgb(0xD6, 0x9A, 0x3C), JAG_LIGHT = Art.rgb(0xE8, 0xB8, 0x66), JAG_BELLY = Art.rgb(0xF1, 0xE0, 0xC0),
            JAG_SPOT = Art.rgb(0x2A, 0x1E, 0x12), JAG_SPOT_IN = Art.rgb(0xB8, 0x7A, 0x2E), JAG_NOSE = Art.rgb(0x6B, 0x3A, 0x3A);
    static final int MON_BASE = Art.rgb(0x5C, 0x43, 0x2B), MON_LIGHT = Art.rgb(0x7A, 0x5B, 0x3C), MON_CAP = Art.rgb(0x2B, 0x1F, 0x14),
            MON_FACE = Art.rgb(0xE3, 0xC9, 0xA4), MON_SKIN = Art.rgb(0x3A, 0x2A, 0x1E);
    static final int SNK_BASE = Art.rgb(0x4F, 0x7A, 0x33), SNK_LIGHT = Art.rgb(0x6E, 0x9A, 0x44), SNK_DARK = Art.rgb(0x25, 0x3E, 0x18),
            SNK_BELLY = Art.rgb(0xC8, 0xC9, 0x8E), SNK_EYE = Art.rgb(0xE8, 0xC2, 0x2E);
    static final int AGO_BASE = Art.rgb(0x8A, 0x5C, 0x2E), AGO_LIGHT = Art.rgb(0xA8, 0x78, 0x3E), AGO_RUMP = Art.rgb(0xC2, 0x8A, 0x3A),
            AGO_BELLY = Art.rgb(0xD9, 0xC4, 0x9C), AGO_DARK = Art.rgb(0x4A, 0x30, 0x18);
    static final int EYE_WHITE = Art.rgb(0xF4, 0xF2, 0xE6), EYE_DARK = Art.rgb(0x12, 0x10, 0x0E);

    static void jaguar() throws IOException {
        Random rng = new Random(3);
        BufferedImage img = Art.canvas(64, 64);
        // корпус
        box(img, rng, 0, 20, 8, 6, 16, JAG_BASE, JAG_LIGHT, JAG_BELLY, 0.9f);
        rosettes(img, rng, 0, 20, 8, 6, 16, 26);
        // хребет
        box(img, rng, 0, 55, 7, 1, 8, Art.shade(JAG_BASE, 0.9f), JAG_BASE, JAG_BASE, 0.9f);
        rosettes(img, rng, 0, 55, 7, 1, 8, 4);
        // шея
        box(img, rng, 42, 0, 6, 5, 4, JAG_BASE, JAG_LIGHT, JAG_BELLY, 0.9f);
        rosettes(img, rng, 42, 0, 6, 5, 4, 5);
        // голова
        box(img, rng, 0, 0, 6, 5, 6, JAG_BASE, JAG_LIGHT, JAG_BELLY, 0.9f);
        rosettes(img, rng, 0, 0, 6, 5, 6, 6);
        // чистая морда без розеток: передняя грань (u+d, v+d) = (6,6), размер 6x5
        int fx = 0 + 6, fy = 0 + 6;
        Art.fill(img, fx, fy, 6, 5, Art.shade(JAG_BASE, 1.05f));
        Art.fill(img, fx, fy + 3, 6, 2, JAG_BELLY); // низ морды светлее
        eye(img, fx + 1, fy + 1, JAG_LIGHT);
        eye(img, fx + 4, fy + 1, JAG_LIGHT);
        Art.fill(img, fx + 2, fy + 4, 2, 1, JAG_NOSE); // нос внизу по центру
        // нос 4x2x2 at (24,0): перед (26,2) 4x2
        box(img, rng, 24, 0, 4, 2, 2, JAG_BELLY, JAG_BELLY, JAG_BELLY, 0.95f);
        Art.fill(img, 27, 2, 2, 1, JAG_NOSE);
        Art.fill(img, 26, 3, 4, 1, Art.shade(JAG_BELLY, 0.8f));
        // уши 2x2x1 at (36,0)
        box(img, rng, 36, 0, 2, 2, 1, JAG_SPOT, JAG_SPOT, JAG_SPOT, 0.9f);
        Art.fill(img, 37, 1, 2, 2, JAG_BASE); // внутренняя сторона уха светлее
        // лапы 3x8x3 at (0,44)
        box(img, rng, 0, 44, 3, 8, 3, JAG_BASE, JAG_LIGHT, JAG_SPOT, 0.9f);
        spots(img, rng, 0, 44, 3, 8, 3, 6);
        // хвост
        box(img, rng, 16, 44, 2, 2, 8, JAG_BASE, JAG_LIGHT, JAG_BELLY, 0.9f);
        spots(img, rng, 16, 44, 2, 2, 8, 6);
        box(img, rng, 36, 44, 2, 2, 6, JAG_BASE, JAG_LIGHT, JAG_BELLY, 0.9f);
        spots(img, rng, 36, 44, 2, 2, 6, 4);
        // тёмный кончик хвоста: задняя грань (u+d+w+d, v+d) = (36+6+2+6, 46) = (50,46) 2x2
        Art.fill(img, 50, 46, 2, 2, JAG_SPOT);
        Art.write("entity/jaguar", img);
    }

    static void monkey() throws IOException {
        Random rng = new Random(5);
        BufferedImage img = Art.canvas(64, 64);
        // тело 6x7x4 at (0,0): спина темнее, живот светлее
        box(img, rng, 0, 0, 6, 7, 4, MON_BASE, MON_LIGHT, MON_LIGHT, 0.88f);
        // голова 6x6x6 at (20,0): тёмная шапка сверху, светлое лицо спереди
        box(img, rng, 20, 0, 6, 6, 6, MON_BASE, MON_CAP, MON_LIGHT, 0.88f);
        int fx = 26, fy = 6; // перед головы 6x6
        Art.fill(img, fx, fy + 1, 6, 5, MON_FACE);
        for (int x = 0; x < 6; x++) {
            if (rng.nextInt(3) == 0) Art.put(img, fx + x, fy + 1, MON_CAP); // неровная граница шапки
        }
        eye(img, fx + 1, fy + 2, MON_FACE);
        eye(img, fx + 4, fy + 2, MON_FACE);
        // морда 3x2x1 at (44,0): перед (45,1) 3x2
        box(img, rng, 44, 0, 3, 2, 1, MON_FACE, MON_FACE, MON_FACE, 0.95f);
        Art.fill(img, 45, 1, 1, 1, MON_SKIN);
        Art.fill(img, 47, 1, 1, 1, MON_SKIN);
        Art.fill(img, 45, 2, 3, 1, Art.shade(MON_FACE, 0.8f));
        // уши 1x2x2 at (52,0)
        box(img, rng, 52, 0, 1, 2, 2, MON_BASE, MON_BASE, MON_SKIN, 0.9f);
        // руки 2x9x2 at (0,12): кисти тёмные
        box(img, rng, 0, 12, 2, 9, 2, MON_BASE, MON_LIGHT, MON_SKIN, 0.88f);
        for (int x = 0; x < 8; x++) for (int y = 0; y < 2; y++) Art.put(img, x, 14 + 9 - 2 + y, MON_SKIN);
        // ноги 2x5x2 at (8,12)
        box(img, rng, 8, 12, 2, 5, 2, MON_BASE, MON_LIGHT, MON_SKIN, 0.88f);
        for (int x = 8; x < 16; x++) Art.put(img, x, 14 + 5 - 1, MON_SKIN);
        // хвост
        box(img, rng, 16, 12, 2, 2, 8, MON_BASE, MON_LIGHT, MON_LIGHT, 0.88f);
        box(img, rng, 36, 12, 2, 2, 6, MON_BASE, MON_LIGHT, MON_LIGHT, 0.88f);
        Art.write("entity/monkey", img);
    }

    static void snake() throws IOException {
        Random rng = new Random(9);
        BufferedImage img = Art.canvas(64, 32);
        // голова 4x3x5 at (0,0)
        box(img, rng, 0, 0, 4, 3, 5, SNK_BASE, SNK_LIGHT, SNK_BELLY, 0.9f);
        // глаза на боках головы: право (0,5) 5x3, лево (9,5) 5x3 — глаз ближе к морде (морда = -Z = левая сторона правой грани?)
        Art.put(img, 1, 6, SNK_EYE);
        Art.put(img, 12, 6, SNK_EYE);
        // перед головы (5,5) 4x3: ноздри
        Art.put(img, 6, 6, SNK_DARK);
        Art.put(img, 7, 6, SNK_DARK);
        // язык 1x1x2 at (18,0)
        box(img, rng, 18, 0, 1, 1, 2, Art.rgb(0xB0, 0x2A, 0x2A), Art.rgb(0xB0, 0x2A, 0x2A), Art.rgb(0xB0, 0x2A, 0x2A), 1f);
        // сегменты
        int[][] segs = {{0, 8, 3, 3, 5}, {16, 8, 3, 3, 5}, {0, 16, 3, 3, 5}, {16, 16, 2, 2, 5}};
        for (int[] s : segs) {
            box(img, rng, s[0], s[1], s[2], s[3], s[4], SNK_BASE, SNK_LIGHT, SNK_BELLY, 0.9f);
            diamonds(img, s[0], s[1], s[2], s[3], s[4]);
        }
        box(img, rng, 32, 8, 1, 1, 5, SNK_BASE, SNK_LIGHT, SNK_BELLY, 0.9f);
        Art.write("entity/snake", img);
    }

    static void agouti() throws IOException {
        Random rng = new Random(13);
        BufferedImage img = Art.canvas(64, 32);
        // тело 6x6x10 at (0,0): круп рыжее, живот светлый
        box(img, rng, 0, 0, 6, 6, 10, AGO_BASE, AGO_LIGHT, AGO_BELLY, 0.88f);
        // верх (10,0) 6x10 — задняя половина рыжая
        for (int z = 5; z < 10; z++) for (int x = 0; x < 6; x++) if (rng.nextInt(4) > 0) Art.put(img, 10 + x, z, AGO_RUMP);
        // бока (0,10) 10x6 и (16,10) 10x6 — задняя часть рыжее
        for (int z = 6; z < 10; z++) for (int y = 0; y < 4; y++) {
            if (rng.nextInt(3) > 0) Art.put(img, z, 10 + y, AGO_RUMP);
            if (rng.nextInt(3) > 0) Art.put(img, 16 + (9 - z), 10 + y, AGO_RUMP);
        }
        // голова 5x4x4 at (32,0): перед (36,4) 5x4
        box(img, rng, 32, 0, 5, 4, 4, AGO_BASE, AGO_LIGHT, AGO_BELLY, 0.88f);
        eye(img, 37, 5, AGO_LIGHT);
        eye(img, 39, 5, AGO_LIGHT);
        // нос 3x2x1 at (50,4): перед (51,5) 3x2
        box(img, rng, 50, 4, 3, 2, 1, AGO_BASE, AGO_LIGHT, AGO_BELLY, 0.9f);
        Art.fill(img, 52, 5, 1, 1, AGO_DARK);
        // уши 1x2x1 at (50,0)
        box(img, rng, 50, 0, 1, 2, 1, AGO_DARK, AGO_BASE, AGO_BASE, 0.9f);
        // лапы
        box(img, rng, 0, 16, 2, 3, 2, AGO_DARK, AGO_BASE, AGO_DARK, 0.9f);
        box(img, rng, 8, 16, 2, 3, 2, AGO_DARK, AGO_BASE, AGO_DARK, 0.9f);
        Art.write("entity/agouti", img);
    }

    // ---- кисти ----

    /** Заливает все шесть граней куба: верх светлее, низ цветом живота, бока базовые. */
    static void box(BufferedImage img, Random rng, int u, int v, int w, int h, int d, int base, int light, int belly, float sideShade) {
        noisy(img, rng, u + d, v, w, d, light);                       // верх
        noisy(img, rng, u + d + w, v, w, d, belly);                   // низ
        noisy(img, rng, u, v + d, d, h, Art.shade(base, sideShade));  // право
        noisy(img, rng, u + d, v + d, w, h, base);                    // перед
        noisy(img, rng, u + d + w, v + d, d, h, Art.shade(base, sideShade)); // лево
        noisy(img, rng, u + d + w + d, v + d, w, h, Art.shade(base, 0.85f));   // зад
    }

    static void noisy(BufferedImage img, Random rng, int x0, int y0, int w, int h, int color) {
        for (int y = y0; y < y0 + h; y++) {
            for (int x = x0; x < x0 + w; x++) {
                int r = rng.nextInt(10);
                int c = r < 6 ? color : r < 9 ? Art.shade(color, 0.93f) : Art.shade(color, 1.07f);
                Art.put(img, x, y, c);
            }
        }
    }

    /** Розетки ягуара по верху и бокам куба: тёмное кольцо, внутри темнее базы. */
    static void rosettes(BufferedImage img, Random rng, int u, int v, int w, int h, int d, int count) {
        int[][] faces = {{u + d, v, w, d}, {u, v + d, d, h}, {u + d, v + d, w, h}, {u + d + w, v + d, d, h}, {u + d + w + d, v + d, w, h}};
        for (int i = 0; i < count; i++) {
            int[] f = faces[rng.nextInt(faces.length)];
            if (f[2] < 3 || f[3] < 3) {
                spotOn(img, rng, f);
                continue;
            }
            int x = f[0] + rng.nextInt(f[2] - 2);
            int y = f[1] + rng.nextInt(f[3] - 2);
            // кольцо 3x3 с разрывом
            for (int dy = 0; dy < 3; dy++) for (int dx = 0; dx < 3; dx++) {
                boolean edge = dx == 0 || dy == 0 || dx == 2 || dy == 2;
                boolean corner = (dx == 0 || dx == 2) && (dy == 0 || dy == 2);
                if (corner && rng.nextBoolean()) continue;
                Art.put(img, x + dx, y + dy, edge ? JAG_SPOT : JAG_SPOT_IN);
            }
        }
    }

    static void spots(BufferedImage img, Random rng, int u, int v, int w, int h, int d, int count) {
        int[][] faces = {{u + d, v, w, d}, {u, v + d, d, h}, {u + d, v + d, w, h}, {u + d + w, v + d, d, h}, {u + d + w + d, v + d, w, h}};
        for (int i = 0; i < count; i++) spotOn(img, rng, faces[rng.nextInt(faces.length)]);
    }

    static void spotOn(BufferedImage img, Random rng, int[] f) {
        int x = f[0] + rng.nextInt(Math.max(1, f[2]));
        int y = f[1] + rng.nextInt(Math.max(1, f[3]));
        Art.put(img, x, y, JAG_SPOT);
    }

    /** Ромбы на спине змеи: верхняя и боковые грани. */
    static void diamonds(BufferedImage img, int u, int v, int w, int h, int d) {
        for (int z = 0; z < d; z++) {
            int cx = u + d + w / 2;
            int y = v + z;
            if (z % 2 == 0) {
                Art.put(img, cx, y, SNK_DARK);
                if (w >= 3) {
                    Art.put(img, cx - 1, y, SNK_DARK);
                    Art.put(img, cx + 1, y, SNK_DARK);
                }
            } else {
                Art.put(img, cx, y, SNK_LIGHT);
            }
            // боковые полоски
            Art.put(img, u + z, v + d, SNK_DARK);
            Art.put(img, u + d + w + z, v + d, SNK_DARK);
        }
    }

    static void eye(BufferedImage img, int x, int y, int lid) {
        Art.put(img, x, y, EYE_DARK);
        Art.put(img, x, y - 1, lid);
    }
}
