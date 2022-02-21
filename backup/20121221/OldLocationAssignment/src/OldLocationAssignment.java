/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.ArrayList;
import java.util.Date;
import java.io.*;
import java.util.*;
import javax.swing.*;

class Dialogbox {
	void createDialogbox(String title, String content) {
		JFrame frame = new JFrame();
		frame.setVisible(true);
		frame.setAlwaysOnTop(true);
		frame.setLocationRelativeTo(null);
		JOptionPane.showMessageDialog(frame, content, title, JOptionPane.ERROR_MESSAGE);
		frame.dispose();
	}
}

class MyProperties {
	String getProperties(String properties) {
		Properties properties1 = System.getProperties();
		String path = properties1.getProperty("user.dir");
		String rootPath = path.substring(0, 1);
		InputStream inputStream;
		Properties p = new Properties();
		try {
			try {
				inputStream = new BufferedInputStream(new FileInputStream(rootPath
						+ ":\\zcr\\program\\LocationAssignment\\data_oldModel\\config.properties"));
				p.load(inputStream);
				String proPath = p.getProperty("path");
				p.setProperty("path", rootPath + proPath);
			} catch (FileNotFoundException e) {
				Dialogbox dialog = new Dialogbox();
				dialog.createDialogbox("\u8B66\u544A",
						"\u53EF\u80FD\u7684\u539F\u56E0\u662F\uFF1A1.config.properties\u6587\u4EF6\u4E0D\u5B58\u5728\uFF1B2."
								+ e.toString());
			}
		} catch (IOException e) {
			Dialogbox dialog = new Dialogbox();
			dialog.createDialogbox("\u8B66\u544A ",
					"\u53EF\u80FD\u7684\u539F\u56E0\u662F\uFF1A1.config.properties\u6587\u4EF6\u4E0D\u5B58\u5728\uFF1B2."
							+ e.toString());
		}
		return p.getProperty(properties);
	}
}

class DynamicProgramming {
	int totalStateSize;
	double finalObjectiveValue;
	String[][] followingState;
	String[] arrivingWeightGroup;
	double[] currentRehandleTimes;
	int[] tierNoChosen;
	int[] stateNumberChosen;
	String[] stateWeightChosen;

	/**
	 * 给定之前的状态，为当前状态找到最适合的位置，并计算目标值
	 * 
	 * @param t
	 * @param nc
	 * @param sc
	 * @param gn
	 * @param ncPrevious
	 * @param scPrevious
	 * @param vaPrevious
	 * @param ratio
	 * @return
	 */
	double calculateObjectiveValue(int t, String nc, String sc, String[] gn, String[] ncPrevious, String[] scPrevious,
			double[] vaPrevious, double[] ratio) {
		followingState = new String[gn.length][2];
		arrivingWeightGroup = new String[gn.length];
		currentRehandleTimes = new double[gn.length];
		tierNoChosen = new int[gn.length];
		stateNumberChosen = new int[gn.length];
		stateWeightChosen = new String[gn.length];

		double[] objectiveValue = new double[gn.length];
		for (int i = 0; i < gn.length; i++)
			objectiveValue[i] = Double.MAX_VALUE;

		// 分每一种重量权重进行讨论
		for (int i = 0; i < gn.length; i++) {
			// 每一个可放置的stack都要试一遍
			for (int j = 0; j < nc.length(); j++) {
				if (nc.charAt(j) > '0') {
					String ncTemp = nc;
					String scTemp = sc;
					int tierNoTemp = Integer.parseInt(String.valueOf(nc.charAt(j)));
					String stateWeightChosenTemp = String.valueOf(sc.charAt(j));
					int rehandleTimes = 0;
					// gn.length represents "*"
					// 此时代表空位置
					int positionOfStateWeightChosen = gn.length;
					for (int ii = 0; ii < gn.length; ii++)
						if (scTemp.charAt(j) == gn[ii].charAt(0))
							positionOfStateWeightChosen = ii;

					// 判断到达的是否更重
					boolean isArrivalHeavier = false;
					// i：代表到达的集装箱重量，positionOfStateWeightChosen:代表当前stack上的重量
					// 数值越小，权重越大
					if (positionOfStateWeightChosen > i)
						isArrivalHeavier = true;

					// the original method -- the optimistic case 1
					int rehandleTimesOfMethod1 = 0;
					// 到达的集装箱更重
					if (positionOfStateWeightChosen >= i)
						rehandleTimesOfMethod1 = 0;
					// 到达的集装箱更轻
					else
						rehandleTimesOfMethod1 = 1;

					rehandleTimes = rehandleTimesOfMethod1;

					// the revised method -- the pessimistic case 2
					// 如果某stack表示为H时，认为只有一个H，而且位于最底层，其它都是比H来的轻
					int rehandleTimesOfMethod2 = 0;
					if (gn.length == 2) {
						if (i == 1) {
							if (positionOfStateWeightChosen >= 1) {
								rehandleTimesOfMethod2 = 0;
							} else {
								rehandleTimesOfMethod2 = t - tierNoTemp;
							}
						}
						if (i == 0) {
							if (positionOfStateWeightChosen >= 1) {
								rehandleTimesOfMethod2 = 0;
							} else {
								rehandleTimesOfMethod2 = t - tierNoTemp - 1;
							}
						}
					}

					if (gn.length == 3) {
						if (i == 2) { // 到达的箱子比较轻
							if (positionOfStateWeightChosen >= 2) {
								rehandleTimesOfMethod2 = 0;
							} else {
								rehandleTimesOfMethod2 = t - tierNoTemp;
							}
						}
						if (i == 1) {
							if (positionOfStateWeightChosen >= 2) {
								rehandleTimesOfMethod2 = 0;
							}
							if (positionOfStateWeightChosen == 1) {
								rehandleTimesOfMethod2 = t - tierNoTemp - 1;
							}
							if (positionOfStateWeightChosen == 0) {
								rehandleTimesOfMethod2 = t - tierNoTemp;
							}
						}
						if (i == 0) {
							if (positionOfStateWeightChosen >= 2) {
								rehandleTimesOfMethod2 = 0;
							}
							if (positionOfStateWeightChosen == 1) {
								rehandleTimesOfMethod2 = t - tierNoTemp - 1;
							}
							if (positionOfStateWeightChosen == 0) {
								rehandleTimesOfMethod2 = t - tierNoTemp - 1;
							}
						}
					}
					rehandleTimes = rehandleTimesOfMethod2;

					// method 3 intermediate case //注意前两个都不能屏蔽，才起效果。
					int rehandleTimesOfMethod3 = 0;
					rehandleTimesOfMethod3 = Math.round((rehandleTimesOfMethod1 + rehandleTimesOfMethod2) / 2);
					rehandleTimes = rehandleTimesOfMethod3;

					// adjust nc
					String firstPart = "";
					String thirdPart = "";
					String secondPart = "";
					if (j > 0)
						firstPart = ncTemp.substring(0, j - 1 - 0 + 1);
					if (j < nc.length() - 1)
						thirdPart = ncTemp.substring(j + 1, ncTemp.length() - 1 - (j + 1) + 1 + (j + 1));
					int leftEmptyNumber = Integer.parseInt(String.valueOf(ncTemp.charAt(j))) - 1;
					secondPart = String.valueOf(leftEmptyNumber);
					ncTemp = firstPart + secondPart + thirdPart;

					// adjust sc
					String firstPart1 = "";
					String thirdPart1 = "";
					String secondPart1 = "";
					if (leftEmptyNumber == 0) {
						if (j > 0)
							firstPart1 = scTemp.substring(0, j - 1 - 0 + 1);
						if (j < sc.length() - 1)
							thirdPart1 = scTemp.substring(j + 1, scTemp.length() - 1 - (j + 1) + 1 + (j + 1));
						secondPart1 = "0";
						scTemp = firstPart1 + secondPart1 + thirdPart1;
					} else {
						if (isArrivalHeavier) { // 代表到达的集装箱更重，此时要更新重量权重
							if (j > 0)
								firstPart1 = scTemp.substring(0, j - 1 - 0 + 1);
							if (j < sc.length() - 1)
								thirdPart1 = scTemp.substring(j + 1, scTemp.length() - 1 - (j + 1) + 1 + (j + 1));
							secondPart1 = gn[i];
							scTemp = firstPart1 + secondPart1 + thirdPart1;
						}
					}
					// re-sequence nc and sc
					for (int k = 0; k < ncTemp.length() - 1; k++) {
						int first = Integer.parseInt(String.valueOf(ncTemp.charAt(k)));
						int second = Integer.parseInt(String.valueOf(ncTemp.charAt(k + 1)));
						String firstPart2 = "";
						String fourthPart2 = "";
						String secondPart2 = "";
						String thirdPart2 = "";
						String firstPart3 = "";
						String fourthPart3 = "";
						String secondPart3 = "";
						String thirdPart3 = "";
						if (first < second) {
							if (k > 0)
								firstPart2 = ncTemp.substring(0, k - 1 - 0 + 1);
							if (k < nc.length() - 2)
								fourthPart2 = ncTemp.substring(k + 2, ncTemp.length() - 1 - (k + 2) + 1 + (k + 2));
							secondPart2 = String.valueOf(second);
							thirdPart2 = String.valueOf(first);
							ncTemp = firstPart2 + secondPart2 + thirdPart2 + fourthPart2;

							if (k > 0)
								firstPart3 = scTemp.substring(0, k - 1 - 0 + 1);
							if (k < sc.length() - 2)
								fourthPart3 = scTemp.substring(k + 2, scTemp.length() - 1 - (k + 2) + 1 + (k + 2));
							secondPart3 = String.valueOf(scTemp.charAt(k + 1));
							thirdPart3 = String.valueOf(scTemp.charAt(k));
							scTemp = firstPart3 + secondPart3 + thirdPart3 + fourthPart3;
						}
					}
					// re-sequence sc
					boolean isRequenceNeeded = true;
					while (isRequenceNeeded) {
						isRequenceNeeded = false;
						for (int k = 0; k < ncTemp.length() - 1; k++) {
							int first = Integer.parseInt(String.valueOf(ncTemp.charAt(k)));
							int second = Integer.parseInt(String.valueOf(ncTemp.charAt(k + 1)));
							int first1 = -1;
							int second1 = -1;
							for (int h = 0; h < gn.length; h++) {
								if (gn[h].equals(String.valueOf(scTemp.charAt(k))))
									first1 = h; // first1 和 second1 谁大，就意味着谁轻
								if (gn[h].equals(String.valueOf(scTemp.charAt(k + 1))))
									second1 = h;
							}

							String firstPart3 = "";
							String fourthPart3 = "";
							String secondPart3 = "";
							String thirdPart3 = "";
							if (first == second && first1 > second1) {
								isRequenceNeeded = true;
								if (k > 0)
									firstPart3 = scTemp.substring(0, k - 1 - 0 + 1);
								if (k < sc.length() - 2)
									fourthPart3 = scTemp.substring(k + 2, scTemp.length() - 1 - (k + 2) + 1 + (k + 2));
								secondPart3 = String.valueOf(scTemp.charAt(k + 1));
								thirdPart3 = String.valueOf(scTemp.charAt(k));
								scTemp = firstPart3 + secondPart3 + thirdPart3 + fourthPart3;
							}
						}
					}

					// match the states;
					double objectiveValueOfFollowingState = 0.0;
					for (int k = 0; k < ncPrevious.length; k++) {
						if (ncTemp.equals(ncPrevious[k]) && scTemp.equals(scPrevious[k])) {
							objectiveValueOfFollowingState = vaPrevious[k];
							break;
						}
					}

					// calculate objective value
					double objectiveValueTemp = objectiveValueOfFollowingState + rehandleTimes;
					if (objectiveValue[i] > objectiveValueTemp) {
						objectiveValue[i] = objectiveValueTemp;
						followingState[i][0] = ncTemp;
						followingState[i][1] = scTemp;
						arrivingWeightGroup[i] = gn[i];
						currentRehandleTimes[i] = rehandleTimes;
						// tier NO是从上到下有1增长到t
						tierNoChosen[i] = tierNoTemp;
						stateNumberChosen[i] = tierNoTemp;
						stateWeightChosen[i] = stateWeightChosenTemp;
					}
				}
			}
		}
		double totalObjectiveValue = 0.0;
		for (int i = 0; i < objectiveValue.length; i++)
			totalObjectiveValue = totalObjectiveValue + objectiveValue[i] * ratio[i];
		return totalObjectiveValue;
	}

	int calculateTotalWeightPermutationSize(int s, int t, int n, String[] gn, String[] result) {
		int totalNumber = 0;
		for (int i = 0; i < result.length; i++) {
			totalNumber = totalNumber + calculateWeightPermutationSize(result[i], t, gn);
		}
		return totalNumber;
	}

	/**
	 * 列出空箱数量等于n的所有空箱排列组合 第一步，先计算总的维度 第二步，给每一个单元赋值
	 * 
	 * @param s
	 * @param t
	 * @param n
	 * @return
	 */
	String[] calculateEmptyPermutation(int s, int t, int n) {
		int total_number = 0;
		if (s == 2) {
			for (int i = t; i >= 0; i--)
				for (int ii = i; ii >= 0; ii--) {
					if (i + ii == n)
						total_number++;
				}
		}
		if (s == 3) {
			for (int i = t; i >= 0; i--)
				for (int ii = i; ii >= 0; ii--)
					for (int j = ii; j >= 0; j--) {
						if (i + ii + j == n)
							total_number++;
					}
		}
		if (s == 4) {
			for (int i = t; i >= 0; i--)
				for (int ii = i; ii >= 0; ii--)
					for (int j = ii; j >= 0; j--)
						for (int jj = j; jj >= 0; jj--) {
							if (i + ii + j + jj == n)
								total_number++;
						}
		}
		if (s == 5) {
			for (int i = t; i >= 0; i--)
				for (int ii = i; ii >= 0; ii--)
					for (int j = ii; j >= 0; j--)
						for (int jj = j; jj >= 0; jj--)
							for (int k = jj; k >= 0; k--) {
								if (i + ii + j + jj + k == n)
									total_number++;
							}
		}
		if (s == 6) {
			for (int i = t; i >= 0; i--)
				for (int ii = i; ii >= 0; ii--)
					for (int j = ii; j >= 0; j--)
						for (int jj = j; jj >= 0; jj--)
							for (int k = jj; k >= 0; k--)
								for (int kk = k; kk >= 0; kk--) {
									if (i + ii + j + jj + k + kk == n)
										total_number++;
								}
		}
		if (s == 7) {
			for (int i = t; i >= 0; i--)
				for (int ii = i; ii >= 0; ii--)
					for (int j = ii; j >= 0; j--)
						for (int jj = j; jj >= 0; jj--)
							for (int k = jj; k >= 0; k--)
								for (int kk = k; kk >= 0; kk--)
									for (int h = kk; h >= 0; h--) {
										if (i + ii + j + jj + k + kk + h == n)
											total_number++;
									}
		}
		if (s == 8) {
			for (int i = t; i >= 0; i--)
				for (int ii = i; ii >= 0; ii--)
					for (int j = ii; j >= 0; j--)
						for (int jj = j; jj >= 0; jj--)
							for (int k = jj; k >= 0; k--)
								for (int kk = k; kk >= 0; kk--)
									for (int h = kk; h >= 0; h--)
										for (int hh = h; hh >= 0; hh--) {
											if (i + ii + j + jj + k + kk + h + hh == n)
												total_number++;
										}
		}

		String[] result = new String[total_number];
		int index = 0;

		if (s == 2) {
			for (int i = t; i >= 0; i--)
				for (int ii = i; ii >= 0; ii--)
					if (i + ii == n) {
						result[index] = String.valueOf(i) + String.valueOf(ii);
						index++;
					}
		}
		if (s == 3) {
			for (int i = t; i >= 0; i--)
				for (int ii = i; ii >= 0; ii--)
					for (int j = ii; j >= 0; j--) {
						if (i + ii + j == n) {
							result[index] = String.valueOf(i) + String.valueOf(ii) + String.valueOf(j);
							index++;
						}
					}
		}
		if (s == 4) {
			for (int i = t; i >= 0; i--)
				for (int ii = i; ii >= 0; ii--)
					for (int j = ii; j >= 0; j--)
						for (int jj = j; jj >= 0; jj--) {
							if (i + ii + j + jj == n) {
								result[index] = String.valueOf(i) + String.valueOf(ii) + String.valueOf(j)
										+ String.valueOf(jj);
								index++;
							}
						}
		}
		if (s == 5) {
			for (int i = t; i >= 0; i--)
				for (int ii = i; ii >= 0; ii--)
					for (int j = ii; j >= 0; j--)
						for (int jj = j; jj >= 0; jj--)
							for (int k = jj; k >= 0; k--) {
								if (i + ii + j + jj + k == n) {
									result[index] = String.valueOf(i) + String.valueOf(ii) + String.valueOf(j)
											+ String.valueOf(jj) + String.valueOf(k);
									index++;
								}
							}
		}
		if (s == 6) {
			for (int i = t; i >= 0; i--)
				for (int ii = i; ii >= 0; ii--)
					for (int j = ii; j >= 0; j--)
						for (int jj = j; jj >= 0; jj--)
							for (int k = jj; k >= 0; k--)
								for (int kk = k; kk >= 0; kk--) {
									if (i + ii + j + jj + k + kk == n) {
										result[index] = String.valueOf(i) + String.valueOf(ii) + String.valueOf(j)
												+ String.valueOf(jj) + String.valueOf(k) + String.valueOf(kk);
										index++;
									}
								}
		}
		if (s == 7) {
			for (int i = t; i >= 0; i--)
				for (int ii = i; ii >= 0; ii--)
					for (int j = ii; j >= 0; j--)
						for (int jj = j; jj >= 0; jj--)
							for (int k = jj; k >= 0; k--)
								for (int kk = k; kk >= 0; kk--)
									for (int h = kk; h >= 0; h--) {
										if (i + ii + j + jj + k + kk + h == n) {
											result[index] = String.valueOf(i) + String.valueOf(ii) + String.valueOf(j)
													+ String.valueOf(jj) + String.valueOf(k) + String.valueOf(kk)
													+ String.valueOf(h);
											index++;
										}
									}
		}
		if (s == 8) {
			for (int i = t; i >= 0; i--)
				for (int ii = i; ii >= 0; ii--)
					for (int j = ii; j >= 0; j--)
						for (int jj = j; jj >= 0; jj--)
							for (int k = jj; k >= 0; k--)
								for (int kk = k; kk >= 0; kk--)
									for (int h = kk; h >= 0; h--)
										for (int hh = h; hh >= 0; hh--) {
											if (i + ii + j + jj + k + kk + h + hh == n) {
												result[index] = String.valueOf(i) + String.valueOf(ii)
														+ String.valueOf(j) + String.valueOf(jj) + String.valueOf(k)
														+ String.valueOf(kk) + String.valueOf(h) + String.valueOf(hh);
												index++;
											}
										}
		}

		for (int i = 0; i < total_number; i++)
			System.out.println(result[i]);
		return result;
	}

	/**
	 * 计算给定一个空想排列，计算对应的重量排列个数 当该垛一个集装箱都没有，此时的重量状态为*
	 * 当该垛有集装箱但至少有一个位置，此时的重量状态size为gn的size 重量状态也按照由重往轻进行排序
	 * 
	 * @param nc
	 * @param t
	 * @param gn
	 * @return
	 */
	int calculateWeightPermutationSize(String nc, int t, String[] gn) {
		int total_column = 0; // calculate the number of stacks on which there
								// are empty slots
		for (int i = 0; i < nc.length(); i++) {
			// System.out.println(nc.charAt(i));
			if (nc.charAt(i) > '0')
				total_column++;
		}
		int index = 0;

		if (total_column == 1) {
			for (int i = 0; i < gn.length; i++) {
				if (nc.charAt(0) == String.valueOf(t).charAt(0)) // 空垛
					i = gn.length;
				index++;
			}
		}

		if (total_column == 2) {
			for (int i = 0; i < gn.length; i++) {
				int start_point2 = 0;
				if (nc.charAt(0) == nc.charAt(1))
					start_point2 = i;
				else
					start_point2 = 0;
				for (int ii = start_point2; ii < gn.length; ii++) {
					if (nc.charAt(0) == String.valueOf(t).charAt(0))
						i = gn.length;
					if (nc.charAt(1) == String.valueOf(t).charAt(0)) {
						ii = gn.length;
					}

					index++;
				}
			}
		}

		if (total_column == 3) {
			// int index=0;
			for (int i = 0; i < gn.length; i++) {
				int start_point2 = 0;
				if (nc.charAt(0) == nc.charAt(1))
					start_point2 = i;
				else
					start_point2 = 0;
				for (int ii = start_point2; ii < gn.length; ii++) {
					int start_point3 = 0;
					if (nc.charAt(1) == nc.charAt(2))
						start_point3 = ii;
					else
						start_point3 = 0;
					for (int j = start_point3; j < gn.length; j++) {
						if (nc.charAt(0) == String.valueOf(t).charAt(0)) {
							i = gn.length;
						}
						if (nc.charAt(1) == String.valueOf(t).charAt(0)) {
							ii = gn.length;
						}
						if (nc.charAt(2) == String.valueOf(t).charAt(0)) {
							j = gn.length;
						}

						index++;
					}
				}
			}
		}

		if (total_column == 4) {
			// int index=0;
			for (int i = 0; i < gn.length; i++) {
				int start_point2 = 0;
				if (nc.charAt(0) == nc.charAt(1))
					start_point2 = i;
				else
					start_point2 = 0;
				for (int ii = start_point2; ii < gn.length; ii++) {
					int start_point3 = 0;
					if (nc.charAt(1) == nc.charAt(2))
						start_point3 = ii;
					else
						start_point3 = 0;
					for (int j = start_point3; j < gn.length; j++) {
						int start_point4 = 0;
						if (nc.charAt(2) == nc.charAt(3))
							start_point4 = j;
						else
							start_point4 = 0;
						for (int jj = start_point4; jj < gn.length; jj++) {
							if (nc.charAt(0) == String.valueOf(t).charAt(0)) {
								i = gn.length;
							}
							if (nc.charAt(1) == String.valueOf(t).charAt(0)) {
								ii = gn.length;
							}
							if (nc.charAt(2) == String.valueOf(t).charAt(0)) {
								j = gn.length;
							}
							if (nc.charAt(3) == String.valueOf(t).charAt(0)) {
								jj = gn.length;
							}

							index++;
						}
					}
				}
			}
		}

		if (total_column == 5) {
			// int index=0;
			for (int i = 0; i < gn.length; i++) {
				int start_point2 = 0;
				if (nc.charAt(0) == nc.charAt(1))
					start_point2 = i;
				else
					start_point2 = 0;
				for (int ii = start_point2; ii < gn.length; ii++) {
					int start_point3 = 0;
					if (nc.charAt(1) == nc.charAt(2))
						start_point3 = ii;
					else
						start_point3 = 0;
					for (int j = start_point3; j < gn.length; j++) {
						int start_point4 = 0;
						if (nc.charAt(2) == nc.charAt(3))
							start_point4 = j;
						else
							start_point4 = 0;
						for (int jj = start_point4; jj < gn.length; jj++) {
							int start_point5 = 0;
							if (nc.charAt(3) == nc.charAt(4))
								start_point5 = jj;
							else
								start_point5 = 0;
							for (int k = start_point5; k < gn.length; k++) {
								if (nc.charAt(0) == String.valueOf(t).charAt(0)) {
									i = gn.length;
								}
								if (nc.charAt(1) == String.valueOf(t).charAt(0)) {
									ii = gn.length;
								}
								if (nc.charAt(2) == String.valueOf(t).charAt(0)) {
									j = gn.length;
								}
								if (nc.charAt(3) == String.valueOf(t).charAt(0)) {
									jj = gn.length;
								}
								if (nc.charAt(4) == String.valueOf(t).charAt(0)) {
									k = gn.length;
								}

								index++;
							}
						}
					}
				}
			}
		}

		if (total_column == 6) {
			// int index=0;
			for (int i = 0; i < gn.length; i++) {
				int start_point2 = 0;
				if (nc.charAt(0) == nc.charAt(1))
					start_point2 = i;
				else
					start_point2 = 0;
				for (int ii = start_point2; ii < gn.length; ii++) {
					int start_point3 = 0;
					if (nc.charAt(1) == nc.charAt(2))
						start_point3 = ii;
					else
						start_point3 = 0;
					for (int j = start_point3; j < gn.length; j++) {
						int start_point4 = 0;
						if (nc.charAt(2) == nc.charAt(3))
							start_point4 = j;
						else
							start_point4 = 0;
						for (int jj = start_point4; jj < gn.length; jj++) {
							int start_point5 = 0;
							if (nc.charAt(3) == nc.charAt(4))
								start_point5 = jj;
							else
								start_point5 = 0;
							for (int k = start_point5; k < gn.length; k++) {
								int start_point6 = 0;
								if (nc.charAt(4) == nc.charAt(5))
									start_point6 = k;
								else
									start_point6 = 0;
								for (int kk = start_point6; kk < gn.length; kk++) {
									if (nc.charAt(0) == String.valueOf(t).charAt(0)) {
										i = gn.length;
									}
									if (nc.charAt(1) == String.valueOf(t).charAt(0)) {
										ii = gn.length;
									}
									if (nc.charAt(2) == String.valueOf(t).charAt(0)) {
										j = gn.length;
									}
									if (nc.charAt(3) == String.valueOf(t).charAt(0)) {
										jj = gn.length;
									}
									if (nc.charAt(4) == String.valueOf(t).charAt(0)) {
										k = gn.length;
									}
									if (nc.charAt(5) == String.valueOf(t).charAt(0)) {
										kk = gn.length;
									}

									index++;
								}
							}
						}
					}
				}
			}
		}

		if (total_column == 7) {
			// int index=0;
			for (int i = 0; i < gn.length; i++) {
				int start_point2 = 0;
				if (nc.charAt(0) == nc.charAt(1))
					start_point2 = i;
				else
					start_point2 = 0;
				for (int ii = start_point2; ii < gn.length; ii++) {
					int start_point3 = 0;
					if (nc.charAt(1) == nc.charAt(2))
						start_point3 = ii;
					else
						start_point3 = 0;
					for (int j = start_point3; j < gn.length; j++) {
						int start_point4 = 0;
						if (nc.charAt(2) == nc.charAt(3))
							start_point4 = j;
						else
							start_point4 = 0;
						for (int jj = start_point4; jj < gn.length; jj++) {
							int start_point5 = 0;
							if (nc.charAt(3) == nc.charAt(4))
								start_point5 = jj;
							else
								start_point5 = 0;
							for (int k = start_point5; k < gn.length; k++) {
								int start_point6 = 0;
								if (nc.charAt(4) == nc.charAt(5))
									start_point6 = k;
								else
									start_point6 = 0;
								for (int kk = start_point6; kk < gn.length; kk++) {
									int start_point7 = 0;
									if (nc.charAt(5) == nc.charAt(6))
										start_point7 = kk;
									else
										start_point7 = 0;
									for (int h = start_point7; h < gn.length; h++) {
										if (nc.charAt(0) == String.valueOf(t).charAt(0)) {
											i = gn.length;
										}
										if (nc.charAt(1) == String.valueOf(t).charAt(0)) {
											ii = gn.length;
										}
										if (nc.charAt(2) == String.valueOf(t).charAt(0)) {
											j = gn.length;
										}
										if (nc.charAt(3) == String.valueOf(t).charAt(0)) {
											jj = gn.length;
										}
										if (nc.charAt(4) == String.valueOf(t).charAt(0)) {
											k = gn.length;
										}
										if (nc.charAt(5) == String.valueOf(t).charAt(0)) {
											kk = gn.length;
										}
										if (nc.charAt(6) == String.valueOf(t).charAt(0)) {
											h = gn.length;
										}

										index++;
									}
								}
							}
						}
					}
				}
			}
		}

		if (total_column == 8) {
			// int index=0;
			for (int i = 0; i < gn.length; i++) {
				int start_point2 = 0;
				if (nc.charAt(0) == nc.charAt(1))
					start_point2 = i;
				else
					start_point2 = 0;
				for (int ii = start_point2; ii < gn.length; ii++) {
					int start_point3 = 0;
					if (nc.charAt(1) == nc.charAt(2))
						start_point3 = ii;
					else
						start_point3 = 0;
					for (int j = start_point3; j < gn.length; j++) {
						int start_point4 = 0;
						if (nc.charAt(2) == nc.charAt(3))
							start_point4 = j;
						else
							start_point4 = 0;
						for (int jj = start_point4; jj < gn.length; jj++) {
							int start_point5 = 0;
							if (nc.charAt(3) == nc.charAt(4))
								start_point5 = jj;
							else
								start_point5 = 0;
							for (int k = start_point5; k < gn.length; k++) {
								int start_point6 = 0;
								if (nc.charAt(4) == nc.charAt(5))
									start_point6 = k;
								else
									start_point6 = 0;
								for (int kk = start_point6; kk < gn.length; kk++) {
									int start_point7 = 0;
									if (nc.charAt(5) == nc.charAt(6))
										start_point7 = kk;
									else
										start_point7 = 0;
									for (int h = start_point7; h < gn.length; h++) {
										int start_point8 = 0;
										if (nc.charAt(6) == nc.charAt(7))
											start_point8 = h;
										else
											start_point8 = 0;
										for (int hh = start_point8; hh < gn.length; hh++) {
											if (nc.charAt(0) == String.valueOf(t).charAt(0)) {
												i = gn.length;
											}
											if (nc.charAt(1) == String.valueOf(t).charAt(0)) {
												ii = gn.length;
											}
											if (nc.charAt(2) == String.valueOf(t).charAt(0)) {
												j = gn.length;
											}
											if (nc.charAt(3) == String.valueOf(t).charAt(0)) {
												jj = gn.length;
											}
											if (nc.charAt(4) == String.valueOf(t).charAt(0)) {
												k = gn.length;
											}
											if (nc.charAt(5) == String.valueOf(t).charAt(0)) {
												kk = gn.length;
											}
											if (nc.charAt(6) == String.valueOf(t).charAt(0)) {
												h = gn.length;
											}
											if (nc.charAt(7) == String.valueOf(t).charAt(0)) {
												hh = gn.length;
											}

											index++;
										}
									}
								}
							}
						}
					}
				}
			}
		}

		return index;

	}

	String[] calculateWeightPermutation(String nc, int t, String[] gn) {

		String[] ss = new String[calculateWeightPermutationSize(nc, t, gn)];
		int total_column = 0; // calculate the number of stacks on which there
								// are empty slots
		for (int i = 0; i < nc.length(); i++) {
			// System.out.println(nc.charAt(i));
			if (nc.charAt(i) > '0')
				total_column++;
		}
		// System.out.println(total_column);
		String tail = ""; // the states on the full stack are "0000"
		for (int j = 0; j < nc.length() - total_column; j++)
			tail = tail + "0";
		// System.out.println(tail);

		if (total_column == 1) {
			int index = 0;
			for (int i = 0; i < gn.length; i++) {
				String first = "";
				if (nc.charAt(0) == String.valueOf(t).charAt(0)) {
					first = "*";
					i = gn.length;
				} else
					first = gn[i];
				ss[index] = first + tail;
				index++;
			}
		}

		if (total_column == 2) {
			int index = 0;
			for (int i = 0; i < gn.length; i++) {
				int start_point2 = 0;
				if (nc.charAt(0) == nc.charAt(1))
					start_point2 = i;
				else
					start_point2 = 0;
				for (int ii = start_point2; ii < gn.length; ii++) {
					String first = "";
					if (nc.charAt(0) == String.valueOf(t).charAt(0)) {
						first = "*";
						i = gn.length;
					} else
						first = gn[i];
					String second = "";
					if (nc.charAt(1) == String.valueOf(t).charAt(0)) {
						second = "*";
						ii = gn.length;
					} else
						second = gn[ii];

					ss[index] = first + second + tail;
					index++;
				}
			}
		}

		if (total_column == 3) {
			int index = 0;
			for (int i = 0; i < gn.length; i++) {
				int start_point2 = 0;
				if (nc.charAt(0) == nc.charAt(1))
					start_point2 = i;
				else
					start_point2 = 0;
				for (int ii = start_point2; ii < gn.length; ii++) {
					int start_point3 = 0;
					if (nc.charAt(1) == nc.charAt(2))
						start_point3 = ii;
					else
						start_point3 = 0;
					for (int j = start_point3; j < gn.length; j++) {
						String first = "";
						if (nc.charAt(0) == String.valueOf(t).charAt(0)) {
							first = "*";
							i = gn.length;
						} else
							first = gn[i];
						String second = "";
						if (nc.charAt(1) == String.valueOf(t).charAt(0)) {
							second = "*";
							ii = gn.length;
						} else
							second = gn[ii];
						String third = "";
						if (nc.charAt(2) == String.valueOf(t).charAt(0)) {
							third = "*";
							j = gn.length;
						} else
							third = gn[j];

						ss[index] = first + second + third + tail;
						index++;
					}
				}
			}
		}

		if (total_column == 4) {
			int index = 0;
			for (int i = 0; i < gn.length; i++) {
				int start_point2 = 0;
				if (nc.charAt(0) == nc.charAt(1))
					start_point2 = i;
				else
					start_point2 = 0;
				for (int ii = start_point2; ii < gn.length; ii++) {
					int start_point3 = 0;
					if (nc.charAt(1) == nc.charAt(2))
						start_point3 = ii;
					else
						start_point3 = 0;
					for (int j = start_point3; j < gn.length; j++) {
						int start_point4 = 0;
						if (nc.charAt(2) == nc.charAt(3))
							start_point4 = j;
						else
							start_point4 = 0;
						for (int jj = start_point4; jj < gn.length; jj++) {
							String first = "";
							if (nc.charAt(0) == String.valueOf(t).charAt(0)) {
								first = "*";
								i = gn.length;
							} else
								first = gn[i];
							String second = "";
							if (nc.charAt(1) == String.valueOf(t).charAt(0)) {
								second = "*";
								ii = gn.length;
							} else
								second = gn[ii];
							String third = "";
							if (nc.charAt(2) == String.valueOf(t).charAt(0)) {
								third = "*";
								j = gn.length;
							} else
								third = gn[j];
							String fourth = "";
							if (nc.charAt(3) == String.valueOf(t).charAt(0)) {
								fourth = "*";
								jj = gn.length;
							} else
								fourth = gn[jj];

							ss[index] = first + second + third + fourth + tail;
							index++;
						}
					}
				}
			}
		}

		if (total_column == 5) {
			int index = 0;
			for (int i = 0; i < gn.length; i++) {
				int start_point2 = 0;
				if (nc.charAt(0) == nc.charAt(1))
					start_point2 = i;
				else
					start_point2 = 0;
				for (int ii = start_point2; ii < gn.length; ii++) {
					int start_point3 = 0;
					if (nc.charAt(1) == nc.charAt(2))
						start_point3 = ii;
					else
						start_point3 = 0;
					for (int j = start_point3; j < gn.length; j++) {
						int start_point4 = 0;
						if (nc.charAt(2) == nc.charAt(3))
							start_point4 = j;
						else
							start_point4 = 0;
						for (int jj = start_point4; jj < gn.length; jj++) {
							int start_point5 = 0;
							if (nc.charAt(3) == nc.charAt(4))
								start_point5 = jj;
							else
								start_point5 = 0;
							for (int k = start_point5; k < gn.length; k++) {
								String first = "";
								if (nc.charAt(0) == String.valueOf(t).charAt(0)) {
									first = "*";
									i = gn.length;
								} else
									first = gn[i];
								String second = "";
								if (nc.charAt(1) == String.valueOf(t).charAt(0)) {
									second = "*";
									ii = gn.length;
								} else
									second = gn[ii];
								String third = "";
								if (nc.charAt(2) == String.valueOf(t).charAt(0)) {
									third = "*";
									j = gn.length;
								} else
									third = gn[j];
								String fourth = "";
								if (nc.charAt(3) == String.valueOf(t).charAt(0)) {
									fourth = "*";
									jj = gn.length;
								} else
									fourth = gn[jj];
								String fifth = "";
								if (nc.charAt(4) == String.valueOf(t).charAt(0)) {
									fifth = "*";
									k = gn.length;
								} else
									fifth = gn[k];

								ss[index] = first + second + third + fourth + fifth + tail;
								index++;
							}
						}
					}
				}
			}
		}

		if (total_column == 6) {
			int index = 0;
			for (int i = 0; i < gn.length; i++) {
				int start_point2 = 0;
				if (nc.charAt(0) == nc.charAt(1))
					start_point2 = i;
				else
					start_point2 = 0;
				for (int ii = start_point2; ii < gn.length; ii++) {
					int start_point3 = 0;
					if (nc.charAt(1) == nc.charAt(2))
						start_point3 = ii;
					else
						start_point3 = 0;
					for (int j = start_point3; j < gn.length; j++) {
						int start_point4 = 0;
						if (nc.charAt(2) == nc.charAt(3))
							start_point4 = j;
						else
							start_point4 = 0;
						for (int jj = start_point4; jj < gn.length; jj++) {
							int start_point5 = 0;
							if (nc.charAt(3) == nc.charAt(4))
								start_point5 = jj;
							else
								start_point5 = 0;
							for (int k = start_point5; k < gn.length; k++) {
								int start_point6 = 0;
								if (nc.charAt(4) == nc.charAt(5))
									start_point6 = k;
								else
									start_point6 = 0;
								for (int kk = start_point6; kk < gn.length; kk++) {
									String first = "";
									if (nc.charAt(0) == String.valueOf(t).charAt(0)) {
										first = "*";
										i = gn.length;
									} else
										first = gn[i];
									String second = "";
									if (nc.charAt(1) == String.valueOf(t).charAt(0)) {
										second = "*";
										ii = gn.length;
									} else
										second = gn[ii];
									String third = "";
									if (nc.charAt(2) == String.valueOf(t).charAt(0)) {
										third = "*";
										j = gn.length;
									} else
										third = gn[j];
									String fourth = "";
									if (nc.charAt(3) == String.valueOf(t).charAt(0)) {
										fourth = "*";
										jj = gn.length;
									} else
										fourth = gn[jj];
									String fifth = "";
									if (nc.charAt(4) == String.valueOf(t).charAt(0)) {
										fifth = "*";
										k = gn.length;
									} else
										fifth = gn[k];
									String sixth = "";
									if (nc.charAt(5) == String.valueOf(t).charAt(0)) {
										sixth = "*";
										kk = gn.length;
									} else
										sixth = gn[kk];

									ss[index] = first + second + third + fourth + fifth + sixth + tail;
									index++;
								}
							}
						}
					}
				}
			}
		}

		if (total_column == 7) {
			int index = 0;
			for (int i = 0; i < gn.length; i++) {
				int start_point2 = 0;
				if (nc.charAt(0) == nc.charAt(1))
					start_point2 = i;
				else
					start_point2 = 0;
				for (int ii = start_point2; ii < gn.length; ii++) {
					int start_point3 = 0;
					if (nc.charAt(1) == nc.charAt(2))
						start_point3 = ii;
					else
						start_point3 = 0;
					for (int j = start_point3; j < gn.length; j++) {
						int start_point4 = 0;
						if (nc.charAt(2) == nc.charAt(3))
							start_point4 = j;
						else
							start_point4 = 0;
						for (int jj = start_point4; jj < gn.length; jj++) {
							int start_point5 = 0;
							if (nc.charAt(3) == nc.charAt(4))
								start_point5 = jj;
							else
								start_point5 = 0;
							for (int k = start_point5; k < gn.length; k++) {
								int start_point6 = 0;
								if (nc.charAt(4) == nc.charAt(5))
									start_point6 = k;
								else
									start_point6 = 0;
								for (int kk = start_point6; kk < gn.length; kk++) {
									int start_point7 = 0;
									if (nc.charAt(5) == nc.charAt(6))
										start_point7 = kk;
									else
										start_point7 = 0;
									for (int h = start_point7; h < gn.length; h++) {
										String first = "";
										if (nc.charAt(0) == String.valueOf(t).charAt(0)) {
											first = "*";
											i = gn.length;
										} else
											first = gn[i];
										String second = "";
										if (nc.charAt(1) == String.valueOf(t).charAt(0)) {
											second = "*";
											ii = gn.length;
										} else
											second = gn[ii];
										String third = "";
										if (nc.charAt(2) == String.valueOf(t).charAt(0)) {
											third = "*";
											j = gn.length;
										} else
											third = gn[j];
										String fourth = "";
										if (nc.charAt(3) == String.valueOf(t).charAt(0)) {
											fourth = "*";
											jj = gn.length;
										} else
											fourth = gn[jj];
										String fifth = "";
										if (nc.charAt(4) == String.valueOf(t).charAt(0)) {
											fifth = "*";
											k = gn.length;
										} else
											fifth = gn[k];
										String sixth = "";
										if (nc.charAt(5) == String.valueOf(t).charAt(0)) {
											sixth = "*";
											kk = gn.length;
										} else
											sixth = gn[kk];
										String seventh = "";
										if (nc.charAt(6) == String.valueOf(t).charAt(0)) {
											seventh = "*";
											h = gn.length;
										} else
											seventh = gn[h];

										ss[index] = first + second + third + fourth + fifth + sixth + seventh + tail;
										index++;
									}
								}
							}
						}
					}
				}
			}
		}

		if (total_column == 8) {
			int index = 0;
			for (int i = 0; i < gn.length; i++) {
				int start_point2 = 0;
				if (nc.charAt(0) == nc.charAt(1))
					start_point2 = i;
				else
					start_point2 = 0;
				for (int ii = start_point2; ii < gn.length; ii++) {
					int start_point3 = 0;
					if (nc.charAt(1) == nc.charAt(2))
						start_point3 = ii;
					else
						start_point3 = 0;
					for (int j = start_point3; j < gn.length; j++) {
						int start_point4 = 0;
						if (nc.charAt(2) == nc.charAt(3))
							start_point4 = j;
						else
							start_point4 = 0;
						for (int jj = start_point4; jj < gn.length; jj++) {
							int start_point5 = 0;
							if (nc.charAt(3) == nc.charAt(4))
								start_point5 = jj;
							else
								start_point5 = 0;
							for (int k = start_point5; k < gn.length; k++) {
								int start_point6 = 0;
								if (nc.charAt(4) == nc.charAt(5))
									start_point6 = k;
								else
									start_point6 = 0;
								for (int kk = start_point6; kk < gn.length; kk++) {
									int start_point7 = 0;
									if (nc.charAt(5) == nc.charAt(6))
										start_point7 = kk;
									else
										start_point7 = 0;
									for (int h = start_point7; h < gn.length; h++) {
										int start_point8 = 0;
										if (nc.charAt(6) == nc.charAt(7))
											start_point8 = h;
										else
											start_point8 = 0;
										for (int hh = start_point8; hh < gn.length; hh++) {
											String first = "";
											if (nc.charAt(0) == String.valueOf(t).charAt(0)) {
												first = "*";
												i = gn.length;
											} else
												first = gn[i];
											String second = "";
											if (nc.charAt(1) == String.valueOf(t).charAt(0)) {
												second = "*";
												ii = gn.length;
											} else
												second = gn[ii];
											String third = "";
											if (nc.charAt(2) == String.valueOf(t).charAt(0)) {
												third = "*";
												j = gn.length;
											} else
												third = gn[j];
											String fourth = "";
											if (nc.charAt(3) == String.valueOf(t).charAt(0)) {
												fourth = "*";
												jj = gn.length;
											} else
												fourth = gn[jj];
											String fifth = "";
											if (nc.charAt(4) == String.valueOf(t).charAt(0)) {
												fifth = "*";
												k = gn.length;
											} else
												fifth = gn[k];
											String sixth = "";
											if (nc.charAt(5) == String.valueOf(t).charAt(0)) {
												sixth = "*";
												kk = gn.length;
											} else
												sixth = gn[kk];
											String seventh = "";
											if (nc.charAt(6) == String.valueOf(t).charAt(0)) {
												seventh = "*";
												h = gn.length;
											} else
												seventh = gn[h];
											String eighth = "";
											if (nc.charAt(7) == String.valueOf(t).charAt(0)) {
												eighth = "*";
												hh = gn.length;
											} else
												eighth = gn[hh];

											ss[index] = first + second + third + fourth + fifth + sixth + seventh
													+ eighth + tail;
											index++;
										}
									}
								}
							}
						}
					}
				}
			}
		}

		for (int i = 0; i < ss.length; i++)
			System.out.println(ss[i]);

		return ss;

	}
	
	
	/**
	 * 生成放到一个stack中的翻到次数
	 * 
	 * @param t
	 * @param gn
	 */
	void generateRehandleTimeOfAStack(int t, String[] gn, double[] ratio, double pessimisticCoe) {

		int previousM = 0;
		String[] previousWeight = new String[gn.length];
		double[][] previousValueForEachWeight = new double[gn.length][gn.length];
		double[] previousValue = new double[gn.length];

		int currentM = 0;
		String[] currentWeight = new String[gn.length];
		double[][] currentValueForEachWeight = new double[gn.length][gn.length];
		double[] currentValue = new double[gn.length];

		// 用于存储数据
		ArrayList values = new ArrayList();
		// 记录初始值
		values.add(0);
		values.add("0");
		for (int arrival = 0; arrival < gn.length; arrival++) {
			values.add(0.0);
		}
		values.add(0.0);

		for (int m = 1; m <= t; m++) {
			currentM = m;
			if (m < t) {
				for (int w = 0; w < gn.length; w++) {
					currentWeight[w] = gn[w];
					for (int arrival = 0; arrival < gn.length; arrival++) {
						// 刚到达的更轻
						if (arrival > w) {
							int index = -2;
							for (int j = 0; j < previousWeight.length; j++) {
								if (previousWeight[j] == gn[w]) {
									index = j;
									break;
								}
							}
							if (m == 1 && m < t) {
								currentValueForEachWeight[w][arrival] = 1 + pessimisticCoe * (t - m - 1);
							}
							if (m > 1 && m < t) {
								currentValueForEachWeight[w][arrival] = 1 + pessimisticCoe * (t - m - 1)
										+ previousValue[index];
							}
						} else {
							int index = -2;
							for (int j = 0; j < previousWeight.length; j++) {
								if (previousWeight[j] == gn[arrival]) {
									index = j;
									break;
								}
							}
							if (m == 1 && m < t) {
								currentValueForEachWeight[w][arrival] = 0;
							}
							if (m > 1 && m < t) {
								currentValueForEachWeight[w][arrival] = previousValue[index];
							}
						}

					}
					values.add(currentM);
					values.add(currentWeight[w]);
					double temp = 0.0;
					for (int arrival = 0; arrival < gn.length; arrival++) {
						values.add(currentValueForEachWeight[w][arrival]);
						temp = temp + currentValueForEachWeight[w][arrival] * ratio[arrival];
					}
					currentValue[w] = temp;
					values.add(currentValue[w]);
				}
				// 为下一次计算准备数据
				for (int w = 0; w < gn.length; w++) {
					previousWeight[w] = currentWeight[w];
					for (int arrival = 0; arrival < gn.length; arrival++) {
						previousValueForEachWeight[w][arrival] = currentValueForEachWeight[w][arrival];
					}
					previousValue[w] = currentValue[w];
				}

			} else {
				values.add(currentM);
				values.add("*");
				double temp = 0.0;
				for (int arrival = 0; arrival < gn.length; arrival++) {
					values.add(previousValue[arrival]);
					temp = temp + previousValue[arrival] * ratio[arrival];
				}
				values.add(temp);
			}

		}

		int length = gn.length + 3;
		int size = values.size() / length;
		int[] emptyNumber = new int[size];
		String[] weightGroup = new String[size];
		double[][] value = new double[size][gn.length+1];
		for (int i = 0; i < size; i++) {
			emptyNumber[i] = (int) values.get(i*length);
			weightGroup[i] = (String) values.get(i*length+1);
			System.out.print(emptyNumber[i]+"  "+weightGroup[i]+"  ");
			for(int j=0; j<gn.length+1; j++){
				value[i][j] = (double) values.get(i*length+2+j);
				System.out.print(value[i][j]+"  ");
			}
			System.out.println("  ");
		}

	}

	/**
	 * 执行动态规划计算最优存储位置
	 * 
	 * @param s
	 * @param t
	 * @param gn
	 * @param ratio
	 * @param input
	 */
	void executeDynamicProgramming(int s, int t, String[] gn, double[] ratio, boolean input) {
		// s: the number of stacks
		// t: the number of tiers
		// g: the number of weight groups
		// n：空的场地位置的个数
		int n = 0;
		String[] ncPrevious;
		String[] scPrevious;
		double[] vaPrevious;
		int tnPrevious;

		// ncCurrent，scCurrent，vaCurrent是后推式动态规划中当前步的结果，即当前需要计算的结果
		String[] ncCurrent;
		String[] scCurrent;
		double[] vaCurrent;
		int tnCurrent;

		tnPrevious = 1;
		ncPrevious = new String[tnPrevious];
		scPrevious = new String[tnPrevious];
		vaPrevious = new double[tnPrevious];

		// 设定初始值：nc_pre，sc_pre，va_pre是后推式动态规划中后一步的结果，即已知的结果
		String temp = "";
		for (int j = 0; j < s; j++)
			temp = temp + "0";
		ncPrevious[0] = temp;
		scPrevious[0] = temp;
		vaPrevious[0] = 0.0;
		totalStateSize = 0;

		MyProperties properties = new MyProperties();
		try {
			String path = properties.getProperties("path");
			FileWriter fileWriter = new FileWriter(path + "store_value" + String.valueOf(s) + String.valueOf(t)
					+ String.valueOf(gn.length) + ".txt", false);
			PrintWriter printWriter = new PrintWriter(fileWriter);
			while (n < s * t) {
				n = n + 1;
				// the total dimension for the case when the number of empty
				// slots equals n
				String[] nc = calculateEmptyPermutation(s, t, n);
				tnCurrent = calculateTotalWeightPermutationSize(s, t, n, gn, nc);
				totalStateSize = totalStateSize + tnCurrent;
				ncCurrent = new String[tnCurrent];
				scCurrent = new String[tnCurrent];
				vaCurrent = new double[tnCurrent];
				int index = 0;
				for (int i = 0; i < nc.length; i++) {
					String[] sc = calculateWeightPermutation(nc[i], t, gn); // 获取对应于一种空箱状态的，所有重量状态
					for (int j = 0; j < sc.length; j++) {
						ncCurrent[index] = nc[i];
						scCurrent[index] = sc[j];
						vaCurrent[index] = calculateObjectiveValue(t, nc[i], sc[j], gn, ncPrevious, scPrevious,
								vaPrevious, ratio);
						printWriter.print(ncCurrent[index] + scCurrent[index] + "\t");
						for (int k = 0; k < gn.length; k++)
							printWriter.print(followingState[k][0] + followingState[k][1] + "\t" + stateNumberChosen[k]
									+ "\t" + stateWeightChosen[k] + "\t");
						printWriter.println();
						if (n == s * t)
							finalObjectiveValue = vaCurrent[index];
						index++;
					}
				}

				tnPrevious = tnCurrent;
				ncPrevious = new String[tnPrevious];
				scPrevious = new String[tnPrevious];
				vaPrevious = new double[tnPrevious];
				for (int i = 0; i < tnPrevious; i++) {
					ncPrevious[i] = ncCurrent[i];
					scPrevious[i] = scCurrent[i];
					vaPrevious[i] = vaCurrent[i];
				}

				for (int i = 0; i < ncCurrent.length; i++) {
					System.out.println(i + "  " + ncCurrent[i] + "  " + scCurrent[i] + " " + vaCurrent[i]);
				}

			}
			fileWriter.close();
			printWriter.close();
		} catch (IOException f) {
			Dialogbox dialog = new Dialogbox();
			dialog.createDialogbox("error", f.toString());
		}

	}

}

class EvalutionPreparation {
	String[][] currentState;
	String[][] followingState;
	int[][] stateNumberChosen;
	String[][] stateWeightChosen;
	int dynamicProgrammingResultSize;

	void importDynamicProgrammingResults(String filename, String[] gn) {
		MyProperties myProperties = new MyProperties();
		String path = myProperties.getProperties("path");

		Input input = new Input();
		String[][] dynamicProgrammingResults = input.readdata(path + filename);

		dynamicProgrammingResultSize = dynamicProgrammingResults.length;
		currentState = new String[dynamicProgrammingResults.length][1];
		followingState = new String[dynamicProgrammingResults.length][gn.length];
		stateNumberChosen = new int[dynamicProgrammingResults.length][gn.length];
		stateWeightChosen = new String[dynamicProgrammingResults.length][gn.length];

		for (int i = 0; i < dynamicProgrammingResults.length; i++) {
			currentState[i][0] = dynamicProgrammingResults[i][0].trim();
			for (int k = 0; k < gn.length; k++) {
				followingState[i][k] = dynamicProgrammingResults[i][3 * k + 1].trim();
				stateNumberChosen[i][k] = Integer.valueOf(dynamicProgrammingResults[i][3 * k + 2].trim());
				stateWeightChosen[i][k] = dynamicProgrammingResults[i][3 * k + 3].trim();
			}
		}
	}

	String[] generateArrivaingContainerPermutation(int s, int t, String[] gn) {
		int accumulation = 1;
		for (int i = 0; i < s * t; i++) {
			accumulation = accumulation * gn.length;
			if (accumulation > 1000000) {
				accumulation = 1000000;
				break;
			}
		}

		String[] permutation = new String[accumulation];
		int index = 0;

		if (s * t == 28)
			for (int i = 0; i < gn.length; i++) {
				for (int i1 = 0; i1 < gn.length; i1++) {
					for (int i2 = 0; i2 < gn.length; i2++) {
						for (int i3 = 0; i3 < gn.length; i3++) {
							for (int i4 = 0; i4 < gn.length; i4++) {
								for (int i5 = 0; i5 < gn.length; i5++) {
									for (int i6 = 0; i6 < gn.length; i6++) {
										for (int i7 = 0; i7 < gn.length; i7++) {
											for (int i8 = 0; i8 < gn.length; i8++) {
												for (int i9 = 0; i9 < gn.length; i9++) {
													for (int j = 0; j < gn.length; j++) {
														for (int j1 = 0; j1 < gn.length; j1++) {
															for (int j2 = 0; j2 < gn.length; j2++) {
																for (int j3 = 0; j3 < gn.length; j3++) {
																	for (int j4 = 0; j4 < gn.length; j4++) {
																		for (int j5 = 0; j5 < gn.length; j5++) {
																			for (int j6 = 0; j6 < gn.length; j6++) {
																				for (int j7 = 0; j7 < gn.length; j7++) {
																					for (int j8 = 0; j8 < gn.length; j8++) {
																						for (int j9 = 0; j9 < gn.length; j9++) {
																							for (int k = 0; k < gn.length; k++) {
																								for (int k1 = 0; k1 < gn.length; k1++) {
																									for (int k2 = 0; k2 < gn.length; k2++) {
																										for (int k3 = 0; k3 < gn.length; k3++) {
																											for (int k4 = 0; k4 < gn.length; k4++) {
																												for (int k5 = 0; k5 < gn.length; k5++) {
																													for (int k6 = 0; k6 < gn.length; k6++) {
																														for (int k7 = 0; k7 < gn.length; k7++) {
																															if (index < accumulation) {
																																permutation[index] = gn[i]
																																		+ gn[i1]
																																		+ gn[i2]
																																		+ gn[i3]
																																		+ gn[i4]
																																		+ gn[i5]
																																		+ gn[i6]
																																		+ gn[i7]
																																		+ gn[i8]
																																		+ gn[i9]
																																		+ gn[j]
																																		+ gn[j1]
																																		+ gn[j2]
																																		+ gn[j3]
																																		+ gn[j4]
																																		+ gn[j5]
																																		+ gn[j6]
																																		+ gn[j7]
																																		+ gn[j8]
																																		+ gn[j9]
																																		+ gn[k]
																																		+ gn[k1]
																																		+ gn[k2]
																																		+ gn[k3]
																																		+ gn[k4]
																																		+ gn[k5]
																																		+ gn[k6]
																																		+ gn[k7];

																																index++;
																															} else
																																break;
																														}
																														if (index >= accumulation)
																															break;
																													}
																													if (index >= accumulation)
																														break;
																												}
																												if (index >= accumulation)
																													break;
																											}
																											if (index >= accumulation)
																												break;
																										}
																										if (index >= accumulation)
																											break;
																									}
																									if (index >= accumulation)
																										break;
																								}
																								if (index >= accumulation)
																									break;
																							}
																							if (index >= accumulation)
																								break;
																						}
																						if (index >= accumulation)
																							break;
																					}
																					if (index >= accumulation)
																						break;
																				}
																				if (index >= accumulation)
																					break;
																			}
																			if (index >= accumulation)
																				break;
																		}
																		if (index >= accumulation)
																			break;
																	}
																	if (index >= accumulation)
																		break;
																}
																if (index >= accumulation)
																	break;
															}
															if (index >= accumulation)
																break;
														}
														if (index >= accumulation)
															break;
													}
													if (index >= accumulation)
														break;
												}
												if (index >= accumulation)
													break;
											}
											if (index >= accumulation)
												break;
										}
										if (index >= accumulation)
											break;
									}
									if (index >= accumulation)
										break;
								}
								if (index >= accumulation)
									break;
							}
							if (index >= accumulation)
								break;
						}
						if (index >= accumulation)
							break;
					}
					if (index >= accumulation)
						break;
				}
				if (index >= accumulation)
					break;
			}

		if (s * t == 24)
			for (int i = 0; i < gn.length; i++) {
				for (int i1 = 0; i1 < gn.length; i1++) {
					for (int i2 = 0; i2 < gn.length; i2++) {
						for (int i3 = 0; i3 < gn.length; i3++) {
							for (int i4 = 0; i4 < gn.length; i4++) {
								for (int i5 = 0; i5 < gn.length; i5++) {
									for (int i6 = 0; i6 < gn.length; i6++) {
										for (int i7 = 0; i7 < gn.length; i7++) {
											for (int i8 = 0; i8 < gn.length; i8++) {
												for (int i9 = 0; i9 < gn.length; i9++) {
													for (int j = 0; j < gn.length; j++) {
														for (int j1 = 0; j1 < gn.length; j1++) {
															for (int j2 = 0; j2 < gn.length; j2++) {
																for (int j3 = 0; j3 < gn.length; j3++) {
																	for (int j4 = 0; j4 < gn.length; j4++) {
																		for (int j5 = 0; j5 < gn.length; j5++) {
																			for (int j6 = 0; j6 < gn.length; j6++) {
																				for (int j7 = 0; j7 < gn.length; j7++) {
																					for (int j8 = 0; j8 < gn.length; j8++) {
																						for (int j9 = 0; j9 < gn.length; j9++) {
																							for (int k = 0; k < gn.length; k++) {
																								for (int k1 = 0; k1 < gn.length; k1++) {
																									for (int k2 = 0; k2 < gn.length; k2++) {
																										for (int k3 = 0; k3 < gn.length; k3++) {
																											if (index < accumulation) {
																												permutation[index] = gn[i]
																														+ gn[i1]
																														+ gn[i2]
																														+ gn[i3]
																														+ gn[i4]
																														+ gn[i5]
																														+ gn[i6]
																														+ gn[i7]
																														+ gn[i8]
																														+ gn[i9]
																														+ gn[j]
																														+ gn[j1]
																														+ gn[j2]
																														+ gn[j3]
																														+ gn[j4]
																														+ gn[j5]
																														+ gn[j6]
																														+ gn[j7]
																														+ gn[j8]
																														+ gn[j9]
																														+ gn[k]
																														+ gn[k1]
																														+ gn[k2]
																														+ gn[k3];
																												index++;
																											} else
																												break;
																										}
																										if (index >= accumulation)
																											break;
																									}
																									if (index >= accumulation)
																										break;
																								}
																								if (index >= accumulation)
																									break;
																							}
																							if (index >= accumulation)
																								break;
																						}
																						if (index >= accumulation)
																							break;
																					}
																					if (index >= accumulation)
																						break;
																				}
																				if (index >= accumulation)
																					break;
																			}
																			if (index >= accumulation)
																				break;
																		}
																		if (index >= accumulation)
																			break;
																	}
																	if (index >= accumulation)
																		break;
																}
																if (index >= accumulation)
																	break;
															}
															if (index >= accumulation)
																break;
														}
														if (index >= accumulation)
															break;
													}
													if (index >= accumulation)
														break;
												}
												if (index >= accumulation)
													break;
											}
											if (index >= accumulation)
												break;
										}
										if (index >= accumulation)
											break;
									}
									if (index >= accumulation)
										break;
								}
								if (index >= accumulation)
									break;
							}
							if (index >= accumulation)
								break;
						}
						if (index >= accumulation)
							break;
					}
					if (index >= accumulation)
						break;
				}
				if (index >= accumulation)
					break;
			}

		if (s * t == 21)
			for (int i = 0; i < gn.length; i++) {
				for (int i1 = 0; i1 < gn.length; i1++) {
					for (int i2 = 0; i2 < gn.length; i2++) {
						for (int i3 = 0; i3 < gn.length; i3++) {
							for (int i4 = 0; i4 < gn.length; i4++) {
								for (int i5 = 0; i5 < gn.length; i5++) {
									for (int i6 = 0; i6 < gn.length; i6++) {
										for (int i7 = 0; i7 < gn.length; i7++) {
											for (int i8 = 0; i8 < gn.length; i8++) {
												for (int i9 = 0; i9 < gn.length; i9++) {
													for (int j = 0; j < gn.length; j++) {
														for (int j1 = 0; j1 < gn.length; j1++) {
															for (int j2 = 0; j2 < gn.length; j2++) {
																for (int j3 = 0; j3 < gn.length; j3++) {
																	for (int j4 = 0; j4 < gn.length; j4++) {
																		for (int j5 = 0; j5 < gn.length; j5++) {
																			for (int j6 = 0; j6 < gn.length; j6++) {
																				for (int j7 = 0; j7 < gn.length; j7++) {
																					for (int j8 = 0; j8 < gn.length; j8++) {
																						for (int j9 = 0; j9 < gn.length; j9++) {
																							for (int k = 0; k < gn.length; k++) {
																								if (index < accumulation) {
																									permutation[index] = gn[i]
																											+ gn[i1]
																											+ gn[i2]
																											+ gn[i3]
																											+ gn[i4]
																											+ gn[i5]
																											+ gn[i6]
																											+ gn[i7]
																											+ gn[i8]
																											+ gn[i9]
																											+ gn[j]
																											+ gn[j1]
																											+ gn[j2]
																											+ gn[j3]
																											+ gn[j4]
																											+ gn[j5]
																											+ gn[j6]
																											+ gn[j7]
																											+ gn[j8]
																											+ gn[j9]
																											+ gn[k];
																									index++;
																								} else
																									break;
																							}
																							if (index >= accumulation)
																								break;
																						}
																						if (index >= accumulation)
																							break;
																					}
																					if (index >= accumulation)
																						break;
																				}
																				if (index >= accumulation)
																					break;
																			}
																			if (index >= accumulation)
																				break;
																		}
																		if (index >= accumulation)
																			break;
																	}
																	if (index >= accumulation)
																		break;
																}
																if (index >= accumulation)
																	break;
															}
															if (index >= accumulation)
																break;
														}
														if (index >= accumulation)
															break;
													}
													if (index >= accumulation)
														break;
												}
												if (index >= accumulation)
													break;
											}
											if (index >= accumulation)
												break;
										}
										if (index >= accumulation)
											break;
									}
									if (index >= accumulation)
										break;
								}
								if (index >= accumulation)
									break;
							}
							if (index >= accumulation)
								break;
						}
						if (index >= accumulation)
							break;
					}
					if (index >= accumulation)
						break;
				}
				if (index >= accumulation)
					break;
			}

		if (s * t == 20)
			for (int i = 0; i < gn.length; i++) {
				for (int i1 = 0; i1 < gn.length; i1++) {
					for (int i2 = 0; i2 < gn.length; i2++) {
						for (int i3 = 0; i3 < gn.length; i3++) {
							for (int i4 = 0; i4 < gn.length; i4++) {
								for (int i5 = 0; i5 < gn.length; i5++) {
									for (int i6 = 0; i6 < gn.length; i6++) {
										for (int i7 = 0; i7 < gn.length; i7++) {
											for (int i8 = 0; i8 < gn.length; i8++) {
												for (int i9 = 0; i9 < gn.length; i9++) {
													for (int j = 0; j < gn.length; j++) {
														for (int j1 = 0; j1 < gn.length; j1++) {
															for (int j2 = 0; j2 < gn.length; j2++) {
																for (int j3 = 0; j3 < gn.length; j3++) {
																	for (int j4 = 0; j4 < gn.length; j4++) {
																		for (int j5 = 0; j5 < gn.length; j5++) {
																			for (int j6 = 0; j6 < gn.length; j6++) {
																				for (int j7 = 0; j7 < gn.length; j7++) {
																					for (int j8 = 0; j8 < gn.length; j8++) {
																						for (int j9 = 0; j9 < gn.length; j9++) {
																							if (index < accumulation) {
																								permutation[index] = gn[i]
																										+ gn[i1]
																										+ gn[i2]
																										+ gn[i3]
																										+ gn[i4]
																										+ gn[i5]
																										+ gn[i6]
																										+ gn[i7]
																										+ gn[i8]
																										+ gn[i9]
																										+ gn[j]
																										+ gn[j1]
																										+ gn[j2]
																										+ gn[j3]
																										+ gn[j4]
																										+ gn[j5]
																										+ gn[j6]
																										+ gn[j7]
																										+ gn[j8]
																										+ gn[j9];
																								index++;
																							} else
																								break;
																						}
																						if (index >= accumulation)
																							break;
																					}
																					if (index >= accumulation)
																						break;
																				}
																				if (index >= accumulation)
																					break;
																			}
																			if (index >= accumulation)
																				break;
																		}
																		if (index >= accumulation)
																			break;
																	}
																	if (index >= accumulation)
																		break;
																}
																if (index >= accumulation)
																	break;
															}
															if (index >= accumulation)
																break;
														}
														if (index >= accumulation)
															break;
													}
													if (index >= accumulation)
														break;
												}
												if (index >= accumulation)
													break;
											}
											if (index >= accumulation)
												break;
										}
										if (index >= accumulation)
											break;
									}
									if (index >= accumulation)
										break;
								}
								if (index >= accumulation)
									break;
							}
							if (index >= accumulation)
								break;
						}
						if (index >= accumulation)
							break;
					}
					if (index >= accumulation)
						break;
				}
				if (index >= accumulation)
					break;
			}

		if (s * t == 18)
			for (int i = 0; i < gn.length; i++) {
				for (int i1 = 0; i1 < gn.length; i1++) {
					for (int i2 = 0; i2 < gn.length; i2++) {
						for (int i3 = 0; i3 < gn.length; i3++) {
							for (int i4 = 0; i4 < gn.length; i4++) {
								for (int i5 = 0; i5 < gn.length; i5++) {
									for (int i6 = 0; i6 < gn.length; i6++) {
										for (int i7 = 0; i7 < gn.length; i7++) {
											for (int i8 = 0; i8 < gn.length; i8++) {
												for (int i9 = 0; i9 < gn.length; i9++) {
													for (int j = 0; j < gn.length; j++) {
														for (int j1 = 0; j1 < gn.length; j1++) {
															for (int j2 = 0; j2 < gn.length; j2++) {
																for (int j3 = 0; j3 < gn.length; j3++) {
																	for (int j4 = 0; j4 < gn.length; j4++) {
																		for (int j5 = 0; j5 < gn.length; j5++) {
																			for (int j6 = 0; j6 < gn.length; j6++) {
																				for (int j7 = 0; j7 < gn.length; j7++) {
																					if (index < accumulation) {
																						permutation[index] = gn[i]
																								+ gn[i1] + gn[i2]
																								+ gn[i3] + gn[i4]
																								+ gn[i5] + gn[i6]
																								+ gn[i7] + gn[i8]
																								+ gn[i9] + gn[j]
																								+ gn[j1] + gn[j2]
																								+ gn[j3] + gn[j4]
																								+ gn[j5] + gn[j6]
																								+ gn[j7];
																						index++;
																					} else
																						break;
																				}
																				if (index >= accumulation)
																					break;
																			}
																			if (index >= accumulation)
																				break;
																		}
																		if (index >= accumulation)
																			break;
																	}
																	if (index >= accumulation)
																		break;
																}
																if (index >= accumulation)
																	break;
															}
															if (index >= accumulation)
																break;
														}
														if (index >= accumulation)
															break;
													}
													if (index >= accumulation)
														break;
												}
												if (index >= accumulation)
													break;
											}
											if (index >= accumulation)
												break;
										}
										if (index >= accumulation)
											break;
									}
									if (index >= accumulation)
										break;
								}
								if (index >= accumulation)
									break;
							}
							if (index >= accumulation)
								break;
						}
						if (index >= accumulation)
							break;
					}
					if (index >= accumulation)
						break;
				}
				if (index >= accumulation)
					break;
			}

		if (s * t == 16)
			for (int i = 0; i < gn.length; i++) {
				for (int i1 = 0; i1 < gn.length; i1++) {
					for (int i2 = 0; i2 < gn.length; i2++) {
						for (int i3 = 0; i3 < gn.length; i3++) {
							for (int i4 = 0; i4 < gn.length; i4++) {
								for (int i5 = 0; i5 < gn.length; i5++) {
									for (int i6 = 0; i6 < gn.length; i6++) {
										for (int i7 = 0; i7 < gn.length; i7++) {
											for (int i8 = 0; i8 < gn.length; i8++) {
												for (int i9 = 0; i9 < gn.length; i9++) {
													for (int j = 0; j < gn.length; j++) {
														for (int j1 = 0; j1 < gn.length; j1++) {
															for (int j2 = 0; j2 < gn.length; j2++) {
																for (int j3 = 0; j3 < gn.length; j3++) {
																	for (int j4 = 0; j4 < gn.length; j4++) {
																		for (int j5 = 0; j5 < gn.length; j5++) {
																			if (index < accumulation) {
																				permutation[index] = gn[i] + gn[i1]
																						+ gn[i2] + gn[i3] + gn[i4]
																						+ gn[i5] + gn[i6] + gn[i7]
																						+ gn[i8] + gn[i9] + gn[j]
																						+ gn[j1] + gn[j2] + gn[j3]
																						+ gn[j4] + gn[j5];
																				index++;
																			} else
																				break;
																		}
																		if (index >= accumulation)
																			break;
																	}
																	if (index >= accumulation)
																		break;
																}
																if (index >= accumulation)
																	break;
															}
															if (index >= accumulation)
																break;
														}
														if (index >= accumulation)
															break;
													}
													if (index >= accumulation)
														break;
												}
												if (index >= accumulation)
													break;
											}
											if (index >= accumulation)
												break;
										}
										if (index >= accumulation)
											break;
									}
									if (index >= accumulation)
										break;
								}
								if (index >= accumulation)
									break;
							}
							if (index >= accumulation)
								break;
						}
						if (index >= accumulation)
							break;
					}
					if (index >= accumulation)
						break;
				}
				if (index >= accumulation)
					break;
			}

		if (s * t == 15)
			for (int i = 0; i < gn.length; i++) {
				for (int i1 = 0; i1 < gn.length; i1++) {
					for (int i2 = 0; i2 < gn.length; i2++) {
						for (int i3 = 0; i3 < gn.length; i3++) {
							for (int i4 = 0; i4 < gn.length; i4++) {
								for (int i5 = 0; i5 < gn.length; i5++) {
									for (int i6 = 0; i6 < gn.length; i6++) {
										for (int i7 = 0; i7 < gn.length; i7++) {
											for (int i8 = 0; i8 < gn.length; i8++) {
												for (int i9 = 0; i9 < gn.length; i9++) {
													for (int j = 0; j < gn.length; j++) {
														for (int j1 = 0; j1 < gn.length; j1++) {
															for (int j2 = 0; j2 < gn.length; j2++) {
																for (int j3 = 0; j3 < gn.length; j3++) {
																	for (int j4 = 0; j4 < gn.length; j4++) {
																		if (index < accumulation) {
																			permutation[index] = gn[i] + gn[i1]
																					+ gn[i2] + gn[i3] + gn[i4] + gn[i5]
																					+ gn[i6] + gn[i7] + gn[i8] + gn[i9]
																					+ gn[j] + gn[j1] + gn[j2] + gn[j3]
																					+ gn[j4];
																			index++;
																		} else
																			break;
																	}
																	if (index >= accumulation)
																		break;
																}
																if (index >= accumulation)
																	break;
															}
															if (index >= accumulation)
																break;
														}
														if (index >= accumulation)
															break;
													}
													if (index >= accumulation)
														break;
												}
												if (index >= accumulation)
													break;
											}
											if (index >= accumulation)
												break;
										}
										if (index >= accumulation)
											break;
									}
									if (index >= accumulation)
										break;
								}
								if (index >= accumulation)
									break;
							}
							if (index >= accumulation)
								break;
						}
						if (index >= accumulation)
							break;
					}
					if (index >= accumulation)
						break;
				}
				if (index >= accumulation)
					break;
			}

		if (s * t == 12)
			for (int i = 0; i < gn.length; i++) {
				for (int i1 = 0; i1 < gn.length; i1++) {
					for (int i2 = 0; i2 < gn.length; i2++) {
						for (int i3 = 0; i3 < gn.length; i3++) {
							for (int i4 = 0; i4 < gn.length; i4++) {
								for (int i5 = 0; i5 < gn.length; i5++) {
									for (int i6 = 0; i6 < gn.length; i6++) {
										for (int i7 = 0; i7 < gn.length; i7++) {
											for (int i8 = 0; i8 < gn.length; i8++) {
												for (int i9 = 0; i9 < gn.length; i9++) {
													for (int j = 0; j < gn.length; j++) {
														for (int j1 = 0; j1 < gn.length; j1++) {
															if (index < accumulation) {
																permutation[index] = gn[i] + gn[i1] + gn[i2] + gn[i3]
																		+ gn[i4] + gn[i5] + gn[i6] + gn[i7] + gn[i8]
																		+ gn[i9] + gn[j] + gn[j1];
																index++;
															} else
																break;
														}
														if (index >= accumulation)
															break;
													}
													if (index >= accumulation)
														break;
												}
												if (index >= accumulation)
													break;
											}
											if (index >= accumulation)
												break;
										}
										if (index >= accumulation)
											break;
									}
									if (index >= accumulation)
										break;
								}
								if (index >= accumulation)
									break;
							}
							if (index >= accumulation)
								break;
						}
						if (index >= accumulation)
							break;
					}
					if (index >= accumulation)
						break;
				}
				if (index >= accumulation)
					break;
			}

		if (s * t == 9)
			for (int i = 0; i < gn.length; i++) {
				for (int i1 = 0; i1 < gn.length; i1++) {
					for (int i2 = 0; i2 < gn.length; i2++) {
						for (int i3 = 0; i3 < gn.length; i3++) {
							for (int i4 = 0; i4 < gn.length; i4++) {
								for (int i5 = 0; i5 < gn.length; i5++) {
									for (int i6 = 0; i6 < gn.length; i6++) {
										for (int i7 = 0; i7 < gn.length; i7++) {
											for (int i8 = 0; i8 < gn.length; i8++) {
												if (index < accumulation) {
													permutation[index] = gn[i] + gn[i1] + gn[i2] + gn[i3] + gn[i4]
															+ gn[i5] + gn[i6] + gn[i7] + gn[i8];
													index++;
												} else
													break;
											}
											if (index >= accumulation)
												break;
										}
										if (index >= accumulation)
											break;
									}
									if (index >= accumulation)
										break;
								}
								if (index >= accumulation)
									break;
							}
							if (index >= accumulation)
								break;
						}
						if (index >= accumulation)
							break;
					}
					if (index >= accumulation)
						break;
				}
				if (index >= accumulation)
					break;
			}

		if (s * t == 4)
			for (int i = 0; i < gn.length; i++)
				for (int i1 = 0; i1 < gn.length; i1++)
					for (int i2 = 0; i2 < gn.length; i2++)
						for (int i3 = 0; i3 < gn.length; i3++) {
							permutation[index] = gn[i] + gn[i1] + gn[i2] + gn[i3];
							index++;
						}

		return permutation;
	}
}

// using a dynamic indicator
class DynamicEvaluation extends BasicOperation {

	// calculate the expected addition number of relocations
	double[][][] calculateExpectedAdditionalRehandle(int s, int t) {
		// 有三个维度，第一个是所有箱子的个数，第二个是某一stack的空位置数量；第三个是该stack的最高权重
		double[][][] additionalRehandleTimes = new double[s * t][t + 1][s * t + 1];
		for (int i = 0; i < s * t; i++)
			// i: 贝中剩余的集装箱个数
			for (int j = 0; j < t + 1; j++)
				// j: 一stack中空的位置个数,默认最高层为 t+1
				for (int k = 0; k < s * t + 1; k++)
					// k：最高优先权
					additionalRehandleTimes[i][j][k] = 0.0;

		for (int i = 0; i < s * t; i++) {
			int currentRemainingContainerInTheBay = i + 1; // The current N
			for (int j = 0; j < t + 1; j++) {
				int currentEmptySlotNumberInAStack = j + 1; // 当前该stack空的位置个数
				int containerNumberAlreadyInAStack = t + 1 - currentEmptySlotNumberInAStack; // 该stack拥有的集装箱个数
				// 只有当贝中的集装箱个数大于该垛的个数时才有意义
				if (currentRemainingContainerInTheBay > containerNumberAlreadyInAStack) {

					// 重量权重可能的取值范围
					int weightRange = 0;
					// 比如说：整个贝中有20个箱子，其中某stack有3个箱子，那么该垛的最高权重可以为：1，2，3，...,18，共18种
					if (containerNumberAlreadyInAStack > 0) {
						weightRange = currentRemainingContainerInTheBay - containerNumberAlreadyInAStack + 1;
					}
					// 当该stack空位置的数量等于1的时候
					if (currentEmptySlotNumberInAStack == 1 && containerNumberAlreadyInAStack > 0) {
						for (int k = 1; k <= weightRange; k++) {
							int currentWeight = k; // 箱子的权重
							double temp = (double) (currentRemainingContainerInTheBay - containerNumberAlreadyInAStack - (currentWeight - 1))
									/ (currentRemainingContainerInTheBay - containerNumberAlreadyInAStack);
							additionalRehandleTimes[i][j][k] = temp;
						}
					}

					// 当该stack空的数量大于1但又不完全空的时候
					if (currentEmptySlotNumberInAStack > 1 && containerNumberAlreadyInAStack > 0) {
						for (int k = 1; k <= weightRange; k++) {
							int currentWeight = k; // 箱子的权重
							double part1 = 0.0;
							// 权重更重的箱子放下来
							for (int h = 1; h < k; h++) {
								part1 = part1 + additionalRehandleTimes[i][j - 1][h]
										/ (double) (currentRemainingContainerInTheBay - containerNumberAlreadyInAStack);
							}
							// 权重更轻的箱子放下来
							double part2 = (double) (currentRemainingContainerInTheBay - currentWeight - (containerNumberAlreadyInAStack - 1))
									* (1 + additionalRehandleTimes[i][j - 1][k])
									/ (currentRemainingContainerInTheBay - containerNumberAlreadyInAStack);
							additionalRehandleTimes[i][j][k] = part1 + part2;
						}
					}

					// 当该stack完全空的时候
					if (currentEmptySlotNumberInAStack == t + 1) {
						double part = 0.0;
						for (int k = 1; k <= currentRemainingContainerInTheBay; k++) {
							part = part + additionalRehandleTimes[i][j - 1][k]
									/ (double) currentRemainingContainerInTheBay;
						}
						for (int k = 1; k <= currentRemainingContainerInTheBay; k++) {
							additionalRehandleTimes[i][j][k] = part;
						}
					}

				}
			}
		}

		/* for(int i=0; i<s*t; i++)
		     for(int j=0; j<t+1; j++)
		         for(int k=0; k<s*t+1; k++)
		             System.out.println(i+" "+j+" "+k+" "+additionalRehandleTimes[i][j][k]);*/

		return additionalRehandleTimes;
	}

	

	// calculate the rehandling number
	void evaluateTotalRehandleTimes(int s, int t, String[] gn) {
		EvalutionPreparation evaluationPreparation = new EvalutionPreparation();
		evaluationPreparation.importDynamicProgrammingResults("store_value" + String.valueOf(s) + String.valueOf(t)
				+ String.valueOf(gn.length) + ".txt", gn);

		String[] arrivingContainerPermuations = evaluationPreparation.generateArrivaingContainerPermutation(s, t, gn);
		arrivingContainerPermutationSize = arrivingContainerPermuations.length;
		int[] totalRehandle = new int[arrivingContainerPermuations.length];
		for (int i = 0; i < totalRehandle.length; i++)
			totalRehandle[i] = 0;
		int finalTotalRehandle = 0;
		long beginTime8, endTime8, duration8;
		Date myDate = new Date();
		beginTime8 = myDate.getTime();
		double[][][] expectedAdditionalRehandleTimes = calculateExpectedAdditionalRehandle(s, t);
		for (int i = 0; i < arrivingContainerPermuations.length; i++) {

			String target = arrivingContainerPermuations[i];

			// 用来记录该贝在不断收箱过程中的状态变化
			String[][] bayState = new String[s][2];
			// initialize bay_state
			for (int k = 0; k < s; k++) {
				bayState[k][0] = String.valueOf(t);
				bayState[k][1] = "*";
			}

			// 记录每个箱子在贝中的存储位置，第4个维度是指提箱顺序
			int[][] assignedYardLocation = new int[target.length()][4];

			// 抽象的贝状态
			String currentNc = "";
			String currentSc = "";
			// the initial state
			for (int k = 0; k < s; k++) {
				currentNc = currentNc + String.valueOf(t);
				currentSc = currentSc + "*";
			}
			int startSearchingIndexInDynamicProgrammingResults = evaluationPreparation.dynamicProgrammingResultSize - 1;
			String currentNcScTemp = currentNc + currentSc;

			// 根据动态规划的结果分配场地位置
			assignLocationAccordingToDPResults(s, gn, evaluationPreparation, target, bayState, assignedYardLocation,
					startSearchingIndexInDynamicProgrammingResults, currentNcScTemp);

			// 计算每种重量的箱子的个数，该数主要是用于后边确定集装箱的提取顺序
			int[] numberOfEachWeight = new int[gn.length];
			for (int h = 0; h < gn.length; h++)
				numberOfEachWeight[h] = 0;
			for (int j = 0; j < target.length(); j++) {
				for (int h = 0; h < gn.length; h++) {
					if (String.valueOf(target.charAt(j)).equals(gn[h]))
						numberOfEachWeight[h] = numberOfEachWeight[h] + 1;
				}
			}
			for (int h = 1; h < gn.length; h++)
				numberOfEachWeight[h] = numberOfEachWeight[h] + numberOfEachWeight[h - 1];
			for (int h = gn.length - 1; h > 0; h--)
				numberOfEachWeight[h] = numberOfEachWeight[h - 1] + 1;
			numberOfEachWeight[0] = 1;

			// 确定提取顺序：1、先重量；2、行；3、列
			for (int h = 0; h < t; h++) {
				for (int k = 0; k < s; k++) {
					for (int j = 0; j < target.length(); j++) {
						if (assignedYardLocation[j][1] == k && assignedYardLocation[j][2] == h + 1) {
							int weightIndex = 0;
							for (int v = 0; v < gn.length; v++)
								if (String.valueOf(target.charAt(j)).equals(gn[v]))
									weightIndex = v;
							assignedYardLocation[j][3] = numberOfEachWeight[weightIndex];
							numberOfEachWeight[weightIndex] = numberOfEachWeight[weightIndex] + 1;
						}

					}
				}
			}

			// 调用评价方法统计总的翻到次数
			totalRehandle[i] = calculateRehandleTimes(s, assignedYardLocation, expectedAdditionalRehandleTimes);
			// System.out.println(i+" "+target+" "+total_rehandle[i]);

			finalTotalRehandle = finalTotalRehandle + totalRehandle[i];
		}

		// 算完一个记录一个
		Date myDate2 = new Date();
		endTime8 = myDate2.getTime();
		duration8 = endTime8 - beginTime8;
		System.out.println("dynamic total rehandle times:   " + finalTotalRehandle);
		totalDynamicRehandleTimes = finalTotalRehandle;
		MyProperties p = new MyProperties();
		try {
			String path = p.getProperties("path");
			FileWriter fileWriter = new FileWriter(path + "dynamic_total_rehandel_times" + String.valueOf(s)
					+ String.valueOf(t) + String.valueOf(gn.length) + ".txt", false);
			PrintWriter printWriter = new PrintWriter(fileWriter);
			printWriter.println(s + "\t" + t + "\t" + arrivingContainerPermuations.length + "\t" + finalTotalRehandle
					+ "\t" + duration8 + "\t");
			fileWriter.close();
			printWriter.close();
		} catch (IOException f) {
			Dialogbox dialog = new Dialogbox();
			dialog.createDialogbox("error", f.toString());
		}
	}

	/**
	 * 目标是通过模拟提箱过程，统计真实发生的翻到次数
	 * 
	 * @param s
	 * @param assignedYardLocation
	 * @param expectedAdditionalRehandleTimes
	 * @return
	 */
	int calculateRehandleTimes(int s, int[][] assignedYardLocation, double[][][] expectedAdditionalRehandleTimes) {
		int rehandleTimes = 0;

		int[][] yardLocation = new int[assignedYardLocation.length][4];
		for (int i = 0; i < assignedYardLocation.length; i++)
			for (int j = 0; j < 4; j++)
				yardLocation[i][j] = assignedYardLocation[i][j];

		// 提箱过程中贝的状态
		int[][] bayStateSimulation = new int[s][2];
		for (int i = 0; i < s; i++) {
			// 初始空箱数量为1，默认高度为 t+1
			bayStateSimulation[i][0] = 1;
			// 初始stack权重
			bayStateSimulation[i][1] = yardLocation.length;
		}
		// yardLocation[i][3]的数量小则优先权越高
		// 找到最高权重
		for (int i = 0; i < yardLocation.length; i++) {
			for (int j = 0; j < s; j++) {
				if (yardLocation[i][1] == j && bayStateSimulation[j][1] > yardLocation[i][3])
					bayStateSimulation[j][1] = yardLocation[i][3];
			}
		}

		// 逐个提取集装箱
		for (int i = 0; i < yardLocation.length; i++) {
			int currentRetrivalContainerNo = i + 1;
			int targetStackNo = -2;
			int targetTierNo = -2;
			for (int j = 0; j < yardLocation.length; j++) {
				if (yardLocation[j][3] == currentRetrivalContainerNo) {
					targetStackNo = yardLocation[j][1]; // 找到stack号
					targetTierNo = yardLocation[j][2]; // 找到tier号
				}
			}
			// the highest tier of the target stack.
			// The tier no is increasing from top to bottom.
			int targetEmptySlotNumber = bayStateSimulation[targetStackNo][0];
			// this means there are containers needed to be relocated.
			if (targetEmptySlotNumber < targetTierNo) {
				// relocate the containers sequentially from top to the one just
				// stacked on the target one.
				for (int k = targetTierNo - targetEmptySlotNumber; k > 0; k--) {
					// record the index of the container to be relocated.
					int relocatedContainerNo = 0;
					for (int j = 0; j < yardLocation.length; j++) {
						if (yardLocation[j][1] == targetStackNo && yardLocation[j][2] == targetTierNo - k) // 确保是从上往下开始逐个翻到
							relocatedContainerNo = j;
					}

					// find the stack with the minimum objective value.
					double objectiveTemp = Double.MAX_VALUE;
					int stackChosenTemp = -2;
					for (int j = 0; j < s; j++) {
						// bay_state_simulation[j][0] > 0 means the stack has at
						// least one empty slot.
						if (j != targetStackNo && bayStateSimulation[j][0] > 0) {

							double thePrevious = expectedAdditionalRehandleTimes[yardLocation.length
									- currentRetrivalContainerNo][bayStateSimulation[j][0] - 1][bayStateSimulation[j][1]
									- currentRetrivalContainerNo + 1];
							// double the_previous = 0;
							double theNew;
							// the case that when the relocated container is
							// stacked at this stack, and then this empty is
							// full.
							if (bayStateSimulation[j][0] == 1) {
								if (yardLocation[relocatedContainerNo][3] < bayStateSimulation[j][1])
									theNew = 0.0;
								else
									theNew = 1.0; // confirmed relocations
							} else {
								if (yardLocation[relocatedContainerNo][3] < bayStateSimulation[j][1]) {

									theNew = expectedAdditionalRehandleTimes[yardLocation.length
											- currentRetrivalContainerNo][bayStateSimulation[j][0] - 1 - 1][yardLocation[relocatedContainerNo][3]
											- currentRetrivalContainerNo + 1];
								} else
									theNew = 1.0 + expectedAdditionalRehandleTimes[yardLocation.length
											- currentRetrivalContainerNo][bayStateSimulation[j][0] - 1 - 1][bayStateSimulation[j][1]
											- currentRetrivalContainerNo + 1];
							}
							if (objectiveTemp > theNew - thePrevious) {
								objectiveTemp = theNew - thePrevious;
								stackChosenTemp = j;
							}
						}
					}

					// update the bay state of the relocated stack and execute
					// the relocation operation.
					bayStateSimulation[stackChosenTemp][0] = bayStateSimulation[stackChosenTemp][0] - 1;
					if (yardLocation[relocatedContainerNo][3] < bayStateSimulation[stackChosenTemp][1])
						bayStateSimulation[stackChosenTemp][1] = yardLocation[relocatedContainerNo][3];

					// 更新被翻到集装箱的位置
					yardLocation[relocatedContainerNo][1] = stackChosenTemp;
					yardLocation[relocatedContainerNo][2] = bayStateSimulation[stackChosenTemp][0];

					// 更新被翻到集装箱原来垛的状态
					bayStateSimulation[targetStackNo][0] = bayStateSimulation[targetStackNo][0] + 1;

					// add 1 to the counter
					rehandleTimes++; // realized relocations.
				}
			}

			// remove the target container and update the bay state of the
			// target stack
			bayStateSimulation[targetStackNo][0] = targetTierNo + 1;
			bayStateSimulation[targetStackNo][1] = yardLocation.length;// 需要重新找出剩下的最高权重
			for (int j = 0; j < yardLocation.length; j++) {
				if (yardLocation[j][3] == currentRetrivalContainerNo) {
					yardLocation[j][1] = -1;
					yardLocation[j][2] = -1;
					yardLocation[j][3] = -1;
				}
			}

			for (int j = 0; j < yardLocation.length; j++) {
				if (bayStateSimulation[targetStackNo][1] > yardLocation[j][3] && yardLocation[j][1] == targetStackNo)
					bayStateSimulation[targetStackNo][1] = yardLocation[j][3]; // 重新找出剩下的最高权重
			}
		}
		return rehandleTimes;
	}

}

/**
 * 利用静态方法计算翻倒次数
 * 
 * @author zcr
 */
class StaticEvaluation extends BasicOperation {
	/**
	 * 获得具体堆垛位置后，计算翻倒次数下限
	 * 
	 * @param input
	 * @return
	 */
	int calculateRehandleTimes(int[][] input) {
		int rehandleTimes = 0;
		// 根据Kang's method 计算的翻到下限值
		for (int i = 0; i < input.length; i++) {
			int currentContainer = input[i][0];
			boolean isRehandledAlready = false;
			for (int j = 0; j < input.length; j++) {
				// 第jth个集装箱比第ith个集装箱重，第ith个集装箱还没有计算翻到
				if (currentContainer < input[j][0] && !isRehandledAlready) {
					if (input[i][1] == input[j][1] && input[i][2] < input[j][2]) {
						// the jth and ith containers are in the same stack and
						// the ith one is stacked above the jth one.
						// the smaller value of input[i][2], the higher tier
						rehandleTimes = rehandleTimes + 1;
						isRehandledAlready = true;
					}
				}
			}
		}
		return rehandleTimes;
	}

	void evaluateTotalRehandleTimes(int s, int t, String[] gn) {
		EvalutionPreparation evaluationPreparation = new EvalutionPreparation();
		evaluationPreparation.importDynamicProgrammingResults("store_value" + String.valueOf(s) + String.valueOf(t)
				+ String.valueOf(gn.length) + ".txt", gn);

		String[] arrivingContainerPermutaions = evaluationPreparation.generateArrivaingContainerPermutation(s, t, gn);
		arrivingContainerPermutationSize = arrivingContainerPermutaions.length;
		int[] rehandleTimes = new int[arrivingContainerPermutaions.length];
		for (int i = 0; i < rehandleTimes.length; i++)
			rehandleTimes[i] = 0;
		int finalTotalRehandleTimes = 0;
		long beginTime8, endTime8, duration8;
		Date myDate = new Date();
		beginTime8 = myDate.getTime();
		for (int i = 0; i < arrivingContainerPermutaions.length; i++) {
			String target = arrivingContainerPermutaions[i];

			// 记录真实的贝的状态
			String[][] bayState = new String[s][2];
			// initialize bay_state
			for (int k = 0; k < s; k++) {
				bayState[k][0] = String.valueOf(t);
				bayState[k][1] = "*";
			}

			// 记录每一个到达集装箱所分配的位置
			int[][] assignedYardLocation = new int[target.length()][3];

			// 抽象出来的贝的状态
			String currentNc = "";
			String currentSc = "";
			// the initial state
			for (int k = 0; k < s; k++) {
				currentNc = currentNc + String.valueOf(t);
				currentSc = currentSc + "*";
			}

			int startSearchingIndexInDynamicProgrammingResults = evaluationPreparation.dynamicProgrammingResultSize - 1;
			String currentNcScTemp = currentNc + currentSc;

			assignLocationAccordingToDPResults(s, gn, evaluationPreparation, target, bayState, assignedYardLocation,
					startSearchingIndexInDynamicProgrammingResults, currentNcScTemp);

			rehandleTimes[i] = calculateRehandleTimes(assignedYardLocation);
			finalTotalRehandleTimes = finalTotalRehandleTimes + rehandleTimes[i];
		}

		Date mydate2 = new Date();
		endTime8 = mydate2.getTime();
		duration8 = endTime8 - beginTime8;
		System.out.println("total rehandle times:   " + finalTotalRehandleTimes);
		totalStaticRehandleTimes = finalTotalRehandleTimes;
		MyProperties p = new MyProperties();
		try {
			String path = p.getProperties("path");
			FileWriter fileWriter = new FileWriter(path + "total_rehandel_times" + String.valueOf(s)
					+ String.valueOf(t) + String.valueOf(gn.length) + ".txt", false);
			PrintWriter printWriter = new PrintWriter(fileWriter);
			printWriter.println(s + "\t" + t + "\t" + arrivingContainerPermutaions.length + "\t"
					+ finalTotalRehandleTimes + "\t" + duration8 + "\t");
			fileWriter.close();
			printWriter.close();
		} catch (IOException f) {
			Dialogbox dialog = new Dialogbox();
			dialog.createDialogbox("error", f.toString());
		}
	}
}

// .txt文件输入
class Input {
	String[][] readdata(String filename) {
		String[] content = null;
		int linenu = 0;
		int totalline = 0;
		String line = null;
		// 先统计文件有多少?
		try {
			FileReader fr = new FileReader(filename);
			BufferedReader br = new BufferedReader(fr);
			while ((line = br.readLine()) != null) {
				totalline = totalline + 1;
			}
			fr.close();
			br.close();
		} catch (IOException ae) {
			Dialogbox dialog = new Dialogbox();
			dialog.createDialogbox("警告 ", "可能的原因是:1.相应的文件没有找到；2." + ae.toString());
		}

		String[][] res = new String[totalline][];
		// 逐行保存到数据里边去
		try {
			FileReader fr1 = new FileReader(filename);
			BufferedReader br1 = new BufferedReader(fr1);
			while ((line = br1.readLine()) != null) {
				content = line.split("\t");
				res[linenu] = content;
				linenu = linenu + 1;
			}
			fr1.close();
			br1.close();

		} catch (IOException ae) {
			Dialogbox dialog = new Dialogbox();
			dialog.createDialogbox("警告 ", "可能的原因是:1.相应的文件没有找到；2." + ae.toString());
		}
		return res;
	}
}

/**
 * 主程序，执行动态规划计算，静态测试，和动态测试。
 * 
 * @author zcr
 */
public class OldLocationAssignment {
	public static void main(String[] args) {
		DynamicProgramming dynamicProgramming = new DynamicProgramming();

		MyProperties myProperties = new MyProperties();
		double[] ratio = { 0.5, 0.5 };
		String[] gn = { "H", "L" };

		// 两个权重的情形
		long beginTime, endTime, duration;
		try {
			String path = myProperties.getProperties("path");
			FileWriter fileWriter = new FileWriter(path + "final2.txt", false);
			PrintWriter printWriter = new PrintWriter(fileWriter);
			for (int s = 4; s < 8; s++) {
				for (int t = 3; t < 5; t++) {
					// if(s*t<=12){
					Date myDate = new Date();
					beginTime = myDate.getTime();
					dynamicProgramming.generateRehandleTimeOfAStack(t, gn, ratio, 1);
					//dynamicProgramming.executeDynamicProgramming(s, t, gn, ratio, true);
					Date myDate2 = new Date();
					endTime = myDate2.getTime();
					duration = endTime - beginTime;
					printWriter.println(s + "\t" + t + "\t" + 2 + "\t" + dynamicProgramming.totalStateSize + "\t"
							+ dynamicProgramming.finalObjectiveValue + "\t" + duration);

				}
			}
			fileWriter.close();
			printWriter.close();
		} catch (IOException f) {
			Dialogbox dialog = new Dialogbox();
			dialog.createDialogbox("error", f.toString());
		}
/*
		// static indicator
		long beginTime1, endTime1, duration1;
		try {
			String path = myProperties.getProperties("path");
			FileWriter fileWriter = new FileWriter(path + "static2.txt", false);
			PrintWriter printWriter = new PrintWriter(fileWriter);
			for (int s = 4; s < 8; s++) {
				for (int t = 3; t < 5; t++) {
					// if(s*t<=12){
					Date myDate = new Date();
					beginTime1 = myDate.getTime();
					StaticEvaluation staticEvaluation = new StaticEvaluation();
					staticEvaluation.evaluateTotalRehandleTimes(s, t, gn);
					Date myDate2 = new Date();
					endTime1 = myDate2.getTime();
					duration1 = endTime1 - beginTime1;
					printWriter.println(s + "\t" + t + "\t" + 2 + "\t"
							+ staticEvaluation.arrivingContainerPermutationSize + "\t"
							+ staticEvaluation.totalStaticRehandleTimes + "\t" + duration1);
				}
			}
			fileWriter.close();
			printWriter.close();
		} catch (IOException f) {
			Dialogbox dialog = new Dialogbox();
			dialog.createDialogbox("error", f.toString());
		}

		// dynamic indicator
		long beginTime5, endTime5, duration5;
		try {
			String path = myProperties.getProperties("path");
			FileWriter fileWriter = new FileWriter(path + "dynamic2.txt", false);
			PrintWriter printWriter = new PrintWriter(fileWriter);
			for (int s = 4; s < 8; s++) {
				for (int t = 3; t < 5; t++) {
					// if(s*t<=12){
					Date myDate = new Date();
					beginTime5 = myDate.getTime();
					DynamicEvaluation dynamicEvaluation = new DynamicEvaluation();
					dynamicEvaluation.evaluateTotalRehandleTimes(s, t, gn);
					Date myDate2 = new Date();
					endTime5 = myDate2.getTime();
					duration5 = endTime5 - beginTime5;
					printWriter.println(s + "\t" + t + "\t" + 2 + "\t"
							+ dynamicEvaluation.arrivingContainerPermutationSize + "\t"
							+ dynamicEvaluation.totalDynamicRehandleTimes + "\t" + duration5);
				}
			}
			fileWriter.close();
			printWriter.close();
		} catch (IOException f) {
			Dialogbox dialog = new Dialogbox();
			dialog.createDialogbox("error", f.toString());
		}

		double[] ratio2 = { 0.33, 0.33, 0.33 };
		String[] gn2 = { "H", "M", "L" };

		// 三个权重的情形
		long beginTime2, endTime2, duration2;
		try {
			String path = myProperties.getProperties("path");
			FileWriter fileWriter = new FileWriter(path + "final3.txt", false);
			PrintWriter printerWriter = new PrintWriter(fileWriter);
			for (int s = 4; s < 8; s++) {
				for (int t = 3; t < 5; t++) {
					// if(s*t<=12){
					Date myDate = new Date();
					beginTime2 = myDate.getTime();
					dynamicProgramming.executeDynamicProgramming(s, t, gn2, ratio2, true);
					Date myDate2 = new Date();
					endTime2 = myDate2.getTime();
					duration2 = endTime2 - beginTime2;

					printerWriter.println(s + "\t" + t + "\t" + 3 + "\t" + dynamicProgramming.totalStateSize + "\t"
							+ dynamicProgramming.finalObjectiveValue + "\t" + duration2);

				}
			}
			fileWriter.close();
			printerWriter.close();
		} catch (IOException f) {
			Dialogbox dialog = new Dialogbox();
			dialog.createDialogbox("error", f.toString());
		}

		// the static indicator.
		long beginTime3, endTime3, duration3;
		try {
			String path = myProperties.getProperties("path");
			FileWriter fileWriter = new FileWriter(path + "static3.txt", false);
			PrintWriter printWriter = new PrintWriter(fileWriter);
			for (int s = 4; s < 8; s++) {
				for (int t = 3; t < 5; t++) {
					// if(s*t<=12){
					Date myDate = new Date();
					beginTime3 = myDate.getTime();
					StaticEvaluation staticEvaluation = new StaticEvaluation();
					staticEvaluation.evaluateTotalRehandleTimes(s, t, gn2);
					Date myDate2 = new Date();
					endTime3 = myDate2.getTime();
					duration3 = endTime3 - beginTime3;
					printWriter.println(s + "\t" + t + "\t" + 3 + "\t"
							+ staticEvaluation.arrivingContainerPermutationSize + "\t"
							+ staticEvaluation.totalStaticRehandleTimes + "\t" + duration3);
				}
			}
			fileWriter.close();
			printWriter.close();
		} catch (IOException f) {
			Dialogbox dialog = new Dialogbox();
			dialog.createDialogbox("error", f.toString());
		}

		// the dynamic indicator.
		long beginTime4, endTime4, duration4;
		try {
			String path = myProperties.getProperties("path");
			FileWriter fileWriter = new FileWriter(path + "dynamic3.txt", false);
			PrintWriter printWriter = new PrintWriter(fileWriter);
			for (int s = 4; s < 8; s++) {
				for (int t = 3; t < 5; t++) {
					// if(s*t<=12){
					Date myDate = new Date();
					beginTime4 = myDate.getTime();
					DynamicEvaluation dynamicEvaluation = new DynamicEvaluation();
					dynamicEvaluation.evaluateTotalRehandleTimes(s, t, gn2);
					Date myDate2 = new Date();
					endTime4 = myDate2.getTime();
					duration4 = endTime4 - beginTime4;
					printWriter.println(s + "\t" + t + "\t" + 3 + "\t"
							+ dynamicEvaluation.arrivingContainerPermutationSize + "\t"
							+ dynamicEvaluation.totalDynamicRehandleTimes + "\t" + duration4);
				}
			}
			fileWriter.close();
			printWriter.close();
		} catch (IOException f) {
			Dialogbox dialog = new Dialogbox();
			dialog.createDialogbox("error", f.toString());
		}
*/
	}

}
ndex++;
									}
								}
							}
						}
					}
				}
			}
		}

		if (total_column == 8) {
			int index = 0;
			for (int i = 0; i < gn.length; i++) {
				int start_point2 = 0;
				if (nc.charAt(0) == nc.charAt(1))
					start_point2 = i;
				else
					start_point2 = 0;
				for (int ii = start_point2; ii < gn.length; ii++) {
					int start_point3 = 0;
					if (nc.charAt(1) == nc.charAt(2))
						start_point3 = ii;
					else
						start_point3 = 0;
					for (int j = start_point3; j < gn.length; j++) {
						int start_point4 = 0;
						if (nc.charAt(2) == nc.charAt(3))
							start_point4 = j;
						else
							start_point4 = 0;
						for (int jj = start_point4; jj < gn.length; jj++) {
							int start_point5 = 0;
							if (nc.charAt(3) == nc.charAt(4))
								start_point5 = jj;
							else
								start_point5 = 0;
							for (int k = start_point5; k < gn.length; k++) {
								int start_point6 = 0;
								if (nc.charAt(4) == nc.charAt(5))
									start_point6 = k;
								else
									start_point6 = 0;
								for (int kk = start_point6; kk < gn.length; kk++) {
									int start_point7 = 0;
									if (nc.charAt(5) == nc.charAt(6))
										start_point7 = kk;
									else
										start_point7 = 0;
									for (int h = start_point7; h < gn.length; h++) {
										int start_point8 = 0;
										if (nc.charAt(6) == nc.charAt(7))
											start_point8 = h;
										else
											start_point8 = 0;
										for (int hh = start_point8; hh < gn.length; hh++) {
											String first = "";
											if (nc.charAt(0) == String.valueOf(t).charAt(0)) {
												first = "*";
												i = gn.length;
											} else
												first = gn[i];
											String second = "";
											if (nc.charAt(1) == String.valueOf(t).charAt(0)) {
												second = "*";
												ii = gn.length;
											} else
												second = gn[ii];
											String third = "";
											if (nc.charAt(2) == String.valueOf(t).charAt(0)) {
												third = "*";
												j = gn.length;
											} else
												third = gn[j];
											String fourth = "";
											if (nc.charAt(3) == String.valueOf(t).charAt(0)) {
												fourth = "*";
												jj = gn.length;
											} else
												fourth = gn[jj];
											String fifth = "";
											if (nc.charAt(4) == String.valueOf(t).charAt(0)) {
												fifth = "*";
												k = gn.length;
											} else
												fifth = gn[k];
											String sixth = "";
											if (nc.charAt(5) == String.valueOf(t).charAt(0)) {
												sixth = "*";
												kk = gn.length;
											} else
												sixth = gn[kk];
											String seventh = "";
											if (nc.charAt(6) == String.valueOf(t).charAt(0)) {
												seventh = "*";
												h = gn.length;
											} else
												seventh = gn[h];
											String eighth = "";
											if (nc.charAt(7) == String.valueOf(t).charAt(0)) {
												eighth = "*";
												hh = gn.length;
											} else
												eighth = gn[hh];

											ss[index] = first + second + third + fourth + fifth + sixth + seventh
													+ eighth + tail;
											index++;
										}
									}
								}
							}
						}
					}
				}
			}
		}

		for (int i = 0; i < ss.length; i++)
			System.out.println(ss[i]);

		return ss;

	}
	
	
	/**
	 * 生成放到一个stack中的翻到次数
	 * 
	 * @param t
	 * @param gn
	 */
	void generateRehandleTimeOfAStack(int t, String[] gn, double[] ratio, double pessimisticCoe) {

		int previousM = 0;
		String[] previousWeight = new String[gn.length];
		double[][] previousValueForEachWeight = new double[gn.length][gn.length];
		double[] previousValue = new double[gn.length];

		int currentM = 0;
		String[] currentWeight = new String[gn.length];
		double[][] currentValueForEachWeight = new double[gn.length][gn.length];
		double[] currentValue = new double[gn.length];

		// 用于存储数据
		ArrayList values = new ArrayList();
		// 记录初始值
		values.add(0);
		values.add("0");
		for (int arrival = 0; arrival < gn.length; arrival++) {
			values.add(0.0);
		}
		values.add(0.0);

		for (int m = 1; m <= t; m++) {
			currentM = m;
			if (m < t) {
				for (int w = 0; w < gn.length; w++) {
					currentWeight[w] = gn[w];
					for (int arrival = 0; arrival < gn.length; arrival++) {
						// 刚到达的更轻
						if (arrival > w) {
							int index = -2;
							for (int j = 0; j < previousWeight.length; j++) {
								if (previousWeight[j] == gn[w]) {
									index = j;
									break;
								}
							}
							if (m == 1 && m < t) {
								currentValueForEachWeight[w][arrival] = 1 + pessimisticCoe * (t - m - 1);
							}
							if (m > 1 && m < t) {
								currentValueForEachWeight[w][arrival] = 1 + pessimisticCoe * (t - m - 1)
										+ previousValue[index];
							}
						} else {
							int index = -2;
							for (int j = 0; j < previousWeight.length; j++) {
								if (previousWeight[j] == gn[arrival]) {
									index = j;
									break;
								}
							}
							if (m == 1 && m < t) {
								currentValueForEachWeight[w][arrival] = 0;
							}
							if (m > 1 && m < t) {
								currentValueForEachWeight[w][arrival] = previousValue[index];
							}
						}

					}
					values.add(currentM);
					values.add(currentWeight[w]);
					double temp = 0.0;
					for (int arrival = 0; arrival < gn.length; arrival++) {
						values.add(currentValueForEachWeight[w][arrival]);
						temp = temp + currentValueForEachWeight[w][arrival] * ratio[arrival];
					}
					currentValue[w] = temp;
					values.add(currentValue[w]);
				}
				// 为下一次计算准备数据
				for (int w = 0; w < gn.length; w++) {
					previousWeight[w] = currentWeight[w];
					for (int arrival = 0; arrival < gn.length; arrival++) {
						previousValueForEachWeight[w][arrival] = currentValueForEachWeight[w][arrival];
					}
					previousValue[w] = currentValue[w];
				}

			} else {
				values.add(currentM);
				values.add("*");
				double temp = 0.0;
				for (int arrival = 0; arrival < gn.length; arrival++) {
					values.add(previousValue[arrival]);
					temp = temp + previousValue[arrival] * ratio[arrival];
				}
				values.add(temp);
			}

		}

		int length = gn.length + 3;
		int size = values.size() / length;
		int[] emptyNumber = new int[size];
		String[] weightGroup = new String[size];
		double[][] value = new double[size][gn.length+1];
		for (int i = 0; i < size; i++) {
			emptyNumber[i] = (int) values.get(i*length);
			weightGroup[i] = (String) values.get(i*length+1);
			System.out.print(emptyNumber[i]+"  "+weightGroup[i]+"  ");
			for(int j=0; j<gn.length+1; j++){
				value[i][j] = (double) values.get(i*length+2+j);
				System.out.print(value[i][j]+"  ");
			}
			System.out.println("  ");
		}

	}

	/**
	 * 执行动态规划计算最优存储位置
	 * 
	 * @param s
	 * @param t
	 * @param gn
	 * @param ratio
	 * @param input
	 */
	void executeDynamicProgramming(int s, int t, String[] gn, double[] ratio, boolean input) {
		// s: the number of stacks
		// t: the number of tiers
		// g: the number of weight groups
		// n：空的场地位置的个数
		int n = 0;
		String[] ncPrevious;
		String[] scPrevious;
		double[] vaPrevious;
		int tnPrevious;

		// ncCurrent，scCurrent，vaCurrent是后推式动态规划中当前步的结果，即当前需要计算的结果
		String[] ncCurrent;
		String[] scCurrent;
		double[] vaCurrent;
		int tnCurrent;

		tnPrevious = 1;
		ncPrevious = new String[tnPrevious];
		scPrevious = new String[tnPrevious];
		vaPrevious = new double[tnPrevious];

		// 设定初始值：nc_pre，sc_pre，va_pre是后推式动态规划中后一步的结果，即已知的结果
		String temp = "";
		for (int j = 0; j < s; j++)
			temp = temp + "0";
		ncPrevious[0] = temp;
		scPrevious[0] = temp;
		vaPrevious[0] = 0.0;
		totalStateSize = 0;

		MyProperties properties = new MyProperties();
		try {
			String path = properties.getProperties("path");
			FileWriter fileWriter = new FileWriter(path + "store_value" + String.valueOf(s) + String.valueOf(t)
					+ String.valueOf(gn.length) + ".txt", false);
			PrintWriter printWriter = new PrintWriter(fileWriter);
			while (n < s * t) {
				n = n + 1;
				// the total dimension for the case when the number of empty
				// slots equals n
				String[] nc = calculateEmptyPermutation(s, t, n);
				tnCurrent = calculateTotalWeightPermutationSize(s, t, n, gn, nc);
				totalStateSize = totalStateSize + tnCurrent;
				ncCurrent = new String[tnCurrent];
				scCurrent = new String[tnCurrent];
				vaCurrent = new double[tnCurrent];
				int index = 0;
				for (int i = 0; i < nc.length; i++) {
					String[] sc = calculateWeightPermutation(nc[i], t, gn); // 获取对应于一种空箱状态的，所有重量状态
					for (int j = 0; j < sc.length; j++) {
						ncCurrent[index] = nc[i];
						scCurrent[index] = sc[j];
						vaCurrent[index] = calculateObjectiveValue(t, nc[i], sc[j], gn, ncPrevious, scPrevious,
								vaPrevious, ratio);
						printWriter.print(ncCurrent[index] + scCurrent[index] + "\t");
						for (int k = 0; k < gn.length; k++)
							printWriter.print(followingState[k][0] + followingState[k][1] + "\t" + stateNumberChosen[k]
									+ "\t" + stateWeightChosen[k] + "\t");
						printWriter.println();
						if (n == s * t)
							finalObjectiveValue = vaCurrent[index];
						index++;
					}
				}

				tnPrevious = tnCurrent;
				ncPrevious = new String[tnPrevious];
				scPrevious = new String[tnPrevious];
				vaPrevious = new double[tnPrevious];
				for (int i = 0; i < tnPrevious; i++) {
					ncPrevious[i] = ncCurrent[i];
					scPrevious[i] = scCurrent[i];
					vaPrevious[i] = vaCurrent[i];
				}

				for (int i = 0; i < ncCurrent.length; i++) {
					System.out.println(i + "  " + ncCurrent[i] + "  " + scCurrent[i] + " " + vaCurrent[i]);
				}

			}
			fileWriter.close();
			printWriter.close();
		} catch (IOException f) {
			Dialogbox dialog = new Dialogbox();
			dialog.createDialogbox("error", f.toString());
		}

	}

}

class EvalutionPreparation {
	String[][] currentState;
	String[][] followingState;
	int[][] stateNumberChosen;
	String[][] stateWeightChosen;
	int dynamicProgrammingResultSize;

	void importDynamicProgrammingResults(String filename, String[] gn) {
		MyProperties myProperties = new MyPr