import java.io.IOException;

/** Предметы 16x16. */
final class Items {
    private Items() {
    }

    static void all() throws IOException {
        tools();
        flasks();
        food();
        medicine();
        clothing();
        misc();
    }

    static void tools() throws IOException {
        // Каменное мачете: широкое лезвие из отщепа, рукоять из бамбука.
        Art.sprite16("item/stone_machete",
                "...........kkkk.",
                "..........kSSSSk",
                ".........kSSSszk",
                "........kSSSszk.",
                ".......kSSSszk..",
                "......kSSsszk...",
                ".....kSSsszk....",
                "....kSSsszk.....",
                "...kSsszzk......",
                "..kkszzzk.......",
                ".kBbkkkk........",
                "kBbnk...........",
                "kbnNk...........",
                ".kkk............");

        // Металлическое мачете: светлая кромка, тёмная спинка, рукоять в коже.
        Art.sprite16("item/metal_machete",
                "...........kkkk.",
                "..........kIIIIk",
                ".........kIIIisk",
                "........kIIIisk.",
                ".......kIIIisk..",
                "......kIIiisk...",
                ".....kIIiisk....",
                "....kIIiisk.....",
                "...kIiissk......",
                "..kkisssk.......",
                ".kthkkkk........",
                "khtjk...........",
                "ktjjk...........",
                ".kkk............");

        // Зубило: кремневый наконечник на палке.
        Art.sprite16("item/stone_gouge",
                "..........kkk...",
                ".........kzSSk..",
                "........kzSSzk..",
                ".......kzSszk...",
                "......kkzzzk....",
                ".....kWwkkk.....",
                "....kWwdk.......",
                "...kWwdk........",
                "..kWwdk.........",
                ".kWwdk..........",
                "kWwdk...........",
                "kwdk............",
                ".kk.............");
    }

    static void flasks() throws IOException {
        String[] body = {
                "....kkkkkk......",
                "...kBBbbnnk.....",
                "...kNNNNNNk.....",
                "...kBBbbnnk.....",
                "...kBBbbnnk.....",
                "...kBBbbnnk.....",
                "...kNNNNNNk.....",
                "...kBBbbnnk.....",
                "...kBBbbnnk.....",
                "...kBbbnnNk.....",
                "....kkkkkk......"
        };
        // Пустая: открытый срез, внутри темно.
        Art.sprite16("item/bamboo_flask", join(new String[]{
                "................",
                "................",
                "....kkkkkk......",
                "...kdddddNk.....",
                "...kNddddNk....."}, slice(body, 1)));
        // Мутная вода.
        Art.sprite16("item/bamboo_flask_murky", join(new String[]{
                "................",
                "................",
                "....kkkkkk......",
                "...kMMmMmNk.....",
                "...kNmMmmNk....."}, slice(body, 1)));
        // Дождевая: зеленоватая прозрачная, без мути.
        Art.sprite16("item/bamboo_flask_fresh", join(new String[]{
                "................",
                "................",
                "....kkkkkk......",
                "...kTLTaaNk.....",
                "...kNaTaaNk....."}, slice(body, 1)));
        // Чистая: голубая с бликом.
        Art.sprite16("item/bamboo_flask_clean", join(new String[]{
                "................",
                "................",
                "....kkkkkk......",
                "...kAXAaaNk.....",
                "...kNAaaaNk....."}, slice(body, 1)));
    }

    static void food() throws IOException {
        // Корень таро: бурый клубень с кольцами и обрезанным стеблем.
        Art.sprite16("item/taro_root",
                ".......kk.......",
                "......kGgk......",
                ".....kGgeek.....",
                "......kgek......",
                ".......kk.......",
                ".....kkkkkk.....",
                "....kWWwwwdk....",
                "...kWWwwdwdk....",
                "...kWwddwwdk....",
                "...kWwwwwwdk....",
                "...kWddwwwdk....",
                "...kWwwwwddk....",
                "....kwddwdk.....",
                ".....kkkkk......");

        // Печёный таро: светлый, с подпалинами.
        Art.sprite16("item/cooked_taro",
                "................",
                "................",
                "................",
                "................",
                "................",
                ".....kkkkkk.....",
                "....kPPppKuk....",
                "...kPPpKpuuk....",
                "...kPpppuuuk....",
                "...kPKpuuKuk....",
                "...kPppuuuuk....",
                "...kpuKuuuuk....",
                "....kuuuKuk.....",
                ".....kkkkk......");

        String[] raw = {
                "................",
                ".....kkkk.......",
                "...kkRRRRkk.....",
                "..kRRRRrRFFk....",
                ".kRRrrrRRFFFk...",
                ".kRrrqrrRRFFk...",
                "kRRrqqqrrRFFk...",
                "kRrrqqqrrrFk....",
                "kRrrrqqrrrFk....",
                ".kRrrrrrrFk.....",
                ".kRRrrrrFFk.....",
                "..kkRRRFFk......",
                "....kkkkk.......",
        };
        Art.sprite16("item/raw_meat", raw);
        Art.sprite16("item/cooked_meat", recolor(raw, "RrqF", "Ccvv"));
        Art.sprite16("item/spoiled_meat", recolor(raw, "RrqF", "Oolo"));
    }

    static void medicine() throws IOException {
        // Полоска: рулон бамбукового волокна с хвостом.
        Art.sprite16("item/bandage",
                "................",
                "....kkkkkk......",
                "..kkYYYYYYkk....",
                ".kYYyyyyyyYYk...",
                ".kYyYYYYYYyYk...",
                "kYYyYyyyyYyYYk..",
                "kYyYyYYYYyYyYk..",
                "kYyYyYuuYyYyYk..",
                "kYyYyYYYYyYyYk..",
                "kYYyYyyyyYyYYk..",
                ".kYyYYYYYYyYkkk.",
                ".kYYyyyyyyYYYYYk",
                "..kkYYYYYYkkyyYk",
                "....kkkkkk..kkkk");

        // Противоядие: бамбуковая чашка с зелёным отваром и листом.
        Art.sprite16("item/antidote",
                "..........kk....",
                ".........kGgk...",
                "........kGgek...",
                "....kkkkkgek....",
                "...kVVVVVkVk....",
                "...kBbVVVVbNk...",
                "...kBbbbbnnNk...",
                "...kNNNNNNNNk...",
                "...kBBbbbnnNk...",
                "...kBBbbbnnNk...",
                "...kBBbbbnnNk...",
                "....kBbbnnNk....",
                ".....kkkkkk.....");

        // Дурной отвар: та же чашка, мутная лиловая жижа, пар.
        Art.sprite16("item/bad_brew",
                ".....k...k......",
                "......k.k.......",
                ".....k...k......",
                "....kkkkkkkk....",
                "...kDfDDfDDDk...",
                "...kBbDDDDbNk...",
                "...kBbbbbnnNk...",
                "...kNNNNNNNNk...",
                "...kBBbbbnnNk...",
                "...kBBbbbnnNk...",
                "...kBBbbbnnNk...",
                "....kBbbnnNk....",
                ".....kkkkkk.....");

        // Респиратор: маска из волокна, бамбуковая коробка с углём, ремни.
        Art.sprite16("item/respirator",
                "................",
                "................",
                ".kk..........kk.",
                "k..k........k..k",
                "k...kkkkkkkk...k",
                ".k.kyYYYYYYyk.k.",
                "..kkYYYYYYYYkk..",
                "..kYYkkkkkkYYk..",
                "..kYkBbKQbBkYk..",
                "..kYkBbQKbBkYk..",
                "..kYYkkkkkkYYk..",
                "...kyYYYYYYyk...",
                "....kkkkkkkk....");
    }

    static void clothing() throws IOException {
        String[] hood = {
                "................",
                "................",
                "....kkkkkkkk....",
                "...kBBBBBBBBk...",
                "..kBBbBBBBbBBk..",
                "..kBbBBBBBBbBk..",
                "..kBBBBBBBBBBk..",
                "..kBbbbbbbbbBk..",
                "..kBBkkkkkkBBk..",
                "..kBBk....kBBk..",
                "..kBBk....kBBk..",
                "..kBBk....kBBk..",
                "..kkkk....kkkk..",
        };
        String[] tunic = {
                "................",
                "..kkkk....kkkk..",
                ".kBBBBkkkkBBBBk.",
                "kBBbBBBBBBBBbBBk",
                "kBbBBBkkkkBBBbBk",
                "kBBBBk....kBBBBk",
                "kkkBBk....kBBkkk",
                "..kBBBkkkkBBBk..",
                "..kBBbBBBBbBBk..",
                "..kBBBbBBbBBBk..",
                "..kBbBBBBBBbBk..",
                "..kBBBbBBbBBBk..",
                "..kBbBBBBBBbBk..",
                "..kBBBBBBBBBBk..",
                "..kkkkkkkkkkkk..",
        };
        String[] leggings = {
                "................",
                "..kkkkkkkkkkkk..",
                ".kBBBBBBBBBBBBk.",
                ".kBbBBBBBBBBbBk.",
                ".kBBBBbkkbBBBBk.",
                ".kBBBBk..kBBBBk.",
                ".kBbBBk..kBBbBk.",
                ".kBBBBk..kBBBBk.",
                ".kBBbBk..kBbBBk.",
                ".kBBBBk..kBBBBk.",
                ".kBbBBk..kBBbBk.",
                ".kBBBBk..kBBBBk.",
                ".kBBBBk..kBBBBk.",
                ".kkkkkk..kkkkkk.",
        };
        String[] boots = {
                "................",
                "................",
                "................",
                "................",
                ".kkkk....kkkk...",
                "kBBBBk..kBBBBk..",
                "kBbBBk..kBBbBk..",
                "kBBBBk..kBBBBk..",
                "kBBBBkk.kBBBBkk.",
                "kBBBBBBkkBBBBBBk",
                "kBbbbbBkkBbbbbBk",
                "kkkkkkkkkkkkkkkk",
        };
        // Сандалии: плоская подошва и ремешки.
        String[] sandals = {
                "................",
                "................",
                "................",
                "................",
                "................",
                "..k.k.....k.k...",
                ".kBkBk...kBkBk..",
                "kBBBBBk.kBBBBBk.",
                "kBbbbBkkkBbbbBkk",
                "kBBBBBBkkBBBBBBk",
                "kbnnnnbkkbnnnnbk",
                "kkkkkkkkkkkkkkkk",
        };
        // Плетёнка: бамбуковое плетение.
        Art.sprite16("item/fiber_hood", hood);
        Art.sprite16("item/fiber_tunic", tunic);
        Art.sprite16("item/fiber_leggings", leggings);
        Art.sprite16("item/fiber_sandals", sandals);
        // Шкура: тёплая, с пятнами и тёмными швами.
        Art.sprite16("item/hide_hood", spots(recolor(hood, "Bb", "Hh")));
        Art.sprite16("item/hide_coat", spots(recolor(tunic, "Bb", "Hh")));
        Art.sprite16("item/hide_leggings", spots(recolor(leggings, "Bb", "Hh")));
        Art.sprite16("item/hide_boots", recolor(boots, "Bbn", "Hht"));
    }

    static void misc() throws IOException {
        // Шкура ягуара: распластанная, с розетками.
        Art.sprite16("item/hide",
                "................",
                ".kk.........kk..",
                "kHHk.......kHHk.",
                "kHHHkkkkkkkHHHk.",
                ".kHHhHHHHHhHHk..",
                ".kHhjHHhjHHhHk..",
                ".kHHHhHHHhHHHk..",
                ".kHhHHjHHHjHHk..",
                ".kHHjHHhHHhHHk..",
                ".kHhHHHjHhHHHk..",
                ".kHHhHHHHHHhHk..",
                "kHHHkkkkkkkHHHk.",
                "kHHk.......kHHk.",
                ".kk.........kk..");

        // Полевой дневник: кожаный переплёт, страницы справа, ремешок.
        Art.sprite16("item/journal",
                "................",
                "..kkkkkkkkkkk...",
                ".kWwwwwwwwwwdk..",
                ".kWwwwwwwwwwdkk.",
                ".kWwwtttwwwwdPk.",
                ".kWwwthtwwwwdPk.",
                ".kWwwtttwwwwdPk.",
                ".kWwwwwwwwwwdPk.",
                ".kWjjjjjjjjjjPk.",
                ".kWwwwwwwwwwdPk.",
                ".kWwwwwwwwwwdPk.",
                ".kWwwwwwwwwwdPk.",
                ".kWwwwwwwwwwdPk.",
                ".kddddddddddduk.",
                "..kkkkkkkkkkkk..");

        // Самородок: неровный серый комок с блеском.
        Art.sprite16("item/nugget",
                "................",
                "................",
                "................",
                "................",
                "................",
                "......kkk.......",
                "....kkIIIk......",
                "...kIIIiIik.....",
                "..kIIiiiiisk....",
                "..kIiiissisk....",
                "..kiiisssszk....",
                "...kisszzzk.....",
                "....kkzzkk......",
                "......kk........");

        // Пучок перьев тукана: чёрные с жёлтым и красным.
        Art.sprite16("item/feather_tuft",
                "..........kk....",
                ".........kKQk...",
                "........kKQQk...",
                ".......kKQQk....",
                "..kk..kKQQk.....",
                ".kBBk.kKQk......",
                ".kBBBkkKQk......",
                "..kBBBkQk.......",
                "...kBBkk........",
                "...kkBBk........",
                "....kZBBk.......",
                ".....kZBk.......",
                "......kZk.......",
                "......kuk.......",
                ".......ku.......",
                ".......kk.......");

        // Костяной осколок: заострённый белый ломоть с сечением.
        Art.sprite16("item/bone_shard",
                "..........kk....",
                ".........kPPk...",
                "........kPPpk...",
                "........kPppk...",
                ".......kPppk....",
                "......kPppk.....",
                ".....kPppk......",
                "....kPppk.......",
                "...kPppk........",
                "..kPppk.........",
                ".kPppk..........",
                ".kPpk...........",
                "kPpk............",
                "kpk.............",
                "kk..............",
                "................");

        // Костяное мачете: длинное кремово-белое лезвие, шершавое у рукояти.
        Art.sprite16("item/bone_machete",
                "...........kkkk.",
                "..........kPPPPk",
                ".........kPPPppk",
                "........kPPPppk.",
                ".......kPPPppk..",
                "......kPPpppk...",
                ".....kPPpppk....",
                "....kPPpppk.....",
                "...kPppppk......",
                "..kkppppk.......",
                ".kBbkkkk........",
                "kBbnk...........",
                "kbnNk...........",
                ".kkk............",
                "................",
                "................");

        // Каучуковый сок: листовая чаша с молочно-янтарной каплей.
        Art.sprite16("item/rubber_sap",
                "................",
                "................",
                "......kk........",
                ".....kTTk.......",
                "....kTTTTk......",
                ".....kTTk.......",
                "..kkkkTTkkkk....",
                ".kGGggggggggk...",
                ".kGgggggggggk...",
                ".kGggggggggk....",
                "..kGGggggggk....",
                "...kkGggggk.....",
                ".....kGggk......",
                "......kkk.......",
                "................",
                "................");

        // Похлёбка: чаша горшка, внутри густое варево с кусками.
        Art.sprite16("item/stew",
                "................",
                "................",
                "................",
                "....kkkkkkkk....",
                "...kCCCCCCCck...",
                "..kCcCCvCCvCCck.",
                "..kCCvCCCvCCCck.",
                "..kCcCCvCCvCCck.",
                "..kCCCCCvCCCck..",
                "...kCCCCCCCck...",
                "...kvvvvvvvvk...",
                "....kkkkkkkk....",
                "................",
                "................",
                "................",
                "................");

        // Гамак: свёрнутое зелёное полотно со стропами.
        Art.sprite16("item/hammock",
                "................",
                "...kk......kk...",
                "..kYyk....kyYk..",
                "..kYyk...kyYk...",
                "..kkkkkkkkkk....",
                ".kGggggggggggk..",
                "kGgGGgGGgGGggGgk",
                "kgGGgGGgGGgGGggk",
                "kGGgGGgGGgGGgGgk",
                ".kggggggggggggk.",
                "..kkkkkkkkkk....",
                "..kyYk...kyYk...",
                "..kyYk....kyYk..",
                "...kk......kk...",
                "................",
                "................");

        // Шёлк: моток белой нити, перевязанный посередине.
        Art.sprite16("item/silk",
                "................",
                "................",
                "....kkkkkkk.....",
                "...kXXxXXxXk....",
                "..kXxXXxXXxXk...",
                "..kXXxXXxXXXk...",
                "..kxXXxXXxXxk...",
                "...kXxkkkxXk....",
                "...kxXkykXxk....",
                "...kXxkkkxXk....",
                "..kXXxXXxXXXk...",
                "..kxXXxXXxXxk...",
                "..kXxXXxXXxXk...",
                "...kXXxXXxXk....",
                "....kkkkkkk.....");
    }

    // ---- помощники ----

    static String[] slice(String[] rows, int from) {
        String[] out = new String[rows.length - from];
        System.arraycopy(rows, from, out, 0, out.length);
        return out;
    }

    static String[] join(String[] a, String[] b) {
        String[] out = new String[a.length + b.length];
        System.arraycopy(a, 0, out, 0, a.length);
        System.arraycopy(b, 0, out, a.length, b.length);
        return out;
    }

    /** Замена символов палитры: from.charAt(i) -> to.charAt(i). */
    static String[] recolor(String[] rows, String from, String to) {
        String[] out = new String[rows.length];
        for (int i = 0; i < rows.length; i++) {
            StringBuilder sb = new StringBuilder(rows[i]);
            for (int x = 0; x < sb.length(); x++) {
                int idx = from.indexOf(sb.charAt(x));
                if (idx >= 0) sb.setCharAt(x, to.charAt(idx));
            }
            out[i] = sb.toString();
        }
        return out;
    }

    /** Редкие тёмные пятна на шкуре. */
    static String[] spots(String[] rows) {
        String[] out = rows.clone();
        int[][] at = {{4, 5}, {9, 7}, {6, 10}, {11, 12}, {3, 9}};
        for (int[] s : at) {
            int y = s[1], x = s[0];
            if (y < out.length && x < out[y].length() && out[y].charAt(x) == 'H') {
                StringBuilder sb = new StringBuilder(out[y]);
                sb.setCharAt(x, 'j');
                out[y] = sb.toString();
            }
        }
        return out;
    }
}
