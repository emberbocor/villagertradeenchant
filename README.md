# Villager Trade Enchant

A villager who enchants your gear, at a price you control.

Villager Trade Enchant adds a new villager profession, the **Enchanter**, whose job site is the enchanting table. Instead of trading, the Enchanter opens an enchant screen: put an item in, pick the exact enchantment and level you want, pay and take the enchanted item. No lapis, no experience levels, no random rolls.

Available for NeoForge, Forge and Fabric.

## Features

- **New profession.** Unemployed villagers near an enchanting table become Enchanters, just like vanilla job sites. The Enchanter has its own look (a crimson robe and hood with a silver stripe and gold trim) for villagers and zombie villagers, and keeps its profession through zombification and curing.
- **Pick exactly what you want.** The list shows one row per enchantment level (Sharpness I, Sharpness II, ...) that can go on the item, sorted by name, with its price next to it. Names are white; curses are red.
- **Anvil rules.** Only enchantments the item can take in an anvil are offered. Enchantments that conflict with one already on the item are hidden, and so are levels the item already has (or exceeds). Upgrading an enchantment costs the full price of the new level.
- **Books too.** A plain book becomes an enchanted book; an enchanted book can get more enchantments. Books accept every enchantment.
- **No rework penalty.** Enchanting here does not raise the item's anvil repair cost.
- **Configurable prices.** Every enchantment level has its own price (any item, 1 to 64), set in the server config. By default they cost emeralds, or emerald blocks for the expensive ones.
- **Works with modded enchantments.** Enchantments from other mods and datapacks are added to the config automatically.
- **Vanilla feel.** The screen looks like the trading screen. Selecting a row moves the payment from your inventory into the payment slot, like selecting a trade. While the screen is open the villager stands still and stays with you, like during a trade.

The Enchanter never trades, never levels up and gives no reputation or Hero of the Village discounts; its prices are exactly what the config says, with no usage limit.

## How to get an Enchanter

1. Place an enchanting table near an unemployed villager (one without a job site), the same way you would place a lectern for a librarian.
2. The villager walks to the table and becomes an Enchanter.
3. Right-click it to open the enchant screen.

Like any villager that has never traded, an Enchanter loses its profession when its enchanting table is broken, and can take another job site afterwards. Nitwits and baby villagers cannot become Enchanters.

In a world that existed before the mod was installed, an enchanting table that was already placed may not be recognized as a job site yet. Break it and place it again.

## Using the enchant screen

1. Put the item to enchant in the first slot (one item at a time).
2. Pick an enchantment level from the list on the left. The payment is moved from your inventory into the second slot if you have it.
3. When the payment is enough, the enchanted item appears in the result slot. Take it to pay and get the item.

Items left in the slots go back to your inventory when you close the screen.

## Requirements

| | NeoForge | Forge | Fabric |
|---|---|---|---|
| Minecraft | 26.1, 26.1.1 or 26.1.2 | 26.1, 26.1.1 or 26.1.2 | 26.1, 26.1.1 or 26.1.2 |
| Java | 25 | 25 | 25 |
| Loader | NeoForge 26.1.0.1-beta or newer | Forge 62.0.0 or newer | Fabric Loader 0.18.4 or newer |
| Other | | | Fabric API 0.144.0 or newer |
| Jar | `villagertradeenchant-neoforge-26.1-26.1.2-<version>.jar` | `villagertradeenchant-forge-26.1-26.1.2-<version>.jar` | `villagertradeenchant-fabric-26.1-26.1.2-<version>.jar` |

Minecraft 26.x needs Java 25. One jar per loader works on all three Minecraft versions. NeoForge only has beta builds for Minecraft 26.1 and 26.1.1; Forge uses the 62.x series for 26.1, 63.x for 26.1.1 and 64.x for 26.1.2.

NeoForge and Forge are separate loaders that do not run each other's mods, so each has its own jar. Pick the jar that matches your loader.

The Fabric jar bundles [NightConfig](https://github.com/TheElectronWill/night-config) (core and toml, LGPL-3.0) to read the TOML config. NeoForge and Forge already ship it.

### Client and server

On every loader the mod is required on both the client and the server: the profession, the job site, the enchant screen and its network messages must be known to both sides. Players without the mod cannot join a server that has it; the connection is refused at login.

The server decides everything that matters: which enchantments are offered, their prices and whether the payment is enough. The client only shows what the server sends.

## Configuration

### Server: `config/villagertradeenchant-server.toml`

The file is created on the first server (or world) start and lists every enchantment in the game with its default prices. It is read every time a server or world starts.

```toml
# Multiplies every price count below. The result is rounded up and capped at 64
# (and at the max stack size of the price item).
priceMultiplier = 1.0

# true: enchantments that conflict with one already on the item (for example Sharpness and Smite)
# are offered too.
allowConflicts = false

# true: plain books and enchanted books can be enchanted. A plain book becomes an enchanted book.
allowBooks = true

[prices."minecraft:mending"]
enabled = true
levels."1" = { item = "minecraft:emerald", count = 38 }

[prices."minecraft:sharpness"]
enabled = true
levels."1" = { item = "minecraft:emerald", count = 19 }
levels."2" = { item = "minecraft:emerald", count = 31 }
levels."3" = { item = "minecraft:emerald", count = 43 }
levels."4" = { item = "minecraft:emerald", count = 55 }
levels."5" = { item = "minecraft:emerald_block", count = 8 }
```

- `priceMultiplier`: multiplies every price count. The result is rounded up and capped at 64 (and at the price item's max stack size, so an unstackable item can only cost 1). Must be a number above 0. Default `1.0`.
- `allowConflicts`: `true` also offers enchantments that conflict with one already on the item, so both end up on it. Default `false`.
- `allowBooks`: `false` stops plain books and enchanted books from being enchanted. Default `true`.
- `[prices."<enchantment id>"]`: one table per enchantment.
  - `enabled`: `false` hides the enchantment from the Enchanter.
  - `levels."<level>" = { item = "<item id>", count = <1-64> }`: the price of one level. Any item can be used, for example `{ item = "minecraft:diamond", count = 3 }`. Renamed items of that type are accepted as payment too.

Default prices: `ceil(1.5 * (2 + 3 * level + (5 + 10 * level) / 2))` emeralds, doubled for treasure enchantments (such as Mending). Prices above 64 emeralds are paid in emerald blocks (`count / 9`, rounded up, at most 64).

Your edits are never overwritten. When new enchantments appear (a new mod or datapack), only their tables are added at the end of the file; everything else, including your comments, stays exactly as it is. To hide an enchantment, set `enabled = false` instead of deleting its table, because a deleted table is added back with default prices on the next start.

Mistakes do not crash the server. Unknown items or enchantments, levels outside an enchantment's range and invalid counts are skipped with a warning in the log, and the default price is used for those levels. If the file cannot be read at all (for example a TOML syntax error), default prices are used, the file is left untouched and the log says why.

## Upgrading a world from Minecraft 1.21.11

Worlds from Minecraft 1.21.11 with this mod open in 26.1 with the 26.1 jar of the same loader. Enchanters stay Enchanters (the profession and its job site keep the same ids) and keep their enchanting tables. The config file has the same format, so `config/villagertradeenchant-server.toml` can be kept as it is.

## Removing the mod

The world still opens without crashing. Enchanters lose their profession and become unemployed villagers, and enchantments already bought stay on the items.

## Compatibility

### Incompatible mods

A block can be the job site of only one villager profession, so mods that also make the enchanting table a job site cannot run together with this mod. NeoForge and Fabric show a clear error message at startup instead of crashing. Forge 26.1 cannot declare incompatible mods, so on Forge the game stops at startup with the error below instead.

| Mod | Mod ID | Reason |
|---|---|---|
| [Villager Enchanter](https://www.curseforge.com/minecraft/mc-mods/villager-enchanter) (Forge/Fabric, up to Minecraft 1.20.2) | `villager_enchanter` | Uses the enchanting table as the job site of its own Enchanter |

Any other mod that registers the enchanting table as a job site clashes the same way; the game then stops at startup with an error like "Point of interest types ... both list ... in their blockstates" (NeoForge and Forge) or "... is defined in more than one PoI type" (Fabric).

### Other mods

- **Villager Trade Peek, Villager Trade Swap, Villager Trade Labels (26.1):** work together. The enchant screen is its own screen, not the trading screen, so those mods leave it alone.
- Enchantments added by other mods or datapacks are supported and appear in the config automatically.
- Trades added to the Enchanter by other mods are ignored; the Enchanter never trades.

## Building from source

```
./gradlew build
```

The jars are written to `neoforge/build/libs/`, `forge/build/libs/` and `fabric/build/libs/`.

## License

MIT. The Fabric jar includes NightConfig, which is licensed under the LGPL-3.0.
