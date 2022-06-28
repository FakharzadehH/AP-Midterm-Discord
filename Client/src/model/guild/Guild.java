package model.guild;

import model.User;

import java.io.Serializable;
import java.util.*;


public class Guild implements Serializable {
    private String name;
    private GuildUser owner;
    private ArrayList<GuildUser> guildUsers;
    private ArrayList<TextChannel> textChannels;
    private ArrayList<VoiceChannel> voiceChannels;

    public Guild(String name, GuildUser owner) {
        this.name = name;
        this.owner = owner;
        guildUsers = new ArrayList<>();
        textChannels = new ArrayList<>();
        voiceChannels = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    //TODO: is in guild()

    public String getOwnerName(){
        return owner.getUsername();
    }

    public void addUser(GuildUser user){
        guildUsers.add(user);
    }

    public void addTextChanel(TextChannel textChannel){
        textChannels.add(textChannel);
    }

    public void addVoiceChannel(VoiceChannel voiceChannel){
        voiceChannels.add(voiceChannel);
    }
}
