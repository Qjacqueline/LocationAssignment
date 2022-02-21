public class BasicOperation {
	protected int totalDynamicRehandleTimes;
	protected int totalStaticRehandleTimes;
	protected int arrivingContainerPermutationSize;

	public BasicOperation() {
		super();
	}

	protected void assignLocationAccordingToDPResults(int s, int t, String[] gn, EvalutionPreparation evaluationPreparation,
			String[] target, int[][] bayState, int[][] assignedYardLocation, String currentNcScTemp, int startSearchingIndexInDynamicProgrammingResults) {
				for (int j = 0; j < target.length; j++) {
					int targetMatchingIndexInDynamicProgrammingResults = -2;
					// find the target index
					for (int k = startSearchingIndexInDynamicProgrammingResults; k >= 0; k--) {
						if (currentNcScTemp.equals(evaluationPreparation.currentState[k][0])) {
							targetMatchingIndexInDynamicProgrammingResults = k;
							startSearchingIndexInDynamicProgrammingResults = k;
							break;
						}
					}
					// find the position of the target weight group
					int targetWeightIndex = -2;
					for (int k = 0; k < gn.length; k++) {
						if (gn[k].equals(target[j])) {
							targetWeightIndex = k;
							break;
						}
					}
					currentNcScTemp = evaluationPreparation.followingState[targetMatchingIndexInDynamicProgrammingResults][targetWeightIndex];
			
					// 更新bayState
					int stackNoChosenInBayState = -2;
					for (int k = 0; k < s; k++) {
						if (bayState[k][0] == evaluationPreparation.stateNumberChosen[targetMatchingIndexInDynamicProgrammingResults][targetWeightIndex])
							if (bayState[k][1] == evaluationPreparation.stateWeightChosen[targetMatchingIndexInDynamicProgrammingResults][targetWeightIndex]) {
								stackNoChosenInBayState = k;
								break;
							}
					}
					bayState[stackNoChosenInBayState][0] = bayState[stackNoChosenInBayState][0] - 1;
					if (bayState[stackNoChosenInBayState][0] == 0)
						bayState[stackNoChosenInBayState][1] = t;
					else {
						if (targetWeightIndex == gn.length - 1)
							bayState[stackNoChosenInBayState][1] = bayState[stackNoChosenInBayState][1] + 1;
						if (targetWeightIndex == gn.length - 2)
							bayState[stackNoChosenInBayState][1] = bayState[stackNoChosenInBayState][1] + 10;
						if (targetWeightIndex == gn.length - 3)
							bayState[stackNoChosenInBayState][1] = bayState[stackNoChosenInBayState][1] + 100;
						if (targetWeightIndex == gn.length - 4)
							bayState[stackNoChosenInBayState][1] = bayState[stackNoChosenInBayState][1] + 1000;
					}
			
					//assignedYardLocation有三个维度
					//第一个是箱子的权重；第二个是存储的stack No；第三个是tier No
					if (targetWeightIndex == gn.length - 1)
						assignedYardLocation[j][0] = 1;
					if (targetWeightIndex == gn.length - 2)
						assignedYardLocation[j][0] = 10;
					if (targetWeightIndex == gn.length - 3)
						assignedYardLocation[j][0] = 100;
					if (targetWeightIndex == gn.length - 4)
						assignedYardLocation[j][0] = 1000;
					assignedYardLocation[j][1] = stackNoChosenInBayState;
					assignedYardLocation[j][2] = evaluationPreparation.stateNumberChosen[targetMatchingIndexInDynamicProgrammingResults][targetWeightIndex];
				}
			}

}