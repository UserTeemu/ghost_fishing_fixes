# Ghost Fishing Fixes
A Fabric Minecraft mod that fixes player clients not displaying fishing rods properly when cast by "ghost/fake players" (i.e. players that the server does not tell the clients about).
Normally these fishing rods are not rendered at all but this makes it possible.

This mod is particularly useful with [Create](https://modrinth.com/mod/create-fabric) in particular because its Deployer block uses a server-side fake player to perform actions like fishing.

# Compatibility
This mod aims to be as compatible as possible with everything. It **should** work on newer Minecraft versions, too, since it doesn't touch too many things in the game, but if a fishing rod overhaul was to come, this would mod would probably break.

While it has not been tested, issues with newer versions of Minecraft seem relatively unlikely, and so the mod currently has been set to be able to run on all Minecraft versions since (and including) 1.20.1 (the version it was made for).

It should be noted not crashing does not guarantee compatibility. For example, a fishing rod might get rendered twice per frame due to an incompatibility or the pulling forces when pulling an entity may get doubled.
If any issues arise, feel free to open an issue on GitHub.
