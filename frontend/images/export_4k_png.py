import math
import os

# Standalone script to export the Eiris 8-dot molecule/sunburst logo to PNG if Pillow is installed
try:
    from PIL import Image, ImageDraw  # type: ignore
    
    def create_dot_logo_png(filename, size=4096):
        img = Image.new('RGBA', (size, size), (0, 0, 0, 0))
        draw = ImageDraw.Draw(img)
        
        center = size // 2
        R_outer = int(size * 0.325)
        r_outer = int(size * 0.065)
        
        R_inner = int(size * 0.17)
        r_inner = int(size * 0.035)
        
        # Color palette matching Eiris website theme (--eiris-sand-tan #e1b382, #c89666, etc.)
        primary_color = (225, 179, 130, 255)    # #e1b382
        secondary_color = (200, 150, 102, 255)  # #c89666
        light_color = (255, 255, 255, 240)      # White-gold highlight
        
        angles = [0, 45, 90, 135, 180, 225, 270, 315]
        
        # Draw Outer Ring
        for i, angle in enumerate(angles):
            rad = math.radians(angle)
            cx = center + R_outer * math.cos(rad)
            cy = center + R_outer * math.sin(rad)
            color = light_color if angle in [0, 45] else (secondary_color if angle == 315 else primary_color)
            draw.ellipse(
                [(cx - r_outer, cy - r_outer), (cx + r_outer, cy + r_outer)],
                fill=color
            )
            
        # Draw Inner Ring
        for i, angle in enumerate(angles):
            rad = math.radians(angle)
            cx = center + R_inner * math.cos(rad)
            cy = center + R_inner * math.sin(rad)
            color = light_color if angle in [0, 45] else (secondary_color if angle == 315 else primary_color)
            draw.ellipse(
                [(cx - r_inner, cy - r_inner), (cx + r_inner, cy + r_inner)],
                fill=color
            )
            
        img.save(filename, "PNG")
        print(f"Successfully generated {filename} ({size}x{size})")

    if __name__ == "__main__":
        base_dir = os.path.dirname(os.path.abspath(__file__))
        create_dot_logo_png(os.path.join(base_dir, "eiris-logo-4k.png"), size=4096)
        create_dot_logo_png(os.path.join(base_dir, "favicon.png"), size=256)
except ImportError:
    print("Pillow library not found. Use the generated high-resolution SVG files (logo-icon.svg, eiris-logo-4k.svg, favicon.svg).")
