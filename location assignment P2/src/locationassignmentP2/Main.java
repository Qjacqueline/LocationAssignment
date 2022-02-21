/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package locationassignmentP2;

import java.util.Date;
import java.io.*;
import java.util.*;
import java.lang.*;
import javax.swing.*;

class dialogbox {
    void dialogbox(String title, String content) {
        JFrame frame = new JFrame();
        frame.setVisible(true);
        frame.setAlwaysOnTop(true);
        frame.setLocationRelativeTo(null);
        JOptionPane.showMessageDialog(frame, content, title,
                JOptionPane.ERROR_MESSAGE);
        frame.dispose();
    }
}

class myproperties {
    String getproperties(String properties) {
        //\u8BFB\u6587\u4EF6\u6240\u5728\u7684\u6839\u76EE\u5F55
        Properties properties1 = System.getProperties();
        String path = properties1.getProperty("user.dir");
        String rootpath = path.substring(0, 1);
        //\u914D\u7F6E\u6587\u4EF6\u5305\u62EC\u76EE\u5F55\u548C\u5C5E\u6027
        InputStream in;
        Properties p = new Properties();
        try {
            try {
                in = new BufferedInputStream(new FileInputStream(
                        rootpath +
                                "/Users/jacqueline/Code/location assignment/data new/config.properties"));
                p.load(in);
                String pro_path = p.getProperty("path");
                p.setProperty("path", rootpath + pro_path);
            } catch (FileNotFoundException e) {
                dialogbox dialog = new dialogbox();
                dialog.dialogbox("\u8B66\u544A",
                        "\u53EF\u80FD\u7684\u539F\u56E0\u662F\uFF1A1.config.properties\u6587\u4EF6\u4E0D\u5B58\u5728\uFF1B2." +
                                e.toString());
            }
        } catch (IOException e) {
            dialogbox dialog = new dialogbox();
            dialog.dialogbox("\u8B66\u544A ",
                    "\u53EF\u80FD\u7684\u539F\u56E0\u662F\uFF1A1.config.properties\u6587\u4EF6\u4E0D\u5B58\u5728\uFF1B2." +
                            e.toString());
        }
        return p.getProperty(properties);
    }
}

class Dynamic_programming {
    int state_size;
    double final_value;
    String[] next_state_number;
    int[][] next_state_weight;
    int[] next_weight_group;
    double[] next_value;

    int calculate_rehandle(int sc, int gn) {
        int rehandle = 0;
        if (gn == 1)
            rehandle = sc / 10000 + (sc % 10000) / 1000 + (sc % 1000) / 100 + (sc % 100) / 10;
        if (gn == 10)
            rehandle = sc / 10000 + (sc % 10000) / 1000 + (sc % 1000) / 100;
        if (gn == 100)
            rehandle = sc / 10000 + (sc % 10000) / 1000;
        if (gn == 1000)
            rehandle = sc / 10000;
        if (gn == 10000)
            rehandle = 0;
        return rehandle;
    }

    double calculate_value(int t, String nc, int[] sc, int[] gn, String[] nc_pre, int[][] sc_pre, double[] va_pre, double[] ratio) {
        next_state_number = new String[gn.length];
        next_state_weight = new int[gn.length][sc.length];
        next_weight_group = new int[gn.length];
        next_value = new double[gn.length];
        double[] objective_value = new double[gn.length];
        for (int i = 0; i < gn.length; i++)
            objective_value[i] = Double.MAX_VALUE;
        for (int i = 0; i < gn.length; i++) { //gn.length kinds of arrival
            for (int j = 0; j < nc.length(); j++) { //there are at most nc.length possible locations
                if (nc.charAt(j) > '0') {
                    String nc_temp = nc;
                    int[] sc_temp = new int[sc.length];
                    for (int k = 0; k < sc.length; k++)
                        sc_temp[k] = sc[k];
                    int rehandle_times = calculate_rehandle(sc_temp[j], gn[i]);
                    //adjust nc
                    String first_part = "";
                    String third_part = "";
                    String second_part = "";
                    if (j > 0)
                        first_part = nc_temp.substring(0, j - 1 - 0 + 1);
                    if (j < nc.length() - 1)
                        third_part = nc_temp.substring(j + 1, nc_temp.length() - 1 - (j + 1) + 1 + (j + 1));
                    int left_empty = Integer.parseInt(String.valueOf(nc_temp.charAt(j))) - 1;
                    second_part = String.valueOf(left_empty);
                    nc_temp = first_part + second_part + third_part;
                    //adjust sc
                    if (left_empty == 0) {
                        sc_temp[j] = t;
                    } else {
                        sc_temp[j] = sc_temp[j] + gn[i];
                    }

                    //re-sequence nc and sc
                    for (int k = 0; k < nc_temp.length() - 1; k++) {
                        int first = Integer.parseInt(String.valueOf(nc_temp.charAt(k)));
                        int second = Integer.parseInt(String.valueOf(nc_temp.charAt(k + 1)));
                        String first_part2 = "";
                        String fourth_part2 = "";
                        String second_part2 = "";
                        String third_part2 = "";
                        if (first < second) {
                            if (k > 0)
                                first_part2 = nc_temp.substring(0, k - 1 - 0 + 1);
                            if (k < nc.length() - 2)
                                fourth_part2 = nc_temp.substring(k + 2, nc_temp.length() - 1 - (k + 2) + 1 + (k + 2));
                            second_part2 = String.valueOf(second);
                            third_part2 = String.valueOf(first);
                            nc_temp = first_part2 + second_part2 + third_part2 + fourth_part2;
                            //re-sequence sc
                            int temp_sc = sc_temp[k + 1];
                            sc_temp[k + 1] = sc_temp[k];
                            sc_temp[k] = temp_sc;
                        }
                    }
                    //re-sequence sc
                    boolean needed = true;
                    while (needed) {
                        needed = false;
                        for (int k = 0; k < nc_temp.length() - 1; k++) {
                            int first = Integer.parseInt(String.valueOf(nc_temp.charAt(k)));
                            int second = Integer.parseInt(String.valueOf(nc_temp.charAt(k + 1)));
                            int first1 = sc_temp[k];
                            int second1 = sc_temp[k + 1];
                            if (first == second && first1 < second1) {
                                needed = true;
                                int temp_sc = sc_temp[k + 1];
                                sc_temp[k + 1] = sc_temp[k];
                                sc_temp[k] = temp_sc;
                            }
                        }
                    }

                    //match the states;
                    double following_value = 0.0;
                    for (int k = 0; k < nc_pre.length; k++) {
                        if (nc_temp.equals(nc_pre[k])) {
                            boolean match = true;
                            for (int m = 0; m < sc_temp.length; m++)
                                if (sc_temp[m] != sc_pre[k][m]) {
                                    match = false;
                                    break;
                                }
                            if (match) {
                                following_value = va_pre[k];
                                break;
                            }
                        }
                    }

                    //calculate objective value
                    double value_temp = following_value + rehandle_times;
                    if (objective_value[i] > value_temp) {
                        objective_value[i] = value_temp;
                        next_state_number[i] = nc_temp;
                        for (int m = 0; m < sc_temp.length; m++)
                            next_state_weight[i][m] = sc_temp[m];
                        next_weight_group[i] = gn[i];
                        next_value[i] = rehandle_times;
                    }
                }
            }
        }
        double total_objective_value = 0.0;
        for (int i = 0; i < objective_value.length; i++)
            total_objective_value = total_objective_value + objective_value[i] * ratio[i];
        return total_objective_value;
    }

    String[] solution_space(int s, int t, int n) {
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
                                result[index] = String.valueOf(i) + String.valueOf(ii) + String.valueOf(j) + String.valueOf(jj);
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
                                    result[index] = String.valueOf(i) + String.valueOf(ii) + String.valueOf(j) + String.valueOf(jj) + String.valueOf(k);
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
                                        result[index] = String.valueOf(i) + String.valueOf(ii) + String.valueOf(j) + String.valueOf(jj) + String.valueOf(k) + String.valueOf(kk);
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
                                            result[index] = String.valueOf(i) + String.valueOf(ii) + String.valueOf(j) + String.valueOf(jj) + String.valueOf(k) + String.valueOf(kk) + String.valueOf(h);
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
                                                result[index] = String.valueOf(i) + String.valueOf(ii) + String.valueOf(j) + String.valueOf(jj) + String.valueOf(k) + String.valueOf(kk) + String.valueOf(h) + String.valueOf(hh);
                                                index++;
                                            }
                                        }
        }


//        for (int i = 0; i < total_number; i++)
//            System.out.println(result[i]);
        return result;
    }

    int permutation_size(String nc, int t, int[] gn) {
        int total_column = 0;  //calculate the number of stacks on which there are empty slots
        for (int i = 0; i < nc.length(); i++) {
            //System.out.println(nc.charAt(i));
            if (nc.charAt(i) > '0')
                total_column++;
        }
        int index = 0;

        if (total_column == 1) {
            int already_have1 = t - Integer.parseInt(String.valueOf(nc.charAt(0)));
            index = column_state(already_have1, gn).length;
        }

        if (total_column == 2) {
            int already_have1 = t - Integer.parseInt(String.valueOf(nc.charAt(0))); //Integer.parseInt(String.valueOf(nc.charAt(0))): the number of empty slot in a stack
            int already_have2 = t - Integer.parseInt(String.valueOf(nc.charAt(1))); // already_have1: the number of containers stacking on the stack.
            int column_state_size1 = column_state(already_have1, gn).length;
            int column_state_size2 = column_state(already_have2, gn).length;
            for (int i = 0; i < column_state_size1; i++) {
                int start_position2;
                if (already_have2 == already_have1)
                    start_position2 = i;
                else
                    start_position2 = 0;
                for (int j = start_position2; j < column_state_size2; j++)
                    index++;
            }
        }

        if (total_column == 3) {
            int already_have1 = t - Integer.parseInt(String.valueOf(nc.charAt(0))); //Integer.parseInt(String.valueOf(nc.charAt(0))): the number of empty slot in a stack
            int already_have2 = t - Integer.parseInt(String.valueOf(nc.charAt(1))); // already_have1: the number of containers stacking on the stack.
            int already_have3 = t - Integer.parseInt(String.valueOf(nc.charAt(2)));
            int column_state_size1 = column_state(already_have1, gn).length;
            int column_state_size2 = column_state(already_have2, gn).length;
            int column_state_size3 = column_state(already_have3, gn).length;
            for (int i = 0; i < column_state_size1; i++) {
                int start_position2;
                if (already_have2 == already_have1)
                    start_position2 = i;
                else
                    start_position2 = 0;
                for (int ii = start_position2; ii < column_state_size2; ii++) {
                    int start_position3;
                    if (already_have3 == already_have2)
                        start_position3 = ii;
                    else
                        start_position3 = 0;
                    for (int j = start_position3; j < column_state_size3; j++)
                        index++;
                }
            }
        }

        if (total_column == 4) {
            int already_have1 = t - Integer.parseInt(String.valueOf(nc.charAt(0))); //Integer.parseInt(String.valueOf(nc.charAt(0))): the number of empty slot in a stack
            int already_have2 = t - Integer.parseInt(String.valueOf(nc.charAt(1))); // already_have1: the number of containers stacking on the stack.
            int already_have3 = t - Integer.parseInt(String.valueOf(nc.charAt(2)));
            int already_have4 = t - Integer.parseInt(String.valueOf(nc.charAt(3)));
            int column_state_size1 = column_state(already_have1, gn).length;
            int column_state_size2 = column_state(already_have2, gn).length;
            int column_state_size3 = column_state(already_have3, gn).length;
            int column_state_size4 = column_state(already_have4, gn).length;
            for (int i = 0; i < column_state_size1; i++) {
                int start_position2;
                if (already_have2 == already_have1)
                    start_position2 = i;
                else
                    start_position2 = 0;
                for (int ii = start_position2; ii < column_state_size2; ii++) {
                    int start_position3;
                    if (already_have3 == already_have2)
                        start_position3 = ii;
                    else
                        start_position3 = 0;
                    for (int j = start_position3; j < column_state_size3; j++) {
                        int start_position4;
                        if (already_have4 == already_have3)
                            start_position4 = j;
                        else
                            start_position4 = 0;
                        for (int jj = start_position4; jj < column_state_size4; jj++)
                            index++;
                    }
                }
            }
        }

        if (total_column == 5) {
            int already_have1 = t - Integer.parseInt(String.valueOf(nc.charAt(0))); //Integer.parseInt(String.valueOf(nc.charAt(0))): the number of empty slot in a stack
            int already_have2 = t - Integer.parseInt(String.valueOf(nc.charAt(1))); // already_have1: the number of containers stacking on the stack.
            int already_have3 = t - Integer.parseInt(String.valueOf(nc.charAt(2)));
            int already_have4 = t - Integer.parseInt(String.valueOf(nc.charAt(3)));
            int already_have5 = t - Integer.parseInt(String.valueOf(nc.charAt(4)));
            int column_state_size1 = column_state(already_have1, gn).length;
            int column_state_size2 = column_state(already_have2, gn).length;
            int column_state_size3 = column_state(already_have3, gn).length;
            int column_state_size4 = column_state(already_have4, gn).length;
            int column_state_size5 = column_state(already_have5, gn).length;
            for (int i = 0; i < column_state_size1; i++) {
                int start_position2;
                if (already_have2 == already_have1)
                    start_position2 = i;
                else
                    start_position2 = 0;
                for (int ii = start_position2; ii < column_state_size2; ii++) {
                    int start_position3;
                    if (already_have3 == already_have2)
                        start_position3 = ii;
                    else
                        start_position3 = 0;
                    for (int j = start_position3; j < column_state_size3; j++) {
                        int start_position4;
                        if (already_have4 == already_have3)
                            start_position4 = j;
                        else
                            start_position4 = 0;
                        for (int jj = start_position4; jj < column_state_size4; jj++) {
                            int start_position5;
                            if (already_have5 == already_have4)
                                start_position5 = jj;
                            else
                                start_position5 = 0;
                            for (int k = start_position5; k < column_state_size5; k++)
                                index++;
                        }
                    }
                }
            }
        }

        if (total_column == 6) {
            int already_have1 = t - Integer.parseInt(String.valueOf(nc.charAt(0))); //Integer.parseInt(String.valueOf(nc.charAt(0))): the number of empty slot in a stack
            int already_have2 = t - Integer.parseInt(String.valueOf(nc.charAt(1))); // already_have1: the number of containers stacking on the stack.
            int already_have3 = t - Integer.parseInt(String.valueOf(nc.charAt(2)));
            int already_have4 = t - Integer.parseInt(String.valueOf(nc.charAt(3)));
            int already_have5 = t - Integer.parseInt(String.valueOf(nc.charAt(4)));
            int already_have6 = t - Integer.parseInt(String.valueOf(nc.charAt(5)));
            int column_state_size1 = column_state(already_have1, gn).length;
            int column_state_size2 = column_state(already_have2, gn).length;
            int column_state_size3 = column_state(already_have3, gn).length;
            int column_state_size4 = column_state(already_have4, gn).length;
            int column_state_size5 = column_state(already_have5, gn).length;
            int column_state_size6 = column_state(already_have6, gn).length;
            for (int i = 0; i < column_state_size1; i++) {
                int start_position2;
                if (already_have2 == already_have1)
                    start_position2 = i;
                else
                    start_position2 = 0;
                for (int ii = start_position2; ii < column_state_size2; ii++) {
                    int start_position3;
                    if (already_have3 == already_have2)
                        start_position3 = ii;
                    else
                        start_position3 = 0;
                    for (int j = start_position3; j < column_state_size3; j++) {
                        int start_position4;
                        if (already_have4 == already_have3)
                            start_position4 = j;
                        else
                            start_position4 = 0;
                        for (int jj = start_position4; jj < column_state_size4; jj++) {
                            int start_position5;
                            if (already_have5 == already_have4)
                                start_position5 = jj;
                            else
                                start_position5 = 0;
                            for (int k = start_position5; k < column_state_size5; k++) {
                                int start_position6;
                                if (already_have6 == already_have5)
                                    start_position6 = k;
                                else
                                    start_position6 = 0;
                                for (int kk = start_position6; kk < column_state_size6; kk++)
                                    index++;
                            }
                        }
                    }
                }
            }
        }

        if (total_column == 7) {
            int already_have1 = t - Integer.parseInt(String.valueOf(nc.charAt(0))); //Integer.parseInt(String.valueOf(nc.charAt(0))): the number of empty slot in a stack
            int already_have2 = t - Integer.parseInt(String.valueOf(nc.charAt(1))); // already_have1: the number of containers stacking on the stack.
            int already_have3 = t - Integer.parseInt(String.valueOf(nc.charAt(2)));
            int already_have4 = t - Integer.parseInt(String.valueOf(nc.charAt(3)));
            int already_have5 = t - Integer.parseInt(String.valueOf(nc.charAt(4)));
            int already_have6 = t - Integer.parseInt(String.valueOf(nc.charAt(5)));
            int already_have7 = t - Integer.parseInt(String.valueOf(nc.charAt(6)));
            int column_state_size1 = column_state(already_have1, gn).length;
            int column_state_size2 = column_state(already_have2, gn).length;
            int column_state_size3 = column_state(already_have3, gn).length;
            int column_state_size4 = column_state(already_have4, gn).length;
            int column_state_size5 = column_state(already_have5, gn).length;
            int column_state_size6 = column_state(already_have6, gn).length;
            int column_state_size7 = column_state(already_have7, gn).length;
            for (int i = 0; i < column_state_size1; i++) {
                int start_position2;
                if (already_have2 == already_have1)
                    start_position2 = i;
                else
                    start_position2 = 0;
                for (int ii = start_position2; ii < column_state_size2; ii++) {
                    int start_position3;
                    if (already_have3 == already_have2)
                        start_position3 = ii;
                    else
                        start_position3 = 0;
                    for (int j = start_position3; j < column_state_size3; j++) {
                        int start_position4;
                        if (already_have4 == already_have3)
                            start_position4 = j;
                        else
                            start_position4 = 0;
                        for (int jj = start_position4; jj < column_state_size4; jj++) {
                            int start_position5;
                            if (already_have5 == already_have4)
                                start_position5 = jj;
                            else
                                start_position5 = 0;
                            for (int k = start_position5; k < column_state_size5; k++) {
                                int start_position6;
                                if (already_have6 == already_have5)
                                    start_position6 = k;
                                else
                                    start_position6 = 0;
                                for (int kk = start_position6; kk < column_state_size6; kk++) {
                                    int start_position7;
                                    if (already_have7 == already_have6)
                                        start_position7 = kk;
                                    else
                                        start_position7 = 0;
                                    for (int m = start_position7; m < column_state_size7; m++)
                                        index++;
                                }
                            }
                        }
                    }
                }
            }
        }

        if (total_column == 8) {
            int already_have1 = t - Integer.parseInt(String.valueOf(nc.charAt(0))); //Integer.parseInt(String.valueOf(nc.charAt(0))): the number of empty slot in a stack
            int already_have2 = t - Integer.parseInt(String.valueOf(nc.charAt(1))); // already_have1: the number of containers stacking on the stack.
            int already_have3 = t - Integer.parseInt(String.valueOf(nc.charAt(2)));
            int already_have4 = t - Integer.parseInt(String.valueOf(nc.charAt(3)));
            int already_have5 = t - Integer.parseInt(String.valueOf(nc.charAt(4)));
            int already_have6 = t - Integer.parseInt(String.valueOf(nc.charAt(5)));
            int already_have7 = t - Integer.parseInt(String.valueOf(nc.charAt(6)));
            int already_have8 = t - Integer.parseInt(String.valueOf(nc.charAt(7)));
            int column_state_size1 = column_state(already_have1, gn).length;
            int column_state_size2 = column_state(already_have2, gn).length;
            int column_state_size3 = column_state(already_have3, gn).length;
            int column_state_size4 = column_state(already_have4, gn).length;
            int column_state_size5 = column_state(already_have5, gn).length;
            int column_state_size6 = column_state(already_have6, gn).length;
            int column_state_size7 = column_state(already_have7, gn).length;
            int column_state_size8 = column_state(already_have8, gn).length;
            for (int i = 0; i < column_state_size1; i++) {
                int start_position2;
                if (already_have2 == already_have1)
                    start_position2 = i;
                else
                    start_position2 = 0;
                for (int ii = start_position2; ii < column_state_size2; ii++) {
                    int start_position3;
                    if (already_have3 == already_have2)
                        start_position3 = ii;
                    else
                        start_position3 = 0;
                    for (int j = start_position3; j < column_state_size3; j++) {
                        int start_position4;
                        if (already_have4 == already_have3)
                            start_position4 = j;
                        else
                            start_position4 = 0;
                        for (int jj = start_position4; jj < column_state_size4; jj++) {
                            int start_position5;
                            if (already_have5 == already_have4)
                                start_position5 = jj;
                            else
                                start_position5 = 0;
                            for (int k = start_position5; k < column_state_size5; k++) {
                                int start_position6;
                                if (already_have6 == already_have5)
                                    start_position6 = k;
                                else
                                    start_position6 = 0;
                                for (int kk = start_position6; kk < column_state_size6; kk++) {
                                    int start_position7;
                                    if (already_have7 == already_have6)
                                        start_position7 = kk;
                                    else
                                        start_position7 = 0;
                                    for (int m = start_position7; m < column_state_size7; m++) {
                                        int start_position8;
                                        if (already_have8 == already_have7)
                                            start_position8 = m;
                                        else
                                            start_position8 = 0;
                                        for (int mm = start_position8; mm < column_state_size8; mm++)
                                            index++;
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

    int[][] permutation(String nc, int t, int[] gn) {
        int total_column = 0;  //calculate the number of stacks on which there are empty slots
        for (int i = 0; i < nc.length(); i++) {
            //System.out.println(nc.charAt(i));
            if (nc.charAt(i) > '0')
                total_column++;
        }

        int permutation_size = permutation_size(nc, t, gn);
        int[][] sc = new int[permutation_size][nc.length()];
        int index = 0;

        if (total_column == 1) {
            int already_have1 = t - Integer.parseInt(String.valueOf(nc.charAt(0)));
            int[] column1;
            column1 = column_state(already_have1, gn);
            for (int i = 0; i < column1.length; i++) {
                sc[index][0] = column1[i];
                for (int h = 1; h < nc.length(); h++)
                    sc[index][h] = t;
                index++;
            }
        }

        if (total_column == 2) {
            int already_have1 = t - Integer.parseInt(String.valueOf(nc.charAt(0))); //Integer.parseInt(String.valueOf(nc.charAt(0))): the number of empty slot in a stack
            int already_have2 = t - Integer.parseInt(String.valueOf(nc.charAt(1))); // already_have1: the number of containers stacking on the stack.
            int[] column1 = column_state(already_have1, gn);
            int[] column2 = column_state(already_have2, gn);
            int column_state_size1 = column1.length;
            int column_state_size2 = column2.length;
            for (int i = 0; i < column_state_size1; i++) {
                int start_position2;
                if (already_have2 == already_have1)
                    start_position2 = i;
                else
                    start_position2 = 0;
                for (int j = start_position2; j < column_state_size2; j++) {
                    sc[index][0] = column1[i];
                    sc[index][1] = column2[j];
                    for (int h = 2; h < nc.length(); h++)
                        sc[index][h] = t;
                    index++;
                }
            }
        }

        if (total_column == 3) {
            int already_have1 = t - Integer.parseInt(String.valueOf(nc.charAt(0))); //Integer.parseInt(String.valueOf(nc.charAt(0))): the number of empty slot in a stack
            int already_have2 = t - Integer.parseInt(String.valueOf(nc.charAt(1))); // already_have1: the number of containers stacking on the stack.
            int already_have3 = t - Integer.parseInt(String.valueOf(nc.charAt(2)));
            int[] column1 = column_state(already_have1, gn);
            int[] column2 = column_state(already_have2, gn);
            int[] column3 = column_state(already_have3, gn);
            int column_state_size1 = column1.length;
            int column_state_size2 = column2.length;
            int column_state_size3 = column3.length;
            for (int i = 0; i < column_state_size1; i++) {
                int start_position2;
                if (already_have2 == already_have1)
                    start_position2 = i;
                else
                    start_position2 = 0;
                for (int ii = start_position2; ii < column_state_size2; ii++) {
                    int start_position3;
                    if (already_have3 == already_have2)
                        start_position3 = ii;
                    else
                        start_position3 = 0;
                    for (int j = start_position3; j < column_state_size3; j++) {
                        sc[index][0] = column1[i];
                        sc[index][1] = column2[ii];
                        sc[index][2] = column3[j];
                        for (int h = 3; h < nc.length(); h++)
                            sc[index][h] = t;
                        index++;
                    }
                }
            }
        }

        if (total_column == 4) {
            int already_have1 = t - Integer.parseInt(String.valueOf(nc.charAt(0))); //Integer.parseInt(String.valueOf(nc.charAt(0))): the number of empty slot in a stack
            int already_have2 = t - Integer.parseInt(String.valueOf(nc.charAt(1))); // already_have1: the number of containers stacking on the stack.
            int already_have3 = t - Integer.parseInt(String.valueOf(nc.charAt(2)));
            int already_have4 = t - Integer.parseInt(String.valueOf(nc.charAt(3)));
            int[] column1 = column_state(already_have1, gn);
            int[] column2 = column_state(already_have2, gn);
            int[] column3 = column_state(already_have3, gn);
            int[] column4 = column_state(already_have4, gn);
            int column_state_size1 = column1.length;
            int column_state_size2 = column2.length;
            int column_state_size3 = column3.length;
            int column_state_size4 = column4.length;
            for (int i = 0; i < column_state_size1; i++) {
                int start_position2;
                if (already_have2 == already_have1)
                    start_position2 = i;
                else
                    start_position2 = 0;
                for (int ii = start_position2; ii < column_state_size2; ii++) {
                    int start_position3;
                    if (already_have3 == already_have2)
                        start_position3 = ii;
                    else
                        start_position3 = 0;
                    for (int j = start_position3; j < column_state_size3; j++) {
                        int start_position4;
                        if (already_have4 == already_have3)
                            start_position4 = j;
                        else
                            start_position4 = 0;
                        for (int jj = start_position4; jj < column_state_size4; jj++) {
                            sc[index][0] = column1[i];
                            sc[index][1] = column2[ii];
                            sc[index][2] = column3[j];
                            sc[index][3] = column4[jj];
                            for (int h = 4; h < nc.length(); h++)
                                sc[index][h] = t;
                            index++;
                        }
                    }
                }
            }
        }

        if (total_column == 5) {
            int already_have1 = t - Integer.parseInt(String.valueOf(nc.charAt(0))); //Integer.parseInt(String.valueOf(nc.charAt(0))): the number of empty slot in a stack
            int already_have2 = t - Integer.parseInt(String.valueOf(nc.charAt(1))); // already_have1: the number of containers stacking on the stack.
            int already_have3 = t - Integer.parseInt(String.valueOf(nc.charAt(2)));
            int already_have4 = t - Integer.parseInt(String.valueOf(nc.charAt(3)));
            int already_have5 = t - Integer.parseInt(String.valueOf(nc.charAt(4)));
            int[] column1 = column_state(already_have1, gn);
            int[] column2 = column_state(already_have2, gn);
            int[] column3 = column_state(already_have3, gn);
            int[] column4 = column_state(already_have4, gn);
            int[] column5 = column_state(already_have5, gn);
            int column_state_size1 = column1.length;
            int column_state_size2 = column2.length;
            int column_state_size3 = column3.length;
            int column_state_size4 = column4.length;
            int column_state_size5 = column5.length;
            for (int i = 0; i < column_state_size1; i++) {
                int start_position2;
                if (already_have2 == already_have1)
                    start_position2 = i;
                else
                    start_position2 = 0;
                for (int ii = start_position2; ii < column_state_size2; ii++) {
                    int start_position3;
                    if (already_have3 == already_have2)
                        start_position3 = ii;
                    else
                        start_position3 = 0;
                    for (int j = start_position3; j < column_state_size3; j++) {
                        int start_position4;
                        if (already_have4 == already_have3)
                            start_position4 = j;
                        else
                            start_position4 = 0;
                        for (int jj = start_position4; jj < column_state_size4; jj++) {
                            int start_position5;
                            if (already_have5 == already_have4)
                                start_position5 = jj;
                            else
                                start_position5 = 0;
                            for (int k = start_position5; k < column_state_size5; k++) {
                                sc[index][0] = column1[i];
                                sc[index][1] = column2[ii];
                                sc[index][2] = column3[j];
                                sc[index][3] = column4[jj];
                                sc[index][4] = column5[k];
                                for (int h = 5; h < nc.length(); h++)
                                    sc[index][h] = t;
                                index++;
                            }
                        }
                    }
                }
            }
        }

        if (total_column == 6) {
            int already_have1 = t - Integer.parseInt(String.valueOf(nc.charAt(0))); //Integer.parseInt(String.valueOf(nc.charAt(0))): the number of empty slot in a stack
            int already_have2 = t - Integer.parseInt(String.valueOf(nc.charAt(1))); // already_have1: the number of containers stacking on the stack.
            int already_have3 = t - Integer.parseInt(String.valueOf(nc.charAt(2)));
            int already_have4 = t - Integer.parseInt(String.valueOf(nc.charAt(3)));
            int already_have5 = t - Integer.parseInt(String.valueOf(nc.charAt(4)));
            int already_have6 = t - Integer.parseInt(String.valueOf(nc.charAt(5)));
            int[] column1 = column_state(already_have1, gn);
            int[] column2 = column_state(already_have2, gn);
            int[] column3 = column_state(already_have3, gn);
            int[] column4 = column_state(already_have4, gn);
            int[] column5 = column_state(already_have5, gn);
            int[] column6 = column_state(already_have6, gn);
            int column_state_size1 = column1.length;
            int column_state_size2 = column2.length;
            int column_state_size3 = column3.length;
            int column_state_size4 = column4.length;
            int column_state_size5 = column5.length;
            int column_state_size6 = column6.length;
            for (int i = 0; i < column_state_size1; i++) {
                int start_position2;
                if (already_have2 == already_have1)
                    start_position2 = i;
                else
                    start_position2 = 0;
                for (int ii = start_position2; ii < column_state_size2; ii++) {
                    int start_position3;
                    if (already_have3 == already_have2)
                        start_position3 = ii;
                    else
                        start_position3 = 0;
                    for (int j = start_position3; j < column_state_size3; j++) {
                        int start_position4;
                        if (already_have4 == already_have3)
                            start_position4 = j;
                        else
                            start_position4 = 0;
                        for (int jj = start_position4; jj < column_state_size4; jj++) {
                            int start_position5;
                            if (already_have5 == already_have4)
                                start_position5 = jj;
                            else
                                start_position5 = 0;
                            for (int k = start_position5; k < column_state_size5; k++) {
                                int start_position6;
                                if (already_have6 == already_have5)
                                    start_position6 = k;
                                else
                                    start_position6 = 0;
                                for (int kk = start_position6; kk < column_state_size6; kk++) {
                                    sc[index][0] = column1[i];
                                    sc[index][1] = column2[ii];
                                    sc[index][2] = column3[j];
                                    sc[index][3] = column4[jj];
                                    sc[index][4] = column5[k];
                                    sc[index][5] = column6[kk];
                                    for (int h = 6; h < nc.length(); h++)
                                        sc[index][h] = t;
                                    index++;
                                }
                            }
                        }
                    }
                }
            }
        }

        if (total_column == 7) {
            int already_have1 = t - Integer.parseInt(String.valueOf(nc.charAt(0))); //Integer.parseInt(String.valueOf(nc.charAt(0))): the number of empty slot in a stack
            int already_have2 = t - Integer.parseInt(String.valueOf(nc.charAt(1))); // already_have1: the number of containers stacking on the stack.
            int already_have3 = t - Integer.parseInt(String.valueOf(nc.charAt(2)));
            int already_have4 = t - Integer.parseInt(String.valueOf(nc.charAt(3)));
            int already_have5 = t - Integer.parseInt(String.valueOf(nc.charAt(4)));
            int already_have6 = t - Integer.parseInt(String.valueOf(nc.charAt(5)));
            int already_have7 = t - Integer.parseInt(String.valueOf(nc.charAt(6)));
            int[] column1 = column_state(already_have1, gn);
            int[] column2 = column_state(already_have2, gn);
            int[] column3 = column_state(already_have3, gn);
            int[] column4 = column_state(already_have4, gn);
            int[] column5 = column_state(already_have5, gn);
            int[] column6 = column_state(already_have6, gn);
            int[] column7 = column_state(already_have7, gn);
            int column_state_size1 = column1.length;
            int column_state_size2 = column2.length;
            int column_state_size3 = column3.length;
            int column_state_size4 = column4.length;
            int column_state_size5 = column5.length;
            int column_state_size6 = column6.length;
            int column_state_size7 = column7.length;
            for (int i = 0; i < column_state_size1; i++) {
                int start_position2;
                if (already_have2 == already_have1)
                    start_position2 = i;
                else
                    start_position2 = 0;
                for (int ii = start_position2; ii < column_state_size2; ii++) {
                    int start_position3;
                    if (already_have3 == already_have2)
                        start_position3 = ii;
                    else
                        start_position3 = 0;
                    for (int j = start_position3; j < column_state_size3; j++) {
                        int start_position4;
                        if (already_have4 == already_have3)
                            start_position4 = j;
                        else
                            start_position4 = 0;
                        for (int jj = start_position4; jj < column_state_size4; jj++) {
                            int start_position5;
                            if (already_have5 == already_have4)
                                start_position5 = jj;
                            else
                                start_position5 = 0;
                            for (int k = start_position5; k < column_state_size5; k++) {
                                int start_position6;
                                if (already_have6 == already_have5)
                                    start_position6 = k;
                                else
                                    start_position6 = 0;
                                for (int kk = start_position6; kk < column_state_size6; kk++) {
                                    int start_position7;
                                    if (already_have7 == already_have6)
                                        start_position7 = kk;
                                    else
                                        start_position7 = 0;
                                    for (int m = start_position7; m < column_state_size7; m++) {
                                        sc[index][0] = column1[i];
                                        sc[index][1] = column2[ii];
                                        sc[index][2] = column3[j];
                                        sc[index][3] = column4[jj];
                                        sc[index][4] = column5[k];
                                        sc[index][5] = column6[kk];
                                        sc[index][6] = column7[m];
                                        for (int h = 7; h < nc.length(); h++)
                                            sc[index][h] = t;
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
            int already_have1 = t - Integer.parseInt(String.valueOf(nc.charAt(0))); //Integer.parseInt(String.valueOf(nc.charAt(0))): the number of empty slot in a stack
            int already_have2 = t - Integer.parseInt(String.valueOf(nc.charAt(1))); // already_have1: the number of containers stacking on the stack.
            int already_have3 = t - Integer.parseInt(String.valueOf(nc.charAt(2)));
            int already_have4 = t - Integer.parseInt(String.valueOf(nc.charAt(3)));
            int already_have5 = t - Integer.parseInt(String.valueOf(nc.charAt(4)));
            int already_have6 = t - Integer.parseInt(String.valueOf(nc.charAt(5)));
            int already_have7 = t - Integer.parseInt(String.valueOf(nc.charAt(6)));
            int already_have8 = t - Integer.parseInt(String.valueOf(nc.charAt(7)));
            int[] column1 = column_state(already_have1, gn);
            int[] column2 = column_state(already_have2, gn);
            int[] column3 = column_state(already_have3, gn);
            int[] column4 = column_state(already_have4, gn);
            int[] column5 = column_state(already_have5, gn);
            int[] column6 = column_state(already_have6, gn);
            int[] column7 = column_state(already_have7, gn);
            int[] column8 = column_state(already_have8, gn);
            int column_state_size1 = column1.length;
            int column_state_size2 = column2.length;
            int column_state_size3 = column3.length;
            int column_state_size4 = column4.length;
            int column_state_size5 = column5.length;
            int column_state_size6 = column6.length;
            int column_state_size7 = column7.length;
            int column_state_size8 = column8.length;
            for (int i = 0; i < column_state_size1; i++) {
                int start_position2;
                if (already_have2 == already_have1)
                    start_position2 = i;
                else
                    start_position2 = 0;
                for (int ii = start_position2; ii < column_state_size2; ii++) {
                    int start_position3;
                    if (already_have3 == already_have2)
                        start_position3 = ii;
                    else
                        start_position3 = 0;
                    for (int j = start_position3; j < column_state_size3; j++) {
                        int start_position4;
                        if (already_have4 == already_have3)
                            start_position4 = j;
                        else
                            start_position4 = 0;
                        for (int jj = start_position4; jj < column_state_size4; jj++) {
                            int start_position5;
                            if (already_have5 == already_have4)
                                start_position5 = jj;
                            else
                                start_position5 = 0;
                            for (int k = start_position5; k < column_state_size5; k++) {
                                int start_position6;
                                if (already_have6 == already_have5)
                                    start_position6 = k;
                                else
                                    start_position6 = 0;
                                for (int kk = start_position6; kk < column_state_size6; kk++) {
                                    int start_position7;
                                    if (already_have7 == already_have6)
                                        start_position7 = kk;
                                    else
                                        start_position7 = 0;
                                    for (int m = start_position7; m < column_state_size7; m++) {
                                        int start_position8;
                                        if (already_have8 == already_have7)
                                            start_position8 = m;
                                        else
                                            start_position8 = 0;
                                        for (int mm = start_position8; mm < column_state_size8; mm++) {
                                            sc[index][0] = column1[i];
                                            sc[index][1] = column2[ii];
                                            sc[index][2] = column3[j];
                                            sc[index][3] = column4[jj];
                                            sc[index][4] = column5[k];
                                            sc[index][5] = column6[kk];
                                            sc[index][6] = column7[m];
                                            sc[index][7] = column8[mm];
                                            for (int h = 8; h < nc.length(); h++)
                                                sc[index][h] = t;
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

        // for(int i=0; i< permutation_size; i++)
        //  for(int j=0; j<nc.length(); j++)
        //     System.out.println(i+" "+j+" "+sc[i][j]);

        return sc;

    }


    int[] column_state(int column_number, int[] gn) {
        int total_number = 0;
        if (gn.length == 2) {
            for (int i = column_number; i >= 0; i--)
                for (int ii = column_number; ii >= 0; ii--) {
                    if (i + ii == column_number)
                        total_number++;
                }
        }
        if (gn.length == 3) {
            for (int i = column_number; i >= 0; i--)
                for (int ii = column_number; ii >= 0; ii--)
                    for (int j = column_number; j >= 0; j--) {
                        if (i + ii + j == column_number)
                            total_number++;
                    }
        }
        if (gn.length == 4) {
            for (int i = column_number; i >= 0; i--)
                for (int ii = column_number; ii >= 0; ii--)
                    for (int j = column_number; j >= 0; j--)
                        for (int jj = column_number; jj >= 0; jj--) {
                            if (i + ii + j + jj == column_number)
                                total_number++;
                        }
        }

        int[] result = new int[total_number];
        int index = 0;

        if (gn.length == 2) {
            for (int i = column_number; i >= 0; i--)
                for (int ii = column_number; ii >= 0; ii--) {
                    if (i + ii == column_number) {
                        result[index] = i * gn[0] + ii * gn[1];
                        index++;
                    }
                }
        }
        if (gn.length == 3) {
            for (int i = column_number; i >= 0; i--)
                for (int ii = column_number; ii >= 0; ii--)
                    for (int j = column_number; j >= 0; j--) {
                        if (i + ii + j == column_number) {
                            result[index] = i * gn[0] + ii * gn[1] + j * gn[2];
                            index++;
                        }
                    }
        }
        if (gn.length == 4) {
            for (int i = column_number; i >= 0; i--)
                for (int ii = column_number; ii >= 0; ii--)
                    for (int j = column_number; j >= 0; j--)
                        for (int jj = column_number; jj >= 0; jj--) {
                            if (i + ii + j + jj == column_number) {
                                result[index] = i * gn[0] + ii * gn[1] + j * gn[2] + jj * gn[3];
                                index++;
                            }
                        }
        }

        //for(int i=0; i<total_number; i++)
        // System.out.println(result[i]);
        return result;


    }

    int dimension_size(int s, int t, int n, int[] gn, String[] result) {
        int total_number = 0;
        for (int i = 0; i < result.length; i++) {
            total_number = total_number + permutation_size(result[i], t, gn);
        }
        return total_number;
    }

    void dynamic_programming(int s, int t, int[] gn, double[] ratio) {
        // s: the number of stacks
        // t: the number of tiers
        // g: the number of weight groups
        int n = 0;
        String[] nc_pre;
        int[][] sc_pre;
        double[] va_pre;
        int tn_pre;
        String[] nc_cur;
        int[][] sc_cur;
        double[] va_cur;
        int tn_cur;

        tn_pre = 1;
        nc_pre = new String[tn_pre];
        sc_pre = new int[tn_pre][s];
        va_pre = new double[tn_pre];

        String temp = "";
        for (int j = 0; j < s; j++)
            temp = temp + "0";
        nc_pre[0] = temp;
        for (int j = 0; j < s; j++)
            sc_pre[0][j] = t;
        va_pre[0] = 0.0;
        state_size = 0;
        while (n < s * t) {
            n = n + 1;
            //the total dimensiion for the case when the number of empty slots equals n
            String[] nc = solution_space(s, t, n);
            tn_cur = dimension_size(s, t, n, gn, nc);
            state_size = state_size + tn_cur;
            nc_cur = new String[tn_cur];
            sc_cur = new int[tn_cur][s];
            va_cur = new double[tn_cur];
            int index = 0;
            for (int i = 0; i < nc.length; i++) {
                int[][] sc = permutation(nc[i], t, gn);
                for (int j = 0; j < sc.length; j++) {
                    nc_cur[index] = nc[i];
                    sc_cur[index] = sc[j];
                    va_cur[index] = calculate_value(t, nc[i], sc[j], gn, nc_pre, sc_pre, va_pre, ratio);
                    if (n == s * t)
                        final_value = va_cur[index];
                    index++;
                }
            }


            tn_pre = tn_cur;
            nc_pre = new String[tn_pre];
            sc_pre = new int[tn_pre][s];
            va_pre = new double[tn_pre];
            for (int i = 0; i < tn_pre; i++) {
                nc_pre[i] = nc_cur[i];
                sc_pre[i] = sc_cur[i];
                va_pre[i] = va_cur[i];
            }

            for (int i = 0; i < nc_pre.length; i++) {
                System.out.print(i + "  " + nc_pre[i] + " ");
                for (int j = 0; j < sc_pre[0].length; j++) {
                    System.out.print(sc_pre[i][j] + " ");
                }
                System.out.println(va_pre[i]);
            }

        }


    }

    void dynamic_programming(int s, int t, int[] gn, double[] ratio, int compare) {
        // s: the number of stacks
        // t: the number of tiers
        // g: the number of weight groups
        int n = 0;
        String[] nc_pre;
        int[][] sc_pre;
        double[] va_pre;
        int tn_pre;
        String[] nc_cur;
        int[][] sc_cur;
        double[] va_cur;
        int tn_cur;

        tn_pre = 1;
        nc_pre = new String[tn_pre];
        sc_pre = new int[tn_pre][s];
        va_pre = new double[tn_pre];

        String temp = "";
        for (int j = 0; j < s; j++)
            temp = temp + "0";
        nc_pre[0] = temp;
        for (int j = 0; j < s; j++)
            sc_pre[0][j] = t;
        va_pre[0] = 0.0;
        state_size = 0;

        myproperties p = new myproperties();
        try {
            String path = p.getproperties("path");
            FileWriter fw = new FileWriter(path + "store_value" + String.valueOf(s) + String.valueOf(t) + ".txt", false);
            PrintWriter write = new PrintWriter(fw);
            while (n < s * t) {
                n = n + 1;
                //the total dimensiion for the case when the number of empty slots equals n
                String[] nc = solution_space(s, t, n);
                tn_cur = dimension_size(s, t, n, gn, nc);
                state_size = state_size + tn_cur;
                nc_cur = new String[tn_cur];
                sc_cur = new int[tn_cur][s];
                va_cur = new double[tn_cur];
                int index = 0;
                for (int i = 0; i < nc.length; i++) {
                    int[][] sc = permutation(nc[i], t, gn);
                    for (int j = 0; j < sc.length; j++) {
                        nc_cur[index] = nc[i];
                        for (int m = 0; m < s; m++)
                            sc_cur[index][m] = sc[j][m];
                        va_cur[index] = calculate_value(t, nc[i], sc[j], gn, nc_pre, sc_pre, va_pre, ratio);

                        write.print(nc_cur[index] + "\t");
                        for (int m = 0; m < s; m++)
                            write.print(sc_cur[index][m] + "\t");
                        write.print(va_cur[index] + "\t");
                        for (int k = 0; k < gn.length; k++) {
                            write.print(next_weight_group[k] + "\t" + next_state_number[k] + "\t");
                            for (int m = 0; m < s; m++)
                                write.print(next_state_weight[k][m] + "\t");
                            write.print(next_value[k] + "\t");
                        }
                        write.println();

                        if (n == s * t)
                            final_value = va_cur[index];
                        index++;
                    }
                }


                tn_pre = tn_cur;
                nc_pre = new String[tn_pre];
                sc_pre = new int[tn_pre][s];
                va_pre = new double[tn_pre];
                for (int i = 0; i < tn_pre; i++) {
                    nc_pre[i] = nc_cur[i];
                    sc_pre[i] = sc_cur[i];
                    va_pre[i] = va_cur[i];
                }

                for (int i = 0; i < nc_cur.length; i++) {
                    System.out.println(i + "  " + nc_cur[i] + " " + sc_cur[i][0] + " " + sc_cur[i][1] + " " + va_cur[i]);
                }

            }
            fw.close();
            write.close();
        } catch (IOException f) {
            dialogbox dialog = new dialogbox();
            dialog.dialogbox("error", f.toString());
        }


    }


}

class Calculate_total_rehandle {
    void calculate_total_rehandle(int s, int t, String[] gn) {
        Calculate_total_rehandle_prepare dataarray = new Calculate_total_rehandle_prepare();
        dataarray.datainput("store_value" + String.valueOf(s) + String.valueOf(t) + ".txt", s, gn);
        //for(int i=0; i<dataarray.size; i++)
        //System.out.println(i+" "+dataarray.next_value[i][1]);

        String[][] string_generate = dataarray.generate_string(s, t, gn);
        double[] total_rehandle = new double[string_generate.length];
        for (int i = 0; i < total_rehandle.length; i++)
            total_rehandle[i] = 0.0;
        double final_total_rehandle = 0.0;
        for (int i = 0; i < string_generate.length; i++) {
            String[] target = string_generate[i];
            String cur_nc = "";
            String[] cur_sc = new String[s];
            for (int k = 0; k < s; k++)
                cur_sc[k] = "";
            //the initial state
            for (int k = 0; k < s; k++) {
                cur_nc = cur_nc + String.valueOf(t);
                cur_sc[k] = String.valueOf(0);
            }
            for (int j = 0; j < target.length; j++) {
                int target_index = 0;
                //find the target index
                for (int k = 0; k < dataarray.size; k++) {
                    if (cur_nc.equals(dataarray.current_state[k][0])) {
                        boolean find = true;
                        for (int h = 0; h < s; h++) {
                            if (!cur_sc[h].equals(dataarray.current_state[k][h + 1]))
                                find = false;
                        }
                        if (find) {
                            target_index = k;
                            break;
                        }
                    }
                }
                //find the position of the target weight group
                int target_k = 0;
                for (int k = 0; k < gn.length; k++) {
                    if (gn[k].equals(target[j])) {
                        target_k = k;
                        break;
                    }
                }
                cur_nc = dataarray.next_state[target_index][(s + 1) * target_k + 0];
                for (int h = 0; h < s; h++)
                    cur_sc[h] = dataarray.next_state[target_index][(s + 1) * target_k + h + 1];
                total_rehandle[i] = total_rehandle[i] + dataarray.next_value[target_index][target_k];
                //System.out.print("next value:   "+cur_nc + "  ");
                //for(int h=0; h<s; h++){
                //  System.out.print(cur_sc[h]+" ");
                //  }
                //  System.out.println(dataarray.next_value[target_index][target_k]);
            }
            System.out.print(i + "  ");
            for (int h = 0; h < target.length; h++) {
                System.out.print(target[h] + " ");
            }
            System.out.println(total_rehandle[i]);
            //System.out.println();
            final_total_rehandle = final_total_rehandle + total_rehandle[i];
        }
        System.out.println("total rehandle times:   " + final_total_rehandle);

        myproperties p = new myproperties();
        try {
            String path = p.getproperties("path");
            FileWriter fw = new FileWriter(path + "total_rehandel_times" + String.valueOf(s) + String.valueOf(t) + ".txt", false);
            PrintWriter write = new PrintWriter(fw);
            write.println(s + "\t" + t + "\t" + string_generate.length + "\t" + final_total_rehandle + "\t");
            fw.close();
            write.close();
        } catch (IOException f) {
            dialogbox dialog = new dialogbox();
            dialog.dialogbox("error", f.toString());
        }
    }
}

class Calculate_total_rehandle_prepare {
    String[][] current_state;
    double[] current_value;
    String[][] next_state;
    String[][] next_weight_group;
    double[][] next_value;
    int size;

    void datainput(String filename, int s, String[] gn) {
        myproperties p = new myproperties();
        String path = p.getproperties("path");

        Input input = new Input();
        String[][] dataarray = input.readdata(path + filename);

        size = dataarray.length;
        current_state = new String[dataarray.length][1 + s];
        current_value = new double[dataarray.length];
        next_state = new String[dataarray.length][(1 + s) * gn.length];
        next_weight_group = new String[dataarray.length][gn.length];
        next_value = new double[dataarray.length][gn.length];

        for (int i = 0; i < dataarray.length; i++) {
            current_state[i][0] = dataarray[i][0];
            for (int j = 0; j < s; j++)
                current_state[i][j + 1] = dataarray[i][j + 1];
            current_value[i] = Double.valueOf(dataarray[i][1 + s]);
            for (int k = 0; k < gn.length; k++) {
                next_weight_group[i][k] = dataarray[i][(1 + 1 + s + 1) * k + (2 + s)];
                next_state[i][(s + 1) * k + 0] = dataarray[i][(1 + 1 + s + 1) * k + (3 + s)];
                for (int j = 0; j < s; j++)
                    next_state[i][(s + 1) * k + j + 1] = dataarray[i][(1 + 1 + s + 1) * k + (4 + s) + j];
                next_value[i][k] = Double.valueOf(dataarray[i][(1 + 1 + s + 1) * k + (5 + s) + s - 1]);
            }
        }
    }


    String[][] generate_string(int s, int t, String[] gn) {
        int accumulation = 1;
        for (int i = 0; i < s * t; i++)
            accumulation = accumulation * gn.length;

        String[][] permutation = new String[accumulation][s * t];
        int index = 0;

        if (s * t == 30)
            for (int i = 0; i < gn.length; i++)
                for (int i1 = 0; i1 < gn.length; i1++)
                    for (int i2 = 0; i2 < gn.length; i2++)
                        for (int i3 = 0; i3 < gn.length; i3++)
                            for (int i4 = 0; i4 < gn.length; i4++)
                                for (int i5 = 0; i5 < gn.length; i5++)
                                    for (int i6 = 0; i6 < gn.length; i6++)
                                        for (int i7 = 0; i7 < gn.length; i7++)
                                            for (int i8 = 0; i8 < gn.length; i8++)
                                                for (int i9 = 0; i9 < gn.length; i9++)
                                                    for (int j = 0; j < gn.length; j++)
                                                        for (int j1 = 0; j1 < gn.length; j1++)
                                                            for (int j2 = 0; j2 < gn.length; j2++)
                                                                for (int j3 = 0; j3 < gn.length; j3++)
                                                                    for (int j4 = 0; j4 < gn.length; j4++)
                                                                        for (int j5 = 0; j5 < gn.length; j5++)
                                                                            for (int j6 = 0; j6 < gn.length; j6++)
                                                                                for (int j7 = 0; j7 < gn.length; j7++)
                                                                                    for (int j8 = 0; j8 < gn.length; j8++)
                                                                                        for (int j9 = 0; j9 < gn.length; j9++)
                                                                                            for (int k = 0; k < gn.length; k++)
                                                                                                for (int k1 = 0; k1 < gn.length; k1++)
                                                                                                    for (int k2 = 0; k2 < gn.length; k2++)
                                                                                                        for (int k3 = 0; k3 < gn.length; k3++)
                                                                                                            for (int k4 = 0; k4 < gn.length; k4++)
                                                                                                                for (int k5 = 0; k5 < gn.length; k5++)
                                                                                                                    for (int k6 = 0; k6 < gn.length; k6++)
                                                                                                                        for (int k7 = 0; k7 < gn.length; k7++)
                                                                                                                            for (int k8 = 0; k8 < gn.length; k8++)
                                                                                                                                for (int k9 = 0; k9 < gn.length; k9++) {
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
                                                                                                                                    permutation[index][28] = gn[k8];
                                                                                                                                    permutation[index][29] = gn[k9];
                                                                                                                                    index++;
                                                                                                                                }

        if (s * t == 25)
            for (int i = 0; i < gn.length; i++)
                for (int i1 = 0; i1 < gn.length; i1++)
                    for (int i2 = 0; i2 < gn.length; i2++)
                        for (int i3 = 0; i3 < gn.length; i3++)
                            for (int i4 = 0; i4 < gn.length; i4++)
                                for (int i5 = 0; i5 < gn.length; i5++)
                                    for (int i6 = 0; i6 < gn.length; i6++)
                                        for (int i7 = 0; i7 < gn.length; i7++)
                                            for (int i8 = 0; i8 < gn.length; i8++)
                                                for (int i9 = 0; i9 < gn.length; i9++)
                                                    for (int j = 0; j < gn.length; j++)
                                                        for (int j1 = 0; j1 < gn.length; j1++)
                                                            for (int j2 = 0; j2 < gn.length; j2++)
                                                                for (int j3 = 0; j3 < gn.length; j3++)
                                                                    for (int j4 = 0; j4 < gn.length; j4++)
                                                                        for (int j5 = 0; j5 < gn.length; j5++)
                                                                            for (int j6 = 0; j6 < gn.length; j6++)
                                                                                for (int j7 = 0; j7 < gn.length; j7++)
                                                                                    for (int j8 = 0; j8 < gn.length; j8++)
                                                                                        for (int j9 = 0; j9 < gn.length; j9++)
                                                                                            for (int k = 0; k < gn.length; k++)
                                                                                                for (int k1 = 0; k1 < gn.length; k1++)
                                                                                                    for (int k2 = 0; k2 < gn.length; k2++)
                                                                                                        for (int k3 = 0; k3 < gn.length; k3++)
                                                                                                            for (int k4 = 0; k4 < gn.length; k4++) {
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
                                                                                                                index++;
                                                                                                            }


        if (s * t == 24)
            for (int i = 0; i < gn.length; i++)
                for (int i1 = 0; i1 < gn.length; i1++)
                    for (int i2 = 0; i2 < gn.length; i2++)
                        for (int i3 = 0; i3 < gn.length; i3++)
                            for (int i4 = 0; i4 < gn.length; i4++)
                                for (int i5 = 0; i5 < gn.length; i5++)
                                    for (int i6 = 0; i6 < gn.length; i6++)
                                        for (int i7 = 0; i7 < gn.length; i7++)
                                            for (int i8 = 0; i8 < gn.length; i8++)
                                                for (int i9 = 0; i9 < gn.length; i9++)
                                                    for (int j = 0; j < gn.length; j++)
                                                        for (int j1 = 0; j1 < gn.length; j1++)
                                                            for (int j2 = 0; j2 < gn.length; j2++)
                                                                for (int j3 = 0; j3 < gn.length; j3++)
                                                                    for (int j4 = 0; j4 < gn.length; j4++)
                                                                        for (int j5 = 0; j5 < gn.length; j5++)
                                                                            for (int j6 = 0; j6 < gn.length; j6++)
                                                                                for (int j7 = 0; j7 < gn.length; j7++)
                                                                                    for (int j8 = 0; j8 < gn.length; j8++)
                                                                                        for (int j9 = 0; j9 < gn.length; j9++)
                                                                                            for (int k = 0; k < gn.length; k++)
                                                                                                for (int k1 = 0; k1 < gn.length; k1++)
                                                                                                    for (int k2 = 0; k2 < gn.length; k2++)
                                                                                                        for (int k3 = 0; k3 < gn.length; k3++) {
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
                                                                                                        }


        if (s * t == 20)
            for (int i = 0; i < gn.length; i++)
                for (int i1 = 0; i1 < gn.length; i1++)
                    for (int i2 = 0; i2 < gn.length; i2++)
                        for (int i3 = 0; i3 < gn.length; i3++)
                            for (int i4 = 0; i4 < gn.length; i4++)
                                for (int i5 = 0; i5 < gn.length; i5++)
                                    for (int i6 = 0; i6 < gn.length; i6++)
                                        for (int i7 = 0; i7 < gn.length; i7++)
                                            for (int i8 = 0; i8 < gn.length; i8++)
                                                for (int i9 = 0; i9 < gn.length; i9++)
                                                    for (int j = 0; j < gn.length; j++)
                                                        for (int j1 = 0; j1 < gn.length; j1++)
                                                            for (int j2 = 0; j2 < gn.length; j2++)
                                                                for (int j3 = 0; j3 < gn.length; j3++)
                                                                    for (int j4 = 0; j4 < gn.length; j4++)
                                                                        for (int j5 = 0; j5 < gn.length; j5++)
                                                                            for (int j6 = 0; j6 < gn.length; j6++)
                                                                                for (int j7 = 0; j7 < gn.length; j7++)
                                                                                    for (int j8 = 0; j8 < gn.length; j8++)
                                                                                        for (int j9 = 0; j9 < gn.length; j9++) {
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
                                                                                        }

        if (s * t == 18)
            for (int i = 0; i < gn.length; i++)
                for (int i1 = 0; i1 < gn.length; i1++)
                    for (int i2 = 0; i2 < gn.length; i2++)
                        for (int i3 = 0; i3 < gn.length; i3++)
                            for (int i4 = 0; i4 < gn.length; i4++)
                                for (int i5 = 0; i5 < gn.length; i5++)
                                    for (int i6 = 0; i6 < gn.length; i6++)
                                        for (int i7 = 0; i7 < gn.length; i7++)
                                            for (int i8 = 0; i8 < gn.length; i8++)
                                                for (int i9 = 0; i9 < gn.length; i9++)
                                                    for (int j = 0; j < gn.length; j++)
                                                        for (int j1 = 0; j1 < gn.length; j1++)
                                                            for (int j2 = 0; j2 < gn.length; j2++)
                                                                for (int j3 = 0; j3 < gn.length; j3++)
                                                                    for (int j4 = 0; j4 < gn.length; j4++)
                                                                        for (int j5 = 0; j5 < gn.length; j5++)
                                                                            for (int j6 = 0; j6 < gn.length; j6++)
                                                                                for (int j7 = 0; j7 < gn.length; j7++) {
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
                                                                                }

        if (s * t == 16)
            for (int i = 0; i < gn.length; i++)
                for (int i1 = 0; i1 < gn.length; i1++)
                    for (int i2 = 0; i2 < gn.length; i2++)
                        for (int i3 = 0; i3 < gn.length; i3++)
                            for (int i4 = 0; i4 < gn.length; i4++)
                                for (int i5 = 0; i5 < gn.length; i5++)
                                    for (int i6 = 0; i6 < gn.length; i6++)
                                        for (int i7 = 0; i7 < gn.length; i7++)
                                            for (int i8 = 0; i8 < gn.length; i8++)
                                                for (int i9 = 0; i9 < gn.length; i9++)
                                                    for (int j = 0; j < gn.length; j++)
                                                        for (int j1 = 0; j1 < gn.length; j1++)
                                                            for (int j2 = 0; j2 < gn.length; j2++)
                                                                for (int j3 = 0; j3 < gn.length; j3++)
                                                                    for (int j4 = 0; j4 < gn.length; j4++)
                                                                        for (int j5 = 0; j5 < gn.length; j5++) {
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
                                                                        }


        if (s * t == 15)
            for (int i = 0; i < gn.length; i++)
                for (int i1 = 0; i1 < gn.length; i1++)
                    for (int i2 = 0; i2 < gn.length; i2++)
                        for (int i3 = 0; i3 < gn.length; i3++)
                            for (int i4 = 0; i4 < gn.length; i4++)
                                for (int i5 = 0; i5 < gn.length; i5++)
                                    for (int i6 = 0; i6 < gn.length; i6++)
                                        for (int i7 = 0; i7 < gn.length; i7++)
                                            for (int i8 = 0; i8 < gn.length; i8++)
                                                for (int i9 = 0; i9 < gn.length; i9++)
                                                    for (int j = 0; j < gn.length; j++)
                                                        for (int j1 = 0; j1 < gn.length; j1++)
                                                            for (int j2 = 0; j2 < gn.length; j2++)
                                                                for (int j3 = 0; j3 < gn.length; j3++)
                                                                    for (int j4 = 0; j4 < gn.length; j4++) {
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
                                                                    }

        if (s * t == 12)
            for (int i = 0; i < gn.length; i++)
                for (int i1 = 0; i1 < gn.length; i1++)
                    for (int i2 = 0; i2 < gn.length; i2++)
                        for (int i3 = 0; i3 < gn.length; i3++)
                            for (int i4 = 0; i4 < gn.length; i4++)
                                for (int i5 = 0; i5 < gn.length; i5++)
                                    for (int i6 = 0; i6 < gn.length; i6++)
                                        for (int i7 = 0; i7 < gn.length; i7++)
                                            for (int i8 = 0; i8 < gn.length; i8++)
                                                for (int i9 = 0; i9 < gn.length; i9++)
                                                    for (int j = 0; j < gn.length; j++)
                                                        for (int j1 = 0; j1 < gn.length; j1++) {
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
                                                        }


        if (s * t == 10)
            for (int i = 0; i < gn.length; i++)
                for (int i1 = 0; i1 < gn.length; i1++)
                    for (int i2 = 0; i2 < gn.length; i2++)
                        for (int i3 = 0; i3 < gn.length; i3++)
                            for (int i4 = 0; i4 < gn.length; i4++)
                                for (int i5 = 0; i5 < gn.length; i5++)
                                    for (int i6 = 0; i6 < gn.length; i6++)
                                        for (int i7 = 0; i7 < gn.length; i7++)
                                            for (int i8 = 0; i8 < gn.length; i8++)
                                                for (int i9 = 0; i9 < gn.length; i9++) {
                                                    //permutation[index] = gn[i]+gn[i1]+gn[i2]+gn[i3]+gn[i4]+gn[i5]+gn[i6]+gn[i7]+gn[i8]+gn[i9];
                                                    index++;
                                                }

        if (s * t == 9)
            for (int i = 0; i < gn.length; i++)
                for (int i1 = 0; i1 < gn.length; i1++)
                    for (int i2 = 0; i2 < gn.length; i2++)
                        for (int i3 = 0; i3 < gn.length; i3++)
                            for (int i4 = 0; i4 < gn.length; i4++)
                                for (int i5 = 0; i5 < gn.length; i5++)
                                    for (int i6 = 0; i6 < gn.length; i6++)
                                        for (int i7 = 0; i7 < gn.length; i7++)
                                            for (int i8 = 0; i8 < gn.length; i8++) {
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
                                            }


        return permutation;
    }
}


//.txt文件输入�?
class Input {
    String[][] readdata(String filename) {
        String[] content = null;
        int linenu = 0;
        int totalline = 0;
        String line = null;
        //先统计文件有多少�?
        try {
            FileReader fr = new FileReader(filename);
            BufferedReader br = new BufferedReader(fr);
            while ((line = br.readLine()) != null) {
                totalline = totalline + 1;
            }
            fr.close();
            br.close();
        } catch (IOException ae) {
            dialogbox dialog = new dialogbox();
            dialog.dialogbox("警告 ", "可能的原因是�?.相应的文件没有找到；2." + ae.toString());
        }

        String[][] res = new String[totalline][];
        //逐行保存到数据里边去
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
            dialogbox dialog = new dialogbox();
            dialog.dialogbox("警告 ", "可能的原因是�?.相应的文件没有找到；2." + ae.toString());
        }
        return res;
    }
}


public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Dynamic_programming dynamic_programming = new Dynamic_programming();

        int[] gn = {10, 1};
        double[] ratio = {0.5, 0.5};
        //int[] gn ={100,10,1};
        //double[] ratio={0.33,0.33, 0.33};
        long begin_time, end_time, duration;
        myproperties p = new myproperties();

        try {
            String path = p.getproperties("path");
            FileWriter fw = new FileWriter(path + "final.txt", false);
            PrintWriter write = new PrintWriter(fw);
            for (int s = 4; s < 6; s++) {
                for (int t = 3; t < 6; t++) {
                     if (s == 5) {
                    Date mydate = new Date();
                    begin_time = mydate.getTime();
                    dynamic_programming.dynamic_programming(s, t, gn, ratio);
                    Date mydate2 = new Date();
                    end_time = mydate2.getTime();
                    duration = end_time - begin_time;
                    write.println(s + "\t" + t + "\t" + 2 + "\t" + dynamic_programming.state_size + "\t" + dynamic_programming.final_value + "\t" + duration);
                     }
                }
            }
            fw.close();
            write.close();
        } catch (IOException f) {
            dialogbox dialog = new dialogbox();
            dialog.dialogbox("error", f.toString());
        }
   /*
        String[] gn_special ={"100","10","1"};
        //String[] gn_special ={"10","1"};
        Calculate_total_rehandle calculate_total_rehandle = new Calculate_total_rehandle();
        for(int s=3; s<5; s++)
                for(int t=3; t<7; t++)
                    if(s*t<=12){
                        calculate_total_rehandle.calculate_total_rehandle(s, t, gn_special);
                    }
    */
    }

}
