public abstract class BasicOperation {
	protected int totalDynamicRehandleTimes;
	protected int totalStaticRehandleTimes;
	protected int arrivingContainerPermutationSize;

	public BasicOperation() {
		super();
	}

	protected void assignLocationAccordingToDPResults(int s, String[] gn, EvalutionPreparation evaluationPreparation,
			String target, String[][] bayState, int[][] assignedYardLocation,
			int startSearchingIndexInDynamicProgrammingResults, String currentNcScTemp) {
		for (int j = 0; j < target.length(); j++) {
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
				if (String.valueOf(target.charAt(j)).equals(gn[k])) {
					targetWeightIndex = k;
					break;
				}
			}
			currentNcScTemp = evaluationPreparation.followingState[targetMatchingIndexInDynamicProgrammingResults][targetWeightIndex];

			int stackNoChosenInBayState = -2;
			// 找到第一个匹配的stack就放下去
			for (int k = 0; k < s; k++) {
				if (bayState[k][0]
						.equals(String
								.valueOf(evaluationPreparation.stateNumberChosen[targetMatchingIndexInDynamicProgrammingResults][targetWeightIndex])))
					if (bayState[k][1]
							.equals(evaluationPreparation.stateWeightChosen[targetMatchingIndexInDynamicProgrammingResults][targetWeightIndex])) {
						stackNoChosenInBayState = k;
						break;
					}
			}
			bayState[stackNoChosenInBayState][0] = String.valueOf(Integer
					.parseInt(bayState[stackNoChosenInBayState][0]) - 1);

			// 匹配的stack所对应的权重
			int weightIndexChosenInDynamicProgrammingResults = gn.length;
			for (int k = 0; k < gn.length; k++) {
				if ((evaluationPreparation.stateWeightChosen[targetMatchingIndexInDynamicProgrammingResults][targetWeightIndex])
						.equals(gn[k])) {
					weightIndexChosenInDynamicProgrammingResults = k;
					break;
				}
			}

			// 更新bayState
			if (bayState[stackNoChosenInBayState][0].equals("0")) {
				bayState[stackNoChosenInBayState][1] = "0";
			} else {
				// 如果达到的集装箱比匹配的stack来得重
				if (weightIndexChosenInDynamicProgrammingResults > targetWeightIndex) {
					bayState[stackNoChosenInBayState][1] = String.valueOf(target.charAt(j));
				}
			}

			//assignedYardLocation有三个维度
			//第一个是箱子的权重；第二个是存储的stack No；第三个是tier No
			if (targetWeightIndex == gn.length - 1) {
				assignedYardLocation[j][0] = 1;
			}
			if (targetWeightIndex == gn.length - 2) {
				assignedYardLocation[j][0] = 10;
			}
			if (targetWeightIndex == gn.length - 3) {
				assignedYardLocation[j][0] = 100;
			}
			if (targetWeightIndex == gn.length - 4) {
				assignedYardLocation[j][0] = 1000;
			}
			assignedYardLocation[j][1] = stackNoChosenInBayState;
			assignedYardLocation[j][2] = evaluationPreparation.stateNumberChosen[targetMatchingIndexInDynamicProgrammingResults][targetWeightIndex];
		}
	}
}
