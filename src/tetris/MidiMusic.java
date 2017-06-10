package tetris;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Track;

/**
 * A simple class for playing midi music
 * 
 * @author Gregary Pergrossi (gpergros@hawk.iit.edu)
 */
public class MidiMusic {

	private static Sequencer sequencer = null;

	private Sequence sequence;

	/**
	 * Constructs a new midi music object that can be played
	 * 
	 * @param filename
	 */
	public MidiMusic(String filename) {
		sequence = null;
		try {
			InputStream is = new FileInputStream(filename);
			sequence = MidiSystem.getSequence(is);
		} catch(MalformedURLException e) {
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		} catch(InvalidMidiDataException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sets the audio sequencer to this track
	 */
	public void setTrack() {
		try {
			sequencer.setSequence(sequence);
			if(sequencer instanceof Synthesizer) {
				Synthesizer synthesizer = (Synthesizer) sequencer;
				MidiChannel[] channels = synthesizer.getChannels();

				// gain is a value between 0 and 1 (loudest)
				double gain = 0.3D;
				for(int i = 0; i < channels.length; i++) {
					channels[i].controlChange(7, (int) (gain * 127.0));
				}
			} else {
				for(Track t : sequence.getTracks()) {
					for (int c = 0; c < 10; c++) {
						t.add(new MidiEvent(new ShortMessage(ShortMessage.CONTROL_CHANGE, c, 7, 30), 0));
					}
				}
			}
		} catch(InvalidMidiDataException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initializes the sequencer
	 */
	public static void initialize() {
		if(sequencer != null) return;
		try {
			sequencer = MidiSystem.getSequencer();
			sequencer.open();

		} catch(MidiUnavailableException e) {
			System.err.println("No MIDI Sequencer found. Music will not be played.");
		}
	}

	/**
	 * Plays the audio sequencer's current track
	 */
	public static void play() {
		sequencer.start();
	}

	/**
	 * Stops the currently playing track
	 */
	public static void stop() {
		sequencer.stop();
	}

	/**
	 * Sets the looping state of the audio sequencer
	 * 
	 * @param loop
	 */
	public static void setLoop(boolean loop) {
		if(loop) {
			sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
		} else {
			sequencer.setLoopCount(0);
		}
	}

	/**
	 * Gets the BPM of the current midi track
	 * 
	 * @return
	 */
	public static float getBPM() {
		return sequencer.getTempoInBPM();
	}

	/**
	 * Sets the audio sequencers BPM to a new value
	 * 
	 * @param BPM
	 */
	public static void setBPM(float BPM) {
		sequencer.setTempoInBPM(BPM);
	}

}
