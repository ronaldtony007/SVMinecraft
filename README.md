# Starter Mod

This Fabric mod changes Minecraft technology progression so villagers teach the player each material tier.

## First Working Loop: Stoneworking

Wood tools remain available normally. Completing the Stoneworking step grants the custom `Stone Age` advancement, but stone-tool recipes remain locked until the translated scroll is handed back.

The first progression is:

```text
Blacksmith Apprentice
    -> request 32 Stone to upgrade to the next level
    -> generate Stoneworking Scroll
    -> Librarian translates scroll
    -> translated scroll is handed back to the blacksmith
    -> blacksmith progression advances
    -> player receives stone-tool recipes
    -> Apprentice trades become available
```

The same engine applies to Toolsmiths, Weaponsmiths, and Armorers. It defines Apprentice -> Journeyman with Iron and Expert -> Master with Diamond. The material, quantity, knowledge, and recipes are defined in `ProgressionDefinitions`, not in the interaction handlers.

## Requirements

- JDK 25
- IntelliJ IDEA with the Minecraft Development plugin (recommended)

## Run

```powershell
.\gradlew.bat runClient
```

Build the mod JAR with:

```powershell
.\gradlew.bat build
```

## Test Commands

Stand within 8 blocks of a Toolsmith and use operator commands:

```text
/villagerprogress reset
/villagerprogress setrank novice
/villagerprogress info
/villagerprogress setresource stone 32
/villagerprogress giveknowledge stoneworking
/villagerprogress unlock stoneworking
```

`setresource stone 32` simulates supplying the resource and creates the pending Stoneworking Scroll. `unlock stoneworking` is a test shortcut that simulates the translated scroll, advances the villager to Apprentice, and unlocks the player's stone-tool recipes.

To test the real loop:

1. Get a Toolsmith, Weaponsmith, or Armorer and keep the player near it.
2. Trade until the blacksmith reaches Apprentice, then interact to see the `Provide 32 Stone to upgrade to the next level` request.
3. Use Stone on the blacksmith until the contribution reaches `32/32`.
4. Take the generated Knowledge Scroll to a nearby Librarian.
5. Take the translated scroll back to the Toolsmith.
6. Hand the translated scroll back to the blacksmith.
7. Open the recipe book and confirm the stone sword, pickaxe, axe, shovel, and hoe are unlocked.

## Architecture

- `progression/ProgressionStep.java` describes one rank-to-rank technology step.
- `progression/ProgressionDefinitions.java` contains the current Toolsmith steps.
- `progression/ProgressionService.java` runs the resource -> scroll -> Librarian -> knowledge -> advancement loop.
- `VillagerProgress` stores villager resource, knowledge, technology, rank, and pending-scroll state.
- `PlayerProgress` stores player recipe features.
- `RecipeProgression` awards recipes only after the matching feature is granted.
- `RecipeBookGateMixin` blocks vanilla advancement recipe awards for gated tools.
- `CraftingMenuMixin` is the final server-side crafting safety check.

See `PROGRESSION_FILE_MAP.md` for the file connection map.
