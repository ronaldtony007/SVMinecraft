# Progression File Map

This is the practical map for the current gameplay loop. Follow the arrows when changing behavior.

## The Loop

```text
Player makes a successful blacksmith trade
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
Player receives the first-trade advancement and current resource request
         |
         v
Player supplies the required resource
        |
         v
Resource criterion is complete
         |
         v
Villager's vanilla rank and offers advance
         |
         v
VillagerContributionHandler
        |
        v
ProgressionService.contributeResource()
        |
        v
Villager offers a knowledge scroll trade
        |
        v
LibrarianTranslationHandler
        |
        v
ModItems.giveTranslatedKnowledgeScroll()
        |
        v
VillagerContributionHandler
        |
        v
ProgressionService.completeScrollUnlock()
        |
        +--> VillagerProgress: unlock next trade rank
         +--> PlayerProgress: unlock recipe feature
         +--> RecipeProgression: send recipes to recipe book
          +--> data/silvers_villagers/advancement/{profession}_{age}.json: award age
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

The player learns the recipes and matching age advancement when a villager reaches the relevant rank through normal trading or a debug rank change. The source villager completes its local progression when the resource requirement is met. That completion unlocks fixed emerald scroll trades. The Librarian translates a scroll, and a different eligible villager can then learn it.

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
| First Novice interaction | None | None | 0 | Profession-specific Stone recipes |
| Novice | Apprentice | Copper ingot | 32 | Profession-specific Copper recipes |
| Apprentice | Journeyman | Iron ingot | 32 | Profession-specific Iron and Gold recipes |
| Journeyman | Expert | None | 0 | No new recipe tier |
| Expert | Master | Diamond | 16 | Profession-specific Diamond recipes |

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
- `unlockedTradeLevel`: highest progression tier completed by this villager.
- `pendingScrollRank`: legacy persisted field retained for old saves; scrolls are now fixed villager trades.

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
- Adds progression-gated emerald scroll trades.
- Completes local progression or a matching translated scroll.
- Upgrades the destination villager's vanilla rank when knowledge is transferred.
- Adds villager knowledge and technology.
- Grants the player's recipe feature.
- Tracks resource-cleared progression separately from vanilla rank, keeps scroll offers at the end of the offer list, and refreshes player recipes.

If a rule must work from a command, interaction, or mixin, put the rule here and let those entry points call it.

### `profession/BlacksmithEligibility.java`

Identifies Toolsmith, Weaponsmith, Armorer, Farmer, Butcher, Fisherman, and Shepherd as progression professions, and identifies Librarians.

The resource, scroll, and knowledge mechanism applies to the resource professions. Shepherds use the same persistent player recipe features and rank synchronization, but do not use resource contributions or knowledge-scroll trades.

Fletchers use the same persistent villager and player progression state. Apprentice requires 3 String + 3 Sticks for Bow and Arrow recipes, Journeyman unlocks Crossbow, and Master unlocks Tipped Arrow crafting. Fletcher Stick-to-Emerald offers are removed after vanilla trade generation; loot and drops are not gated.

## Player Interactions

### `interaction/VillagerMilestoneHandler.java`

On villager interaction, asks the service to synchronize player unlocks and announce the current request. It does not change villager state itself.

### `interaction/VillagerContributionHandler.java`

Handles two items used on villagers:

- Generic translated knowledge scroll: completes the matching step and upgrades the villager if needed.
- A material required by the current step: increments that material's request.

The required item comes from `ProgressionStep`; this handler does not contain material-specific branches.

### `interaction/LibrarianTranslationHandler.java`

Consumes an untranslated scroll when used on a Librarian and gives a translated scroll carrying the same technology and profession. It does not locate or require a target villager.

## Recipes and Recipe Locking

### `recipe/RecipeProgression.java`

Maps completed profession technologies to player recipe features and awards only the matching vanilla recipe holders. It also removes gated recipes when the player does not own the feature, and checks food inputs in furnaces and smokers; campfire cooking remains unrestricted. Shepherd colored beds, dyes, banners, and painting use the same recipe feature path; White Bed remains vanilla-controlled. Netherite smithing remains vanilla-controlled.

Add a new recipe group here when the definitions table gains a new technology.

### `mixin/RecipeBookGateMixin.java`

Filters vanilla `ServerPlayer.awardRecipesByKey()` calls. This is necessary because vanilla advancements can award stone recipes before the villager loop completes.

Without this mixin, the recipe book may unlock stone tools from the normal Minecraft `upgrade_tools` advancement.

### `mixin/CraftingMenuMixin.java`

Final server-side safety gate. Even if a client has stale recipe data, a gated recipe result is removed unless the player owns the matching profession and technology feature.

## Villager Trade Mixins

### `mixin/VillagerProgressionMixin.java`

Hooks successful vanilla villager trades and asks `ProgressionService` to synchronize player unlocks and announce the current request. It does not issue scrolls directly.

### `mixin/VillagerTradeGateMixin.java`

Keeps custom scroll offers at the end of the offer list by removing and re-adding them around vanilla trade generation. Vanilla rank advancement remains independent; each reached rank is then checked against its resource criterion.

### `mixin/VillagerTradesMixin.java`

Invoker interface exposing vanilla `updateTrades()`. The progression service uses it for debug rank changes and scroll-offer ordering.

## Items and Resources

### `item/ModItems.java`

Registers two generic item types:

- `knowledge_scroll`
- `translated_knowledge_scroll`

The displayed name includes the technology, profession, and rank. The item type is generic so future technologies do not require new item classes.

### `assets/silvers_villagers/items/*.json`

Connect registered item IDs to item models.

### `assets/silvers_villagers/models/item/*.json`

Choose the visual texture: paper for the untranslated scroll and book for the translated scroll.

### `assets/silvers_villagers/lang/en_us.json`

Fallback names for the generic item types. Runtime custom names are produced by `ModItems`.

### `data/silvers_villagers/advancement/villager_progression.json`

Root of the Villager Progression advancement tree. It is completed by the first vanilla villager trade or when progression starts through a translated scroll.

### `data/silvers_villagers/advancement/{profession}_{age}.json`

The nine profession-specific nodes awarded when the player learns Stoneworking, Ironworking, or Diamondworking from the matching Toolsmith, Weaponsmith, or Armorer. These nodes have no recipe rewards; recipes remain controlled by the villager progression system.

### `fabric.mod.json`

Declares the main entrypoint and common mixin configuration.

### `silversvillagers.mixins.json`

Lists common mixins, including the recipe-book gate.

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

The real loop is resource -> local progression -> emerald scroll trade -> Librarian -> translated scroll -> destination blacksmith. `unlock` is only a test shortcut.

## Adding a New Progression

1. Add IDs in `TechnologyId`, `KnowledgeId`, and `PlayerFeatureId`.
2. Add a `ProgressionStep` and its recipe IDs in `ProgressionDefinitions`.
3. Confirm the profession's recipe and cooking gates are covered by `RecipeProgression`.
4. Confirm the profession is accepted by `BlacksmithEligibility`.
5. Test resource contribution, scroll trade availability, Librarian translation, translated-scroll acceptance, recipes, and vanilla trade unlocking.

Do not add a new interaction handler for Stoneworking, Ironworking, or Diamondworking. The point of `ProgressionStep` is that those use the same engine.

## Verification

```powershell
.\gradlew.bat clean build
.\gradlew.bat runClient
```

The build validates Java, resources, and mixin configuration. The client run validates startup. The full progression loop still requires an in-world test.
