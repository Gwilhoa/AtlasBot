# AtlasBot
![logo_b2K.svg](documentations%2Flogo_b2K.svg)


Atlas est un projet d'un bot développé pour le groupe Bitume2000 il a un interet amusant et utile pour les membres du groupe.


# Dépendances principales

- [JDA](https://github.com/discord-jda/JDA)
- [B2K-Api](https://github.com/Gwilhoa/apib2k)
- [maven](https://maven.apache.org/)

# structure du projet
```
.
├── src
│   └── main
│       ├── java
│       │   └── fr
│       │       └── atlas
│       │           ├── BotDiscord.java
│       │           ├── commands
│       │           ├── listeners
│       │           ├── objects
│       │           └── requests
│       └── resources
└── documentation

```

### prérequis

#### installation de maven et java
avant toutes chose il vous faut les libraries necessaire pour le fonctionnement global
##### sur linux
1. mettez a jour l'index de packet
```shell
sudo apt-get update
```

2. installez java
```shell
sudo apt-get java
```
3. installez maven
```shell
sudo apt-get maven
```

##### sur macos
1. installez les package par brew
```shell
brew install java
brew install maven
```

##### compilation du bot
pour récuperer le bot en jar il faut exectuer le script suivant :
```bash
bash scripts/generate.sh
```

suite a cela le code compilé du bot se retrouve dans le dossier ``rendu``