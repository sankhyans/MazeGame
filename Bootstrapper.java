import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;

public class Bootstrapper implements BootstrapperInterface
{
	MazeGame state;
	char playerDispId = 'B';
	
	@Override
	public synchronized Map<String, Object> bootstrap(Map<String,String> playerProperties) throws RemoteException{
		Registry registry = LocateRegistry.getRegistry(1099);
		Map<String,Object> gameProps = new HashMap<String, Object>();
		/*state = GameStateFactory.getGameState();
		PeerProperties peerProp = P2Player.getPeerProp();
		String name = playerProperties.get("name");
		String uuid = playerProperties.get("uuid");

		try {
			String primaryIP = (String) peerProp.getPrimaryProperties().get("ip");
			Nominator nominator = (Nominator) registry.lookup(uuid);
			if(state.getNumPlayers() == 1){
				System.out.println("Backup server has arrived");
				gameProps.put("isNominated", true);
				gameProps.put("isSameMachine", primaryIP.equals(playerProperties.get("ip"))?true:false);
				nominator.nominate(gameProps);
				state.setNumPlayers(state.getNumPlayers()+1);
				peerProp.getSecondaryPeerIp().put("uuid", uuid);
				peerProp.getSecondaryPeerIp().put("ip", playerProperties.get("machineIP"));
				
				if(primaryIP.equals(playerProperties.get("machineIP"))){
					peerProp.getSecondaryPeerIp().put("port", "1100"); //TODO: Looks incorrect, so changed
				}else{
					peerProp.getSecondaryPeerIp().put("port", "1099"); //TODO: Looks incorrect, so changed
				}
			}else{
				gameProps.put( "isNominated", false );
				gameProps.put("isSameMachine", primaryIP.equals(playerProperties.get("machineIP"))?true:false);
				nominator.nominate( gameProps );
				state.setNumPlayers( state.getNumPlayers() + 1 );
			}
		} catch (NotBoundException e) {
			e.printStackTrace();
		}
		gameProps.put("waitTime", peerProp.getInitTime() - System.currentTimeMillis());
		Player player = new Player( name , uuid);
		player.setPlayerDispId( playerDispId++ );
		gameProps.put("playerDispId", playerDispId);
		state.getPlayers().put( uuid, player );
		while(!state.initializePlayer(uuid));*/
		return gameProps;
	}
}