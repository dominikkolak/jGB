SECTION "Header", ROM0[$0100]

    nop
    jp Start

    db $CE,$ED,$66,$66,$CC,$0D,$00,$0B
    db $03,$73,$00,$83,$00,$0C,$00,$0D
    db $00,$08,$11,$1F,$88,$89,$00,$0E
    db $DC,$CC,$6E,$E6,$DD,$DD,$D9,$99
    db $BB,$BB,$67,$63,$6E,$0E,$EC,$CC
    db $DD,$DC,$99,$9F,$BB,$B9,$33,$3E

    db "TESTROM"
    db $00
    db $00
    db $00,$00
    db $00
    db $00
    db $00
    db $00
    db $00
    db $00
    db $00
    db $00
    dw $0000


SECTION "Main", ROM0[$0150]

Start:

    di

    ld sp, $FFFE

WaitVBlank:
    ld a, [$FF44]
    cp 144
    jr c, WaitVBlank

    ld a, 0
    ld [$FF40], a

    ld hl, $9800
    ld bc, 1024
    xor a

ClearLoop:
    ld [hl+], a
    dec bc
    ld a, b
    or c
    jr nz, ClearLoop

    ld a, %10010001
    ld [$FF40], a

MainLoop:
    jr MainLoop
