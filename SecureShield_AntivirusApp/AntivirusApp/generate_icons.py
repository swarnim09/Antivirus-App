#!/usr/bin/env python3
"""Generate a simple launcher icon for the antivirus app."""
import struct, zlib, os

def create_simple_png(width, height, color_rgb, filename):
    """Create a minimal PNG with a solid color."""
    r, g, b = color_rgb
    raw = b''
    for y in range(height):
        raw += b'\x00'  # filter type none
        for x in range(width):
            raw += bytes([r, g, b])

    def chunk(name, data):
        c = name + data
        return struct.pack('>I', len(data)) + c + struct.pack('>I', zlib.crc32(c) & 0xFFFFFFFF)

    sig = b'\x89PNG\r\n\x1a\n'
    ihdr_data = struct.pack('>IIBBBBB', width, height, 8, 2, 0, 0, 0)
    idat_data = zlib.compress(raw)
    png = sig + chunk(b'IHDR', ihdr_data) + chunk(b'IDAT', idat_data) + chunk(b'IEND', b'')
    with open(filename, 'wb') as f:
        f.write(png)
    print(f"Created: {filename}")

os.makedirs('app/src/main/res/mipmap-hdpi', exist_ok=True)
create_simple_png(72, 72, (26, 35, 126), 'app/src/main/res/mipmap-hdpi/ic_launcher.png')
create_simple_png(72, 72, (26, 35, 126), 'app/src/main/res/mipmap-hdpi/ic_launcher_round.png')
print("Icons generated!")
