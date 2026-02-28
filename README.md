<h1 align="center">Avocado Client</h1>

<p align="center">
  <strong>Performance-focused Minecraft Forge 1.8.9 Client</strong><br>
  Forked from LiquidBounce b100 and redesigned for optimization, stability, and bypass reliability.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Minecraft-1.8.9-green?style=for-the-badge">
  <img src="https://img.shields.io/badge/Forge-Supported-orange?style=for-the-badge">
  <img src="https://img.shields.io/badge/Mixin-Based-blue?style=for-the-badge">
  <img src="https://img.shields.io/badge/Optimized-Yes-brightgreen?style=for-the-badge">
  <img src="https://img.shields.io/badge/License-GPLv3-red?style=for-the-badge">
  <img src="https://img.shields.io/badge/Made%20in-Vietnam-red?style=for-the-badge">
</p>

---

## Overview

**Avocado Client** is a mixin-based injection hacked client developed for **Minecraft Forge 1.8.9**.

This project is a fork of **LiquidBounce b100**, rebuilt with a focus on:

- performance optimization
- improved combat timing
- enhanced anti-cheat bypass systems
- cleaner and more maintainable architecture
- rendering and event pipeline improvements

Avocado is developed and maintained by Vietnamese developers, aiming to deliver a stable, optimized, and competitive client tailored for the Minecraft 1.8.9 environment.

---

## Official Links

Website: NONE  
YouTube: https://www.youtube.com/@beophiman  
Discord: https://discord.gg/NnZabqafzh

---

## Core Improvements Over LiquidBounce b100

- Reduced CPU overhead and improved tick handling
- Optimized event system execution
- Enhanced velocity and reach handling modes
- Improved bypass logic for modern anti-cheats
- Cleaner module architecture
- Refactored rendering pipeline
- Increased runtime stability

---

## Feature Highlights

### Performance

- Lightweight execution
- Optimized module updates
- Reduced frame drops
- Efficient packet handling

### Combat System

- Advanced bypass techniques
- Multiple velocity modes
- Configurable reach handling
- Improved hit registration logic

### Rendering & HUD

- Clean rendering structure
- Stable 2D and 3D rendering
- Customizable module display

### Architecture

- Modular system design
- Easier debugging and development
- Structured codebase for scalability

---

## Technical Details

Avocado uses **Sponge Mixins** to inject custom logic into Minecraft at runtime.  
This allows modification of behavior without redistributing Mojang’s proprietary source code.

More information about Mixins:  
https://docs.spongepowered.org/5.1.0/en/plugin/internals/mixins.html

---

## Setting Up a Development Workspace

Avocado uses **Gradle**. Make sure Gradle is properly installed.

### 1. Clone the repository

```
git clone https://github.com/AvocadoMC/Avocado-1.8.9.git
```

### 2. Enter project directory

```
cd Avocado-1.8.9
```

### 3. Generate IDE workspace

**For IntelliJ**

```
gradlew --debug setupDevWorkspace idea genIntellijRuns build
```

**For Eclipse**

```
gradlew --debug setupDevWorkspace eclipse build
```

### 4. Open the folder as a Gradle project
### 5. Launch using Forge or Vanilla run configuration

---

## License

This project is licensed under the **GNU General Public License v3.0**.

The license applies only to the clean source code in this repository.  
External source code used during development or compilation may not be covered.

### You are allowed to:

- Use the project
- Modify the project
- Share the project
- Use it commercially

### Requirements:

- You must disclose your modified source code
- You must disclose any code taken from this project
- You may not use this code in closed-source or obfuscated software
- Your modified version must also be licensed under GPL v3

If you redistribute Avocado Client, it must remain open-source.

---

## Contributing

Contributions are welcome.

Current development goals:

1. Performance optimization
2. Rendering system refactor
3. Combat logic improvements
4. Bypass stability enhancements
5. Codebase cleanup and modularization

Developers experienced with Minecraft internals, networking, rendering, or anti-cheat analysis are encouraged to contribute.

---

## Credits

Avocado Client is based on the foundation of **LiquidBounce b100**.

### Main Development & Direction
- BeoPhiMan
- TLZ

Special thanks to the original LiquidBounce developers for the base architecture and mixin framework.

---

<p align="center">
Developed in Vietnam • Built for performance • Designed for competitive environments
</p>