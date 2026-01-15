# DMG-CPU SM83 Emulator
CPU emulation module with Load and Arithmetic instructions implemented.

## Features

- Components (ALU, Register File, etc..)
- Load / Arithmetic Instructions

### Fully Implemented Instructions

- Load Instructions (LD)
- Arithmetic Instructions (ADD / SUB / SBC / INC / DEC)
- Misc (NOP / HALT / JP nn / JP cc, nn)

### Instructions To Be Implemented

- Logic instructions
- Bit instructions
- Stack operations
- Memory indirect
- Relative jumps
- Interrupt control
- STOP

## Test Results on Reduced Instruction Set

- E... Executed
- R... Result

```bash
E: A=0x08, B=0x03
R: A=0x08, B=0x03

E: A=0x42, B=0x42, C=0x42
R: A=0x42, B=0x42, C=0x42

E: A=0x09
R: A=0x09

E: A=0x0D (13), N=1
R: A=0x0D (13), N=1

E: A=0x01, C=1
R: A=0x01, C=1, Z=0
```