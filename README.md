# MultiverseCreatures

Themed creatures and items plugin for Spigot/Paper **1.21+**.

MultiverseCreatures adds custom entities with unique trades and custom items to your Minecraft server.

## 🛒 Multiverse Merchant & Themed Items

### Multiverse Merchant NPC
A Wandering Trader replacement that spawns naturally (30% chance) with custom trades from across the multiverse:

| Item | Cost | Uses | Effect |
|------|------|------|--------|
| **Scooby Cookie** (5x) | 20 Diamonds | 999 | Grants **Resistance VI** for 10s when eaten |
| **Excalibur Sword** (1x) | 8 Netherite Ingots + 16 Diamond Blocks | 1 | Grants **Strength III** for 4s while held |
| **Ice King's Crown** (1x) | 64 Packed Ice + 16 Blue Ice + 12 Diamonds | 1 | Winter crown with ice abilities |
| **Mantis Claws** (1x) | 16 Iron Ingots + 8 String | ∞ | Cling to walls with **SHIFT**, jump off with **SPACE** |

### 🧊 Ice King's Crown (Adventure Time)
The legendary crown of the Ice King grants mastery over ice and snow.

| Control | Ability | Cooldown |
|---------|---------|----------|
| **Right-Click** | Snow Block Launch — lifts targeted snow/ice block and launches it at your enemy | 10s |
| **Shift + Right-Click** | Blizzard — AoE freezing storm that damages and slows nearby enemies | 60s |
| **Left-Click** | Toggle Ice Path — walk on water by freezing it beneath your feet | No cooldown |

**Snow Block Launch effects:**
- Snow block → Slowness 3 + Weakness 1 (5s) + 6 damage
- Ice block → Slowness 2 + Weakness 2 + Nausea 1 (5s) + 6 damage

**Passive:** 20% damage reduction when holding the crown + freeze immunity.

### 🦗 Mantis Claws (Hollow Knight)
Replica of the Mantis Claws from Hallownest. Grants the wielder the ability to cling to and scale walls.

| Control | Ability |
|---------|---------|
| **Shift** (mid-air, near wall) | Cling to wall + slow fall |
| **Space** (while clinging) | Wall-jump upward |

**Passive:** Cannot break blocks while holding. Unbreakable.

### 🧟 Head Slime (Half-Life)
A parasitic headcrab that leaps onto targets and controls them.

| Target | Effect |
|--------|--------|
| **Player** | Blindness II + Slowness I + periodic true damage (3❤/2s) |
| **Hostile Mob** | Strength II + Speed II + Resistance I — then attacks nearest player |

**Detach conditions:** Player takes damage, slime is attacked, or host dies (slime survives).

**Spawn:** 10% chance to replace natural slime spawns. Use `/msc spawn headslime` to summon.

---

### Custom Items
- **Scooby Cookie** (✦ Mystery Inc. ✦) — `§6Scooby Cookie` — "A cookie that will fill you with courage"
- **Excalibur** (✦ Avalon ✦) — `§6Excalibur` — "The legendary blade of kings"
- **Ice King's Crown** (✦ Ooo ✦) — `§bIce King's Crown` — "A crown of eternal winter"
- **Mantis Claws** (✦ Hallownest ✦) — `§6Mantis Claws` — "Claws forged from the silk and iron of Deepnest"
- **Wirt's Lantern** (✦ Khand ✦) — `§5Wirt's Lantern` — "A lantern that holds a lost soul"
- **Dio's Stand Head** (✦ JoJo ✦) — `§6Dio's Stand Head` — "Za Warudo! Toki wo tomare!"
- **Star Core** — `§eStar Core` — "A compressed core of pure cosmic energy"

### 💥 Creeper Jr.
A smaller, faster creeper that deals true damage (bypasses armor).

| Stat | Value |
|------|-------|
| Size | 60% of normal creeper |
| Speed | 1.5x normal creeper |
| Explosion Radius | 2 (normal is 3) |
| Damage Type | True damage (ignores armor) |
| Block Damage | None (yield = 0) |

**Spawn:** 15% chance to replace natural creeper spawns. Use `/msc spawn creeperjr` to summon.

---

### 🐴 Military Zombie Trap (Full Moon Event)
During a **Full Moon**, 0.1% of natural zombie spawns are replaced with a **Military Zombie Horse**. When a player approaches, the trap activates and spawns an undead army:

| Unit | Description |
|------|-------------|
| **Zombie Tank** (center) | 350 HP, 10 dmg, 20% dmg reduction, 3 blocks tall, **arrow immune**, iron armor |
| **Skeleton Duelist** (x2, flanking behind) | 50 HP, swaps between Flame 3/Power 3 bow and Sharp 3/Knockback 2 sword based on distance, chainmail armor |
| **Zombie Lancer** (mounted, 3 blocks behind) | Iron lance, horse has Res 1 + Speed 3; on horse death gains Speed 3 + Str 1, iron armor |
| **Camel Rider** (x2, flanking Lancers) | Zombie with diamond lance + Bogged skeleton; camel has Speed 2 + Res 2; on camel death riders gain speed/resistance |
| **Sniper Skeleton** (rearguard) | Wither skeleton with Power 5 bow, 40-block range, arrows apply **Wither 1** + **Weakness 1** |

*All units wear a dyed leather helmet matching their name color.*

**Spawn:** Use `/msc spawn zombietrap` to summon the trap horse.

---

## 🛠️ Commands

All plugin interactions are handled via the `/msc` command (**Permission:** `msc.admin`):

| Command | Description |
|---|---|---|
| `/msc spawn merchant` | Spawns the Multiverse Merchant at your location |
| `/msc spawn dio` | Spawns Dio Brando boss at your location |
| `/msc spawn creeperjr` | Spawns a Creeper Jr. at your location |
| `/msc spawn headslime` | Spawns a Head Slime at your location |
| `/msc spawn zombietrap` | Spawns a Military Zombie Horse trap at your location |
| `/msc give <item>` | Give yourself a MultiverseCreatures item |
| `/msc reload` | Reloads configuration |

**Spawn types:** `merchant`, `dio`, `creeperjr`, `headslime`, `zombietrap`

**Give items:** `scoobycookie`, `excalibur`, `crown`, `mantisclaws`, `claws`, `icekingscrown`

---

## 🚀 Requirements

- **Server:** Purpur / Paper / Spigot **1.21+** (built against `purpur-api 1.21.11`)
- **Java:** **21** or higher

## 🔨 Build

```bash
mvn clean package -DskipTests
```

Output: `target/MultiverseCreatures-v${version}.jar`

---

## 🏗️ Project Structure

```
src/main/java/com/Chagui68/
├── MultiverseCreatures.java      # Plugin entrypoint: onEnable/onDisable, recipe + listener registration
├── ability/                       # Player abilities (e.g. FreezeAbility)
├── commands/                      # /msc command executor and tab completer
│   └── MSCCommand.java
├── entities/                      # Custom mobs (each one implements Listener)
│   ├── boss/
│   │   ├── ArmorStandBoss.java    # Final boss: spawn, phases, shield, bar, AI ticker
│   │   ├── MagicSealListener.java  # Particle seal rendering (not a Listener)
│   │   ├── BossInstance.java       # Per-instance boss state struct
│   │   └── attack/                # Boss attack framework + concrete attacks
│   │       ├── BossAttack.java         # interface: execute(BossInstance), getName()
│   │       ├── BossAttackBase.java     # abstract base: boss/plugin/random/sealDamage/...
│   │       ├── aerial/                 # 13 air attacks (starfall, airslam, ...)
│   │       ├── ground/                 # 11 ground attacks (shieldbash, groundslam, ...)
│   │       └── ranged/                 # 12 ranged attacks (meteorstorm, spiritbeam, ...)
│   ├── miniboss/                  # DioBoss, Mahoraga
│   └── handler/                   # MobHandler (vanilla-spawn router)
├── items/                         # Custom items, grouped by category
│   ├── armor/      items/food/     items/dio/
│   ├── components/                # Crafting ingredients (VoidEssence, MagmaCore, ...)
│   ├── misc/                      # IceCrown, MantisClaws, WirtsLantern, MilitaryMine
│   │   └── offhand/               # MarrowAegis, VeilwalkerMantle, FrostHeartOffhand
│   └── weapons/{melee,ranged,magic}/
├── listener/                      # Bukkit event handlers for items, bosses, rituals
├── music/                         # NBS song playback (BossThemes)
├── ritual/                        # Boss invocation rituals & private boss dimension
└── utils/                         # Reusable helpers
    ├── ItemBuilder.java           # Fluent builder for ItemStacks (lore, PDC tags, enchants)
    └── MscEntityUtils.java        # setAttribute, spawnTagged, permanentFireResistance,
                                   # isValidTarget, handleDeath — shared mob utilities
```

### Architectural conventions

**Boss attacks** — All `ArmorStandBoss` attacks live as individual classes extending `BossAttackBase` under `entities/boss/attack/{aerial,ground,ranged}/`. They are registered in `ArmorStandBoss.initAttacks()` and dispatched polymorphically via `attackRegistry.get(name).execute(instance)`. The three random selectors (`executeRandomAerialAttack`, `executeRandomGroundAttack`, `executeRangedAttack`) and the `/msc triggerattack` switch all dispatch through the registry — adding a new attack is "create class + `registerAttack(new XxxAttack(this))`", no edits to dispatch code needed.

**Mobs** — Each custom mob class implements `Listener` and self-registers in its own constructor (`Bukkit.getPluginManager().registerEvents(this, plugin)`). `MobHandler` is the exception: it's externally registered by `MultiverseCreatures.onEnable()` because it routes natural spawns.

**Items** — All `ItemStack` construction goes through `utils/ItemBuilder` (fluent API: `ItemBuilder.of(Material).name(...).lore(...).tagged(KEY).build()`). Persistent data tags use `PersistentDataType.INTEGER` with a `NamespacedKey("multiversecreatures", "msc_<item>")` per item.

**Attribute modifiers** — Use the modern `AttributeModifier(NamespacedKey, double, Operation)` constructor (NOT the deprecated UUID-based one). `ObsidianBastionHandler` is the reference implementation for armor set bonuses: idempotent `getModifier(key)` check before adding, `removeModifier(key)` on cleanup — no per-player modifier maps required.

### Adding new content

| To add... | Steps |
|---|---|
| **New item** | Create class under `items/<category>/` using `ItemBuilder`. Expose `public static final ItemStack` + `NamespacedKey KEY`. Register recipes in `MultiverseCreatures.registerRecipes()`. |
| **New mob** | Create class under `entities/<...>/` implementing `Listener`. Use `MscEntityUtils.spawnTagged/setAttribute/handleDeath`. Self-register in constructor. Instantiate it in `MultiverseCreatures.onEnable()`. |
| **New boss attack** | Create class extending `BossAttackBase` under `entities/boss/attack/<aerial\|ground\|ranged>/` returning a unique `getName()`. Delegate shared logic via `boss.<helper>()`. Register it in `ArmorStandBoss.initAttacks()`. |
| **New tool/weapon handler** | Create `listener/XxxHandler` implementing `Listener`. Use `MscEntityUtils.isCreativeOrSpectator` for game-mode guards. Register it in `MultiverseCreatures.onEnable()` via `getServer().getPluginManager().registerEvents(new XxxHandler(this), this)`. |