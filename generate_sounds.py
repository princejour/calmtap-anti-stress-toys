import wave
import struct
import math
import random

def generate_wav(filename, duration, sample_rate, wave_type, freq, vol=0.5, fade_out=True):
    with wave.open(filename, 'w') as w:
        w.setnchannels(1)
        w.setsampwidth(2)
        w.setframerate(sample_rate)
        
        n_samples = int(duration * sample_rate)
        
        for i in range(n_samples):
            t = float(i) / sample_rate
            
            # Fade out
            env = 1.0
            if fade_out:
                env = max(0, 1.0 - (i / n_samples))
                
            if wave_type == 'sine':
                val = math.sin(2.0 * math.pi * freq * t)
            elif wave_type == 'noise':
                val = random.uniform(-1.0, 1.0)
                # lowpass simple
            elif wave_type == 'pop':
                val = math.sin(2.0 * math.pi * freq * t * math.exp(-20 * t))
            elif wave_type == 'squish':
                val = math.sin(2.0 * math.pi * freq * t) * math.exp(-5 * t)
            elif wave_type == 'rain':
                val = random.uniform(-1.0, 1.0) * env
            elif wave_type == 'bgm':
                # Ambient chord
                v1 = math.sin(2.0 * math.pi * 220 * t)
                v2 = math.sin(2.0 * math.pi * 277.18 * t) # C#
                v3 = math.sin(2.0 * math.pi * 329.63 * t) # E
                lfo = 0.5 + 0.5 * math.sin(2.0 * math.pi * 0.1 * t)
                val = ((v1 + v2 + v3) / 3.0) * lfo
            elif wave_type == 'spin':
                # low freq rumble
                val = math.sin(2.0 * math.pi * freq * t)
            elif wave_type == 'breathe':
                # gentle wind/noise
                val = random.uniform(-1.0, 1.0) * math.sin(math.pi * t / duration)
            else:
                val = 0
                
            val = val * vol * env
            val_int = int(val * 32767.0)
            val_int = max(-32768, min(32767, val_int))
            w.writeframesraw(struct.pack('<h', val_int))

# Generate BGM (ambient loop)
generate_wav('app/src/main/res/raw/bgm_ambient.wav', 10.0, 44100, 'bgm', 220, 0.3, fade_out=False)

# Generate SFX
generate_wav('app/src/main/res/raw/sfx_pop.wav', 0.1, 44100, 'pop', 800, 0.5, fade_out=True)
generate_wav('app/src/main/res/raw/sfx_squish.wav', 0.3, 44100, 'squish', 150, 0.5, fade_out=True)
generate_wav('app/src/main/res/raw/sfx_bubble.wav', 0.05, 44100, 'pop', 1200, 0.4, fade_out=True)
generate_wav('app/src/main/res/raw/sfx_slime.wav', 0.4, 44100, 'squish', 100, 0.6, fade_out=True)
generate_wav('app/src/main/res/raw/sfx_sand.wav', 0.2, 44100, 'noise', 0, 0.2, fade_out=True)
generate_wav('app/src/main/res/raw/sfx_spin.wav', 0.5, 44100, 'spin', 60, 0.3, fade_out=True)
generate_wav('app/src/main/res/raw/sfx_breathe.wav', 6.0, 44100, 'breathe', 0, 0.4, fade_out=False)
generate_wav('app/src/main/res/raw/sfx_rain.wav', 2.0, 44100, 'rain', 0, 0.2, fade_out=False)
generate_wav('app/src/main/res/raw/sfx_water_drop.wav', 0.15, 44100, 'pop', 1500, 0.5, fade_out=True)

