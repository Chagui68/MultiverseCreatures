# 🌌 Ritual Dimension

The **Ritual Dimension** (world `boss_dimension`) is a private, boss-only world where the **Obsidian Sentinel** is fought. It is a void world: a bedrock floor with 5 layers of crying obsidian, an eternal red sky, no weather, no daylight cycle and no natural mob spawning. The dimension is generated automatically the first time a player enters it.

> The dimension is heavily restricted on purpose: players cannot place or break blocks, and almost all commands are blocked (`/say`, `/me`, `/help`, `/?` and `/dimtp` still work). Admins bypass these restrictions with the `msc.admin.bypass` permission.

---

## 🕯️ Entering: the Ritual Structure

To enter the dimension you must build and light the **Ritual Structure** in the overworld.

### Layout (7×7, ground level)

```
S S S S S S S        S = polished blackstone stairs (border)
S X X X X X S        X = chiseled polished blackstone
S X O C O X S        O = crying obsidian
S X C X C X S        C = cracked polished blackstone bricks
S X O C O X S        X = chiseled polished blackstone
S X X X X X S        center block: obsidian
S S S S S S S
```

- The **center block** must be **obsidian**.
- On the **second layer** (1 block above the ground), place **12 candles** in a ring around the inside of the border, one at the middle of each edge (3 per edge, 12 total).

### Activation

1. Build the structure and place all 12 candles.
2. Light **every candle** with flint & steel, a fire charge, or another candle item.
3. Red/blue fire circle particles and portal particles appear around the structure.
4. After **~5 seconds**, any player standing inside the circle (radius 5 from the center) is teleported to the Ritual Dimension (they get a short blindness effect — *"There is no escape."*).

> Only one ritual can be active per world at a time. If the structure is broken or the candles go out, the ritual stops.

---

## ⚔️ Invoking the boss: the Invocation Circle

Once inside the dimension, the **Obsidian Sentinel** must be invoked manually.

### Layout (5×5, red candle ring)

```
R _ R R R _ R        (ring of 12 red candles, empty inside)
R _ _ _ _ R          center: empty — drop the Echo Shard here
R _ _ _ _ R
R _ _ _ _ R
R R R R R R
```

1. Place **12 red candles** in a 5×5 ring (the corners and edges of a square, leaving the center empty).
2. Light **all** of them with flint & steel or a fire charge.
3. A flaming **pentagram** animation spawns in the middle while the invocation is active.
4. **Drop an Echo Shard** (`echo_shard`) into the center of the circle.
5. The shard is consumed, the candles extinguish, and the **Obsidian Sentinel** awakens at the center.

> Killing the Sentinel drops a **Sentinel Core** (configurable chance, `armor-stand-boss.sentinel-core-drop-chance`, default 100%) — a key ingredient for the **Sentinel Grimoire** and other apex recipes.

---

## 🚪 Leaving

- Use `/dimtp` (works inside the dimension) to teleport to another world.
- If the plugin is disabled/reloaded with players inside, everyone is sent back to the overworld spawn automatically.

---

## 🧰 Technical notes

- World name: `boss_dimension` (created on first entry, unloaded on plugin disable).
- Spawn point: `0.5, 10, 0.5` (above the crying obsidian floor).
- Game rules: no daylight cycle, no weather cycle, no mob spawning, immediate respawn, no advancement announcements.
- The sky is forced red via a biome override.
- Relevant classes: `BossDimensionManager`, `BossInvocationManager`, `RitualManager`, `RitualStructure`, `BossInvocationStructure` — see [Architecture](./Architecture.md).
