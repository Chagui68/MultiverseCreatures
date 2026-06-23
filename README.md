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

---

### Custom Items
- **Scooby Cookie** — `§6Scooby Cookie` — "A cookie that will fill you with courage"
- **Excalibur Sword** — `§6Excalibur Sword` (Netherite Sword) — "THE REAL SWORD"
- **Ice King's Crown** — `§bIce King's Crown` — "A crown of eternal winter... Gunter, why you gotta be like that?"

---

## 🛠️ Commands

All plugin interactions are handled via the `/msc` command (**Permission:** `msc.admin`):

| Command | Description |
|---|---|
| `/msc spawn merchant` | Spawns the Multiverse Merchant at your location |
| `/msc reload` | Reloads configuration |

---

## 🚀 Requirements

- **Server:** Paper or Spigot **1.21+**
- **Java:** **21** or higher

## 🔨 Build

```bash
mvn clean package -DskipTests
```

Output: `target/MultiverseCreatures-v${version}.jar`