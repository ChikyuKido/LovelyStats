### Introduction
LovelyStats is a server side plugin to track your in-game infos. <br>
It also provides a leaderboard to see the top stats on the server.  <br>
I tried to make it as efficient as possible so there is no perfomance impact. <br>
### Screenshots
|                                          |                                          |                                          |                               |
|------------------------------------------|------------------------------------------|------------------------------------------|-------------------------------|
| ![Player](.github/Player.png)            | ![Player](.github/Playtime.png)          | ![Blocks](.github/Blocks.png)            | ![Entity](.github/Entity.png) |
| ![Player](.github/LeaderboardPlayer.png) | ![Blocks](.github/LeaderboardBlocks.png) | ![Entity](.github/LeaderboardEntity.png) |                               |

### Stats tracked
- Playtime is tracked as sessions with active and idle time
- Blocks placed
- Blocks broken
- Items collected
- Items dropped
- Items crafted
- Entities killed, killed by, damage dealt, damage received
- Tools broken (not implemented yet)

### Displaying stats
Use the /stats command to open the ui and see your stats. <br> 
Use the /stats --player=<name or uuid> command to open the stats of another player<br> 
Use the /leaderboardStats command to see the leaderboard <br> 

### Implementation
I tried to use events as much as possible. But some things are not possible with it. <br> 
Items collected. There is an event *InteractivelyPickupItemEvent* but this is only for interactables. <br>
Items crafting. There is an event *CraftRecipeEvent* but this is only for the players crafting. <br>

To fix this I use the PacketAdapters. I dont like this solution because i dont know how consistent it is. Especially the one for the crafting.
So if someone knows a better solution to that I would gladly take some help.
