Skillful
========

A Minecraft Forge mod adding a comprehensive and highly configurable skill and
perk system.

The mod is not quite ready for release, but you can watch here for progress!

Roadmap
-------

Skillful aims to *comprehensively remedy* the lack of a solid role-playing or
skills mod. More directly, the end goal is to provide the necessary backend for
all mods to cooperatively create a fantastic role-playing experience by giving
both developers and users the tools necessary to do so.

Core Features
-------------

### Configurability

Skillful has been designed to be *insanely configurable*, and you don't need
any modding experience to enjoy the benefits. Craft and design your skills and
perks from scratch, and combine in-game events and effects in entirely new ways
to create a completely unique experience.

Intimidated by all of the options? Don't worry! Sane defaults are included as
well. Through the use of **skill and perk packs**, third-party mods can add
pre-configured sets of skills and perks to provide a great experience with any
supported mod out of the box, with next to no configuration necessary.

### Extensibility

Skillful has been designed from the ground up to be *ridiculously extensible*.
Third-party mods are treated as first-class citizens, and are able to add,
remove, modify, or even replace entirely any piece of added functionality, all
in a user-configurable way.

On top of this, the entire codebase has been [obsessively documented](http://skillful.asrex.net/javadoc/latest/), so if you
want to tweak something, help is readily available.

### Concepts

 * Seed: some event that can cause a skill to gain in progress.
 * Skill: a metric of progress in some area. Skills are "seeded" by in-game
   events to eventually increase in level.
 * Perk: a purchasable collection of one or more effects. Perks may have
   associated requirements and costs.
 * Effect: some modification to the player.

In the coming days, expect these terms to be more fully defined on the wiki.

Licensing and Modpack Use
-------------------------

Code for the mod itself falls under the
[MIT license](http://opensource.org/licenses/MIT) and can be freely used,
modified, and distributed for essentially any purpose.

Additionally, you may freely redistribute this mod without needing to request
permission of any sort (including for use in mod packs). We'd love to
hear from you if you make creative use of it!

That said, if you do chose to make use of Skillful from within your own mod,
please *do not* include Skillful code directly in your own mod's jar, and try
to avoid redistributing the jar itself if possible, as doing so may cause
version and classloading conflicts. Please point your users to the relevant
release on the [Releases page](https://github.com/timothyb89/skillful/releases)
here on Github whenever possible, thanks!

Developer Notes
===============

 * The project makes extensive use of
   [Lombok annotations](http://projectlombok.org/). If you need to depend on
   Skillful, you do **not** need to include Lombok.
 * [SnakeYAML](http://code.google.com/p/snakeyaml/) is required to parse
   configuration files. You may download it from
   [Maven Central](http://mvnrepository.com/artifact/org.yaml/snakeyaml/1.14).
 * The latest JavaDoc can be found at http://skillful.asrex.net/javadoc/latest/
