package game.lib.ai;

import game.model.GameBoard;
import game.model.Player;

public interface AutoSearching {
	ComputerDecisionResult doSearch(GameBoard gb, Player Player1, Player player2, int maxDepth);
}
