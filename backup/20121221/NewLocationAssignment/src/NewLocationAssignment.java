/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.Date;
import java.io.*;
import java.util.*;
import javax.swing.*;

class DialogBox {
	void createDialogBox(String title, String content) {
		JFrame frame = new JFrame();
		frame.setVisible(true);
		frame.setAlwaysOnTop(true);
		frame.setLocationRelativeTo(null);
		JOptionPane.showMessageDialog(frame, content, title, JOptionPane.ERROR_MESSAGE);
		frame.dispose();
	}
}

class MyProperties {
	String getproperties(String properties) {
		// \u8BFB\u6587\u4EF6\u6240\u5728\u7684\u6839\u76EE\u5F55
		Properties properties1 = System.getProperties();
		String path = properties1.getProperty("user.dir");
		String rootPath = path.substring(0, 1);
		// \u914D\u7F6E\u6587\u4EF6\u5305\u62EC\u76EE\u5F55\u548C\u5C5E\u6027
		InputStream inputStream;
		Properties p = new Properties();
		try {
			try {
				inputStream = new BufferedInputStream(new FileInputStream(rootPath
						+ ":\\zcr\\program\\LocationAssignment\\data_newModel\\config.properties"));
				p.load(inputStream);
				String proPath = p.getProperty("path");
				p.setProperty("path", rootPath + proPath);
			} catch (FileNotFoundException e) {
				DialogBox dialogBox = new DialogBox();
				dialogBox.createDialogBox("\u8B66\u544A",
						"\u53EF\u80FD\u7684\u539F\u56E0\u662F\uFF1A1.config.properties\u6587\u4EF6\u4E0D\u5B58\u5728\uFF1B2."
								+ e.toString());
			}
		} catch (IOException e) {
			DialogBox dialogBox = new DialogBox();
			dialogBox.createDialogBox("\u8B66\u544A ",
					"\u53EF\u80FD\u7684\u539F\u56E0\u662F\uFF1A1.config.properties\u6587\u4EF6\u4E0D\u5B58\u5728\uFF1B2."
							+ e.toString());
		}
		return p.getProperty(properties);
	}
}

class DynamicProgram {
	int stateSize;
	double finalObjectiveValue;
	String[] followingStateNumber;
	int[][] followingStateWeight;
	int[] arrivingWeightGroup;
	double[] currentRehandleTimes;
	int[] tierNoChosen;
	int[] stateNumberChosen;
	int[] stateWeightChosen;

	int calculateRehandleTimes(int sc, int gn, int size) {
		int rehandle = 0;

		int r1 = sc % 10;
		int r10 = (sc % 100) / 10;
		int r100 = (sc % 1000) / 100;

		// method 1 optimistic case
		int rehandleMethod1 = 0;
		if (gn == 1) {
			if (r10 + r100 > 0)
				rehandleMethod1 = 1;
		}
		if (gn == 10) {
			if (r100 > 0)
				rehandleMethod1 = 1;
		}
		rehandle = rehandleMethod1;

		/*
		  //method 2 pessimistic case
		  int rehandleMethod2=0;
		  if(size == 2){
		     if(gn==1){
		         if(r10 > 0)
		              rehandleMethod2 = 1 + r1;
		     }
		      if(gn==10){
		          if(r10 > 0)
		              rehandleMethod2 = r1;
		      }
		  }
		  
		  if(size == 3){
		     if(gn==1){
		         if(r100 > 0)
		              rehandleMethod2 = 1 + r1 + r10;
		         else{
		             if(r10 > 0)
		                 rehandleMethod2 = 1 + r1;
		         }
		     }
		      if(gn==10){
		          if(r100 > 0)
		              rehandleMethod2 = 1 + r1 + r10;
		          else{
		             if(r10 > 0)
		                 rehandleMethod2 = r1;
		         }
		      }
		      if(gn==100){
		          if(r100 > 0)
		              rehandleMethod2 = r1 + r10;
		          else{
		             if(r10 > 0)
		                 rehandleMethod2 = r1;
		         }
		      }
		  }
		  rehandle = rehandleMethod2;
		  

		
		//method 3 intermediate case //注意前两个都不能屏蔽，才起效果。
		int rehandleMethod3=0;
		rehandleMethod3 = Math.round((rehandleMethod1 + rehandleMethod2)/2);
		rehandle = rehandleMethod3;
		*/

		return rehandle;
	}

	double calculateObjectiveValue(int t, String nc, int[] sc, int[] gn, String[] ncPrevious, int[][] scPrevious,
			double[] vaPrevious, double[] ratio) {
		followingStateNumber = new String[gn.length];
		followingStateWeight = new int[gn.length][sc.length];
		arrivingWeightGroup = new int[gn.length];
		currentRehandleTimes = new double[gn.length];
		tierNoChosen = new int[gn.length];
		stateNumberChosen = new int[gn.length];
		stateWeightChosen = new int[gn.length];

		double[] objectiveValue = new double[gn.length];
		for (int i = 0; i < gn.length; i++)
			objectiveValue[i] = Double.MAX_VALUE;

		// 到达的集装箱
		for (int i = 0; i < gn.length; i++) {

			// 每一个有空位置的stack都要检查一遍
			for (int j = 0; j < nc.length(); j++) {
				if (nc.charAt(j) > '0') {
					String ncTemp = nc;
					int[] scTemp = new int[sc.length];
					for (int k = 0; k < sc.length; k++)
						scTemp[k] = sc[k];

					// 可以看成是空位置的层数，也就是到达集装箱可以放置的层数，数值越大，越在底层
					int tierNoTemp = Integer.parseInt(String.valueOf(nc.charAt(j)));
					int stateWeightChosenTemp = sc[j]; // 该stack的权重组合

					int rehandleTimes = calculateRehandleTimes(scTemp[j], gn[i], gn.length);

					if (objectiveValue[i] > rehandleTimes) {
						// adjust nc
						String firstPart = "";
						String thirdPart = "";
						String secondPart = "";
						if (j > 0)
							firstPart = ncTemp.substring(0, j - 1 - 0 + 1);
						if (j < nc.length() - 1)
							thirdPart = ncTemp.substring(j + 1, ncTemp.length() - 1 - (j + 1) + 1 + (j + 1));
						int leftEmpty = Integer.parseInt(String.valueOf(ncTemp.charAt(j))) - 1;
						secondPart = String.valueOf(leftEmpty);
						ncTemp = firstPart + secondPart + thirdPart;

						// adjust sc
						// 满了
						if (leftEmpty == 0) {
							scTemp[j] = t;
						} else { // 还不满
							scTemp[j] = scTemp[j] + gn[i];
						}

						// re-sequence nc and sc　//其实只是根据ｎｃ进行排序，为保证一致性，也需对ｓｃ进行排序
						for (int k = 0; k < ncTemp.length() - 1; k++) {
							int first = Integer.parseInt(String.valueOf(ncTemp.charAt(k)));
							int second = Integer.parseInt(String.valueOf(ncTemp.charAt(k + 1)));
							String firstPart2 = "";
							String fourthPart2 = "";
							String secondPart2 = "";
							String thirdPart2 = "";
							if (first < second) {
								if (k > 0)
									firstPart2 = ncTemp.substring(0, k - 1 - 0 + 1);
								if (k < nc.length() - 2)
									fourthPart2 = ncTemp.substring(k + 2, ncTemp.length() - 1 - (k + 2) + 1 + (k + 2));
								secondPart2 = String.valueOf(second);
								thirdPart2 = String.valueOf(first);
								ncTemp = firstPart2 + secondPart2 + thirdPart2 + fourthPart2;
								// re-sequence sc
								int tempSc = scTemp[k + 1];
								scTemp[k + 1] = scTemp[k];
								scTemp[k] = tempSc;
							}
						}

						// re-sequence sc　//对具有相等ｎｃ的ｓｔａｃｋ按重量权重组合进行排序
						boolean needed = true;
						while (needed) {
							needed = false;
							for (int k = 0; k < ncTemp.length() - 1; k++) {
								int first = Integer.parseInt(String.valueOf(ncTemp.charAt(k)));
								int second = Integer.parseInt(String.valueOf(ncTemp.charAt(k + 1)));
								int first1 = scTemp[k];
								int second1 = scTemp[k + 1];
								if (first == second && first1 < second1) {
									needed = true;
									int tempSc = scTemp[k + 1];
									scTemp[k + 1] = scTemp[k];
									scTemp[k] = tempSc;
								}
							}
						}

						// 先找到　ｎｃ　相等　的状态　,但ｓｃ不一定相等
						int startIndex = 0;
						for (int k = 0; k < ncPrevious.length; k++) {
							if (ncTemp.equals(ncPrevious[k])) {
								startIndex = k;
								break;
							}
						}

						int start = startIndex;
						int interval = 100;
						while (start < ncPrevious.length) {
							if (ncTemp.equals(ncPrevious[start])) {
								boolean small = false;
								for (int m = 0; m < scTemp.length; m++) {
									// scPrevious是按照从大到小进行排序的，如果scTemp[m]
									// <
									// scPrevious[start][m]，则要匹配的sc_temp[m]应该还在后边
									if (scTemp[m] < scPrevious[start][m]) {
										boolean allEqual = true;
										for (int k = 0; k < m; k++) { // 判断之前的是否都相等
											if (scTemp[k] != scPrevious[start][k]) {
												allEqual = false;
												break;
											}
										}
										if (allEqual) {
											small = true;
											break;
										}
									}
								}

								if (small) { // 发现 要找的还在后边
									int nextStart = start + interval;
									if (nextStart < ncPrevious.length) {
										if (ncTemp.compareTo(ncPrevious[nextStart]) > 0) { // 找过头了，即要找的在这之前
											break;
										}
										if (ncTemp.equals(ncPrevious[nextStart])) {
											boolean small2 = false;
											for (int m = 0; m < scTemp.length; m++) {
												if (scTemp[m] < scPrevious[nextStart][m]) {
													boolean allEqual2 = true;
													for (int k = 0; k < m; k++) {
														if (scTemp[k] != scPrevious[nextStart][k]) {
															allEqual2 = false;
															break;
														}
													}
													if (allEqual2) {
														small2 = true;
														break;
													}
												}
											}
											if (small2) { // 发现 要找的还在后边
												start = start + interval + 1;
											}
											if (!small2) {
												break;
											}
										}
									} else {
										break;
									}
								} else {
									break;
								}
							}
						}

						// 找到匹配的
						double followingValue = 0.0;
						for (int k = start; k < ncPrevious.length; k++) {
							if (ncTemp.equals(ncPrevious[k])) {
								boolean match = true;
								for (int m = 0; m < scTemp.length; m++)
									if (scTemp[m] != scPrevious[k][m]) {
										match = false;
										break;
									}
								if (match) {
									followingValue = vaPrevious[k];
									break;
								}
							}
						}

						// calculate objective value
						double valueTemp = followingValue + rehandleTimes;
						if (objectiveValue[i] > valueTemp) {
							objectiveValue[i] = valueTemp;
							followingStateNumber[i] = ncTemp;
							for (int m = 0; m < scTemp.length; m++)
								followingStateWeight[i][m] = scTemp[m];
							arrivingWeightGroup[i] = gn[i];
							currentRehandleTimes[i] = rehandleTimes;
							tierNoChosen[i] = tierNoTemp;
							stateNumberChosen[i] = tierNoTemp; // 挑的ｓｔａｃｋ的层号
							stateWeightChosen[i] = stateWeightChosenTemp; // 挑的ｓｔａｃｋ的重量权重
						}
					}
				}
			}
		}
		double totalObjectiveValue = 0.0;
		for (int i = 0; i < objectiveValue.length; i++)
			totalObjectiveValue = totalObjectiveValue + objectiveValue[i] * ratio[i];
		return totalObjectiveValue;
	}

	String[] calculateEmptyPermutation(int s, int t, int n) {
		ArrayList<String> value = new ArrayList<String>();

		if (s == 2) {
			for (int i = t; i >= 0; i--)
				for (int ii = i; ii >= 0; ii--)
					if (i + ii == n) {
						value.add(String.valueOf(i) + String.valueOf(ii));
					}
		}

		if (s == 3) {
			for (int i = t; i >= 0; i--)
				for (int ii = i; ii >= 0; ii--)
					for (int j = ii; j >= 0; j--) {
						if (i + ii + j == n) {
							value.add(String.valueOf(i) + String.valueOf(ii) + String.valueOf(j));
						}
					}
		}

		if (s == 4) {
			for (int i = t; i >= 0; i--)
				for (int ii = i; ii >= 0; ii--)
					for (int j = ii; j >= 0; j--)
						for (int jj = j; jj >= 0; jj--) {
							if (i + ii + j + jj == n) {
								value.add(String.valueOf(i) + String.valueOf(ii) + String.valueOf(j)
										+ String.valueOf(jj));
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
									value.add(String.valueOf(i) + String.valueOf(ii) + String.valueOf(j)
											+ String.valueOf(jj) + String.valueOf(k));
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
										value.add(String.valueOf(i) + String.valueOf(ii) + String.valueOf(j)
												+ String.valueOf(jj) + String.valueOf(k) + String.valueOf(kk));
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
											value.add(String.valueOf(i) + String.valueOf(ii) + String.valueOf(j)
													+ String.valueOf(jj) + String.valueOf(k) + String.valueOf(kk)
													+ String.valueOf(h));
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
												value.add(String.valueOf(i) + String.valueOf(ii) + String.valueOf(j)
														+ String.valueOf(jj) + String.valueOf(k) + String.valueOf(kk)
														+ String.valueOf(h) + String.valueOf(hh));
											}
										}
		}

		String[] result = new String[value.size()];

		for (int i = 0; i < value.size(); i++)
			result[i] = (String) value.get(i);

		for (int i = 0; i < result.length; i++)
			System.out.println(result[i]);
		return result;
	}

	// calculate the number of stacks on which there are empty slots
	int[][] calculateWeightPermutation(String nc, int t, int[] gn) {
		int totalColumn = 0;
		for (int i = 0; i < nc.length(); i++) {
			if (nc.charAt(i) > '0')
				totalColumn++;
		}

		ArrayList<Integer> value = new ArrayList<Integer>();

		if (totalColumn == 1) {
			// 该stack拥有的集装箱个数
			int alreadyHave1 = t - Integer.parseInt(String.valueOf(nc.charAt(0)));
			int[] column1;
			column1 = generateColumnState(alreadyHave1, gn);
			for (int i = 0; i < column1.length; i++) {
				value.add(column1[i]); // 取第一项
				for (int h = 1; h < nc.length(); h++)
					value.add(t); // 其他stack用“t”补满
			}
		}

		if (totalColumn == 2) {
			int alreadyHave1 = t - Integer.parseInt(String.valueOf(nc.charAt(0)));
			int alreadyHave2 = t - Integer.parseInt(String.valueOf(nc.charAt(1)));
			int[] column1 = generateColumnState(alreadyHave1, gn);
			int[] column2 = generateColumnState(alreadyHave2, gn);
			int columnStateSize1 = column1.length;
			int columnStateSize2 = column2.length;
			for (int i = 0; i < columnStateSize1; i++) {
				int startPosition2;
				// 确保空位置相等的两stack之间，重量权重大的排在前边
				if (alreadyHave2 == alreadyHave1)
					startPosition2 = i;
				else
					startPosition2 = 0;
				for (int j = startPosition2; j < columnStateSize2; j++) {
					value.add(column1[i]); // 取第一项
					value.add(column2[j]); // 取第二项
					for (int h = 2; h < nc.length(); h++)
						// 之后项补满
						value.add(t);
				}
			}
		}

		if (totalColumn == 3) {
			int alreadyHave1 = t - Integer.parseInt(String.valueOf(nc.charAt(0)));
			int alreadyHave2 = t - Integer.parseInt(String.valueOf(nc.charAt(1)));
			int alreadyHave3 = t - Integer.parseInt(String.valueOf(nc.charAt(2)));
			int[] column1 = generateColumnState(alreadyHave1, gn);
			int[] column2 = generateColumnState(alreadyHave2, gn);
			int[] column3 = generateColumnState(alreadyHave3, gn);
			int columnStateSize1 = column1.length;
			int columnStateSize2 = column2.length;
			int columnStateSize3 = column3.length;
			for (int i = 0; i < columnStateSize1; i++) {
				int startPosition2;
				if (alreadyHave2 == alreadyHave1)
					startPosition2 = i;
				else
					startPosition2 = 0;
				for (int ii = startPosition2; ii < columnStateSize2; ii++) {
					int startPosition3;
					if (alreadyHave3 == alreadyHave2)
						startPosition3 = ii;
					else
						startPosition3 = 0;
					for (int j = startPosition3; j < columnStateSize3; j++) {
						value.add(column1[i]);
						value.add(column2[ii]);
						value.add(column3[j]);
						for (int h = 3; h < nc.length(); h++)
							value.add(t);
					}
				}
			}
		}

		if (totalColumn == 4) {
			int alreadyHave1 = t - Integer.parseInt(String.valueOf(nc.charAt(0)));
			int alreadyHave2 = t - Integer.parseInt(String.valueOf(nc.charAt(1)));
			int alreadyHave3 = t - Integer.parseInt(String.valueOf(nc.charAt(2)));
			int alreadyHave4 = t - Integer.parseInt(String.valueOf(nc.charAt(3)));
			int[] column1 = generateColumnState(alreadyHave1, gn);
			int[] column2 = generateColumnState(alreadyHave2, gn);
			int[] column3 = generateColumnState(alreadyHave3, gn);
			int[] column4 = generateColumnState(alreadyHave4, gn);
			int columnStateSize1 = column1.length;
			int columnStateSize2 = column2.length;
			int columnStateSize3 = column3.length;
			int columnStateSize4 = column4.length;
			for (int i = 0; i < columnStateSize1; i++) {
				int startPosition2;
				if (alreadyHave2 == alreadyHave1)
					startPosition2 = i;
				else
					startPosition2 = 0;
				for (int ii = startPosition2; ii < columnStateSize2; ii++) {
					int startPosition3;
					if (alreadyHave3 == alreadyHave2)
						startPosition3 = ii;
					else
						startPosition3 = 0;
					for (int j = startPosition3; j < columnStateSize3; j++) {
						int startPosition4;
						if (alreadyHave4 == alreadyHave3)
							startPosition4 = j;
						else
							startPosition4 = 0;
						for (int jj = startPosition4; jj < columnStateSize4; jj++) {
							value.add(column1[i]);
							value.add(column2[ii]);
							value.add(column3[j]);
							value.add(column4[jj]);
							for (int h = 4; h < nc.length(); h++)
								value.add(t);
						}
					}
				}
			}
		}

		if (totalColumn == 5) {
			int alreadyHave1 = t - Integer.parseInt(String.valueOf(nc.charAt(0)));
			int alreadyHave2 = t - Integer.parseInt(String.valueOf(nc.charAt(1)));
			int alreadyHave3 = t - Integer.parseInt(String.valueOf(nc.charAt(2)));
			int alreadyHave4 = t - Integer.parseInt(String.valueOf(nc.charAt(3)));
			int alreadyHave5 = t - Integer.parseInt(String.valueOf(nc.charAt(4)));
			int[] column1 = generateColumnState(alreadyHave1, gn);
			int[] column2 = generateColumnState(alreadyHave2, gn);
			int[] column3 = generateColumnState(alreadyHave3, gn);
			int[] column4 = generateColumnState(alreadyHave4, gn);
			int[] column5 = generateColumnState(alreadyHave5, gn);
			int columnStateSize1 = column1.length;
			int columnStateSize2 = column2.length;
			int columnStateSize3 = column3.length;
			int columnStateSize4 = column4.length;
			int columnStateSize5 = column5.length;
			for (int i = 0; i < columnStateSize1; i++) {
				int startPosition2;
				if (alreadyHave2 == alreadyHave1)
					startPosition2 = i;
				else
					startPosition2 = 0;
				for (int ii = startPosition2; ii < columnStateSize2; ii++) {
					int startPosition3;
					if (alreadyHave3 == alreadyHave2)
						startPosition3 = ii;
					else
						startPosition3 = 0;
					for (int j = startPosition3; j < columnStateSize3; j++) {
						int startPosition4;
						if (alreadyHave4 == alreadyHave3)
							startPosition4 = j;
						else
							startPosition4 = 0;
						for (int jj = startPosition4; jj < columnStateSize4; jj++) {
							int startPosition5;
							if (alreadyHave5 == alreadyHave4)
								startPosition5 = jj;
							else
								startPosition5 = 0;
							for (int k = startPosition5; k < columnStateSize5; k++) {
								value.add(column1[i]);
								value.add(column2[ii]);
								value.add(column3[j]);
								value.add(column4[jj]);
								value.add(column5[k]);
								for (int h = 5; h < nc.length(); h++)
									value.add(t);
							}
						}
					}
				}
			}
		}

		if (totalColumn == 6) {
			int alreadyHave1 = t - Integer.parseInt(String.valueOf(nc.charAt(0)));
			int alreadyHave2 = t - Integer.parseInt(String.valueOf(nc.charAt(1)));
			int alreadyHave3 = t - Integer.parseInt(String.valueOf(nc.charAt(2)));
			int alreadyHave4 = t - Integer.parseInt(String.valueOf(nc.charAt(3)));
			int alreadyHave5 = t - Integer.parseInt(String.valueOf(nc.charAt(4)));
			int alreadyHave6 = t - Integer.parseInt(String.valueOf(nc.charAt(5)));
			int[] column1 = generateColumnState(alreadyHave1, gn);
			int[] column2 = generateColumnState(alreadyHave2, gn);
			int[] column3 = generateColumnState(alreadyHave3, gn);
			int[] column4 = generateColumnState(alreadyHave4, gn);
			int[] column5 = generateColumnState(alreadyHave5, gn);
			int[] column6 = generateColumnState(alreadyHave6, gn);
			int columnStateSize1 = column1.length;
			int columnStateSize2 = column2.length;
			int columnStateSize3 = column3.length;
			int columnStateSize4 = column4.length;
			int columnStateSize5 = column5.length;
			int columnStateSize6 = column6.length;
			for (int i = 0; i < columnStateSize1; i++) {
				int startPosition2;
				if (alreadyHave2 == alreadyHave1)
					startPosition2 = i;
				else
					startPosition2 = 0;
				for (int ii = startPosition2; ii < columnStateSize2; ii++) {
					int startPosition3;
					if (alreadyHave3 == alreadyHave2)
						startPosition3 = ii;
					else
						startPosition3 = 0;
					for (int j = startPosition3; j < columnStateSize3; j++) {
						int startPosition4;
						if (alreadyHave4 == alreadyHave3)
							startPosition4 = j;
						else
							startPosition4 = 0;
						for (int jj = startPosition4; jj < columnStateSize4; jj++) {
							int startPosition5;
							if (alreadyHave5 == alreadyHave4)
								startPosition5 = jj;
							else
								startPosition5 = 0;
							for (int k = startPosition5; k < columnStateSize5; k++) {
								int startPosition6;
								if (alreadyHave6 == alreadyHave5)
									startPosition6 = k;
								else
									startPosition6 = 0;
								for (int kk = startPosition6; kk < columnStateSize6; kk++) {
									value.add(column1[i]);
									value.add(column2[ii]);
									value.add(column3[j]);
									value.add(column4[jj]);
									value.add(column5[k]);
									value.add(column6[kk]);
									for (int h = 6; h < nc.length(); h++)
										value.add(t);
								}
							}
						}
					}
				}
			}
		}

		if (totalColumn == 7) {
			int alreadyHave1 = t - Integer.parseInt(String.valueOf(nc.charAt(0)));
			int alreadyHave2 = t - Integer.parseInt(String.valueOf(nc.charAt(1)));
			int alreadyHave3 = t - Integer.parseInt(String.valueOf(nc.charAt(2)));
			int alreadyHave4 = t - Integer.parseInt(String.valueOf(nc.charAt(3)));
			int alreadyHave5 = t - Integer.parseInt(String.valueOf(nc.charAt(4)));
			int alreadyHave6 = t - Integer.parseInt(String.valueOf(nc.charAt(5)));
			int alreadyHave7 = t - Integer.parseInt(String.valueOf(nc.charAt(6)));
			int[] column1 = generateColumnState(alreadyHave1, gn);
			int[] column2 = generateColumnState(alreadyHave2, gn);
			int[] column3 = generateColumnState(alreadyHave3, gn);
			int[] column4 = generateColumnState(alreadyHave4, gn);
			int[] column5 = generateColumnState(alreadyHave5, gn);
			int[] column6 = generateColumnState(alreadyHave6, gn);
			int[] column7 = generateColumnState(alreadyHave7, gn);
			int columnStateSize1 = column1.length;
			int columnStateSize2 = column2.length;
			int columnStateSize3 = column3.length;
			int columnStateSize4 = column4.length;
			int columnStateSize5 = column5.length;
			int columnStateSize6 = column6.length;
			int columnStateSize7 = column7.length;
			for (int i = 0; i < columnStateSize1; i++) {
				int startPosition2;
				if (alreadyHave2 == alreadyHave1)
					startPosition2 = i;
				else
					startPosition2 = 0;
				for (int ii = startPosition2; ii < columnStateSize2; ii++) {
					int startPosition3;
					if (alreadyHave3 == alreadyHave2)
						startPosition3 = ii;
					else
						startPosition3 = 0;
					for (int j = startPosition3; j < columnStateSize3; j++) {
						int startPosition4;
						if (alreadyHave4 == alreadyHave3)
							startPosition4 = j;
						else
							startPosition4 = 0;
						for (int jj = startPosition4; jj < columnStateSize4; jj++) {
							int startPosition5;
							if (alreadyHave5 == alreadyHave4)
								startPosition5 = jj;
							else
								startPosition5 = 0;
							for (int k = startPosition5; k < columnStateSize5; k++) {
								int startPosition6;
								if (alreadyHave6 == alreadyHave5)
									startPosition6 = k;
								else
									startPosition6 = 0;
								for (int kk = startPosition6; kk < columnStateSize6; kk++) {
									int startPosition7;
									if (alreadyHave7 == alreadyHave6)
										startPosition7 = kk;
									else
										startPosition7 = 0;
									for (int m = startPosition7; m < columnStateSize7; m++) {
										value.add(column1[i]);
										value.add(column2[ii]);
										value.add(column3[j]);
										value.add(column4[jj]);
										value.add(column5[k]);
										value.add(column6[kk]);
										value.add(column7[m]);

										for (int h = 7; h < nc.length(); h++)
											value.add(t);
									}
								}
							}
						}
					}
				}
			}
		}

		if (totalColumn == 8) {
			int alreadyHave1 = t - Integer.parseInt(String.valueOf(nc.charAt(0)));
			int alreadyHave2 = t - Integer.parseInt(String.valueOf(nc.charAt(1)));
			int alreadyHave3 = t - Integer.parseInt(String.valueOf(nc.charAt(2)));
			int alreadyHave4 = t - Integer.parseInt(String.valueOf(nc.charAt(3)));
			int alreadyHave5 = t - Integer.parseInt(String.valueOf(nc.charAt(4)));
			int alreadyHave6 = t - Integer.parseInt(String.valueOf(nc.charAt(5)));
			int alreadyHave7 = t - Integer.parseInt(String.valueOf(nc.charAt(6)));
			int alreadyHave8 = t - Integer.parseInt(String.valueOf(nc.charAt(7)));
			int[] column1 = generateColumnState(alreadyHave1, gn);
			int[] column2 = generateColumnState(alreadyHave2, gn);
			int[] column3 = generateColumnState(alreadyHave3, gn);
			int[] column4 = generateColumnState(alreadyHave4, gn);
			int[] column5 = generateColumnState(alreadyHave5, gn);
			int[] column6 = generateColumnState(alreadyHave6, gn);
			int[] column7 = generateColumnState(alreadyHave7, gn);
			int[] column8 = generateColumnState(alreadyHave8, gn);
			int columnStateSize1 = column1.length;
			int columnStateSize2 = column2.length;
			int columnStateSize3 = column3.length;
			int columnStateSize4 = column4.length;
			int columnStateSize5 = column5.length;
			int columnStateSize6 = column6.length;
			int columnStateSize7 = column7.length;
			int columnStateSize8 = column8.length;
			for (int i = 0; i < columnStateSize1; i++) {
				int startPosition2;
				if (alreadyHave2 == alreadyHave1)
					startPosition2 = i;
				else
					startPosition2 = 0;
				for (int ii = startPosition2; ii < columnStateSize2; ii++) {
					int startPosition3;
					if (alreadyHave3 == alreadyHave2)
						startPosition3 = ii;
					else
						startPosition3 = 0;
					for (int j = startPosition3; j < columnStateSize3; j++) {
						int startPosition4;
						if (alreadyHave4 == alreadyHave3)
							startPosition4 = j;
						else
							startPosition4 = 0;
						for (int jj = startPosition4; jj < columnStateSize4; jj++) {
							int startPosition5;
							if (alreadyHave5 == alreadyHave4)
								startPosition5 = jj;
							else
								startPosition5 = 0;
							for (int k = startPosition5; k < columnStateSize5; k++) {
								int startPosition6;
								if (alreadyHave6 == alreadyHave5)
									startPosition6 = k;
								else
									startPosition6 = 0;
								for (int kk = startPosition6; kk < columnStateSize6; kk++) {
									int startPosition7;
									if (alreadyHave7 == alreadyHave6)
										startPosition7 = kk;
									else
										startPosition7 = 0;
									for (int m = startPosition7; m < columnStateSize7; m++) {
										int startPosition8;
										if (alreadyHave8 == alreadyHave7)
											startPosition8 = m;
										else
											startPosition8 = 0;
										for (int mm = startPosition8; mm < columnStateSize8; mm++) {
											value.add(column1[i]);
											value.add(column2[ii]);
											value.add(column3[j]);
											value.add(column4[jj]);
											value.add(column5[k]);
											value.add(column6[kk]);
											value.add(column7[m]);
											value.add(column8[mm]);
											for (int h = 8; h < nc.length(); h++)
												value.add(t);
										}
									}
								}
							}
						}
					}
				}
			}
		}

		int[][] sc = new int[value.size() / nc.length()][nc.length()];
		for (int i = 0; i < value.size() / nc.length(); i++) {
			for (int j = 0; j < nc.length(); j++) {
				sc[i][j] = value.get(i * nc.length() + j); // 把值取出来
			}
		}
		return sc;
	}

	int[] generateColumnState(int columnNumber, int[] gn) {
		ArrayList<Integer> value = new ArrayList<Integer>();

		if (gn.length == 2) {
			// 第一种重量权重的集装箱的个数
			for (int i = columnNumber; i >= 0; i--)
				// 第二种重量权重的集装箱的个数
				for (int ii = columnNumber; ii >= 0; ii--) {
					if (i + ii == columnNumber) {
						value.add(i * gn[0] + ii * gn[1]);

					}
				}
		}
		if (gn.length == 3) {
			// 第一种重量权重的集装箱的个数
			for (int i = columnNumber; i >= 0; i--)
				// 第二种重量权重的集装箱的个数
				for (int ii = columnNumber; ii >= 0; ii--)
					// 第三种重量权重的集装箱的个数
					for (int j = columnNumber; j >= 0; j--) {
						if (i + ii + j == columnNumber) {
							value.add(i * gn[0] + ii * gn[1] + j * gn[2]);

						}
					}
		}
		if (gn.length == 4) {
			for (int i = columnNumber; i >= 0; i--)
				for (int ii = columnNumber; ii >= 0; ii--)
					for (int j = columnNumber; j >= 0; j--)
						for (int jj = columnNumber; jj >= 0; jj--) {
							if (i + ii + j + jj == columnNumber) {
								value.add(i * gn[0] + ii * gn[1] + j * gn[2] + jj * gn[3]);
							}
						}
		}

		int[] result = new int[value.size()];
		for (int i = 0; i < value.size(); i++)
			result[i] = value.get(i);

		return result;

	}

	void executeDynamicProgram(int s, int t, int[] gn, double[] ratio, boolean input) {
		// s: the number of stacks
		// t: the number of tiers
		// g: the number of weight groups

		// 被移走的集装箱的个数，即贝上空的堆垛位置个数
		int n = 0;
		String[] ncPrevious;
		int[][] scPrevious;
		double[] vaPrevious;
		int tnPrevious;

		tnPrevious = 1;
		ncPrevious = new String[tnPrevious];
		scPrevious = new int[tnPrevious][s];
		vaPrevious = new double[tnPrevious];

		// 赋初始值
		String temp = "";
		for (int j = 0; j < s; j++)
			temp = temp + "0";
		ncPrevious[0] = temp;
		for (int j = 0; j < s; j++)
			scPrevious[0][j] = t;
		vaPrevious[0] = 0.0;
		stateSize = 0;

		MyProperties myProperties = new MyProperties();
		try {
			String path = myProperties.getproperties("path");
			FileWriter fileWriter = new FileWriter(path + "store_value" + String.valueOf(s) + String.valueOf(t)
					+ String.valueOf(gn.length) + ".txt", false);
			PrintWriter printWriter = new PrintWriter(fileWriter);

			while (n < s * t) {
				n = n + 1;

				// 空位置数=n的各种组合
				String[] nc = calculateEmptyPermutation(s, t, n);
				ArrayList values = new ArrayList();

				for (int i = 0; i < nc.length; i++) {
					int[][] sc = calculateWeightPermutation(nc[i], t, gn);// 生成对应的重量权重组合

					for (int j = 0; j < sc.length; j++) {
						values.add(nc[i]);
						for (int k = 0; k < sc[j].length; k++)
							values.add(sc[j][k]);
						double finalValue = calculateObjectiveValue(t, nc[i], sc[j], gn, ncPrevious, scPrevious,
								vaPrevious, ratio);
						values.add(finalValue);

						if (n == s * t)
							finalObjectiveValue = finalValue;

						// 把生成的结果写到文件里
						String scTemp = "";
						for (int m = 0; m < s; m++)
							scTemp = scTemp + sc[j][m];
						printWriter.print(nc[i] + scTemp + "\t");
						for (int k = 0; k < gn.length; k++) {
							String followingStateWeightTemp = "";
							for (int m = 0; m < s; m++)
								followingStateWeightTemp = followingStateWeightTemp + followingStateWeight[k][m];
							printWriter.print(followingStateNumber[k] + followingStateWeightTemp + "\t");
							printWriter.print(stateNumberChosen[k] + "\t");
							printWriter.print(stateWeightChosen[k] + "\t");
						}
						printWriter.println();
					}
				}

				tnPrevious = values.size() / (s + 2); // 这一阶段生成的状态总数（数量状态＋重量状态的组合）
				stateSize = stateSize + tnPrevious; // 总的状态总数

				// 更新这一阶段的值
				ncPrevious = new String[tnPrevious];
				scPrevious = new int[tnPrevious][s];
				vaPrevious = new double[tnPrevious];
				for (int i = 0; i < tnPrevious; i++) {
					ncPrevious[i] = (String) values.get(i * (s + 2));
					for (int k = 1; k <= s; k++)
						scPrevious[i][k - 1] = (Integer) values.get(i * (s + 2) + k);
					vaPrevious[i] = (Double) values.get(i * (s + 2) + s + 1);
				}

				for (int i = 0; i < ncPrevious.length; i++) {
					System.out.print(i + "  " + ncPrevious[i] + " ");
					for (int k = 0; k < s; k++)
						System.out.print(scPrevious[i][k] + " ");
					System.out.println(vaPrevious[i]);
				}

			}
			fileWriter.close();
			printWriter.close();
		} catch (IOException f) {
			DialogBox dialogBox = new DialogBox();
			dialogBox.createDialogBox("error", f.toString());
		}

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
				+ String.valueOf(gn.length) + ".txt", s, gn);

		String[][] arrivingContainerPermuations = evaluationPreparation.generateArrivaingContainerPermutation(s, t, gn);
		arrivingContainerPermutationSize = arrivingContainerPermuations.length;
		int[] totalRehandle = new int[arrivingContainerPermuations.length];
		for (int i = 0; i < totalRehandle.length; i++)
			totalRehandle[i] = 0;
		int finalTotalRehandle = 0;

		long beginTime8, endTime8, duration8;
		Date myDate3 = new Date();
		beginTime8 = myDate3.getTime();
		// incur the expected_addition routine.
		double[][][] expectedAdditionalRehandleTimes = calculateExpectedAdditionalRehandle(s, t);
		for (int i = 0; i < arrivingContainerPermuations.length; i++) {
			// if(i==35){
			String[] target = arrivingContainerPermuations[i];
			int[][] bayState = new int[s][2]; // used to describe the state of
												// the bay
			for (int k = 0; k < s; k++) {
				bayState[k][0] = t; // initialize bay_state
				bayState[k][1] = 0;
			}
			int[][] assignedYardLocation = new int[target.length][4]; // used to
																		// describe
			// the positions of the containers
			String currentNc = "";
			String currentSc = "";
			// the initial state
			for (int k = 0; k < s; k++) {
				currentNc = currentNc + String.valueOf(t);
				currentSc = currentSc + String.valueOf(0);
			}
			String currentNcScTemp = currentNc + currentSc;
			int startSearchingIndexInDynamicProgrammingResults = evaluationPreparation.dynamicProgrammingResultSize - 1;

			// 根据动态规划的结果分配场地位置
			assignLocationAccordingToDPResults(s, t, gn, evaluationPreparation, target, bayState, assignedYardLocation,
					currentNcScTemp, startSearchingIndexInDynamicProgrammingResults);

			// count the number of containers for each kind of weight group
			int[] numberOfEachWeight = new int[gn.length];
			for (int h = 0; h < gn.length; h++)
				numberOfEachWeight[h] = 0;
			for (int j = 0; j < target.length; j++) {
				for (int h = 0; h < gn.length; h++) {
					if (target[j].equals(gn[h]))
						numberOfEachWeight[h] = numberOfEachWeight[h] + 1;
				}
			}
			for (int h = 1; h < gn.length; h++)
				numberOfEachWeight[h] = numberOfEachWeight[h] + numberOfEachWeight[h - 1];
			for (int h = gn.length - 1; h > 0; h--)
				numberOfEachWeight[h] = numberOfEachWeight[h - 1] + 1;
			numberOfEachWeight[0] = 1;

			for (int h = 0; h < t; h++)
				for (int k = 0; k < s; k++)
					for (int j = 0; j < target.length; j++) {
						if (assignedYardLocation[j][1] == k && assignedYardLocation[j][2] == h + 1) {
							int weightIndex = 0;
							for (int v = 0; v < gn.length; v++)
								if (String.valueOf(assignedYardLocation[j][0]).equals(gn[v]))
									weightIndex = v;
							assignedYardLocation[j][3] = numberOfEachWeight[weightIndex];
							numberOfEachWeight[weightIndex] = numberOfEachWeight[weightIndex] + 1;
						}

					}
			totalRehandle[i] = calculateRehandleTimes(s, t, assignedYardLocation, expectedAdditionalRehandleTimes);
			finalTotalRehandle = finalTotalRehandle + totalRehandle[i];
		}

		Date myDate4 = new Date();
		endTime8 = myDate4.getTime();
		duration8 = endTime8 - beginTime8;
		System.out.println("total rehandle times for the dynamic indicator:   " + finalTotalRehandle);
		totalDynamicRehandleTimes = finalTotalRehandle;
		MyProperties myProperties = new MyProperties();
		try {
			String path = myProperties.getproperties("path");
			FileWriter fileWriter = new FileWriter(path + "indictor_total_rehandel_times" + String.valueOf(s)
					+ String.valueOf(t) + String.valueOf(gn.length) + ".txt", false);
			PrintWriter printWriter = new PrintWriter(fileWriter);
			printWriter.println(s + "\t" + t + "\t" + arrivingContainerPermuations.length + "\t" + finalTotalRehandle
					+ "\t" + duration8 + "\t");
			fileWriter.close();
			printWriter.close();
		} catch (IOException f) {
			DialogBox dialogBox = new DialogBox();
			dialogBox.createDialogBox("error", f.toString());
		}
	}

	int calculateRehandleTimes(int s, int t, int[][] assignedYardLocation, double[][][] expectedAdditionalRehandleTimes) {
		// The number of realized relocations which the objective.
		int rehandleTimes = 0;

		int[][] yardLocation = new int[assignedYardLocation.length][4];
		for (int i = 0; i < assignedYardLocation.length; i++)
			for (int j = 0; j < 4; j++)
				yardLocation[i][j] = assignedYardLocation[i][j];

		// The state of the bay.
		int[][] bayStateSimulation = new int[s][2];
		for (int i = 0; i < s; i++) {
			bayStateSimulation[i][0] = 1;
			bayStateSimulation[i][1] = assignedYardLocation.length;
		}
		for (int i = 0; i < assignedYardLocation.length; i++) {
			for (int j = 0; j < s; j++) {
				if (assignedYardLocation[i][1] == j && bayStateSimulation[j][1] > assignedYardLocation[i][3])
					bayStateSimulation[j][1] = assignedYardLocation[i][3];
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

// using a static indicator
class StaticEvaluation extends BasicOperation {
	int calculateRehandleTimes(int[][] input) {
		int rehandleTimes = 0;
		for (int i = 0; i < input.length; i++) {
			int currentContainer = input[i][0];
			boolean isRehandledAlready = false;
			for (int j = 0; j < input.length; j++) {
				if (currentContainer < input[j][0] && !isRehandledAlready)
					if (input[i][1] == input[j][1] && input[i][2] < input[j][2]) {
						// the jth and ith containers are in the same stack and
						// the ith one is stacked above the jth one.
						// the smaller value of input[i][2], the higher tier
						// 根据kang's method 计算的翻到下限值
						rehandleTimes = rehandleTimes + 1;
						isRehandledAlready = true;

					}
			}
		}
		return rehandleTimes;
	}

	void evaluateTotalRehandleTimes(int s, int t, String[] gn) {
		EvalutionPreparation evaluationPreparation = new EvalutionPreparation();
		evaluationPreparation.importDynamicProgrammingResults("store_value" + String.valueOf(s) + String.valueOf(t)
				+ String.valueOf(gn.length) + ".txt", s, gn);

		String[][] arrivingContainerPermutaions = evaluationPreparation.generateArrivaingContainerPermutation(s, t, gn);
		arrivingContainerPermutationSize = arrivingContainerPermutaions.length;
		int[] rehandleTimes = new int[arrivingContainerPermutaions.length];
		for (int i = 0; i < rehandleTimes.length; i++)
			rehandleTimes[i] = 0;
		int finalTotalRehandleTimes = 0;

		long beginTime8, endTime8, duration8;
		Date myDate3 = new Date();
		beginTime8 = myDate3.getTime();
		for (int i = 0; i < arrivingContainerPermutaions.length; i++) {
			// if(i==35){
			String[] target = arrivingContainerPermutaions[i];

			// 记录真实的贝的状态
			int[][] bayState = new int[s][2];
			// initialize bay_state
			for (int k = 0; k < s; k++) {
				bayState[k][0] = t;
				bayState[k][1] = 0;
			}

			// 记录每一个到达集装箱所分配的位置
			int[][] assignedYardLocation = new int[target.length][3];

			// 抽象出来的贝的状态
			String currentNc = "";
			String currentSc = "";
			// the initial state
			for (int k = 0; k < s; k++) {
				currentNc = currentNc + String.valueOf(t);
				currentSc = currentSc + String.valueOf(0);
			}

			String currentNcScTemp = currentNc + currentSc;
			int startSearchingIndexInDynamicProgrammingResults = evaluationPreparation.dynamicProgrammingResultSize - 1;

			assignLocationAccordingToDPResults(s, t, gn, evaluationPreparation, target, bayState, assignedYardLocation,
					currentNcScTemp, startSearchingIndexInDynamicProgrammingResults);

			rehandleTimes[i] = calculateRehandleTimes(assignedYardLocation);
			finalTotalRehandleTimes = finalTotalRehandleTimes + rehandleTimes[i];
		}

		Date myDate4 = new Date();
		endTime8 = myDate4.getTime();
		duration8 = endTime8 - beginTime8;
		System.out.println("total rehandle times:   " + finalTotalRehandleTimes);
		totalStaticRehandleTimes = finalTotalRehandleTimes;
		MyProperties myProperties = new MyProperties();
		try {
			String path = myProperties.getproperties("path");
			FileWriter fileWriter = new FileWriter(path + "total_rehandel_times" + String.valueOf(s)
					+ String.valueOf(t) + String.valueOf(gn.length) + ".txt", false);
			PrintWriter printWrite = new PrintWriter(fileWriter);
			printWrite.println(s + "\t" + t + "\t" + arrivingContainerPermutaions.length + "\t"
					+ finalTotalRehandleTimes + "\t" + duration8 + "\t");
			fileWriter.close();
			printWrite.close();
		} catch (IOException f) {
			DialogBox dialogBox = new DialogBox();
			dialogBox.createDialogBox("error", f.toString());
		}
	}

}

class EvalutionPreparation {
	String[][] currentState;
	String[][] followingState;
	int[][] stateNumberChosen;
	int[][] stateWeightChosen;
	int dynamicProgrammingResultSize;

	void importDynamicProgrammingResults(String fileName, int s, String[] gn) {
		MyProperties myProperties = new MyProperties();
		String path = myProperties.getproperties("path");

		Input input = new Input();
		String[][] dataArray = input.readData(path + fileName);

		dynamicProgrammingResultSize = dataArray.length;
		currentState = new String[dataArray.length][1];
		followingState = new String[dataArray.length][gn.length];
		stateNumberChosen = new int[dataArray.length][gn.length];
		stateWeightChosen = new int[dataArray.length][gn.length];

		for (int i = 0; i < dataArray.length; i++) {
			currentState[i][0] = dataArray[i][0];
			for (int k = 0; k < gn.length; k++) {
				followingState[i][k] = dataArray[i][3 * k + 1];
				stateNumberChosen[i][k] = Integer.valueOf(dataArray[i][3 * k + 2]);
				stateWeightChosen[i][k] = Integer.valueOf(dataArray[i][3 * k + 3]);
			}
		}
	}

	String[][] generateArrivaingContainerPermutation(int s, int t, String[] gn) {
		int accumulation = 1;
		for (int i = 0; i < s * t; i++) {
			accumulation = accumulation * gn.length;
			if (accumulation > 1000000) {
				accumulation = 1000000;
				break;
			}
		}

		String[][] permutation = new String[accumulation][s * t];
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
																																permutation[index][0] = gn[i];
																																permutation[index][1] = gn[i1];
																																permutation[index][2] = gn[i2];
																																permutation[index][3] = gn[i3];
																																permutation[index][4] = gn[i4];
																																permutation[index][5] = gn[i5];
																																permutation[index][6] = gn[i6];
																																permutation[index][7] = gn[i7];
																																permutation[index][8] = gn[i8];
																																permutation[index][9] = gn[i9];
																																permutation[index][10] = gn[j];
																																permutation[index][11] = gn[j1];
																																permutation[index][12] = gn[j2];
																																permutation[index][13] = gn[j3];
																																permutation[index][14] = gn[j4];
																																permutation[index][15] = gn[j5];
																																permutation[index][16] = gn[j6];
																																permutation[index][17] = gn[j7];
																																permutation[index][18] = gn[j8];
																																permutation[index][19] = gn[j9];
																																permutation[index][20] = gn[k];
																																permutation[index][21] = gn[k1];
																																permutation[index][22] = gn[k2];
																																permutation[index][23] = gn[k3];
																																permutation[index][24] = gn[k4];
																																permutation[index][25] = gn[k5];
																																permutation[index][26] = gn[k6];
																																permutation[index][27] = gn[k7];

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
																												permutation[index][0] = gn[i];
																												permutation[index][1] = gn[i1];
																												permutation[index][2] = gn[i2];
																												permutation[index][3] = gn[i3];
																												permutation[index][4] = gn[i4];
																												permutation[index][5] = gn[i5];
																												permutation[index][6] = gn[i6];
																												permutation[index][7] = gn[i7];
																												permutation[index][8] = gn[i8];
																												permutation[index][9] = gn[i9];
																												permutation[index][10] = gn[j];
																												permutation[index][11] = gn[j1];
																												permutation[index][12] = gn[j2];
																												permutation[index][13] = gn[j3];
																												permutation[index][14] = gn[j4];
																												permutation[index][15] = gn[j5];
																												permutation[index][16] = gn[j6];
																												permutation[index][17] = gn[j7];
																												permutation[index][18] = gn[j8];
																												permutation[index][19] = gn[j9];
																												permutation[index][20] = gn[k];
																												permutation[index][21] = gn[k1];
																												permutation[index][22] = gn[k2];
																												permutation[index][23] = gn[k3];
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
																									permutation[index][0] = gn[i];
																									permutation[index][1] = gn[i1];
																									permutation[index][2] = gn[i2];
																									permutation[index][3] = gn[i3];
																									permutation[index][4] = gn[i4];
																									permutation[index][5] = gn[i5];
																									permutation[index][6] = gn[i6];
																									permutation[index][7] = gn[i7];
																									permutation[index][8] = gn[i8];
																									permutation[index][9] = gn[i9];
																									permutation[index][10] = gn[j];
																									permutation[index][11] = gn[j1];
																									permutation[index][12] = gn[j2];
																									permutation[index][13] = gn[j3];
																									permutation[index][14] = gn[j4];
																									permutation[index][15] = gn[j5];
																									permutation[index][16] = gn[j6];
																									permutation[index][17] = gn[j7];
																									permutation[index][18] = gn[j8];
																									permutation[index][19] = gn[j9];
																									permutation[index][20] = gn[k];
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
																								permutation[index][0] = gn[i];
																								permutation[index][1] = gn[i1];
																								permutation[index][2] = gn[i2];
																								permutation[index][3] = gn[i3];
																								permutation[index][4] = gn[i4];
																								permutation[index][5] = gn[i5];
																								permutation[index][6] = gn[i6];
																								permutation[index][7] = gn[i7];
																								permutation[index][8] = gn[i8];
																								permutation[index][9] = gn[i9];
																								permutation[index][10] = gn[j];
																								permutation[index][11] = gn[j1];
																								permutation[index][12] = gn[j2];
																								permutation[index][13] = gn[j3];
																								permutation[index][14] = gn[j4];
																								permutation[index][15] = gn[j5];
																								permutation[index][16] = gn[j6];
																								permutation[index][17] = gn[j7];
																								permutation[index][18] = gn[j8];
																								permutation[index][19] = gn[j9];
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
																						permutation[index][0] = gn[i];
																						permutation[index][1] = gn[i1];
																						permutation[index][2] = gn[i2];
																						permutation[index][3] = gn[i3];
																						permutation[index][4] = gn[i4];
																						permutation[index][5] = gn[i5];
																						permutation[index][6] = gn[i6];
																						permutation[index][7] = gn[i7];
																						permutation[index][8] = gn[i8];
																						permutation[index][9] = gn[i9];
																						permutation[index][10] = gn[j];
																						permutation[index][11] = gn[j1];
																						permutation[index][12] = gn[j2];
																						permutation[index][13] = gn[j3];
																						permutation[index][14] = gn[j4];
																						permutation[index][15] = gn[j5];
																						permutation[index][16] = gn[j6];
																						permutation[index][17] = gn[j7];
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
																				permutation[index][0] = gn[i];
																				permutation[index][1] = gn[i1];
																				permutation[index][2] = gn[i2];
																				permutation[index][3] = gn[i3];
																				permutation[index][4] = gn[i4];
																				permutation[index][5] = gn[i5];
																				permutation[index][6] = gn[i6];
																				permutation[index][7] = gn[i7];
																				permutation[index][8] = gn[i8];
																				permutation[index][9] = gn[i9];
																				permutation[index][10] = gn[j];
																				permutation[index][11] = gn[j1];
																				permutation[index][12] = gn[j2];
																				permutation[index][13] = gn[j3];
																				permutation[index][14] = gn[j4];
																				permutation[index][15] = gn[j5];
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
																			permutation[index][0] = gn[i];
																			permutation[index][1] = gn[i1];
																			permutation[index][2] = gn[i2];
																			permutation[index][3] = gn[i3];
																			permutation[index][4] = gn[i4];
																			permutation[index][5] = gn[i5];
																			permutation[index][6] = gn[i6];
																			permutation[index][7] = gn[i7];
																			permutation[index][8] = gn[i8];
																			permutation[index][9] = gn[i9];
																			permutation[index][10] = gn[j];
																			permutation[index][11] = gn[j1];
																			permutation[index][12] = gn[j2];
																			permutation[index][13] = gn[j3];
																			permutation[index][14] = gn[j4];
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
																permutation[index][0] = gn[i];
																permutation[index][1] = gn[i1];
																permutation[index][2] = gn[i2];
																permutation[index][3] = gn[i3];
																permutation[index][4] = gn[i4];
																permutation[index][5] = gn[i5];
																permutation[index][6] = gn[i6];
																permutation[index][7] = gn[i7];
																permutation[index][8] = gn[i8];
																permutation[index][9] = gn[i9];
																permutation[index][10] = gn[j];
																permutation[index][11] = gn[j1];
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
													permutation[index][0] = gn[i];
													permutation[index][1] = gn[i1];
													permutation[index][2] = gn[i2];
													permutation[index][3] = gn[i3];
													permutation[index][4] = gn[i4];
													permutation[index][5] = gn[i5];
													permutation[index][6] = gn[i6];
													permutation[index][7] = gn[i7];
													permutation[index][8] = gn[i8];
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
							permutation[index][0] = gn[i];
							permutation[index][1] = gn[i1];
							permutation[index][2] = gn[i2];
							permutation[index][3] = gn[i3];
							index++;
						}

		return permutation;
	}
}

// .txt文件输入?
class Input {
	String[][] readData(String fileName) {
		int totalLine = 0;
		String line = null;
		int columnNumber = 0;
		// 先统计文件有多少?
		ArrayList<String> values = new ArrayList<String>();
		try {
			FileReader fileReader = new FileReader(fileName);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			boolean firstTime = true;
			while ((line = bufferedReader.readLine()) != null) {
				String[] ss = line.split("\t");
				for (int j = 0; j < ss.length; j++)
					values.add(ss[j]);
				if (firstTime) {
					columnNumber = line.split("\t").length;
					firstTime = false;
				}
				totalLine = totalLine + 1;
			}
			fileReader.close();
			bufferedReader.close();
		} catch (IOException ae) {
			DialogBox dialogBox = new DialogBox();
			dialogBox.createDialogBox("警告 ", "可能的原因是: 1.相应的文件没有找到；2." + ae.toString());
		}

		String[][] res = new String[totalLine][columnNumber];
		for (int i = 0; i < totalLine; i++)
			for (int j = 0; j < columnNumber; j++)
				res[i][j] = values.get(i * columnNumber + j);

		return res;
	}
}

public class NewLocationAssignment {

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		DynamicProgram dynamicProgram = new DynamicProgram();
		MyProperties myProperties = new MyProperties();

		int[] gn = { 10, 1 };
		double[] ratio = { 0.5, 0.5 };

		long beginTime, endTime, duration;
		try {
			String path = myProperties.getproperties("path");
			FileWriter fileWriter = new FileWriter(path + "final2.txt", false);
			PrintWriter printWriter = new PrintWriter(fileWriter);
			for (int s = 4; s < 8; s++)
				for (int t = 3; t < 5; t++) {
					// if(s*t<=24){
					Date myDate = new Date();
					beginTime = myDate.getTime();
					dynamicProgram.executeDynamicProgram(s, t, gn, ratio, true);
					Date myDate2 = new Date();
					endTime = myDate2.getTime();
					duration = endTime - beginTime;
					printWriter.println(s + "\t" + t + "\t" + 2 + "\t" + dynamicProgram.stateSize + "\t"
							+ dynamicProgram.finalObjectiveValue + "\t" + duration);
				}
			fileWriter.close();
			printWriter.close();
		} catch (IOException f) {
			DialogBox dialogBox = new DialogBox();
			dialogBox.createDialogBox("error", f.toString());
		}

		// using the static indicator to test the quality of the new model.
		long beginTime1, endTime1, duration1;
		try {
			String path = myProperties.getproperties("path");
			FileWriter fileWriter = new FileWriter(path + "evaluation2.txt", false);
			PrintWriter printWriter = new PrintWriter(fileWriter);
			for (int s = 4; s < 8; s++)
				for (int t = 3; t < 5; t++) {
					String[] gnSpecial = { "10", "1" };
					Date myDate = new Date();
					beginTime1 = myDate.getTime();
					StaticEvaluation staticEvaluation = new StaticEvaluation();
					staticEvaluation.evaluateTotalRehandleTimes(s, t, gnSpecial);
					Date myDate2 = new Date();
					endTime1 = myDate2.getTime();
					duration1 = endTime1 - beginTime1;
					printWriter.println(s + "\t" + t + "\t" + 2 + "\t"
							+ staticEvaluation.arrivingContainerPermutationSize + "\t"
							+ staticEvaluation.totalStaticRehandleTimes + "\t" + duration1);
				}
			fileWriter.close();
			printWriter.close();
		} catch (IOException f) {
			DialogBox dialogBox = new DialogBox();
			dialogBox.createDialogBox("error", f.toString());
		}

		long beginTime5, endTime5, duration5;
		try {
			String path = myProperties.getproperties("path");
			FileWriter fileWriter = new FileWriter(path + "dynamic2.txt", false);
			PrintWriter printWriter = new PrintWriter(fileWriter);
			for (int s = 4; s < 8; s++)
				for (int t = 3; t < 5; t++) {
					// if(s*t<=24){
					String[] gnSpecial = { "10", "1" };
					Date myDate = new Date();
					beginTime5 = myDate.getTime();
					DynamicEvaluation dynamicEvaluation = new DynamicEvaluation();
					dynamicEvaluation.evaluateTotalRehandleTimes(s, t, gnSpecial);
					Date myDate2 = new Date();
					endTime5 = myDate2.getTime();
					duration5 = endTime5 - beginTime5;
					printWriter.println(s + "\t" + t + "\t" + 2 + "\t"
							+ dynamicEvaluation.arrivingContainerPermutationSize + "\t"
							+ dynamicEvaluation.totalDynamicRehandleTimes + "\t" + duration5);
				}
			fileWriter.close();
			printWriter.close();
		} catch (IOException f) {
			DialogBox dialogBox = new DialogBox();
			dialogBox.createDialogBox("error", f.toString());
		}

		// for the case with three weight groups.
		// using dymanic programming to calculate the optimal locations.
		int[] gn2 = { 100, 10, 1 };
		double[] ratio2 = { 0.33, 0.33, 0.33 };

		long beginTime2, endTime2, duration2;
		try {
			String path = myProperties.getproperties("path");
			FileWriter fileWriter = new FileWriter(path + "final3.txt", false);
			PrintWriter printWriter = new PrintWriter(fileWriter);
			for (int s = 4; s < 8; s++)
				for (int t = 3; t < 5; t++) {
					// if(s*t==24){
					Date myDate = new Date();
					beginTime2 = myDate.getTime();
					dynamicProgram.executeDynamicProgram(s, t, gn2, ratio2, true);
					Date myDate2 = new Date();
					endTime2 = myDate2.getTime();
					duration2 = endTime2 - beginTime2;
					printWriter.println(s + "\t" + t + "\t" + 3 + "\t" + dynamicProgram.stateSize + "\t"
							+ dynamicProgram.finalObjectiveValue + "\t" + duration2);
				}
			fileWriter.close();
			printWriter.close();
		} catch (IOException f) {
			DialogBox dialogBox = new DialogBox();
			dialogBox.createDialogBox("error", f.toString());
		}

		// using the static indicator to test the quality of the new model.
		long beginTime3, endTime3, duration3;
		try {
			String path = myProperties.getproperties("path");
			FileWriter fileWriter = new FileWriter(path + "evaluation3.txt", false);
			PrintWriter printWriter = new PrintWriter(fileWriter);
			for (int s = 4; s < 8; s++)
				for (int t = 3; t < 5; t++) {
					// if(s*t<=24){
					String[] gnSpecial = { "100", "10", "1" };
					Date myDate = new Date();
					beginTime3 = myDate.getTime();
					StaticEvaluation evaluate = new StaticEvaluation();
					evaluate.evaluateTotalRehandleTimes(s, t, gnSpecial);
					Date myDate2 = new Date();
					endTime3 = myDate2.getTime();
					duration3 = endTime3 - beginTime3;
					printWriter.println(s + "\t" + t + "\t" + 3 + "\t" + evaluate.arrivingContainerPermutationSize
							+ "\t" + evaluate.totalStaticRehandleTimes + "\t" + duration3);
				}
			fileWriter.close();
			printWriter.close();
		} catch (IOException f) {
			DialogBox dialogBox = new DialogBox();
			dialogBox.createDialogBox("error", f.toString());
		}

		// using the dynamic indicator to test the quality of the new model.
		long beginTime4, endTime4, duration4;
		try {
			String path = myProperties.getproperties("path");
			FileWriter fileWriter = new FileWriter(path + "dynamic3.txt", false);
			PrintWriter printWriter = new PrintWriter(fileWriter);
			for (int s = 4; s < 8; s++)
				for (int t = 3; t < 5; t++) {
					// if(s*t<=24){
					String[] gnSpecial = { "100", "10", "1" };
					Date myDate = new Date();
					beginTime4 = myDate.getTime();
					DynamicEvaluation dynamicEvaluation = new DynamicEvaluation();
					dynamicEvaluation.evaluateTotalRehandleTimes(s, t, gnSpecial);
					Date myDate2 = new Date();
					endTime4 = myDate2.getTime();
					duration4 = endTime4 - beginTime4;
					printWriter.println(s + "\t" + t + "\t" + 3 + "\t"
							+ dynamicEvaluation.arrivingContainerPermutationSize + "\t"
							+ dynamicEvaluation.totalDynamicRehandleTimes + "\t" + duration4);
				}
			fileWriter.close();
			printWriter.close();
		} catch (IOException f) {
			DialogBox dialogBox = new DialogBox();
			dialogBox.createDialogBox("error", f.toString());
		}

	}

}
	startPosition3 = ii;
					else
						startPosition3 = 0;
					for (int j = startPosition3; j < columnStateSize3; j++) {
						int startPosition4;
						if (alreadyHave4 == alreadyHave3)
							startPosition4 = j;
						else
							startPosition4 = 0;
						for (int jj = startPosition4; jj < columnStateSize4; jj++) {
							int startPosition5;
							if (alreadyHave5 == alreadyHave4)
								startPosition5 = jj;
							else
								startPosition5 = 0;
							for (int k = startPosition5; k < columnStateSize5; k++) {
								int startPosition6;
								if (alreadyHave6 == alreadyHave5)
									startPosition6 = k;
								else
									startPosition6 = 0;
								for (int kk = startPosition6; kk < columnStateSize6; kk++) {
									int startPosition7;
									if (alreadyHave7 == alreadyHave6)
										startPosition7 = kk;
									else
										startPosition7 = 0;
									for (int m = startPosition7; m < columnStateSize7; m++) {
										int startPosition8;
										if (alreadyHave8 == alreadyHave7)
											startPosition8 = m;
										else
											startPosition8 = 0;
										for (int mm = startPosition8; mm < columnStateSize8; mm++) {
											value.add(column1[i]);
											value.add(column2[ii]);
											value.add(column3[j]);
											value.add(column4[jj]);
											value.add(column5[k]);
											value.add(column6[kk]);
											value.add(column7[m]);
											value.add(column8[mm]);
											for (int h = 8; h < nc.length(); h++)
												value.add(t);
										}
									}
								}
							}
						}
					}
				}
			}
		}

		int[][] sc = new int[value.size() / nc.length()][nc.length()];
		for (int i = 0; i < value.size() / nc.length(); i++) {
			for (int j = 0; j < nc.length(); j++) {
				sc[i][j] = value.get(i * nc.length() + j); // 把值取出来
			}
		}
		return sc;
	}

	int[] generateColumnState(int columnNumber, int[] gn) {
		ArrayList<Integer> value = new ArrayList<Integer>();

		if (gn.length == 2) {
			// 第一种重量权重的集装箱的个数
			for (int i = columnNumber; i >= 0; i--)
				// 第二种重量权重的集装箱的个数
				for (int ii = columnNumber; ii >= 0; ii--) {
					if (i + ii == columnNumber) {
						value.add(i * gn[0] + ii * gn[1]);

					}
				}
		}
		if (gn.length == 3) {
			// 第一种重量权重的集装箱的个数
			for (int i = columnNumber; i >= 0; i--)
				// 第二种重量权重的集装箱的个数
				for (int ii = columnNumber; ii >= 0; ii--)
					// 第三种重量权重的集装箱的个数
					for (int j = columnNumber; j >= 0; j--) {
						if (i + ii + j == columnNumber) {
							value.add(i * gn[0] + ii * gn[1] + j * gn[2]);

						}
					}
		}
		if (gn.length == 4) {
			for (int i = columnNumber; i >= 0; i--)
				for (int ii = columnNumber; ii >= 0; ii--)
					for (int j = columnNumber; j >= 0; j--)
						for (int jj = columnNumber; jj >= 0; jj--) {
							if (i + ii + j + jj == columnNumber) {
								value.add(i * gn[0] + ii * gn[1] + j * gn[2] + jj * gn[3]);
							}
						}
		}

		int[] result = new int[value.size()];
		for (int i = 0; i < value.size(); i++)
			result[i] = value.get(i);

		return result;

	}

	void executeDynamicProgram(int s, int t, int[] gn, double[] ratio, boolean input) {
		// s: the number of stacks
		// t: the number of tiers
		// g: the number of weight groups

		// 被移走的集装箱的个数，即贝上空的堆垛位置个数
		int n = 0;
		String[] ncPrevious;
		int[][] scPrevious;
		double[] vaPrevious;
		int tnPrevious;

		tnPrevious = 1;
		ncPrevious = new String[tnPrevious];
		scPrevious = new int[tnPrevious][s];
		vaPrevious = new double[tnPrevious];

		// 赋初始值
		String temp = "";
		for (int j = 0; j < s; j++)
			temp = temp + "0";
		ncPrevious[0] = temp;
		for (int j = 0; j < s; j++)
			scPrevious[0][j] = t;
		vaPrevious[0] = 0.0;
		stateSize = 0;

		MyProperties myProperties = new MyProperties();
		try {
			String path = myProperties.getproperties("path");
			FileWriter fileWriter = new FileWriter(path + "store_value" + String.valueOf(s) + String.valueOf(t)
					+ String.valueOf(gn.length) + ".txt", false);
			PrintWriter printWriter = new PrintWriter(fileWriter);

			while (n < s * t) {
				n = n + 1;

				// 空位置数=n的各种组合
				String[] nc = calculateEmptyPermutation(s, t, n);
				ArrayList values = new ArrayList();

				for (int i = 0; i < nc.length; i++) {
					int[][] sc = calculateWeightPermutation(nc[i], t, gn);// 生成对应的重量权重组合

					for (int j = 0; j < sc.length; j++) {
						values.add(nc[i]);
						for (int k = 0; k < sc[j].length; k++)
							values.add(sc[j][k]);
						double finalValue = calculateObjectiveValue(t, nc[i], sc[j], gn, ncPrevious, scPrevious,
								vaPrevious, ratio);
						values.add(finalValue);

						if (n == s * t)
							finalObjectiveValue