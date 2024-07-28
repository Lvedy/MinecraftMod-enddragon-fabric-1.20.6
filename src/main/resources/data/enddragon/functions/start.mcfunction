gamemode survival @a
execute as @e[type=minecraft:ender_dragon] at @s run data modify entity @s NoAI set value false
execute as @e[type=minecraft:ender_dragon] at @s run tp ~ ~-70 ~
title @a title "追杀开始"
start true
ok true