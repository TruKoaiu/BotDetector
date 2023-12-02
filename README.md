# BotDetector (requested)
A Minecraft 1.19.1 Spigot plugin (and possibly other versions) that asks players simple questions on join.

It's a straightforward plugin (the name may be misleading) designed to make it more challenging for advertising bots to join the server. The plugin achieves this by restricting player movements (to an admin-chosen extent, configurable in the config file) and presenting them with simple algebra questions, such as 2 + 4. If a player answers correctly (clicks the right chat message), they can be teleported back to their original location or to a specified destination.

You have the flexibility to customize messages extensively using placeholders, set restrictions, and manage smaller details like keeping effects to prevent interference with other mods, if necessary, and more.

If you have any questions or advice, please contact me on Discord (trukoaiu).
Thank you for using this plugin.

# BotDetector

A Minecraft 1.19.1 plugin (and possibly other versions) that asks players simple questions and can teleport them upon request.

## Overview

It's a straightforward plugin (the name may be misleading) designed to make it more challenging for advertising bots to join the server. The plugin achieves this by restricting player movements (to an admin-chosen extent, configurable in the config file) and presenting them with simple algebra questions, such as 2 + 4. If a player answers correctly (clicks the right chat message), they can be teleported back to their original location or to a specified destination.

## Features

- Restrict player movements (to chosen extend).
- Teleports on join, on answer, or teleports back after answer (or nothing, choose yourself).
- Ask players simple algebra questions, to free themselfs.
- Customize messages using placeholders.
- Set restrictions and manage smaller details like keeping effects.

## Usage

To install you just put "BotDetector-1.1" (or higher version) in plugins folder. Then if you want to change config file, I recomend to turn the server off (but you can just /reload after), and change what you want there. Everthing should be explained inside the config file.

Next are the commands, you can use 2 commands that have 4 options each.
- On Answer Teleport
/onanswerteleport [set/remove/true/false].
It let you set and remove teleporation on answering the question. 
[set] You can use it to teleport player to the special place, spawn, or anything you like. You can also remove (obvious), and there are also true and false options, that changes if this option should work or not. When you use set or remove it automaticly set's true or false, but you might want to save some place but not use it yes, so you place use /onanswerteleport false, and it will not work.
- On Join Teleport
/onjointeleport [set/remove/true/false].
Works the same way as On Answer Teleport, but teleport player just after joining the server to this place (can used as a simple spawn plugin).
- Answer
/answer [number]
You do not really have to ever use it, but if you do not like clicking messages on chat, have it your way. This is originally early catcher for this command, but I found out it can be used as a writen answer option.

After commands, few comments from me.
This plugin works with multiple worlds, and most plugins, BUT you can make it not working if you make it that way in config.
If you have spawn plugin, then you probably don't need On Join Teleport, so don't try to use everything "because I can" 

Permission
the permissions are "botdetector.setters" for On Answer Teleport and On Join Teleport, but answer do not really have permission set, because i don't find it necessary (maybe I am wrong, contact me then).

## Configuration

Config file, should have all necessary decription how to use it and what is recomended or neccessary to implement in messages. You can play with the config file almost to any extend, but if you use both teleport-back-on-answer and teleport-on-answer then teleport-on-answer takes priority.

## Contact

If you have any questions or advice, please contact me on Discord (trukoaiu).

Thank you for using this plugin.
