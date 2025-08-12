# DMG-CPU Cartridge Emulator
Game Boy cartridge emulation in Java with ROM/RAM handling and Memory Bank Controller (MBC) support.

## Features

- MBC0
- MBC1
- ROM/RAM Management
- Header Parsing and Validation

## Example Usage
Example shows how to load a ROM and create a Cartridge. Then how to read from a address and write with bank switching.
```java
byte[] romData = RomLoader.loadRom("game.gb");
Cartridge cart = new Cartridge(romData);

int value = cart.read(0x0100);
cart.write(0x2000, 0x01);
```


## Example Test Output
```bash

cartridge{title='CPU_INSTRS', mbc=MBC1, rom=64 KB, ram=0 KB}
CPU_INSTRS
MBC1
-----------------------------
0x0100: true
0xA000: true
0x8000: false
-----------------------------
0x0100: 0x00
0x0150: 0x00
0x4000: 0xC3
-----------------------------
0x4000 = 0xC3
0x4000 = 0xC3
0x4000 = 0xC3
-----------------------------
Cartridge[CPU_INSTRS]
TICK / RESET
-----------------------------
TEST COMPLETE

```

## Memory Map

| Address | Description |
|---------|-------------|
| 0x0000-0x3FFF | ROM Bank 0 (fixed) |
| 0x4000-0x7FFF | ROM Bank N (switchable) |
| 0xA000-0xBFFF | External RAM |

## Requirements

- Java 17+
- Maven 3.6+