# jGB - Game Boy Emulator
An experimental Game Boy (DMG) emulator written in Java, created to explore how a managed, object-oriented language can model real hardware systems, featuring integrated debugging tools and a complete application environment.

<p align="center">
  <img src="docs/tetris_dark_light.png" alt="Tetris">
</p>

## Features

- **Sharp LR35902 CPU Emulation** - Complete instruction set implemented (512 instructions)
- **Sub-Instruction Cycle Model** - Instructions broken into machine cycles for improved timing accuracy
- **PPU Graphics** - Functional Picture Processing Unit implementation
- **Memory Bank Controllers** - MBC0, MBC1, MBC2, MBC3 (RAM4)
- **Real-Time Clock** - MBC3 RTC support
- **Save States** - Battery-backed RAM for game saves and game history
- **Debug Tools** - Registers, Memory Viewer, Disassembler, Breakpoints though snapshots

## Showcase

<p align="center">
  <img src="docs/tetris.gif" alt="Tetris Demo">
</p>

## Installation

### Download Release

Download the latest release from the [Releases](../../releases) page.

### Build from Source

**Requirements:**
- Java 17 or higher
- Maven 3.6+

**Steps:**

```bash
git clone https://github.com/dominikkolak/jGB.git
cd jGB

mvn clean package

java -jar target/jGB-1.0.jar
```

## Screenshots

| Pokémon Red | Super Mario Land | The Legend of Zelda |
|:-----------:|:----------------:|:-------------------:|
| ![](screenshots/pokemon_red_title.png) | ![](screenshots/super_mario_land_title.png) | ![](screenshots/the_legend_of_zelda_title.png) |

| Metroid II | Donkey Kong | Dr. Mario |
|:----------:|:-----------:|:---------:|
| ![](screenshots/metroid_2_title.png) | ![](screenshots/donkey_kong_title.png) | ![](screenshots/dr_mario_title.png) |


## Usage
The emulator provides a graphical interface by default and can also be run in headless mode for debugging purposes. A simplified backup interface is available as an alternative.

```bash
java -jar jGB.jar

java -jar jGB.jar --headless

java -jar jGB.jar --backup
```

The User Interface also provides options to reconfigure the control mapping and change color pallets, even to non standard colors.

## Test Results

The emulator has been tested against several standard Game Boy validation suites. 
Results are very mixed. The Core CPU instruction handling is mostly functional, but timing accuracy, interrupts, DMA, and especially the PPU implementation are incomplete and unreliable.

| Test Suite | Result |
|------------|--------|
| **Blargg CPU tests** | Most individual instruction tests pass, but `cpu_instr`, `oam_bug`, and interrupt timing still fail |
| **Memory / timing tests** | Basic timing behavior works, but edge cases remain incorrect |
| **Mooneye acceptance tests** | Many failures, mainly related to PPU timing, interrupts, DMA, and hardware edge cases |
| **MBC tests** | Largely functional, with some MBC2 RAM-related failures |

### Major Known Problems

- The **PPU implementation is fundamentally incorrect** and requires full rewrite  
- **OAM DMA behavior is broken** and causes test failures and glitches  
- **Interrupt handling is unreliable** and often triggers at incorrect times or not at all  
- **Timing accuracy is inconsistent**, especially with edge cases  
- Several hardware edge cases and quirks are missing

Overall hardware accuracy is limited and compatibility with real Cartridges is inconsistent.

### Raw Results

Detailed per-test results are available here:

See: [test_results.pdf](docs/test_results.pdf)

## Debugging

The emulator includes the following debugging tools:

- **CPU Debugger** - Step through instructions, view registers
- **Memory Viewer** - Inspect memory regions like around Program Counter and Stack Pointer or Custome Memory Region in real-time  
- **Disassembler** - View disassembled code at current PC (only snapshot in live mode)
- **Breakpoints** - Set execution breakpoints by address
- **PPU Debugger** - Inspect PPU Registers
- **Flags / Interrupts** - Inspect Flags and Interrupts

## Architecture
The emulator is organized into subsystems that try to mirror the Game Boy hardware organization:

<p align="center">
  <img src="docs/architecture.png" width="50%"/>
</p>

## Sub-Instruction Execution Model
Instead of executing instructions atomically in a single step and the doing a callback at the end, jGB breaks each instruction into machine cycles (M-cycles). 
Each M-cycle represents hardware operations and consumes 4 T-cycles. This decreses the complexity and severity of tming issues and makes the emulator more accurate.

### Execution Flow
When the CPU executes an instruction, it progresses through multiple cycles:

After each instruction or "sub-operation", the CPU returns the number of T-cycles consumed. The Master Time Controller then advances all other components by the same amount.

![](docs/execution.png)

This ensures all components observe the same passage of time and remain synchronized. 

This model provides better timing granularity than naive atomic executions but it does not achieve perfect cycle accuracy on its own.
The sub-instruction model is an architectural choice that improves timing behavior over naive implementations, but significant work remains to make it a true cycle-accurate emulator.

## Related Work
This repository has been squashed from three previouse repos. They can be found here:
https://github.com/dominikkolak/dmg-core-prototype
https://github.com/dominikkolak/dmg-cpu-sm83-e
https://github.com/dominikkolak/dmg-cpu-cart-e
