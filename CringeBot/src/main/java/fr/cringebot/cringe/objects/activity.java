package fr.cringebot.cringe.objects;

import net.dv8tion.jda.internal.entities.ActivityImpl;

public class activity extends ActivityImpl {

    public activity(String name) {
        super(name);
    }

    public activity(String name, String url) {
        super(name, url);
    }

    public activity(String name, String url, ActivityType type) {
        super(name, url, type);
    }

    public activity(String name, String url, ActivityType type, Timestamps timestamps, Emoji emoji) {
        super(name, url, type, timestamps, emoji);
    }
}
