package example;

import io.anuke.arc.*;
import io.anuke.arc.util.*;
import io.anuke.mindustry.*;
import io.anuke.mindustry.content.*;
import io.anuke.mindustry.entities.type.*;
import io.anuke.mindustry.game.EventType.*;
import io.anuke.mindustry.gen.*;
import io.anuke.mindustry.plugin.Plugin;
import io.anuke.mindustry.world.Tile;
import io.anuke.mindustry.world.blocks.power.NuclearReactor;

import java.util.ArrayList;

import static io.anuke.mindustry.Vars.playerGroup;

public class ExamplePlugin extends Plugin{
    private ArrayList<Tile> nukedata = new ArrayList<>();

    //register event handlers and create variables in the constructor
    public ExamplePlugin(){
        //listen for a block selection event
        Events.on(BuildSelectEvent.class, event -> {
            if(!event.breaking && event.builder != null && event.builder.buildRequest() != null && event.builder.buildRequest().block == Blocks.thoriumReactor && event.builder instanceof Player){
                //send a message to everyone saying that this player has begun building a reactor
                Call.sendMessage("[scarlet]ALERT![] " + ((Player)event.builder).name + " has begun building a reactor at " + event.tile.x + ", " + event.tile.y);
            }
        });

        //run when world loaded
        Events.on(WorldLoadEvent.class, event -> {
            //Log.info("Game over!");
        });

        //run when player connected
        Events.on(PlayerConnect.class, event -> {
            //Log.info(event.player.name+" player connected.");
        });

        //If the player has moved the resource directly
        Events.on(DepositEvent.class, event -> {
            // If deposit block name is thorium reactor
            if(event.tile.block() == Blocks.thoriumReactor){
                // Prevent the main thread from hanging when thread.sleep
                Thread t = new Thread(() -> {
                    try {
                        NuclearReactor.NuclearReactorEntity entity = (NuclearReactor.NuclearReactorEntity) event.tile.entity;
                        Thread.sleep(50);
                        // If thorium reactor overheat
                        if (entity.heat >= 0.01) {
                            // Will show alert message
                            Call.sendMessage("ALERT! [scarlet]" + event.player + "[white] put [pink]thorium[] in [green]Thorium Reactor[] without [sky]Cryofluid[]!\n");
                            // then, destroy overheated reactor
                            Call.onTileDestroyed(event.tile);
                        }
                    } catch (Exception ex){
                        ex.printStackTrace();
                    }
                });
                t.start();
            }
        });

        //run when player joined.
        Events.on(PlayerJoin.class, event -> {
            //Log.info(event.player.name+" player joined.");
        });

        //run when player disconnected.
        Events.on(PlayerLeave.class, event -> {
            //Log.info(event.player.name+" player disconnected.");
        });

        //run when players chat
        Events.on(PlayerChatEvent.class, event -> {
            //Log.info(event.player.name+" say: "+ event.message);
        });

        //run when block build end
        Events.on(BlockBuildEndEvent.class, event -> {
            //If block not breaking and builder isn't drone
            if (!event.breaking && event.player != null && event.player.buildRequest() != null) {
                //Log.info(event.player.name+" player place "+event.tile.entity.block.name+".");

                //if player place thorium reactor
                if(event.tile.entity.block == Blocks.thoriumReactor){
                    //add reactor information in ArrayList
                    nukedata.add(event.tile);
                }
            }
        });

        //run when build selected
        Events.on(BuildSelectEvent.class, event -> {
            //If builder isn't drone and died during construction
            if(event.builder instanceof Player && event.builder.buildRequest() != null && !event.builder.buildRequest().block.name.matches(".*build.*")) {
                //If player is breaking block
                if (event.breaking) {
                    //Log.info(((Player)event.builder).name+" Player break " +event.builder.buildRequest().block.name+".\n");
                }
            }
        });

        //run when player mech changed
        Events.on(MechChangeEvent.class, event -> {
            //Log.info(event.player.name+" player mech is now "+event.mech.name+".");
        });

        //run when anything unit destroyed
        Events.on(UnitDestroyEvent.class, event -> {
            //if player dead
            if(event.unit instanceof Player){
                Player player = (Player)event.unit;
                Log.info(player.name+" is dead.");
            }
        });

        Core.app.addListener(new ApplicationListener(){
            int delaycount = 0;
            boolean a1,a2,a3,a4 = false;

            //run every game tick. 60FPS = 0.016/s
            @Override
            public void update(){
                //run code every 20tick
                if (delaycount == 20) {
                    delaycount = 0;
                    a1 = false;
                    a2 = false;
                    a3 = false;
                    a4 = false;
                } else {
                    delaycount++;
                }

                for (int i=0;i<nukedata.size();i++) {
                    Tile target = nukedata.get(i);
                    try{
                        NuclearReactor.NuclearReactorEntity entity = (NuclearReactor.NuclearReactorEntity) target.entity;
                        if(entity.heat >= 0.2f && entity.heat <= 0.39f && !a1){
                            Call.sendMessage("[green]Thorium reactor overheat [green]"+Math.round(entity.heat*100)/100.0+"%[white] warning! X: "+target.x+", Y: "+target.y);
                            a1 = true;
                        }
                        if(entity.heat >= 0.4f && entity.heat <= 0.59f && !a2){
                            Call.sendMessage("[green]Thorium reactor overheat [yellow]"+Math.round(entity.heat*100)/100.0+"%[white] warning! X: "+target.x+", Y: "+target.y);
                            a2 = true;
                        }
                        if(entity.heat >= 0.6f && entity.heat <= 0.79f && !a3){
                            Call.sendMessage("[green]Thorium reactor overheat [yellow]"+Math.round(entity.heat*100)/100.0+"%[white] warning! X: "+target.x+", Y: "+target.y);
                            a3 = true;
                        }
                        if(entity.heat >= 0.8f && entity.heat <= 0.95f && !a4){
                            Call.sendMessage("[green]Thorium reactor overheat [scarlet]"+Math.round(entity.heat*100)/100.0+"%[white] warning! X: "+target.x+", Y: "+target.y);
                            a4 = true;
                        }
                        if(entity.heat >= 0.95f){
                            Call.onDeconstructFinish(target, Blocks.air, 0);
                            Call.sendMessage("[green]Thorium reactor overheat [scarlet]"+Math.round(entity.heat*100)/100.0+"%[white] warning! X: "+target.x+", Y: "+target.y);
                        }
                    }catch (Exception e){
                        nukedata.remove(i);
                    }
                }
            }

            //run when server shutdown
            @Override
            public void dispose(){
                Log.info("ExamplePlugin is off.");
            }
        });
    }

    //register commands that run on the server
    @Override
    public void registerServerCommands(CommandHandler handler){
        handler.register("reactors", "List all thorium reactors in the map.", args -> {
            for(int x = 0; x < Vars.world.width(); x++){
                for(int y = 0; y < Vars.world.height(); y++){
                    //loop through and log all found reactors
                    if(Vars.world.tile(x, y).block() == Blocks.thoriumReactor){
                        Log.info("Reactor at {0}, {1}", x, y);
                    }
                }
            }
        });
    }

    //register commands that player can invoke in-game
    @Override
    public void registerClientCommands(CommandHandler handler){

        //register a simple reply command
        handler.<Player>register("reply", "<text...>", "A simple ping command that echoes a player's text.", (args, player) -> {
            player.sendMessage("You said: [accent] " + args[0]);
        });

        //register a whisper command which can be used to send other players messages
        handler.<Player>register("whisper", "<player> <text...>", "Whisper text to another player.", (args, player) -> {
            //find player by name
            Player other = Vars.playerGroup.find(p -> p.name.equalsIgnoreCase(args[0]));

            //give error message with scarlet-colored text if player isn't found
            if(other == null){
                player.sendMessage("[scarlet]No player by that name found!");
                return;
            }

            //send the other player a message, using [lightgray] for gray text color and [] to reset color
            other.sendMessage("[lightgray](whisper) " + player.name + ":[] " + args[1]);
        });
    }
}
