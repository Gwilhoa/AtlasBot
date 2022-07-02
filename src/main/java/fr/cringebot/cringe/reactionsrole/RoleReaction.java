package fr.cringebot.cringe.reactionsrole;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.User;

import java.util.List;

public class RoleReaction {
    private String Name;
    private final String Id;
    private final String Emote;

    public RoleReaction(String name, String roleId, String emote) {
        this.Name = name;
        this.Id = roleId;
        this.Emote = emote;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public String getEmote() {
        return Emote;
    }

    public String getId() {
        return Id;
    }

    public int verif(Message msg)
    {
        List<User> userList = null;
        if (msg.getGuild().getRoleById(this.getId()) == null)
            return -1;
        this.setName(msg.getGuild().getRoleById(this.getId()).getName());
        for (MessageReaction mr : msg.getReactions()) {
            if (mr.getEmoji().getAsReactionCode().equals(this.getEmote())) {
                userList = mr.retrieveUsers().complete();
                for (User user : userList)
                    if ( msg.getGuild().getMemberById(user.getId()) != null && !msg.getGuild().getMemberById(user.getId()).getRoles().contains(msg.getGuild().getRoleById(this.getId())))
                        msg.getGuild().addRoleToMember(user,msg.getGuild().getRoleById(this.getId())).queue();
                    break;
            }
        }
        for (Member mem : msg.getGuild().getMembersWithRoles(msg.getGuild().getRoleById(this.getId())))
        {
            if (userList != null && !userList.contains(mem.getUser()))
                msg.getGuild().removeRoleFromMember(mem, msg.getGuild().getRoleById(this.getId())).queue();
        }
        return 0;
    }
}
