package fr.cringebot.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import fr.cringebot.music.MusicPlayer;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.util.Queue;

public class EmbedGenerator {
	public static EmbedBuilder getVolumeEmbed(Integer vol) {
		EmbedBuilder eb = new EmbedBuilder();
		if ((vol >= 100) && (vol < 199))
			eb.setColor(Color.ORANGE);
		else if (vol >= 200)
			eb.setColor(Color.RED);
		else
			eb.setColor(Color.GREEN);
		eb.setTitle("Volume");
		int i = 0;
		eb.setDescription("\n" + vol + "%\n\n");
		if (vol > 10) {
			eb.appendDescription("||");
			while (i < vol) {
				eb.appendDescription(" . ");
				i = i + 10;
			}
			eb.appendDescription("||");
		}
		while (i < 100) {
			eb.appendDescription(" I ");
			i = i + 10;
		}
		eb.appendDescription("|");
		eb.setFooter("MusicManager");
		return eb;
	}

	public static EmbedBuilder getQueueEmbed(MusicPlayer player) {
		Queue<AudioTrack> at = player.getListener().getTracks();
		EmbedBuilder eb = new EmbedBuilder().setColor(Color.cyan);
		if (!player.getListener().isLoop())
			eb.setTitle("les prochaines musique...");
		else
			eb.setTitle("la musique est en boucle");
		if (at.isEmpty() && player.getListener().getCurrent() == null)
			eb.setDescription("Offline").setColor(Color.red);
		else if (at.isEmpty())
			eb.setDescription("il n'y a pas de musique pour la suite").setColor(Color.BLUE);
		else {
			AudioTrack[] track = at.toArray(new AudioTrack[]{});
			int i = 0;
			while (i < 10 && i < track.length) {
				eb.appendDescription(track[i].getInfo().title + "\n");
				i++;
			}
		}
		return eb;
	}


}
