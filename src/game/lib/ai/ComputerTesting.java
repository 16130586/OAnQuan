package game.lib.ai;

import game.model.GameBoard;
import game.model.Player;

public class ComputerTesting {
	public static void main(String[] args) {
		double timeCounter = 0;

		GameBoard b = new GameBoard();
		System.out.println(b);
//		Node.move(7, Direction.RIGHT, Player.PLAYER_1, b);
		System.out.println(b);

		Player.PLAYER_2.setComputer(true);
		AutoSearching p = new MiniMaxComputer();

		timeCounter = System.currentTimeMillis();
		ComputerDecisionResult rs = p.doSearch(b, Player.PLAYER_2, Player.PLAYER_1, 1);
		timeCounter = (1.0 * (System.currentTimeMillis() - timeCounter)) / 1000;

//		System.out.println("result by MinimaxComputer : " + rs + " that consumes " + timeCounter);

		System.out.println("******************");
		p = new AlphaBetaPruningComputer();
		timeCounter = System.currentTimeMillis();
		rs = p.doSearch(b, Player.PLAYER_2, Player.PLAYER_1, 1);
		timeCounter = (1.0 * (System.currentTimeMillis() - timeCounter)) / 1000;
		System.out.println("result by AlphaBetaPrunning : " + rs + " that consumes " + timeCounter);
	}
}
