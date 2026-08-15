# Starter Mod

This Fabric mod changes Minecraft technology progression so villagers teach the player each material tier.

## First Working Loop: Stoneworking

Wood tools remain available normally. Completing the Stoneworking step grants the custom `Stone Age` advancement and unlocks a fixed emerald trade for a portable Stoneworking Scroll.

The first progression is:

```text
Blacksmith Apprentice
    -> normal villager trading teaches the player Stoneworking
    -> request 32 Stone to upgrade the source villager
    -> Stoneworking Scroll trade becomes available
    -> buy the scroll for emeralds
    -> travel to another village
    -> Librarian translates scroll
    -> translated scroll is given to that village's blacksmith
    -> destination blacksmith progression advances
    -> player receives the matching recipes and advancement at each new rank
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
/villagerprogress setrank apprentice
/villagerprogress info
/villagerprogress setresource stone 32
/villagerprogress giveknowledge stoneworking
/villagerprogress unlock stoneworking
```

`setresource stone 32` simulates supplying the resource, completes the source progression, and unlocks its scroll trade. `unlock stoneworking` is a test shortcut that completes the same progression without resource handoff.

To test the real loop:

1. Get a Toolsmith, Weaponsmith, or Armorer and keep the player near it.
2. Trade until the blacksmith reaches Apprentice, then interact to see the `Provide 32 Stone to upgrade to the next level` request.
3. Use Stone on the blacksmith until the contribution reaches `32/32`.
4. Buy the Stoneworking Scroll from the source blacksmith for emeralds.
5. Travel to another village and give the scroll to a Librarian.
6. Give the translated scroll to the destination blacksmith.
7. Open the recipe book and confirm the stone sword, pickaxe, axe, shovel, and hoe are unlocked.

## Architecture

- `progression/ProgressionStep.java` describes one rank-to-rank technology step.
- `progression/ProgressionDefinitions.java` contains the current Toolsmith steps.
- `progression/ProgressionService.java` runs local progression, scroll trades, Librarian translation, knowledge transfer, and advancement awarding.
- `VillagerProgress` stores villager resource, knowledge, technology, rank, and compatible legacy scroll state.
- `PlayerProgress` stores player recipe features.
- `RecipeProgression` awards recipes only after the matching feature is granted.
- `RecipeBookGateMixin` blocks vanilla advancement recipe awards for gated tools.
- `CraftingMenuMixin` is the final server-side crafting safety check.

See `PROGRESSION_FILE_MAP.md` for the file connection map.
