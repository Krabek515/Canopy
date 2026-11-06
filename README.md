# Canopy

**Джунгли, которые стали живыми.** Аддон для Minecraft 1.20.1 / Forge.

Обычный мир Minecraft остаётся собой — но там, где рождаются джунгли, начинается другая игра. Пять новых биомов превращают тропики в дремучую, дышащую чащу: низина, затопленная топь, заросли бамбука, возвышенность и каньон. Жара давит, ливень промачивает до костей, раны ноют — выживать здесь приходится по-настоящему. Вокруг своя жизнь: ягуары охотятся в подлеске, капибары греются у воды, туканы и обезьяны шумят в кронах, кайманы ждут в мутной воде. Из волокна и шкур собирается своё снаряжение, а полевой дневник заполняется находками — каждое растение, зверь и рецепт становится открытием. И всё найденное остаётся полезным в обычном мире.

Это не новый мир вместо Minecraft — это Minecraft, у которого появилось сердце, бьющееся где-то в зелёной глубине.

## Содержание

- **5 биомов** в климатической зоне ванильных джунглей: `canopy:lowland` (низина), `canopy:flooded` (топь), `canopy:bamboo_thicket` (бамбук), `canopy:highland` (возвышенность), `canopy:canyon` (каньон). Остальной мир — обычная ваниль.
- **9 зверей** на GeckoLib: ягуар, капибара, тапир, кайман, горилла, обезьяна, агути, змея, тукан. Свои модели, анимации и звуковые события.
- **Джунглевые условия**: жара в чаще, промокание под ливнем, раны от укусов и колючек, змеиный яд, удушливый газ в низинах. Ванильные голод и здоровье работают как обычно.
- **Снаряжение**: плетёная броня из волокна (≈ кожа), одежда из шкур (≈ железо), респиратор от газа, мачете, гамак для сна между деревьями, верёвка, глиняная печь.
- **Растения**: таро, горький лист, банановые грозди под кронами, орхидеи-эпифиты, геликония и имбирная лилия (красители), бромелея, грибы на стволах, водяные лианы.
- **Находки**: гнилой настил-ловушка, помеченный груз, высокое дерево с шёлковым узлом, жилы в каньоне, чужие стоянки, тайники экспедиций.
- **Полевой дневник**: выдаётся один раз на старте, древо открытий с иконками и рецептами. Обычный предмет — можно выбросить и скрафтить заново (бумага + кожа).

## Установка (игрокам)

1. Minecraft **1.20.1** + **Forge 47.4+**
2. **GeckoLib 4.8+** (обязательная зависимость — модели и анимации зверей)
3. `canopy-*.jar` и `geckolib-*.jar` в папку `mods/`

## Команды

| Команда | Что делает |
| --- | --- |
| `/canopy` | Справка по всем подкомандам |
| `/canopy locate` | Координаты ближайшего биома Canopy |
| `/canopy temperature|wetness|wound|gas|…` | Отладочные команды (нужны права 2) |
| `/locate biome canopy:lowland` | Штатный поиск конкретного биома |

## Сборка из исходников

Нужны Git и JDK 17+ (Gradle сам скачает toolchain для игры):

```
gradlew.bat genIntellijRuns   # или gradlew genEclipseRuns
gradlew.bat runClient         # клиент с модом
gradlew.bat build             # jar в build/libs/
```

Первый запуск долго качает Minecraft, Forge и mappings (Parchment 2023.09.03-1.20.1).

## Структура репозитория

```
src/main/java/io/github/krabek515/canopy/   код мода
src/main/resources/assets/canopy/           текстуры, модели, звуки, языки (ru/en)
src/main/resources/data/canopy/             биомы, фичи, рецепты, лут, теги
docs/                                       дизайн-документы проекта (на русском)
tools/art/                                  генератор пиксель-арт ассетов (Java)
```

Дизайн-документы в `docs/` описывают замысел и историю решений — код в `src/` является финальной реализацией.

## Лицензия и авторство

Код, текстуры, модели и тексты — **All Rights Reserved**, © Krabek515 ([LICENSE.txt](LICENSE.txt)). Все ассеты нарисованы вручную генератором в `tools/art/` — сторонних файлов в репозитории нет. Звуки зверей — собственные звуковые события, переиспользующие сэмплы ванили со сдвигом высоты тона (файлы ванили не копируются). Подробности — в [CREDITS.md](CREDITS.md). GeckoLib используется как зависимость (MPL-2.0), Forge — LGPL.

---

## English

**Canopy is a jungle survival addon for Minecraft 1.20.1 / Forge.** The vanilla world stays vanilla — but wherever jungles generate, they are replaced by five custom biomes: lowland, flooded swamp, bamboo thicket, highland and canyon. Heat, rain, wounds, venom and choking gas make the tropics a real survival experience, while everything found there — bananas, orchids, fibers, hides, gear — stays useful in the ordinary world.

- 9 custom GeckoLib animals: jaguar, capybara, tapir, caiman, gorilla, monkey, agouti, snake, toucan
- Woven fiber armor (≈ leather), hide clothing (≈ iron), respirator, machete, hammock, rope, clay oven
- A field journal granted once at spawn: a discovery tree with item icons and recipes. A normal item — droppable, craftable (paper + leather)
- Points of interest: rotten decks, marked cargo, giant trees, ore veins, abandoned camps, expedition caches

**Requirements:** Minecraft 1.20.1, Forge 47.4+, GeckoLib 4.8+. Drop both jars into `mods/`.

`/canopy` prints help; `/canopy locate` finds the nearest Canopy biome; `/locate biome canopy:lowland` also works.

Design docs in `docs/` are in Russian. All assets are original, generated by `tools/art/`. License: All Rights Reserved — see [LICENSE.txt](LICENSE.txt).
