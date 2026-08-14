# Starter Mod

This Fabric mod changes Minecraft technology progression so villagers teach the player each material tier.

## First Working Loop: Stoneworking

Wood tools remain available normally. Discovering stone grants the custom `Stone Age` advancement, but stone-tool recipes remain locked.

The first progression is:

```text
Toolsmith Novice
    -> request 32 Stone
    -> generate Stoneworking Scroll
    -> Librarian translates scroll
    -> Toolsmith reaches Apprentice
    -> player receives stone-tool recipes
    -> Apprentice trades become available
```

The same engine then defines Toolsmith Apprentice -> Journeyman with Iron, and later ranks with Diamond. The material, quantity, knowledge, and recipes are defined in `ProgressionDefinitions`, not in the interaction handlers.

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

1. Get a Toolsmith and keep the player near it.
2. Interact with the Toolsmith to see the `32 Stone` request.
3. Use Stone on the Toolsmith until the contribution reaches `32/32`.
4. Take the generated Knowledge Scroll to a nearby Librarian.
5. Take the translated scroll back to the Toolsmith.
6. Trade until the Toolsmith reaches Apprentice, then give it the translated scroll.
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
