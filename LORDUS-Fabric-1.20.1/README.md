# LORDUS — Minecraft Java 1.20.1 + Fabric

This is a 1.20.1 compatibility starter project for the LORDUS mod.

## Prototype features in this source
- `/lordus summon nether`
- `/lordus summon end`
- Uses Minecraft's three-headed Wither as the temporary boss base.
- 600 health and 24 attack damage.
- Separate drop rolls when the tagged Lordus dies:
  - One-hit sword item: 1% (1 in 100) — currently a named Netherite Sword; one-hit damage behavior is not implemented.
  - Immortality helmet: 1% (1 in 100) — currently a named Turtle Helmet; prevents one lethal hit and is consumed.
  - Lordus axe: 50% (1 in 2) — currently a named Netherite Axe; 100 damage behavior is not implemented.

## Planned features not yet implemented
- Nether Warped Forest cave and End tower-area cave generation / natural boss spawning.
- Custom three-headed Lordus model and head-break/full-heal phases.
- Block throwing and minion summoning.
- Three Overworld-only ores around Y=-30.
- Kızıl, Safir, and Lordus Kristali tool sets (sword/pickaxe/axe).
- Exact sword damage values 15, 25, and 35; the 35-damage ore should be rarest.
- Custom weapon damage values and crafting recipes.

## Build
1. Install JDK 17.
2. Open this project in IntelliJ IDEA or another Gradle IDE.
3. Run Gradle task `build`.
4. The JAR will be generated in `build/libs/`.
5. Put the JAR and Fabric API for Minecraft 1.20.1 in `.minecraft/mods`.

The project has not been compiled or tested in-game in this environment. Gradle resolves Minecraft/Fabric dependencies online during build.
