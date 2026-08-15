# Starter Mod

This Fabric mod changes Minecraft technology progression so villagers teach the player each material tier.

## First Working Loop: Stoneworking

Wood tools remain available normally. The first interaction with an eligible Novice blacksmith grants the custom `Stone Age` advancement and Stone recipes. The first portable knowledge scroll is created for Copperworking after the villager reaches Apprentice.

The first progression is:

```text
Blacksmith Novice
    -> first interaction unlocks Stone recipes
    -> villager reaches Apprentice through normal trading
    -> request 32 Copper to upgrade the source villager
    -> Copperworking Scroll trade becomes available after the Copper requirement is completed
    -> buy the scroll for emeralds
    -> travel to another village
    -> Librarian translates scroll
    -> translated scroll is given to that village's blacksmith
    -> destination blacksmith progression advances
    -> player receives only that profession's matching recipes and advancement at each new rank
    -> later vanilla trades remain gated by each resource requirement
```

The same engine applies to Toolsmiths, Weaponsmiths, and Armorers. The first interaction unlocks Stone immediately, Copper requires the Novice -> Apprentice upgrade, Iron and Gold require Apprentice -> Journeyman, Journeyman -> Expert has no requirement, and Diamond requires the Expert -> Master upgrade. Netherite remains vanilla smithing progression. The material, quantity, knowledge, and recipes are defined in `ProgressionDefinitions`, not in the interaction handlers.

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

`setresource stone 0` completes the immediate Stone progression for testing. `unlock stoneworking` is a test shortcut that completes the same progression without resource handoff.

To test the real loop:

1. Get a Toolsmith, Weaponsmith, or Armorer and keep the player near it.
2. Interact with the Novice blacksmith once to unlock Stone.
3. Trade until the blacksmith reaches Apprentice, then interact to see the `Provide 32 Copper to upgrade to the next level` request.
4. Use Copper on the blacksmith until the contribution reaches `32/32`.
5. Buy the Copperworking Scroll, then travel to another village and give it to a Librarian.
6. Give the translated scroll to the destination blacksmith.
7. Open the recipe book and confirm only the matching profession's Stone recipes are unlocked.

## Architecture

- `progression/ProgressionStep.java` describes one technology step and its rank requirement.
- `progression/ProgressionDefinitions.java` contains the Toolsmith, Weaponsmith, and Armorer steps and recipe groups.
- `progression/ProgressionService.java` runs local progression, scroll trades, Librarian translation, knowledge transfer, and advancement awarding.
- `VillagerProgress` stores villager resource, knowledge, technology, rank, and compatible legacy scroll state.
- `PlayerProgress` stores player recipe features.
- `RecipeProgression` awards recipes only after the matching feature is granted.
- `RecipeBookGateMixin` blocks vanilla advancement recipe awards for gated blacksmith recipes.
- `CraftingMenuMixin` is the final server-side crafting safety check.

See `PROGRESSION_FILE_MAP.md` for the file connection map.
