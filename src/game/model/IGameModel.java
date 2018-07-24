package game.model;

import game.lib.ai.ComputerDecisionResult;

public interface IGameModel extends Iterable<GameSquare> {
	Player whoWin(Player player1, Player player2);

	boolean stillHasOnBoardMilitary(Player player);

	boolean isEndGame();

	GameState getGameState();

	void getRewardInSquare(Player player, int index);

	int getMilitaryAt(int index);

	void removeMiltaryAt(int index);

	default int getAndRemoveMilitaryAt(int index) {
		int miltaries = getMilitaryAt(index);
		removeMiltaryAt(index);
		return miltaries;
	}

	GameSquare getLastestLoopedSquare();

	void addMiltaries(int index, int numberMiltary);

	Direction getLastestLoopedDirection();

	void setLoopDirection(Direction direction);

	void setIndexLoop(int index);

	GameSquare[] getGameSquares();

	boolean isValidMove(int c, int r, Player curPlayer);

	void reAssign();

	ComputerDecisionResult autoSearch();

	void setGameLevel(int inputGameLevel);

	void outMilitaries(Player curPlayer);

}
