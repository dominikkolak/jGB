package snapshot;

import ppu.PPUMode;

public record PictureRegisterSnapshot(int lcdc,
                                      int stat,
                                      int scy,
                                      int scx,
                                      int ly,
                                      int lyc,
                                      int bgp,
                                      int obp0,
                                      int obp1,
                                      int wy,
                                      int wx,
                                      PPUMode mode
) {}
