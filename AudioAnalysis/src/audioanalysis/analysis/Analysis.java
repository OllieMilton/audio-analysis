package audioanalysis.analysis;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

public class Analysis {
	
	public static void main(String[] args) throws Exception {
		Analysis a = new Analysis();
		AudioInputStream in = AudioSystem.getAudioInputStream(new File("C:\\java\\test.wav"));
		a.analise(in);
	}

	public void analise(AudioInputStream audio) throws IOException {
		AudioFormat format = audio.getFormat();
		if (format.matches(new AudioFormat(format.getSampleRate(), 16, format.getChannels(), true, false))) {
			// sample rate * bytes per sample * no of channels
			int samplesPerSec = ((int)audio.getFormat().getSampleRate() * 2) * audio.getFormat().getChannels();
			for (int i=0; i < 100; i++) {
				byte[] buffer = new byte[samplesPerSec/10];
				audio.read(buffer);
				AudioLevel level = getLevel(getWindow(buffer));
				System.out.println("left: "+level.leftDb);
				System.out.println("right: "+level.rightDb);
			}
		}
	}
	
	class AudioLevel {
		float leftDb;
		float rightDb;
	}
	
	public AudioLevel getLevel(float[] window) {
		double lsum = 0.0f;
		double rsum = 0.0f;
		for (int i=0; i<window.length; i++) {
			if (i % 2 == 0) {
				lsum += (window[i] * window[i]);
			} else {
				rsum += (window[i] * window[i]);
			}
		}
		AudioLevel level = new AudioLevel();
		float lrms = (float) Math.sqrt(lsum / (window.length / 2));
		float rrms = (float) Math.sqrt(rsum / (window.length / 2));
		level.leftDb = (float) (20 * Math.log10(lrms));
		level.rightDb = (float) (20 * Math.log10(rrms));
		return level;
	}
	
	public float[] getWindow(byte[] buffer) {
		float[] result = new float[buffer.length/2];
		for (int i=1, j=0; i<buffer.length; i+=2, j++) {
			result[j] = (1.0f/32768.0f) * ((float)littleEndian(buffer[i-1], buffer[i])) ; 
		}
		return result;
	}
	
	private short littleEndian(byte b1, byte b2) {
		short sample = (short) ((b2 << 8) | b1);
		return sample;
	}
}
