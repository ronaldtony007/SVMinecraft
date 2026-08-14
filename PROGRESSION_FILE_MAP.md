# Progression File Map

This is the practical map for the current gameplay loop. Follow the arrows when changing behavior.

## The Loop

```text
Player reaches the next blacksmith rank
        |
        v
Player interacts with Toolsmith, Weaponsmith, or Armorer
        |
        v
VillagerMilestoneHandler
        |
        v
ProgressionService.requestNextResource()
        |
        v
Player supplies Stone
        |
        v
BlacksmithContributionHandler
        |
        v
ProgressionService.contributeResource()
        |
        v
ModItems.giveKnowledgeScroll()
        |
        v
LibrarianTranslationHandler
        |
        v
ModItems.giveTranslatedKnowledgeScroll()
        |
        v
BlacksmithContributionHandler
        |
        v
ProgressionService.completeScrollUnlock()
        |
        +--> VillagerProgress: unlock next trade rank
         +--> PlayerProgress: unlock recipe feature
         +--> RecipeProgression: send recipes to recipe book
         +--> data/startermod/advancement/{profession}_{age}.json: award age
         +--> vanilla villager offers: next tier available
```

## The Important Separation

Do not combine these three states:

```text
Vanilla villager rank
    Toolsmith Novice, Apprentice, Journeyman, Expert, Master

Villager knowledge
    STONEWORKING, IRONWORKING, DIAMONDWORKING

Player recipe features
    STONEWORKING_RECIPES, IRONWORKING_RECIPES, DIAMONDWORKING_RECIPES
```

The translated scroll is the bridge. A villager can reach a vanilla rank without being allowed to use that rank's trades until the progression step is complete. The player receives recipes and the matching age advancement only when the translated knowledge is transferred.

## Where Definitions Live

### `progression/ProgressionStep.java`

Data structure for one repeatable progression step. It contains:

- Technology identifier
- Knowledge identifier
- Profession
- Starting rank
- Next rank
- Required material
- Required quantity
- Player recipe identifiers

Do not add interaction code here.

### `progression/ProgressionDefinitions.java`

The content table. Current blacksmith definitions are:

| Starting rank | Next rank | Material | Quantity | Player recipes |
| --- | --- | --- | --- | --- |
| Novice | Apprentice | Stone | 32 | Stone tools |
| Apprentice | Journeyman | Iron ingot | 32 | Iron tools |
| Expert | Master | Diamond | 16 | Diamond tools |

To add a future profession, add definitions here. The handlers should not need a new `if` branch.

### `progression/TechnologyId.java`

Stable IDs for villager technologies.

### `progression/KnowledgeId.java`

Stable IDs for knowledge stored on villagers.

### `progression/PlayerFeatureId.java`

Stable IDs for recipes/features stored on players.

## State Files

### `progression/VillagerProgress.java`

Persistent state attached to each villager:

- `resourceContribution`: current request progress, such as `12/32`.
- `knowledge`: knowledge already learned by this villager.
- `unlockedTechnologies`: completed technology steps.
- `unlockedTradeLevel`: highest trade tier made available by this system.
- `pendingScrollRank`: rank whose scroll was generated and is waiting for Librarian translation/return.

Change the `CODEC` and `empty()` if you add or rename persistent fields.

### `progression/PlayerProgress.java`

Persistent player state. `unlockedPlayerFeatures` controls which recipe groups the player owns.

### `persistence/ModAttachments.java`

Registers the persistent `VillagerProgress` and `PlayerProgress` attachments. It connects the records to Minecraft world saving.

## Core Engine

### `progression/ProgressionService.java`

The only file that should make progression decisions. It:

- Finds the next `ProgressionStep`.
- Announces the current material request.
- Accepts material contributions.
- Generates the generic knowledge scroll.
- Completes the translated scroll.
- Advances the villager's gated trade level.
- Adds villager knowledge and technology.
- Grants the player's recipe feature.
- Refreshes villager trades and player recipes.

If a rule must work from a command, interaction, or mixin, put the rule here and let those entry points call it.

### `profession/BlacksmithEligibility.java`

Identifies Toolsmith, Weaponsmith, and Armorer as eligible blacksmithing professions, and identifies Librarians.

The first definitions use Toolsmith. Later definitions can use other professions without changing the resource/scroll/knowledge mechanism.

### `interaction/VillagerLocator.java`

Finds the nearest eligible blacksmith for a Librarian. This supports carrying scrolls between villages.

## Player Interactions

### `interaction/VillagerMilestoneHandler.java`

On villager interaction, asks the service to announce the current request. It does not change state itself.

### `interaction/BlacksmithContributionHandler.java`

Handles two items used on villagers:

- Generic translated knowledge scroll: completes the current step.
- The material required by the current step: increments the request.

The required item comes from `ProgressionStep`; this handler does not contain a Stone-only or Iron-only branch.

### `interaction/LibrarianTranslationHandler.java`

Consumes an untranslated generic scroll when used on a Librarian and gives a translated generic scroll for the nearest blacksmith's pending step.

### `interaction/IronworkingWorkstationHandler.java`

The older Smithing Table restriction remains as a separate workstation rule. It checks the nearby villager's Ironworking technology.

## Recipes and Recipe Locking

### `recipe/RecipeProgression.java`

Maps completed technologies to player recipe features and awards the matching vanilla recipe holders. It also removes gated recipes when the player does not own the feature.

Add a new recipe group here when the definitions table gains a new technology.

### `mixin/RecipeBookGateMixin.java`

Filters vanilla `ServerPlayer.awardRecipesByKey()` calls. This is necessary because vanilla advancements can award stone recipes before the villager loop completes.

Without this mixin, the recipe book may unlock stone tools from the normal Minecraft `upgrade_tools` advancement.

### `mixin/CraftingMenuMixin.java`

Final server-side safety gate. Even if a client has stale recipe data, the locked stone/iron/diamond tool result is removed unless the corresponding player feature exists.

## Villager Trade Mixins

### `mixin/VillagerTradeGateMixin.java`

Stops vanilla `Villager.updateTrades()` from generating a higher tier for an eligible villager until `unlockedTradeLevel` permits it.

### `mixin/VillagerProgressionMixin.java`

Hooks successful vanilla villager trades and asks `ProgressionService` to announce the current request. It does not issue scrolls directly.

### `mixin/VillagerTradesMixin.java`

Invoker interface exposing vanilla `updateTrades()`. `ProgressionService.refreshTrades()` uses it after a successful progression step.

## Items and Resources

### `item/ModItems.java`

Registers two generic item types:

- `knowledge_scroll`
- `translated_knowledge_scroll`

The displayed name includes the technology, profession, and rank. The item type is generic so future technologies do not require new item classes.

### `assets/startermod/items/*.json`

Connect registered item IDs to item models.

### `assets/startermod/models/item/*.json`

Choose the visual texture: paper for the untranslated scroll and book for the translated scroll.

### `assets/startermod/lang/en_us.json`

Fallback names for the generic item types. Runtime custom names are produced by `ModItems`.

### `data/startermod/advancement/villager_progression.json`

Root of the Villager Progression advancement tree. It is completed by the first vanilla villager trade or when progression starts through a translated scroll.

### `data/startermod/advancement/{profession}_{age}.json`

The nine profession-specific nodes awarded when a translated Stoneworking, Ironworking, or Diamondworking scroll is handed back to the matching Toolsmith, Weaponsmith, or Armorer. These nodes have no recipe rewards; recipes remain controlled by the villager progression system.

### `fabric.mod.json`

Declares the main/client entrypoints and the common/client mixin configurations.

### `startermod.mixins.json`

Lists common mixins, including the recipe-book gate.

### `src/client/resources/startermod.client.mixins.json`

Lists client-only template mixins.

## Commands

### `command/VillagerProgressCommand.java`

Operator testing entrypoint. The useful commands are:

```text
/villagerprogress reset
/villagerprogress info
/villagerprogress setrank novice
/villagerprogress setrank apprentice
/villagerprogress setresource stone 32
/villagerprogress giveknowledge stoneworking
/villagerprogress unlock stoneworking
```

The real loop is still resource -> scroll -> Librarian -> translated scroll -> Toolsmith. `unlock` is only a test shortcut.

## Adding a New Progression

1. Add IDs in `TechnologyId`, `KnowledgeId`, and `PlayerFeatureId`.
2. Add a `ProgressionStep` in `ProgressionDefinitions`.
3. Add its recipe IDs to `RecipeProgression` and its output items to `CraftingMenuMixin`.
4. Confirm the profession is accepted by `BlacksmithEligibility`.
5. Test resource contribution, scroll generation, Librarian translation, translated-scroll acceptance, recipes, and trade gating.

Do not add a new interaction handler for Stoneworking, Ironworking, or Diamondworking. The point of `ProgressionStep` is that those use the same engine.

## Verification

```powershell
.\gradlew.bat clean build
.\gradlew.bat runClient
```

The build validates Java, resources, and mixin configuration. The client run validates startup. The full progression loop still requires an in-world test.
