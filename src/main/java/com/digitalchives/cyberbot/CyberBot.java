package com.digitalchives.cyberbot;

import com.digitalchives.cyberbot.commands.CommandManager;
import com.digitalchives.cyberbot.listeners.EventListener;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;

//invite link https://discord.com/api/oauth2/authorize?client_id=1159518907602833450&permissions=8&scope=bot%20applications.commands
public class CyberBot {

    private final ShardManager shardManager;
    private final Dotenv config;

    public CyberBot() throws LoginException {
        config = Dotenv.configure().load();
        String token = config.get("TOKEN");

        DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(token);

        builder.setStatus(OnlineStatus.ONLINE);
        builder.setActivity(Activity.customStatus("Transcending Physicality"));
        builder.enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGES);

        //builder.setMemberCachePolicy(MemberCachePolicy.ALL);
        //builder.setChunkingFilter(ChunkingFilter.ALL);
        //builder.enableCache(CacheFlag.ROLE_TAGS);

        shardManager = builder.build();
        shardManager.addEventListener(new EventListener(), new CommandManager());
    }

    public Dotenv getConfig(){
        return config;
    }

    public ShardManager getShardManager() {
        return shardManager;
    }

    public static void main(String[] args){
        try {
            CyberBot cyberBot = new CyberBot();
        } catch (LoginException e){
            System.out.println("ERROR: Provided bot token is invalid!");
        }
    }
}
