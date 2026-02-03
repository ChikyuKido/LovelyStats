import json
import os

manual_map = {
    "Bear_Polar": "Bear_Polar",
    "Wolf_White": "Wolf_White",
    "Spider_Cave": "Spider_Cave",
    "Snake_Rattle": "Snake_Rattle",
    "Snake_Cobra": "Snake_Cobra",
    "Risen_Knight": "Skeleton_Knight",
    "Dungeon_Skeleton_Sand_Archer": "Skeleton_Sand_Archer",
    "Dungeon_Skeleton_Sand_Mage": "Skeleton_Sand_Mage",
    "Dungeon_Skeleton_Sand_Assassin": "Skeleton_Sand_Assassin",
    "Dungeon_Skeleton_Sand_Soldier": "Skeleton_Sand_Soldier",
    "Kweebec_Prisoner":"Kweebec_Rootling",
    "Kweebec_Merchant": "Kweebec_Rootling",
    "Trork_Unarmed": "Trork_Hunter",
    "Edible_Rat": "Rat",
    "Goblin_Duke_Phase_3_Fast": "Goblin_Duke",
    "Goblin_Duke_Phase_3_Slow": "Goblin_Duke",
    "Edible_Goblin_Scrapper": "Goblin_Scrapper",
    "Goblin_Duke_Phase_2": "Goblin_Duke",
    "Dungeon_Scarak_Defender": "Scarak_Defender",
    "Dungeon_Scarak_Defender_Patrol": "Scarak_Defender",
    "Dungeon_Scarak_Louse": "Scarak_Louse",
    "Dungeon_Scarak_Broodmother": "Scarak_Broodmother",
    "Dungeon_Scarak_Broodmother_Young": "Scarak_Broodmother",
    "Dungeon_Scarak_Seeker": "Scarak_Seeker",
    "Dungeon_Scarak_Seeker_Patrol": "Scarak_Seeker",
    "Dungeon_Scarak_Fighter": "Scarak_Fighter",
    "Dungeon_Scarak_Fighter_Patrol": "Scarak_Fighter",
    "Spectre_Void": "Spectre_Void",
    "Klops_Miner_Patrol": "Klops_Miner",
    "Klops_Merchant_Patrol": "Klops_Merchant",
    "Klops_Merchant_Wandering": "Klops_Merchant",

    "Golem_Guardian_Void": "NONE",
    "Hatworm": "NONE",
    "Wraith_Lantern": "NONE",
    "Skeleton": "NONE",
    "Tuluk_Fisherman": "NONE",
    "Scarak_Fighter_Royal_Guard": "NONE",
    "Bramblekin": "NONE",
    "Bramblekin_Shaman": "NONE",
    "Slothian": "NONE",
    "Klops_Merchant": "NONE",
    "Klops_Gentleman": "NONE",
    "Klops_Miner": "NONE",
    "Quest_Master": "NONE",
    "Temple_Kweebec_Merchant": "NONE",
    "Temple_Klops": "NONE",
    "Temple_Klops_Merchant": "NONE",
    "Temple_Mithril_Guard": "NONE",
    "Dragon_Fire": "NONE"
}

def get_memory_name(json_data, file_name):
    if file_name in manual_map:
        return manual_map[file_name]
    modify = json_data.get("Modify", {})
    is_memory = modify.get("IsMemory", False)
    if is_memory:
        memory_override = modify.get("MemoriesNameOverride")
        return memory_override if isinstance(memory_override, str) else file_name
    else:
        print("Without Image: "+file_name)
    return None

def scan_jsons(dir_path):
    result = {}
    for root, _, files in os.walk(dir_path):
        if "_Core" in root:
            continue
        for file in files:
            if file.lower().endswith(".json"):
                file_path = os.path.join(root, file)
                if "Component" in file_path or "Template" in file_path or "Empty_Role" in file_path:
                    continue
                if "Wander" in file_path:
                    file_path = file_path.replace("_Wander","")
                if "Patrol" in file_path:
                    file_path = file_path.replace("_Patrol","")
                try:
                    with open(file_path, "r", encoding="utf-8") as f:
                        data = json.load(f)

                    file_id = os.path.splitext(file)[0]
                    memory_name = get_memory_name(data, file_id)
                    if memory_name:
                        result[file_id] = memory_name
                except (json.JSONDecodeError, FileNotFoundError):
                    continue
    return result

def scan_pngs(dir_path):
    pngs = set()
    for root, _, files in os.walk(dir_path):
        for file in files:
            if file.lower().endswith(".png"):
                name = os.path.splitext(file)[0]
                pngs.add(name)
    return pngs

def compare_memories(memory_map, png_set):
    memory_values = set(v for v in memory_map.values() if isinstance(v, str))

    for mem in memory_values:
        if mem not in png_set:
            print(f"ERROR: Memory '{mem}' does not have a corresponding PNG!")


    for png in png_set:
        if png not in memory_values:
            print(f"ERROR: PNG '{png}.png' is not referenced by any memory map!")

if __name__ == "__main__":
    json_dir = "/home/kido/.var/app/com.hypixel.HytaleLauncher/data/Hytale/install/release/package/game/latest/Assets/Server/NPC/Roles/"
    png_dir = "/home/kido/.var/app/com.hypixel.HytaleLauncher/data/Hytale/install/release/package/game/latest/Assets/Common/UI/Custom/Pages/Memories/npcs/"

    memory_map = scan_jsons(json_dir)
    pngs = scan_pngs(png_dir)

    compare_memories(memory_map, pngs)
    with open("roles.json", "w", encoding="utf-8") as f:
        json.dump(memory_map, f, indent=4, ensure_ascii=False)

