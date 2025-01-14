package grab.szan.commands;

import grab.szan.Game;
import grab.szan.Player;
import grab.szan.Server;

public class JoinGameCommand implements Command {

    @Override
    public void execute(String[] args, Player player) {
        // Sprawdzenie poprawności argumentów
        if (args.length < 3) {
            player.sendMessage("display Error Game name and nickname is required to join.");
            return;
        }

        String roomName = args[1]; // Nazwa pokoju z argumentów
        Server server = Server.getInstance();

        // Sprawdzenie, czy gra o podanej nazwie istnieje
        if (!server.gameExists(roomName)) {
            player.sendMessage("display Error Game room '" + roomName + "' does not exist.");
            return;
        }

        // Pobranie gry z serwera
        Game game = server.getGame(roomName);

        for(Player p: game.getPlayers()){
            if(p.getNickname().equals(args[2])){
                player.sendMessage("display Error player with this nickname exists");
                return;
            }
        }
        // Próba dodania gracza do gry
        if (game.addPlayer(player)) {
            player.sendMessage("display Success You have joined the game '" + roomName + "'.");
            player.setActiveGame(game);
            player.setNickname(args[2]);
            
            StringBuilder commandBuilder = new StringBuilder();
       
            for(int i = 0; i < game.getPlayers().size()-1; i++){
                commandBuilder.append(game.getPlayer(i).getNickname()).append(" ");
            }

            //update list of players for each player in the game
            game.broadcast("updateList " + player.getNickname());

            //send information about accepting join request
            // acceptJoin <gameType> <room name> <player Id> <players> <nickname>
            player.sendMessage("acceptJoin " + game.getGameType() + " " + roomName + " " + player.getId() + " " + commandBuilder.toString() + player.getNickname());

        } else {
            player.sendMessage("display Error Game '" + roomName + "' is full.");
        }
    }
}
