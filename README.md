### Introduction
LovelyStats is there to track your in-game infos. Currently there is no page for viewing the stats. Instead you have commands.
I tried to make it as efficient as possible so there is no perfomance impact. 

### Stats tracked
- Playtime is tracked as sessions with active and idle time
- Blocks placed
- Blocks broken
- Items collected
- Items dropped
- Items crafted
- Tools broken (not implemented yet)

### Displaying stats
Yeah so currently there is no UI for it actually and I will make one soon but currently we have commands
- /stats playtime
- /stats block
- /stats item

### Implementation
I tried to use events as much as possible. But some things are not possible with it. <br> 
Items collected. There is an event *InteractivelyPickupItemEvent* but this is only for interactables. <br>
Items crafting. There is an event *CraftRecipeEvent* but this is only for the players crafting. <br>

To fix this I use the PacketAdapters. I dont like this solution because i dont know how consistent it is. Especially the one for the crafting.
So if someone knows a better solution to that I would gladly take some help.
