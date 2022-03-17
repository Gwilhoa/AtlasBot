package fr.cringebot.cringe.objects;

import net.dv8tion.jda.api.entities.*;

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

    public void verif(Message msg)
    {
        List<User> userList = null;
        this.setName(msg.getGuild().getRoleById(this.getId()).getName());
        for (MessageReaction mr : msg.getReactions()) {
            if (mr.getReactionEmote().getAsReactionCode().equals(this.getEmote())) {
                userList = mr.retrieveUsers().complete();
                for (User user : userList)
                    if ( msg.getGuild().getMemberById(user.getId()) != null && !msg.getGuild().getMemberById(user.getId()).getRoles().contains(msg.getGuild().getRoleById(this.getId())))
                        msg.getGuild().addRoleToMember(user.getId(),msg.getGuild().getRoleById(this.getId())).queue();
                    break;
            }
        }
        for (Member mem : msg.getGuild().getMembersWithRoles(msg.getGuild().getRoleById(this.getId())))
        {
            if (userList != null && !userList.contains(mem.getUser()))
                msg.getGuild().removeRoleFromMember(mem, msg.getGuild().getRoleById(this.getId())).queue();
        }
    }
}
