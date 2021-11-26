package fr.cringebot.cringe.objects;

import net.dv8tion.jda.api.entities.Message;

import javax.annotation.Nullable;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class MessageConsumer implements Consumer<Message> {
    public String edit;
    public int time;
    public Consumer<Message> next;
    public MessageConsumer(String edit, int time, @Nullable Consumer<Message> next){
        this.edit = edit;
        this.time = time;
        this.next = next;
    }

    @Override
    public void accept(Message m) {
        m.editMessage(edit).delay(time, TimeUnit.MILLISECONDS).queue(next);
    }
}